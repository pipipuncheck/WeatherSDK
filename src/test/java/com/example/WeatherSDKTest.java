/*
Для тестов используются специальные классы, которых нет на GitHub,
в них немного видоизменена логика, для более удобного тестирования
*/
package com.example;

import com.example.testing.WeatherSDKForTesting;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherSDKTest {

    private static final String API_KEY = "your_api_key";
    private MockWebServer mockWebServer;
    private WeatherSDKForTesting sdk;

    @BeforeEach
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        sdk = WeatherSDKForTesting.addInstance("test-api-key", Mode.POLLING, mockWebServer.url("/").toString());
    }

    @AfterEach
    public void tearDown() throws Exception{
        WeatherSDKForTesting.clearInstancesForTesting();
        mockWebServer.shutdown();
    }

    @Test
    void addInstance_shouldCreateNewSDKInstance() {
        WeatherSDKForTesting sdk = WeatherSDKForTesting.addInstance(API_KEY, Mode.ON_DEMAND, mockWebServer.url("/").toString());
        assertNotNull(sdk);
    }

    @Test
    void addInstance_shouldThrowException_whenApiKeyAlreadyExists() {
        WeatherSDKForTesting.addInstance(API_KEY, Mode.ON_DEMAND, mockWebServer.url("/").toString());
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> WeatherSDKForTesting.addInstance(API_KEY, Mode.ON_DEMAND, mockWebServer.url("/").toString()));

        assertTrue(exception.getMessage().contains("Ошибка: экземпляр с ключом '" + API_KEY + "' уже создан."));
    }

    @Test
    void removeInstance_shouldDeleteInstance() {
        WeatherSDKForTesting.addInstance(API_KEY, Mode.ON_DEMAND, mockWebServer.url("/").toString());
        WeatherSDKForTesting.removeInstance(API_KEY);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> WeatherSDKForTesting.removeInstance(API_KEY));

        assertTrue(exception.getMessage().contains("Ошибка: экземпляр с ключом '" + API_KEY + "' не найден."));
    }

    @Test
    void getWeather_shouldReturnCachedData_whenNotExpired() {
        // Мокируем ответ от MockWebServer
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"name\":\"London\", \"dt\":1630425600}")
                .addHeader("Content-Type", "application/json"));

        WeatherData data = sdk.getWeather("London");

        assertNotNull(data);
        assertEquals("London", data.name);

        WeatherData cachedData = sdk.getWeather("London");

        assertNotNull(cachedData);
        assertEquals("London", cachedData.name);
    }

    @Test
    void getWeather_shouldFetchNewData_whenCacheExpired() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"name\":\"Paris\", \"dt\":1630425600}")
                .addHeader("Content-Type", "application/json"));

        WeatherData oldData = sdk.getWeather("Paris");

        Thread.sleep(1000);

        // Мокируем новый ответ
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"name\":\"Paris\", \"dt\":1630426600}")
                .addHeader("Content-Type", "application/json"));

        WeatherData newData = sdk.getWeather("Paris");

        assertNotNull(newData);
        assertEquals("Paris", newData.name);
        assertNotEquals(oldData.dt, newData.dt);
    }

    @Test
    void getWeatherAsJson_shouldReturnValidJson() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"name\":\"Berlin\", \"dt\":1630425600}")
                .addHeader("Content-Type", "application/json"));

        String json = sdk.getWeatherAsJson("Berlin");

        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"Berlin\""));
        assertTrue(json.contains("\"dt\""));
    }

    @Test
    void startPolling_shouldUpdateWeatherAutomatically() throws InterruptedException{
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"name\":\"Tokyo\", \"dt\":1630425600}")
                .addHeader("Content-Type", "application/json"));

        sdk.getWeather("Tokyo");

        Thread.sleep(500);

        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"name\":\"Tokyo\", \"dt\":1630426600}")
                .addHeader("Content-Type", "application/json"));

        WeatherData updatedData = sdk.getWeather("Tokyo");

        assertNotNull(updatedData);
        assertEquals("Tokyo", updatedData.name);
    }
}