package com.example.expense.controller;

import com.example.expense.model.Expense;
import com.example.expense.service.ExpenseService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService service;

    public ExpenseController(ExpenseService service) { this.service = service; }

    @PostMapping
    public Expense addExpense(@RequestBody Expense expense) {
        return service.addExpense(expense);
    }

    @GetMapping("/{id}")
    public Optional<Expense> getExpense(@PathVariable Integer id) {
        return service.getExpenseById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Expense> getUserExpenses(@PathVariable Integer userId) {
        return service.getExpensesByUserId(userId);
    }

    @GetMapping
    public List<Expense> getAllExpenses() {
        return service.getAllExpenses();
    }

 @DeleteMapping("/{id}")
public ResponseEntity<String> deleteExpense(@PathVariable Integer id) {
    try {
        service.deleteExpense(id);
        return ResponseEntity.status(204).body("Expense deleted successfully!");
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
}


}
