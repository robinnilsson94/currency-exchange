package com.rn.currencyexchange.model;

public enum Currency {
    SEK("SEKETT", 1),
    EUR("SEKEURPMI", 2),
    USD("SEKUSDPMI", 3);

    private final String seriesId;
    private final int id;

    Currency(String seriesId, int id) {
        this.seriesId = seriesId;
        this.id = id;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public int getId() {
        return id;
    }
}

