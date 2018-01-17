package com.engagetech.expenses.controller;

import com.engagetech.expenses.domain.model.Expense;
import com.engagetech.expenses.domain.virtual.ExpenseDto;
import com.engagetech.expenses.service.ExpenseService;
import com.engagetech.security.domain.model.User;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/app/expenses", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<Expense> getExpenses() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return expenseService.findByUserId(user.getId());
    }

    @PostMapping
    public Expense saveExpense(@RequestBody ExpenseDto expenseDto) {
        return expenseService.save(expenseDto);
    }
}
