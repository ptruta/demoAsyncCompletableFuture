package com.example.demo;

public class Weather {
    private String temperature;

    private Integer hour;

    public Weather(String temperature, Integer hour) {
        this.temperature = temperature;
        this.hour = hour;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return "{\"Weather\":{"
                + "\"temperature\":\"" + temperature + "\""
                + ",\"hour\":\"" + hour + "\""
                + "}}";
    }
}
