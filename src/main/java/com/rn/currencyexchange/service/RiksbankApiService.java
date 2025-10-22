package com.rn.currencyexchange.service;

import com.rn.currencyexchange.exception.RiksbankApiException;
import com.rn.currencyexchange.model.CalendarDay;
import com.rn.currencyexchange.model.CrossRate;
import com.rn.currencyexchange.model.Currency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Service
public class RiksbankApiService {

    private static final String BASE_URL = "https://api.riksbank.se/swea/v1/";
    private static final String SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";
    private static final LocalTime PUBLISH_TIME = LocalTime.of(16, 15);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${riksbank.api.key}")
    private String apiKey;

    public LocalDate getLatestBankDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime cutoff = LocalDateTime.of(today, PUBLISH_TIME);

        return now.isBefore(cutoff) ? today.minusDays(1) : today;
    }

    public List<CrossRate> getLatestCrossRates(Currency fromCurrency, Currency toCurrency) {
        HttpHeaders headers = createHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        CalendarDay bankDay = getLatestCalendarBankDate(entity);

        String url = BASE_URL + "CrossRates/" +
                fromCurrency.getSeriesId() + "/" +
                toCurrency.getSeriesId() + "/" +
                bankDay.getCalendarDate();

        ResponseEntity<CrossRate[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, CrossRate[].class);

        if (response.getBody() == null || response.getBody().length == 0) {
            throw new RiksbankApiException("No cross rates returned from Riksbank for " + fromCurrency + " to " + toCurrency);
        }

        return List.of(response.getBody());
    }

    private CalendarDay getLatestCalendarBankDate(HttpEntity<Void> entity) {
        String url = BASE_URL + "CalendarDays/" + getLatestBankDay();

        ResponseEntity<CalendarDay[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, CalendarDay[].class);

        if (response.getBody() == null || response.getBody().length == 0) {
            throw new RiksbankApiException("No bank days found in Riksbank API response");
        }

        return Arrays.stream(response.getBody())
                .filter(CalendarDay::isSwedishBankday)
                .findFirst()
                .orElseThrow(() -> new RiksbankApiException("No valid Swedish bank day found"));
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SUBSCRIPTION_KEY, apiKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
