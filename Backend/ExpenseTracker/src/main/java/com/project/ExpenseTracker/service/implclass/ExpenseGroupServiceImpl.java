package com.project.ExpenseTracker.service.implclass;

import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.model.ExpenseGroup;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.payload.expenseGroup.RequestGroupDTO;
import com.project.ExpenseTracker.payload.expenseGroup.ResponseGroupDTO;
import com.project.ExpenseTracker.repository.ExpenseGroupRepo;
import com.project.ExpenseTracker.repository.UserRepo;
import com.project.ExpenseTracker.service.abstractclass.ExpenseGroupService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public ResponseGroupDTO createGroup(Long uid, @Valid RequestGroupDTO requestGroupDTO) {
        Users user = userRepo.findById(uid)
                .orElseThrow(() -> new UserNotFound("User Not found with ID: " + uid));
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
}
