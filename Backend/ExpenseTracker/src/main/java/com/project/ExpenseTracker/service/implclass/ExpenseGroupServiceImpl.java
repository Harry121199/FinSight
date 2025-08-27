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

    private UserDetails getUserDetails() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    @Transactional
    public ResponseGroupDTO createGroup(Long uid, @Valid RequestGroupDTO requestGroupDTO) {
        UserDetails userDetails = getUserDetails();

        Users user = userRepo.findById(uid)
                .orElseThrow(() -> new UserNotFound("User Not found with ID: " + uid));

        if(!userDetails.getUsername().equals(user.getEmail()))
            throw new SecurityException("You are allowed to perform this action");

        ExpenseGroup mapped = modelMapper.map(requestGroupDTO, ExpenseGroup.class);
        mapped.setCreatedBy(user);
        List<Users> members = new ArrayList<>();
        members.add(user);
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
        UserDetails userDetails = getUserDetails();

        Users currentUser = userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not logged in: "));

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
        UserDetails userDetails = getUserDetails();
        Users currentUser = userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not logged in: "));
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
    public String addUser(UserNameDTO userNameDTO, Long gid, Long uid) {
        UserDetails userDetails = getUserDetails();
        Users currentUser = userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not logged in: "));

        ExpenseGroup expenseGroup = expenseGroupRepo.findById(gid)
                .orElseThrow(() -> new GroupNotFound("Group not found with ID: " + gid));

        boolean notPresent = expenseGroup.getMembers().stream()
                .noneMatch(members -> members.getUid().equals(currentUser.getUid()));
        if(notPresent) throw new UserNotInGroup("Current user not a member of this group.");

        Users memberToAdd = userRepo.findByEmail(userNameDTO.getEmail())
                .orElseThrow(() -> new UserNotFound("User not found with email: " + userNameDTO.getEmail()));

        expenseGroup.getMembers().add(memberToAdd);

        expenseGroupRepo.save(expenseGroup);

        return "User Successfully";
    }

    @Override
    public List<ResponseUserDTO> getAllUser(Long gid, Long uid) {
        UserDetails userDetails = getUserDetails();
        Users currentUser = userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not loggedIn."));
        ExpenseGroup expenseGroup = expenseGroupRepo.findById(gid)
                .orElseThrow(() -> new GroupNotFound("Group not found with ID: " + gid));

        boolean notPresent = expenseGroup.getMembers().stream()
                .noneMatch(members -> members.getUid().equals(currentUser.getUid()));
        if(notPresent) throw new UserNotInGroup("Current user not a member of this group.");

        return expenseGroup.getMembers().stream()
                .map(members -> modelMapper.map(members, ResponseUserDTO.class))
                .toList();
    }
}
