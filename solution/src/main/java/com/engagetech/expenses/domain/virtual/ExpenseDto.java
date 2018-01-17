package com.engagetech.expenses.domain.virtual;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ExpenseDto {

    private Date date;
    private String amount;
    private String reason;
    private BigDecimal vat;
}
