package com.rn.currencyexchange.model;

public class CalendarDay {

    private String calendarDate;
    private boolean swedishBankday;

    public String getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(String calendarDate) {
        this.calendarDate = calendarDate;
    }

    public boolean isSwedishBankday() {
        return swedishBankday;
    }

    public void setSwedishBankday(boolean swedishBankday) {
        this.swedishBankday = swedishBankday;
    }
}
