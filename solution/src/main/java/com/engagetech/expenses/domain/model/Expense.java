package com.engagetech.expenses.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
public class Expense implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id")
    @NotNull
    private Long ownerId;

    @Column
    @NotNull
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = Shape.STRING, pattern = "dd/MM/yyyy")
    private Date date;

    @NotNull
    @Min(0)
    @Max(9999999999999999L)
    private BigDecimal amount;

    @NotNull
    @Min(0)
    @Max(9999999999999999L)
    private BigDecimal vat;

    @NotNull
    @Size(min = 3, max = 3)
    private String currency;

    @Transient
    private String currencySymbol;

    @NotNull
    @Column(name = "gbp_equivalent")
    @Min(0)
    @Max(9999999999999999L)
    private BigDecimal gbpEquivalent;

    @NotNull
    private String reason;

    @PostLoad
    protected void initCurrencySymbol() {
        currencySymbol = Currency.getInstance(currency).getSymbol(Locale.UK);
    }

}
