package com.example.expense.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;
    private Integer categoryId;
    private Double amount;
    private String description;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private LocalDate expenseDate;

    public enum PaymentMode { CASH, CARD, UPI, OTHER }

    public Expense() {}

    public Expense(Integer userId, Integer categoryId, Double amount, String description,
                   PaymentMode paymentMode, LocalDate expenseDate) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.paymentMode = paymentMode;
        this.expenseDate = expenseDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }
}
