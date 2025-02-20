package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class WeatherClient {

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    private final String apiKey;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WeatherClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public WeatherData fetchWeather(String city) throws WeatherClientException{
        String url = String.format("%s?q=%s&appid=%s", BASE_URL, city, apiKey);
        Request request = new Request.Builder().url(url).build();

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
