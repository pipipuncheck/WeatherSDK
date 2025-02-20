package com.example;

public class CachedWeather {

    private final WeatherData data;
    private final long timestamp;

    public CachedWeather(WeatherData data) {
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() - timestamp) > 10 * 60 * 1000;
    }

    public WeatherData getData() {
        return data;
    }

}
