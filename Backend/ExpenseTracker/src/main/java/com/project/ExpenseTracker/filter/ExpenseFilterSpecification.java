package com.project.ExpenseTracker.filter;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ExpenseFilterSpecification<T> implements Specification<T> {

    private final Map<String, Object> filters;
    private final Long userId;

    public ExpenseFilterSpecification(Long userId, Map<String, Object> filters) {
        this.filters = filters;
        this.userId = userId;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> orPredicate = new ArrayList<>(), andPredicate = new ArrayList<>();

        if (userId != null) {
        andPredicate.add((criteriaBuilder.equal(root.get("user").get("uid"), userId)));
    }

    filters.forEach((key,value)->{
        if (value == null || value.toString().isEmpty()) {
            return;
        }
        try {
            Field field = root.getJavaType().getDeclaredField(key);
            Class<?> fieldType = field.getType();

            if(fieldType.equals(String.class)){
                orPredicate.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(key)), "%" + value + "%"));
            } else if (fieldType.isEnum()) {
                ExpenseCategory enumValue = ExpenseCategory.valueOf((Class<ExpenseCategory>) fieldType, value.toString());
                andPredicate.add(criteriaBuilder.equal(root.get(key), enumValue));
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    });
        Predicate orClause = orPredicate.isEmpty() ? null : criteriaBuilder.or(orPredicate.toArray(new Predicate[0]));
        Predicate andClause = criteriaBuilder.and(andPredicate.toArray(new Predicate[0]));
        return orClause != null ? criteriaBuilder.and(andClause, orClause) : andClause;
    }
}
