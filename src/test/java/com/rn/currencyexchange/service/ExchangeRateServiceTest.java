package com.rn.currencyexchange.service;

import com.rn.currencyexchange.dto.ConversionRequest;
import com.rn.currencyexchange.model.CrossRate;
import com.rn.currencyexchange.model.Currency;
import com.rn.currencyexchange.model.ExchangeRate;
import com.rn.currencyexchange.repository.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    public static final BigDecimal SEK_TO_USD = BigDecimal.valueOf(0.11);
    private static final BigDecimal SEK_TO_EUR = BigDecimal.valueOf(0.09);
    private static final BigDecimal EUR_TO_USD = BigDecimal.valueOf(1.16);
    public static final BigDecimal AMOUNT = BigDecimal.valueOf(10);
    public static final int NO_OF_RATE_COMBOS = 6;
    private static final LocalDate TODAY = LocalDate.now();

    @Mock
    private RiksbankApiService riksbankApiService;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Test
    void convert_amount_returns_converted_value() {
        ConversionRequest request = new ConversionRequest();
        request.setFromCurrency(Currency.SEK);
        request.setToCurrency(Currency.USD);
        request.setAmount(AMOUNT);

        ExchangeRate rate = new ExchangeRate(Currency.SEK, Currency.USD, SEK_TO_USD, TODAY);

        when(exchangeRateRepository.findFirstByFromCurrencyAndToCurrencyOrderByRateDateDesc(Currency.SEK, Currency.USD))
                .thenReturn(Optional.of(rate));

        BigDecimal result = exchangeRateService.convertAmount(request);

        assertEquals(AMOUNT.multiply(SEK_TO_USD), result);
    }

    @Test
    void convert_amount_same_currency_returns_original_amount() {
        ConversionRequest request = new ConversionRequest();
        request.setFromCurrency(Currency.EUR);
        request.setToCurrency(Currency.EUR);
        request.setAmount(AMOUNT);

        BigDecimal result = exchangeRateService.convertAmount(request);

        assertEquals(AMOUNT, result);
    }

    @Test
    void update_and_fetch_latest_exchange_rates_creates_all_rates_including_inverse() {
        stubRiksbankCrossRatesAndReturnNothingWhenFetchingFromRepository();

        List<ExchangeRate> rates = exchangeRateService.updateAndFetchLatestExchangeRates();

        assertEquals(NO_OF_RATE_COMBOS, rates.size());

        verify(exchangeRateRepository, times(NO_OF_RATE_COMBOS / 2)).saveAll(anyList());
    }

    @Test
    void update_and_fetch_latest_exchange_rates_returns_all_rate_combinations() {
        stubExistingRatesInRepository();

        List<ExchangeRate> rates = exchangeRateService.updateAndFetchLatestExchangeRates();

        assertEquals(NO_OF_RATE_COMBOS, rates.size());
    }

    @Test
    void update_and_fetch_latest_exchange_rates_does_not_call_api_or_save_when_rates_exist() {
        stubExistingRatesInRepository();

        exchangeRateService.updateAndFetchLatestExchangeRates();

        verify(exchangeRateRepository, never()).saveAll(anyList());
        verify(riksbankApiService, never()).getLatestCrossRates(any(), any());
    }

    private void stubRiksbankCrossRatesAndReturnNothingWhenFetchingFromRepository() {
        when(riksbankApiService.getLatestBankDay()).thenReturn(TODAY);

        when(exchangeRateRepository.findByFromCurrencyAndToCurrencyAndRateDate(any(), any(), any()))
                .thenReturn(Optional.empty());

        when(riksbankApiService.getLatestCrossRates(Currency.SEK, Currency.USD))
                .thenReturn(List.of(createCrossRate(SEK_TO_USD)));

        when(riksbankApiService.getLatestCrossRates(Currency.SEK, Currency.EUR))
                .thenReturn(List.of(createCrossRate(SEK_TO_EUR)));

        when(riksbankApiService.getLatestCrossRates(Currency.EUR, Currency.USD))
                .thenReturn(List.of(createCrossRate(EUR_TO_USD)));
    }

    private void stubExistingRatesInRepository() {
        when(riksbankApiService.getLatestBankDay()).thenReturn(TODAY);

        when(exchangeRateRepository.findByFromCurrencyAndToCurrencyAndRateDate(Currency.SEK, Currency.EUR, TODAY))
                .thenReturn(Optional.of(createExchangeRate(Currency.SEK, Currency.EUR)));

        when(exchangeRateRepository.findByFromCurrencyAndToCurrencyAndRateDate(Currency.SEK, Currency.USD, TODAY))
                .thenReturn(Optional.of(createExchangeRate(Currency.SEK, Currency.USD)));

        when(exchangeRateRepository.findByFromCurrencyAndToCurrencyAndRateDate(Currency.EUR, Currency.USD, TODAY))
                .thenReturn(Optional.of(createExchangeRate(Currency.EUR, Currency.USD)));
    }

    private CrossRate createCrossRate(BigDecimal value) {
        CrossRate rate = new CrossRate();
        rate.setValue(value);
        rate.setDate(ExchangeRateServiceTest.TODAY.toString());

        return rate;
    }

    private ExchangeRate createExchangeRate(Currency fromCurrency, Currency toCurrency) {
        ExchangeRate rate = new ExchangeRate();
        rate.setFromCurrency(fromCurrency);
        rate.setToCurrency(toCurrency);
        rate.setConversionRate(BigDecimal.ONE);
        rate.setRateDate(TODAY);

        return rate;
    }
}
