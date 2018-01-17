package com.engagetech.expenses.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.engagetech.expenses.domain.virtual.AmountCurrency;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class AmountCurrencyParserTest {

    private static final AmountCurrencyParser PARSER = new AmountCurrencyParser();

    @Test
    @Parameters(method = "params")
    public void testParse(String input, String expectedCurrency, String expectedAmount) throws Exception {
        AmountCurrency result = PARSER.parse(input);
        assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
        assertThat(result.getAmount()).isEqualByComparingTo(expectedAmount);
    }

    private Object params() {
        return new Object[] {
                new Object[] { "100", null, "100" },
                new Object[] { "100 EUR", "EUR", "100" },
                new Object[] { "EUR 100", "EUR", "100" },
                new Object[] { "100EUR", "EUR", "100" },
                new Object[] { "EUR100EUR", "EUR", "100" }
        };
    }
}
