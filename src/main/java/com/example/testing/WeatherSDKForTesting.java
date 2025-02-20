package com.example.testing;

import com.example.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherSDKForTesting {
    private final String apiKey;
    private final Mode mode;
    private final Map<String, CachedWeatherForTesting> cache = new LinkedHashMap<>();
    private final WeatherClientForTesting client;
    private static final Map<String, WeatherSDKForTesting> instances = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final String baseUrl;

    private WeatherSDKForTesting(String apiKey, Mode mode, String baseUrl) {
        this.apiKey = apiKey;
        this.mode = mode;
        this.client = new WeatherClientForTesting(apiKey, baseUrl);
        this.baseUrl = baseUrl;

        if (mode == Mode.POLLING) {
            startPolling();
        }
    }


    public static WeatherSDKForTesting addInstance(String apiKey, Mode mode, String baseUrl) {
        if (instances.containsKey(apiKey)) {
            throw new IllegalArgumentException("Ошибка: экземпляр с ключом '" + apiKey + "' уже создан.");
        }
        WeatherSDKForTesting sdk = new WeatherSDKForTesting(apiKey, mode, baseUrl);
        instances.put(apiKey, sdk);
        return sdk;
    }

    public static void removeInstance(String apiKey) {
        if (!instances.containsKey(apiKey))
            throw new IllegalArgumentException("Ошибка: экземпляр с ключом '" + apiKey + "' не найден.");

        instances.remove(apiKey);
    }

    public WeatherData getWeather(String city) {
        if (cache.containsKey(city) && !cache.get(city).isExpired()) {
            return cache.get(city).getData();
        }
        WeatherData data = null;
        try {
            data = client.fetchWeather(city);
        } catch (WeatherClientException e) {
            throw new RuntimeException(e);
        }
        updateCache(city, data);
        return data;
    }

    public String getWeatherAsJson(String city) {
        WeatherData data = getWeather(city);
        return serializeToJson(data);
    }

    private String serializeToJson(WeatherData data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сериализации JSON", e);
        }
    }

    private void updateCache(String city, WeatherData data) {
        if (cache.size() >= 10) {
            cache.remove(cache.keySet().iterator().next());
        }
        cache.put(city, new CachedWeatherForTesting(data));
    }
    public static void clearInstancesForTesting() {
        instances.clear();
    }

    private void startPolling() {
        scheduler.scheduleAtFixedRate(() -> {
            for (String city : cache.keySet()) {
                WeatherData data = null;
                try {
                    data = client.fetchWeather(city);
                } catch (WeatherClientException e) {
                    throw new RuntimeException(e);
                }
                updateCache(city, data);
            }
        }, 0, 10, TimeUnit.MINUTES);
    }
}
