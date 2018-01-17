package com.engagetech.expenses.domain.repository;

import com.engagetech.expenses.domain.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByOwnerIdOrderByDateDesc(long ownerId);
}
