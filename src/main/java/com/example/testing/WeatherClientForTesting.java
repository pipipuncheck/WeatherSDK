package com.example.testing;

import com.example.WeatherClient;
import com.example.WeatherClientException;
import com.example.WeatherData;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class WeatherClientForTesting {
    private final String baseUrl;
    private final String apiKey;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WeatherClientForTesting(String apiKey, String baseUrl) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public WeatherData fetchWeather(String city) throws WeatherClientException {
        Request request = new Request.Builder().url(baseUrl).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new WeatherClientException("Error fetching weather: " + response.message());
            }
            return objectMapper.readValue(response.body().string(), WeatherData.class);
        } catch (IOException e) {
            throw new WeatherClientException("Network error while fetching weather data", e);
        }
    }


}
