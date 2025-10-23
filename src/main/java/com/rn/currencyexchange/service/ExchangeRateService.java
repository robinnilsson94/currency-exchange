package com.rn.currencyexchange.service;

import com.rn.currencyexchange.dto.ConversionRequest;
import com.rn.currencyexchange.model.CrossRate;
import com.rn.currencyexchange.model.Currency;
import com.rn.currencyexchange.model.ExchangeRate;
import com.rn.currencyexchange.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExchangeRateService {

    @Autowired
    private RiksbankApiService riksbankApiService;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    /**
     * Converts an amount from one currency to another using the latest exchange rate.
     *
     * @param request conversion request containing fromCurrency, toCurrency, and amount
     * @return converted amount
     * @throws IllegalArgumentException if no exchange rate is found
     */
    public BigDecimal convertAmount(ConversionRequest request) {
        // No conversion needed if source and target currency are the same
        if (request.getFromCurrency() == request.getToCurrency()) {
            return request.getAmount();
        }

        ExchangeRate rate = exchangeRateRepository.findFirstByFromCurrencyAndToCurrencyOrderByRateDateDesc(request.getFromCurrency(), request.getToCurrency())
                .orElseThrow(() -> new IllegalArgumentException("No exchange rate found, please update to latest exchange rates"));

        return rate.getConversionRate().multiply(request.getAmount());
    }

    /**
     * Updates and fetches all latest exchange rates for all currency combinations.
     * Creates inverse rates for each fetched rate.
     *
     * @return list of exchange rates including inverse rates
     */
    public List<ExchangeRate> updateAndFetchLatestExchangeRates() {
        List<ExchangeRate> allRates = new ArrayList<>();

        LocalDate latestBankDay = riksbankApiService.getLatestBankDay();

        for (Currency fromCurrency : Currency.values()) {
            for (Currency toCurrency : Currency.values()) {
                // Make sure we only use descending order since we can manually calculate the inverse rate
                if (fromCurrency.ordinal() < toCurrency.ordinal()) {
                    List<ExchangeRate> rateAndInverse = fetchOrCreateExchangeRateWithInverse(fromCurrency, toCurrency, latestBankDay);
                    allRates.addAll(rateAndInverse);
                }
            }
        }

        return allRates;
    }

    /**
     * Fetches an exchange rate for a specific currency pair and bank date.
     * If the rate does not exist in the repository, it fetches from Riksbankens API and saves both rate and its inverse.
     *
     * @param fromCurrency source currency
     * @param toCurrency target currency
     * @param latestBankDate date for which the rate is fetched
     * @return list containing the rate and its inverse
     */
    public List<ExchangeRate> fetchOrCreateExchangeRateWithInverse(Currency fromCurrency, Currency toCurrency, LocalDate latestBankDate) {
        ExchangeRate exchangeRate = exchangeRateRepository.findByFromCurrencyAndToCurrencyAndRateDate(fromCurrency, toCurrency, latestBankDate)
                .orElseGet(() -> createAndSaveExchangeRateAndInverseBasedOnRiksbankenApi(fromCurrency, toCurrency));

        return List.of(exchangeRate, createInverseExchangeRate(exchangeRate));
    }

    private ExchangeRate createInverseExchangeRate(ExchangeRate exchangeRate) {
        BigDecimal inverseRate = BigDecimal.ONE.divide(exchangeRate.getConversionRate(), MathContext.DECIMAL32);

        return new ExchangeRate(exchangeRate.getToCurrency(), exchangeRate.getFromCurrency(), inverseRate, exchangeRate.getRateDate());
    }

    private ExchangeRate createAndSaveExchangeRateAndInverseBasedOnRiksbankenApi(Currency fromCurrency, Currency toCurrency) {
        List<CrossRate> crossRates = riksbankApiService.getLatestCrossRates(fromCurrency, toCurrency);

        ExchangeRate rateFromApi = crossRates.stream()
                .findFirst()
                .map(crossRate -> new ExchangeRate(
                        fromCurrency,
                        toCurrency,
                        crossRate.getValue(),
                        LocalDate.parse(crossRate.getDate())
                ))
                .orElseThrow();

        ExchangeRate inverseRate = createInverseExchangeRate(rateFromApi);

        exchangeRateRepository.saveAll(List.of(rateFromApi, inverseRate));

        return rateFromApi;
    }
}
