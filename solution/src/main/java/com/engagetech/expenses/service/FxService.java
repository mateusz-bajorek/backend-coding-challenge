package com.engagetech.expenses.service;

import com.engagetech.expenses.FxException;
import org.joda.money.CurrencyUnit;
import org.springframework.stereotype.Service;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxQuote;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * FX service used to convert given amount of given currency into different currency.
 */
@Service
public class FxService {

    public BigDecimal convert(BigDecimal amount, String currencyFrom, String currencyTo) {
        try {
            FxQuote quote = YahooFinance.getFx(currencyFrom.toUpperCase() + currencyTo.toUpperCase() + "=X");
            return amount.multiply(quote.getPrice()).setScale(CurrencyUnit.GBP.getDecimalPlaces(), RoundingMode.HALF_UP);
        } catch (IOException e) {
            throw new FxException("Problem with communicating with exchange service, please try again later.", e);
        }
    }

}
