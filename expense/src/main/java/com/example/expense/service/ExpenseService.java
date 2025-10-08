package com.example.expense.service;

import com.example.expense.model.Expense;
import com.example.expense.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
@Service
public class ExpenseService {
    private final ExpenseRepository repo;

    public ExpenseService(ExpenseRepository repo) { this.repo = repo; }

    public Expense addExpense(Expense expense) { 
        return repo.save(expense); 
    }

    public Optional<Expense> getExpenseById(Integer id) { 
        return repo.findById(id); 
    }

    public List<Expense> getExpensesByUserId(Integer userId) { 
        return repo.findByUserId(userId); 
    }

    public List<Expense> getAllExpenses() { 
        return repo.findAll(); 
    }

    // Check existence before deleting
    public void deleteExpense(Integer id) { 
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Expense with id " + id + " does not exist");
        }
        repo.deleteById(id); 
    }

    // Helper to check existence from controller
    public boolean existsById(Integer id) {
        return repo.existsById(id);
    }
}

