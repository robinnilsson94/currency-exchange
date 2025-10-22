package com.rn.currencyexchange.dto;

import com.rn.currencyexchange.model.Currency;

import java.math.BigDecimal;

public class ConversionRequest {

    private Currency fromCurrency;
    private Currency toCurrency;
    private BigDecimal amount;

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

