package com.project.ExpenseTracker.service.implclass;

import com.project.ExpenseTracker.exception.GroupNotFound;
import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.exception.UserNotInGroup;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.model.ExpenseGroup;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.payload.expense.RequestExpenseDTO;
import com.project.ExpenseTracker.payload.expense.ResponseExpenseDTO;
import com.project.ExpenseTracker.payload.expenseGroup.RequestGroupDTO;
import com.project.ExpenseTracker.payload.expenseGroup.ResponseGroupDTO;
import com.project.ExpenseTracker.payload.user.ResponseUserDTO;
import com.project.ExpenseTracker.payload.user.UserNameDTO;
import com.project.ExpenseTracker.repository.ExpenseGroupRepo;
import com.project.ExpenseTracker.repository.ExpenseRepo;
import com.project.ExpenseTracker.repository.UserRepo;
import com.project.ExpenseTracker.service.abstractclass.ExpenseGroupService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseGroupServiceImpl implements ExpenseGroupService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ExpenseGroupRepo expenseGroupRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ExpenseRepo expenseRepo;

    private Users getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("You are not loggedIN"));
    }

    @Override
    @Transactional
    public ResponseGroupDTO createGroup(@Valid RequestGroupDTO requestGroupDTO) {
        Users currentUser = getCurrentUser();
        ExpenseGroup mapped = modelMapper.map(requestGroupDTO, ExpenseGroup.class);
        mapped.setCreatedBy(currentUser);
        List<Users> members = new ArrayList<>();
        members.add(currentUser);
        if(requestGroupDTO.getMembersIdentifiers() != null){
            requestGroupDTO.getMembersIdentifiers()
                    .forEach(membersEmail -> members.add(userRepo.findByEmail(membersEmail)
                            .orElseThrow(() -> new UserNotFound("Cannot add member with email: ".concat(membersEmail).concat(" Member cannot be found")))));
        }
        mapped.setMembers(members);
        ExpenseGroup saved = expenseGroupRepo.save(mapped);
        return modelMapper.map(saved, ResponseGroupDTO.class);
    }

    @Override
    @Transactional
    public List<ResponseExpenseDTO> addAllExpenses(Long gid, List<RequestExpenseDTO> requestExpenseDTOList) {
        Users currentUser = getCurrentUser();

        ExpenseGroup expenseGroup = expenseGroupRepo.findById(gid)
                .orElseThrow(() -> new GroupNotFound("Group not found with ID: " + gid));

        boolean notPresent = expenseGroup.getMembers().stream()
                .noneMatch(member -> member.getUid().equals(currentUser.getUid()));

        if(notPresent) throw new UserNotInGroup("User not a member of this group.");

        List<Expense> expenseList = requestExpenseDTOList.stream()
                .map(requestExpenseDTO -> {
                    Expense expense = modelMapper.map(requestExpenseDTO, Expense.class);
                    expense.setUser(currentUser);
                    expense.setExpenseGroup(expenseGroup);
                    return expense;
                })
                .toList();
        List<Expense> expenses = expenseRepo.saveAll(expenseList);

        return expenses.stream()
                .map(expense -> modelMapper.map(expense, ResponseExpenseDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public ResponseExpenseDTO addExpense(Long gid, RequestExpenseDTO requestExpenseDTO) {
        Users currentUser = getCurrentUser();

        ExpenseGroup expenseGroup = expenseGroupRepo.findById(gid)
                .orElseThrow(() -> new GroupNotFound("Group not found with ID: " + gid));

        boolean notPresent = expenseGroup.getMembers().stream()
                .noneMatch(members -> members.getUid().equals(currentUser.getUid()));

        if(notPresent) throw new UserNotInGroup("User not a member of this group.");

        Expense mapped = modelMapper.map(requestExpenseDTO, Expense.class);
        mapped.setUser(currentUser);
        mapped.setExpenseGroup(expenseGroup);
        Expense saved = expenseRepo.save(mapped);

        return modelMapper.map(saved, ResponseExpenseDTO.class);
    }

    @Override
    @Transactional
    public String addUser(UserNameDTO userNameDTO, Long gid) {
        Users currentUser = getCurrentUser();

        ExpenseGroup expenseGroup = expenseGroupRepo.findById(gid)
                .orElseThrow(() -> new GroupNotFound("Group not found with ID: " + gid));

        boolean notPresent = expenseGroup.getMembers().stream()
                .noneMatch(members -> members.getUid().equals(currentUser.getUid()));

        if(notPresent) throw new UserNotInGroup("Current user not a member of this group.");

        Users memberToAdd = userRepo.findByEmail(userNameDTO.getEmail())
                .orElseThrow(() -> new UserNotFound("User not found with email: " + userNameDTO.getEmail()));

        expenseGroup.getMembers().add(memberToAdd);

        expenseGroupRepo.save(expenseGroup);

        return "User added Successfully";
    }

    @Override
    public List<ResponseUserDTO> getAllUser(Long gid) {
        Users currentUser = getCurrentUser();

        ExpenseGroup expenseGroup = expenseGroupRepo.findById(gid)
                .orElseThrow(() -> new GroupNotFound("Group not found with ID: " + gid));

        boolean notPresent = expenseGroup.getMembers().stream()
                .noneMatch(members -> members.getUid().equals(currentUser.getUid()));

        if(notPresent) throw new UserNotInGroup("Current user not a member of this group.");


        return expenseGroup.getMembers().stream()
                .map(members -> modelMapper.map(members, ResponseUserDTO.class))
                .toList();
    }

    @Override
    public List<ResponseExpenseDTO> getAllExpense(Long gid) {
        Users currentUser = getCurrentUser();
        ExpenseGroup expenseGroup = expenseGroupRepo.findById(gid)
                .orElseThrow(() -> new GroupNotFound("Group cannot be found with ID: " + gid));
        boolean notMember = expenseGroup.getMembers().stream().noneMatch(member -> member.getUid().equals(currentUser.getUid()));
        if(notMember) {
            throw new UserNotInGroup("Current user not a member of group with ID: " + gid);
        }
        return expenseGroup.getExpenses().stream()
                .map(expense -> modelMapper.map(expense, ResponseExpenseDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public String removeUser(UserNameDTO userNameDTO, Long gid) {
        Users currentUser = getCurrentUser();

        ExpenseGroup expenseGroup = expenseGroupRepo.findById(gid)
                .orElseThrow(() -> new GroupNotFound("Cannot found group with ID: " + gid));

        if (!currentUser.getUid().equals(expenseGroup.getCreatedBy().getUid())) {
            throw new SecurityException("Only admins can remove users from group");
        }

        Users memberToRemove = userRepo.findByEmail(userNameDTO.getEmail())
                .orElseThrow(() -> new UserNotFound("User cannot be found with email: " + userNameDTO.getEmail()));

        boolean removed = expenseGroup.getMembers().removeIf(member -> member.getUid().equals(memberToRemove.getUid()));
        if(!removed) throw
                new UserNotInGroup("User cannot be found in this group");

        return "User removed successfully";
    }

    @Override
    @Transactional
    public String removeExpense(Long eid, Long gid) {
        Users currentUser = getCurrentUser();
        Expense expense = expenseRepo.findById(eid)
                .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + eid));

        // 3. Verify the expense belongs to the specified group
        if (expense.getExpenseGroup() == null || !expense.getExpenseGroup().getGid().equals(gid)) {
            throw new IllegalArgumentException("Expense does not belong to the specified group.");
        }

        // 4. Authorization Check:
        // Allow deletion if the user is the one who added the expense OR is the group creator.
        boolean isCreator = expense.getExpenseGroup().getCreatedBy().getUid().equals(currentUser.getUid());
        boolean isPayer = expense.getUser().getUid().equals(currentUser.getUid());

        if (!isCreator && !isPayer) {
            throw new SecurityException("User is not authorized to delete this expense.");
        }

        // 5. If all checks pass, delete the expense
        expenseRepo.delete(expense);
        return "Expense removed successfully";
    }
}
