package com.engagetech.expenses.service;

import com.engagetech.expenses.domain.virtual.AmountCurrency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a possible combination of amount and currency from the client.
 */
@Component
public class AmountCurrencyParser {

    private static final Pattern CURERNCY_AMOUNT_CURRENCY_PATTERN =
            Pattern.compile("(?<CODE1>[A-Za-z]+)?\\s*(?<AMOUNT>[.\\d,]+)\\s*(?<CODE2>[A-Za-z]+)?");

    public AmountCurrency parse(String amountCurrency) {
        Matcher matcher = CURERNCY_AMOUNT_CURRENCY_PATTERN.matcher(amountCurrency);
        String curr1 = null;
        String amount = null;
        String curr2 = null;
        while (matcher.find()) {
            curr1 = matcher.group("CODE1");
            amount = matcher.group("AMOUNT");
            curr2 = matcher.group("CODE2");
        }
        return new AmountCurrency(amount != null ? new BigDecimal(amount) : null, getCurrency(curr1, curr2));
    }

    private String getCurrency(String curr1, String curr2) {
        if (curr1 != null) {
            return curr1.toUpperCase();
        } else if (curr2 != null) {
            return curr2.toUpperCase();
        } else {
            return null;
        }
    }
}
