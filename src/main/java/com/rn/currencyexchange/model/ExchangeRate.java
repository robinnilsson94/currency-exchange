package com.rn.currencyexchange.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal conversionRate;

    @Enumerated(EnumType.STRING)
    private Currency fromCurrency;

    @Enumerated(EnumType.STRING)
    private Currency toCurrency;

    private LocalDate rateDate;

    public ExchangeRate() {
    }

    public ExchangeRate(Currency fromCurrency, Currency toCurrency, BigDecimal conversionRate, LocalDate rateDate) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.conversionRate = conversionRate;
        this.rateDate = rateDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(BigDecimal conversionRate) {
        this.conversionRate = conversionRate;
    }

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

    public LocalDate getRateDate() {
        return rateDate;
    }

    public void setRateDate(LocalDate rateDate) {
        this.rateDate = rateDate;
    }
}

