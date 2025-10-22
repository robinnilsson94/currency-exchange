package com.rn.currencyexchange.controller;

import com.rn.currencyexchange.dto.ConversionRequest;
import com.rn.currencyexchange.model.ExchangeRate;
import com.rn.currencyexchange.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/exchange-rate")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @PostMapping("/latestExchangeRates")
    public List<ExchangeRate> updateAndFetchLatestExchangeRates() {
        return exchangeRateService.updateAndFetchLatestExchangeRates();
    }

    @PostMapping("/convert")
    public BigDecimal getConversionRate(@RequestBody ConversionRequest request) {
        return exchangeRateService.convertAmount(request);
    }
}
