package com.project.ExpenseTracker.service.implclass;


import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.exception.ExpenseNotFound;
import com.project.ExpenseTracker.filter.ExpenseFilterRequest;
import com.project.ExpenseTracker.filter.ExpenseFilterSpecification;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.payload.dashboard.CategorySpendingDTO;
import com.project.ExpenseTracker.payload.dashboard.DashBoardDTO;
import com.project.ExpenseTracker.payload.dashboard.MonthlyTrendDTO;
import com.project.ExpenseTracker.payload.expense.RequestExpenseDTO;
import com.project.ExpenseTracker.payload.expense.ExpenseSummaryResponse;
import com.project.ExpenseTracker.payload.expense.ExpenseUpdateDTO;
import com.project.ExpenseTracker.payload.expense.ResponseExpenseDTO;
import com.project.ExpenseTracker.repository.ExpenseRepo;
import com.project.ExpenseTracker.repository.UserRepo;
import com.project.ExpenseTracker.service.abstractclass.ExpenseService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private Validator validator;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ExpenseRepo expenseRepo;
    @Autowired
    private EntityManager entityManager;


    private Users getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not loggedIn with ID:".concat(userDetails.getUsername())));
    }

    @Override
    @Transactional
    public ResponseExpenseDTO addExpenseOfUser(RequestExpenseDTO requestExpenseDTO) {
        Users currentUser = getCurrentUser();
        Expense expense = modelMapper.map(requestExpenseDTO, Expense.class);
        expense.setUser(currentUser);
        Expense saved = expenseRepo.save(expense);
        return modelMapper.map(saved, ResponseExpenseDTO.class);
    }

    @Override
    @Transactional
    public List<ResponseExpenseDTO> addAllExpenses(List<RequestExpenseDTO> requestExpenseDTOList) {
        Users currentUser = getCurrentUser();
        List<Expense> expenseList = requestExpenseDTOList.stream()
                .map(expenseDTO -> modelMapper.map(expenseDTO, Expense.class))
                .toList();
        return expenseList.stream()
                .map(expense -> {
                    expense.setUser(currentUser);
                    Expense saved = expenseRepo.save(expense);
                    return modelMapper.map(saved, ResponseExpenseDTO.class);
                }).toList();
    }

    @SuppressWarnings(value = {"unchecked"})
    private void mapToObject(Expense expense, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            try {
                Field field = expense.getClass().getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType().isEnum()) {
                    // Convert the String to the correct Enum constant
                    Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), value.toString().toUpperCase());
                    field.set(expense, enumValue);
                } else {
                    // For all other fields, set the value directly
                    field.set(expense, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @SuppressWarnings(value = {"unchecked"})
    public List<String> validation(ExpenseUpdateDTO expenseDTO, Map<String, Object> updates) {
        List<String> errors = new ArrayList<>();
        String key = "";
        Object value;
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            try {
            key = entry.getKey();
            value = entry.getValue();
            Field field = expenseDTO.getClass().getDeclaredField(key);
            field.setAccessible(true);
                if (field.getType().isEnum()) {
                    // Convert the String to the correct Enum constant
                    Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), value.toString().toUpperCase());
                    field.set(expenseDTO, enumValue);
                } else {
                    // For all other fields, set the value directly
                    field.set(expenseDTO, value);
                }
            }catch (NoSuchFieldException e){
                errors.add("Key with filed name ".concat(key).concat(" doesn't exists"));
            }catch (IllegalAccessException e) {
                throw new RuntimeException("something went wrong. Try reloading page again.");
            }
        }
        if(!errors.isEmpty()) return errors;
        validator.validate(expenseDTO).stream()
                .map(expenseUpdateDTOConstraintViolation -> expenseUpdateDTOConstraintViolation.getMessage())
                .forEach(e -> errors.add(e));
        return errors;
    }

    @Override
    @Transactional
    public ResponseExpenseDTO updateExpenseOfUser(Long eid, Map<String, Object> updates) {
        Users currentUser = getCurrentUser();
        Expense expense = expenseRepo.findById(eid)
                .orElseThrow(() -> new ExpenseNotFound("No expense found with ID: " + eid));
        if (!expense.getUser().getUid().equals(currentUser.getUid())) {
            throw new SecurityException("user is not authorized to make changes in expense");
        }
        mapToObject(expense, updates);
        Expense saved = expenseRepo.save(expense);
        return modelMapper.map(saved, ResponseExpenseDTO.class);
    }

    @Override
    public List<String> validateFilter(Expense expense, ExpenseFilterRequest expenseFilterRequest) {
        List<String> errors = new ArrayList<>(
                expenseFilterRequest.getFilters().keySet().stream()
                .map(key -> {
                    try {
                        Field field = expense.getClass().getDeclaredField(key);
                    } catch (NoSuchFieldException e) {
                        return "No field exists with key: ".concat(key);
                    }
                    return null;
                }).filter(Objects::nonNull)
                .toList()
        );
        try {
            Field field = expense.getClass().getDeclaredField(expenseFilterRequest.getSortField());
        } catch (NoSuchFieldException e) {
            errors.add("Invalid sort field");
        }
        return errors;
    }

    @Override
    public List<ResponseExpenseDTO> getFilterExpenses(ExpenseFilterRequest expenseFilterRequest) {
        Users currentUser = getCurrentUser();
        ExpenseFilterSpecification<Expense> expenseFilterSpecification = new ExpenseFilterSpecification<>(currentUser.getUid(), expenseFilterRequest.getFilters());
        Sort sort = Sort.unsorted();
        if(expenseFilterRequest.getSortField()!=null&&!expenseFilterRequest.getSortField().isEmpty()){
            Sort.Direction direction = "desc".equalsIgnoreCase(expenseFilterRequest.getSortDirection())
                    ? Sort.Direction.DESC: Sort.Direction.ASC;
            sort = Sort.by(direction, expenseFilterRequest.getSortField());
        }
        return expenseRepo.findAll(expenseFilterSpecification, sort).stream()
                .map(expense -> modelMapper.map(expense, ResponseExpenseDTO.class))
                .toList();
    }

    @Override
    public List<ExpenseSummaryResponse> getExpenseSummary(ExpenseCategory expenseCategory, String period) {
        Users currentUser = getCurrentUser();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ExpenseSummaryResponse> cq = cb.createQuery(ExpenseSummaryResponse.class);
        Root<Expense> expenseRoot = cq.from(Expense.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(expenseRoot.get("user").get("uid"), currentUser.getUid()));
        if(period!=null&&!period.isEmpty()){
            YearMonth yearMonth = YearMonth.parse(period);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            predicates.add(cb.between(expenseRoot.get("transactionDate"), startDate, endDate));
        }
        if (expenseCategory!=null) {
            predicates.add(cb.equal(expenseRoot.get("expenseCategory"), expenseCategory));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        cq.groupBy(expenseRoot.get("expenseCategory"));

        cq.select(cb.construct(
                ExpenseSummaryResponse.class,
                expenseRoot.get("expenseCategory"),
                cb.sum(expenseRoot.get("amount"))
        ));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public DashBoardDTO gerUserSummary(String period) {
        Users currentUser = getCurrentUser();
        YearMonth yearMonth;
        if (period == null || period.isEmpty()) {
            yearMonth = YearMonth.now();
        }else {
           yearMonth = YearMonth.parse(period);
        }
        DashBoardDTO dashBoardDTO = new DashBoardDTO();
        dashBoardDTO.setSpendingByCategoryThisMonth(getListOfCategorySpendingDTO(currentUser, yearMonth));
        dashBoardDTO.setTotalSpendingThisMonth(getTotalSpendingThisMonth(currentUser,yearMonth));
        dashBoardDTO.setTotalSpendingLastMonth(getTotalSpendingThisMonth(currentUser, yearMonth.minusMonths(1)));
        return dashBoardDTO;
    }

    @Override
    public List<MonthlyTrendDTO> getMonthlyTrendForYear(Integer months) {
        Users currentUser = getCurrentUser();
        List<MonthlyTrendDTO> monthlyTrendList = new ArrayList<>();
        YearMonth currentYearMonth = YearMonth.now();
        for (int month = 1; month <= months; month++) {
            Double monthlyExpenses = getTotalSpendingThisMonth(currentUser,currentYearMonth.minusMonths(month));
            String monthName = currentYearMonth.minusMonths(month).getMonth().name();
            monthlyTrendList.add(new MonthlyTrendDTO(monthName, monthlyExpenses));
        }
        return monthlyTrendList;
    }

    @Override
    @Transactional(readOnly = true)
    public void exportExpenseToCSV(PrintWriter writer) {
        Users currentUser = getCurrentUser();
        List<ResponseExpenseDTO> expenses = expenseRepo.findAllByUserUid(currentUser.getUid()).stream()
                .map(expense -> modelMapper.map(expense, ResponseExpenseDTO.class))
                .toList();
        try{
            StatefulBeanToCsv<ResponseExpenseDTO> beanToCsv = new StatefulBeanToCsvBuilder<ResponseExpenseDTO>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            beanToCsv.write(expenses);
        }catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            throw new RuntimeException("Error while writing CSV file " + e.getMessage());
        }
    }

    private Double getTotalSpendingThisMonth(Users currentUser, YearMonth yearMonth) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Double> cq = cb.createQuery(Double.class);
        Root<Expense> expenseRoot = cq.from(Expense.class);
        LocalDate startDate = yearMonth.atDay(1), endDate = yearMonth.atEndOfMonth();
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(expenseRoot.get("user").get("uid"), currentUser.getUid()));
        predicates.add(cb.between(expenseRoot.get("transactionDate"), startDate, endDate));

        cq.where(predicates.toArray(new Predicate[0]));
        cq.select(cb.sum(expenseRoot.get("amount")));
        Double totalSpending = entityManager.createQuery(cq).getSingleResult();
        return totalSpending == null ? 0.0 : totalSpending;
    }

    private List<CategorySpendingDTO> getListOfCategorySpendingDTO(Users currentUser, YearMonth yearMonth) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CategorySpendingDTO> cq = cb.createQuery(CategorySpendingDTO.class);
        Root<Expense> expenseRoot = cq.from(Expense.class);
        LocalDate startDate = yearMonth.atDay(1), endDate = yearMonth.atEndOfMonth();
        cq.where(
                cb.equal(expenseRoot.get("user").get("uid"), currentUser.getUid()),
                cb.between(expenseRoot.get("transactionDate"), startDate, endDate)
        );
        cq.groupBy(expenseRoot.get("expenseCategory"));
        cq.select(cb.construct(
                CategorySpendingDTO.class,
                cb.sum(expenseRoot.get("amount")),
                expenseRoot.get("expenseCategory")
        ));
        return entityManager.createQuery(cq).getResultList();
    }
}
