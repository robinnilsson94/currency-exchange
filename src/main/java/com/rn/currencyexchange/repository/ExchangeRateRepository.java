package com.rn.currencyexchange.repository;

import com.rn.currencyexchange.model.Currency;
import com.rn.currencyexchange.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findByFromCurrencyAndToCurrencyAndRateDate(Currency from, Currency to, LocalDate rateDate);

    Optional<ExchangeRate> findFirstByFromCurrencyAndToCurrencyOrderByRateDateDesc(Currency fromCurrency, Currency toCurrency);
}

