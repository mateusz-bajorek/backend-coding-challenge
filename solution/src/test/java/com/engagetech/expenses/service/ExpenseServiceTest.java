package com.engagetech.expenses.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.engagetech.expenses.domain.model.Expense;
import com.engagetech.expenses.domain.repository.ExpenseRepository;
import com.engagetech.expenses.domain.virtual.ExpenseDto;
import com.engagetech.security.domain.model.User;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class ExpenseServiceTest {

    private static final long USER_ID = 10L;
    private static final Date NOW = new Date();
    private static final String REASON = "reason";
    private static final String VAT = "20";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private FxService fxService;

    @Mock
    private AmountCurrencyParser amountCurrencyParser;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Captor
    private ArgumentCaptor<Expense> expenseCaptor;

    @InjectMocks
    private ExpenseService expenseService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        User user = new User();
        user.setId(USER_ID);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
        when(amountCurrencyParser.parse(anyString())).thenCallRealMethod();
    }

    @Test
    public void testSaveDateInFuture() throws Exception {
        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setDate(LocalDate.parse("2019-01-01").toDate());
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot save an expense from the future.");
        expenseService.save(expenseDto);
        verify(amountCurrencyParser, never()).parse(anyString());
        verify(fxService, never()).convert(any(BigDecimal.class), anyString(), anyString());
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    public void testSaveCorrectDate() throws Exception {
        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setDate(NOW);
        expenseDto.setAmount("100 GBP");
        expenseDto.setReason(REASON);
        expenseDto.setVat(new BigDecimal(VAT));
        expenseService.save(expenseDto);
        verify(expenseRepository).save(expenseCaptor.capture());
        Expense expense = expenseCaptor.getValue();
        assertThat(expense).isNotNull();
        assertThat(expense.getCurrency()).isEqualTo("GBP");
        assertThat(expense.getAmount()).isEqualByComparingTo("100");
        assertThat(expense.getReason()).isEqualTo(REASON);
        assertThat(expense.getVat()).isEqualByComparingTo(VAT);
        assertThat(expense.getDate()).isSameAs(NOW);
        assertThat(expense.getGbpEquivalent()).isEqualByComparingTo("100");
        assertThat(expense.getOwnerId()).isEqualTo(USER_ID);
        verify(amountCurrencyParser).parse("100 GBP");
        verify(fxService, never()).convert(any(BigDecimal.class), anyString(), anyString());
    }

    @Test
    public void testSaveWithFx() throws Exception {
        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setDate(NOW);
        expenseDto.setAmount("100 EUR");
        expenseDto.setReason(REASON);
        expenseDto.setVat(new BigDecimal(VAT));
        String gbpEquivalent = "90";
        when(fxService.convert(new BigDecimal("100.00"), "EUR", "GBP")).thenReturn(new BigDecimal(gbpEquivalent));
        expenseService.save(expenseDto);
        verify(expenseRepository).save(expenseCaptor.capture());
        Expense expense = expenseCaptor.getValue();
        assertThat(expense).isNotNull();
        assertThat(expense.getCurrency()).isEqualTo("EUR");
        assertThat(expense.getAmount()).isEqualByComparingTo("100");
        assertThat(expense.getReason()).isEqualTo(REASON);
        assertThat(expense.getVat()).isEqualByComparingTo(VAT);
        assertThat(expense.getDate()).isSameAs(NOW);
        assertThat(expense.getGbpEquivalent()).isEqualByComparingTo(gbpEquivalent);
        assertThat(expense.getOwnerId()).isEqualTo(USER_ID);
        verify(amountCurrencyParser).parse("100 EUR");
    }

    @Test
    public void testSaveWithNoCurrency() throws Exception {
        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setDate(NOW);
        expenseDto.setAmount("100");
        expenseDto.setReason(REASON);
        expenseDto.setVat(new BigDecimal(VAT));
        expenseService.save(expenseDto);
        verify(expenseRepository).save(expenseCaptor.capture());
        Expense expense = expenseCaptor.getValue();
        assertThat(expense).isNotNull();
        assertThat(expense.getCurrency()).isEqualTo("GBP");
        assertThat(expense.getAmount()).isEqualByComparingTo("100");
        assertThat(expense.getReason()).isEqualTo(REASON);
        assertThat(expense.getVat()).isEqualByComparingTo(VAT);
        assertThat(expense.getDate()).isSameAs(NOW);
        assertThat(expense.getGbpEquivalent()).isEqualByComparingTo("100");
        assertThat(expense.getOwnerId()).isEqualTo(USER_ID);
        verify(amountCurrencyParser).parse("100");
        verify(fxService, never()).convert(any(BigDecimal.class), anyString(), anyString());
    }

    @Test
    public void testSaveWithInvalidCurrency() throws Exception {
        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setDate(NOW);
        expenseDto.setAmount("100 III");
        expenseDto.setReason(REASON);
        expenseDto.setVat(new BigDecimal(VAT));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid currency provided.");
        expenseService.save(expenseDto);
        verify(amountCurrencyParser).parse("100 III");
        verify(fxService, never()).convert(any(BigDecimal.class), anyString(), anyString());
        verify(expenseRepository, never()).save(any(Expense.class));
    }
}

