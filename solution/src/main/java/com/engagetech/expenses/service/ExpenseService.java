package com.engagetech.expenses.service;

import com.engagetech.expenses.domain.model.Expense;
import com.engagetech.expenses.domain.repository.ExpenseRepository;
import com.engagetech.expenses.domain.virtual.AmountCurrency;
import com.engagetech.expenses.domain.virtual.ExpenseDto;
import com.engagetech.security.domain.model.User;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Service for interacting with {@link Expense}.
 */
@Service
@Transactional
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private FxService fxService;

    @Autowired
    private AmountCurrencyParser amountCurrencyParser;

    /**
     * Find expenses for given {@code userId}.
     * @param userId the owner of the expenses
     * @return list of expenses submitted but the given user
     */
    public List<Expense> findByUserId(long userId) {
        return expenseRepository.findAllByOwnerIdOrderByDateDesc(userId);
    }

    /**
     * Save new expense. Validate submitted date, parse currency and amount, validate currency, set GBP equivalent.
     * @param expenseDto data received from the client
     * @return saved expense
     */
    public Expense save(ExpenseDto expenseDto) {
        if (expenseDto.getDate().after(new Date())) {
            throw new IllegalArgumentException("Cannot save an expense from the future.");
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Expense expense = new Expense();
        expense.setDate(expenseDto.getDate());
        expense.setReason(expenseDto.getReason());
        expense.setOwnerId(user.getId());
        expense.setVat(expenseDto.getVat());
        AmountCurrency amountCurrency = amountCurrencyParser.parse(expenseDto.getAmount());
        expense.setCurrency(amountCurrency.getCurrency());
        validateCurrency(expense);
        expense.setAmount(amountCurrency.getAmount()
                .setScale(CurrencyUnit.of(expense.getCurrency()).getDecimalPlaces(), RoundingMode.HALF_UP)
        );
        setGbpEquivalent(expense);
        return expenseRepository.save(expense);
    }

    private void validateCurrency(Expense expense) {
        if (expense.getCurrency() == null) {
            expense.setCurrency(CurrencyUnit.GBP.getCode());
        } else if(CurrencyUnit.registeredCurrencies().stream()
                    .map(CurrencyUnit::getCode)
                    .noneMatch(currency -> Objects.equals(expense.getCurrency(), currency))) {
            throw new IllegalArgumentException("Invalid currency provided.");
        }
    }

    private void setGbpEquivalent(Expense expense) {
        if (!Objects.equals(expense.getCurrency(), CurrencyUnit.GBP.getCode())) {
            BigDecimal amount = fxService.convert(expense.getAmount(), expense.getCurrency(), CurrencyUnit.GBP.getCode());
            expense.setGbpEquivalent(amount);
        } else {
            expense.setGbpEquivalent(expense.getAmount());
        }
    }

}
