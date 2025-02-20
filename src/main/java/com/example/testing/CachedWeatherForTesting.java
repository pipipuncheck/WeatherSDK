package com.example.testing;

import com.example.WeatherData;

public class CachedWeatherForTesting {

    private final WeatherData data;
    private final long timestamp;

    public CachedWeatherForTesting(WeatherData data) {
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() - timestamp) > 1000;
    }

    public WeatherData getData() {
        return data;
    }
}
