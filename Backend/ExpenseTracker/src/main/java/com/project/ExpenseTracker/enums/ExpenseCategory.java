package com.project.ExpenseTracker.enums;

public enum ExpenseCategory {
    // --- Core Living Expenses ---
    FOOD,
    TRANSPORT,
    BILLS_UTILITIES,
    HEALTHCARE,
    HOME_MAINTENANCE,

    // --- Discretionary & Lifestyle ---
    SHOPPING,
    ENTERTAINMENT,
    PERSONAL_CARE,
    TRAVEL, // New: For vacations and trips
    PETS, // New: For pet-related costs

    // --- Financial & Obligations ---
    EDUCATION,
    INVESTMENTS,
    GIFTS_DONATIONS,
    INSURANCE, // New: For health, auto, life insurance
    DEBT_PAYMENT, // New: For credit cards, loans
    TAXES, // New: For income, property taxes

    // --- Miscellaneous ---
    OTHER // Corrected from OTHERS for consistency
}