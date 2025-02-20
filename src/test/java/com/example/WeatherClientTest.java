/*
Для тестов используются специальные классы, которых нет на GitHub,
в них немного видоизменена логика, для более удобного тестирования
*/
package com.example;

import com.example.testing.WeatherClientForTesting;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherClientTest {

    private MockWebServer mockWebServer;
    private WeatherClientForTesting weatherClientForTesting;

    @BeforeEach
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        weatherClientForTesting = new WeatherClientForTesting("test-api-key", mockWebServer.url("/").toString());
    }

    @AfterEach
    public void tearDown() throws Exception{
        mockWebServer.shutdown();
    }

    @Test
    public void testFetchWeather_Success() throws Exception {
        String weatherJson = "{\n" +
                "  \"weather\": [{\"main\": \"Clear\", \"description\": \"clear sky\"}],\n" +
                "  \"main\": {\"temp\": 20.5, \"feels_like\": 19.0},\n" +
                "  \"visibility\": 10000,\n" +
                "  \"wind\": {\"speed\": 5.1},\n" +
                "  \"dt\": 1618317045,\n" +
                "  \"sys\": {\"sunrise\": 1618301045, \"sunset\": 1618354200},\n" +
                "  \"timezone\": 10800,\n" +
                "  \"name\": \"London\"\n" +
                "}";

        mockWebServer.enqueue(new MockResponse().setBody(weatherJson).setResponseCode(200));

        WeatherData weatherData = weatherClientForTesting.fetchWeather("London");

        assertNotNull(weatherData);
        assertEquals(20.5, weatherData.main.temp);
        assertEquals("clear sky", weatherData.weather.get(0).description);
    }

    @Test
    public void testFetchWeather_Failure() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertThrows(WeatherClientException.class, () -> weatherClientForTesting.fetchWeather("London"));
    }

    @Test
    public void testFetchWeather_NetworkError() throws Exception {
        mockWebServer.shutdown();

        assertThrows(WeatherClientException.class, () -> weatherClientForTesting.fetchWeather("London"));
    }
}