package com.engagetech.expenses.domain.virtual;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmountCurrency {

    private BigDecimal amount;
    private String currency;
}
