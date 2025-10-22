package com.rn.currencyexchange.model;

import java.math.BigDecimal;

public class CrossRate {

    private String date;
    private BigDecimal value;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
