package com.project.ExpenseTracker.service.implclass;

import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.model.RecurringExpense;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.payload.recurringExpenses.RequestRecurringExpenseDTO;
import com.project.ExpenseTracker.payload.recurringExpenses.ResponseRecurringExpenseDTO;
import com.project.ExpenseTracker.repository.ExpenseRepo;
import com.project.ExpenseTracker.repository.RecurringExpenseRepo;
import com.project.ExpenseTracker.repository.UserRepo;
import com.project.ExpenseTracker.service.abstractclass.RecurringExpenseService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecurringExpenseServiceImpl implements RecurringExpenseService {
    private static final Logger logger;

    static {
        logger = LoggerFactory.getLogger(RecurringExpense.class);
    }

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RecurringExpenseRepo recurringExpenseRepo;

    @Autowired
    private ExpenseRepo expenseRepo;

    private Users getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("You are not logged in"));
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * ?")
    public void createExpenseFromRecurring() {
        logger.info("Running scheduled job to create recurring expenses...");
        LocalDate today = LocalDate.now();
        Integer currentDay = today.getDayOfMonth();
        List<RecurringExpense> dueTemplates = recurringExpenseRepo.findAllByDayOfMonthAndStartDateLessThanEqual(currentDay, today);


        dueTemplates.stream()
                .filter(recurring -> recurring.getEndDate() == null || !today.isAfter(recurring.getEndDate()))
                .forEach(recurring -> {
                    boolean alreadyExists = expenseRepo.existsByRecurringSourceForMonth(recurring.getRid(), today.getYear(), today.getMonthValue());
                    if (!alreadyExists) {
                        logger.info("Creating new expense for recurring item: {}", recurring.getItemName());
                        Expense newExpense = modelMapper.map(recurring, Expense.class);
                        newExpense.setUser(recurring.getUser());
                        newExpense.setTransactionDate(today);
                        newExpense.setRecurringExpense(recurring);
                        newExpense.setDescription("Auto-Generated: " + recurring.getDescription());
                        expenseRepo.save(newExpense);
                    }
                });
        logger.info("Scheduled job finished.");
    }

    @Override
    @Transactional
    public ResponseRecurringExpenseDTO createRecurringExpense(RequestRecurringExpenseDTO requestRecurringExpenseDTO) {
        Users currentUser = getCurrentUser();
        RecurringExpense recurringExpense = modelMapper.map(requestRecurringExpenseDTO, RecurringExpense.class);
        recurringExpense.setUser(currentUser);
        recurringExpenseRepo.save(recurringExpense);
        return modelMapper.map(recurringExpense, ResponseRecurringExpenseDTO.class);
    }

    @Override
    @Transactional
    public List<ResponseRecurringExpenseDTO> createAllRecurringExpenses(List<RequestRecurringExpenseDTO> recurringExpenseDTOS) {
        Users currentUser = getCurrentUser();
        List<RecurringExpense> recurringExpenseList = recurringExpenseDTOS.stream()
                .map(recurring -> {
                    RecurringExpense recurringExpense = modelMapper.map(recurring, RecurringExpense.class);
                    recurringExpense.setUser(currentUser);
                    return recurringExpense;
                }).toList();
        List<RecurringExpense> savedList = recurringExpenseRepo.saveAll(recurringExpenseList);
        return savedList.stream()
                .map(recurringExpense -> modelMapper.map(recurringExpense, ResponseRecurringExpenseDTO.class))
                .toList();
    }

    @Override
    public List<ResponseRecurringExpenseDTO> getAllRecurringService() {
        Users currentUser = getCurrentUser();
        List<RecurringExpense> response = recurringExpenseRepo.findAllByUserUid(currentUser.getUid());
        return response.stream()
                .map(recurringExpense -> modelMapper.map(recurringExpense, ResponseRecurringExpenseDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public String deleteRecurringExpenseById(Long rid) {
        Users currentUser = getCurrentUser();
        RecurringExpense recurringExpense = recurringExpenseRepo.findById(rid)
                .orElseThrow(() -> new RuntimeException("Cannot find Recurring Expense with ID: " + rid));
        if (!recurringExpense.getUser().getUid().equals(currentUser.getUid())) {
            throw new SecurityException("You are not authorized to delete this Recurring Expense");
        }
        recurringExpenseRepo.deleteById(rid);
        return "Recurring Expense Deleted successfully";
    }

}
