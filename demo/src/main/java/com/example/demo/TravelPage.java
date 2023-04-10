package com.example.demo;

public class TravelPage {
    private Quotation quotation;

    private Weather weather;

    public TravelPage(Quotation quotation, Weather weather) {
        this.quotation = quotation;
        this.weather = weather;
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "{\"TravelPage\":{"
                + "\"quotation\":" + quotation
                + ",\"weather\":" + weather
                + "}}";
    }
}
