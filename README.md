# WeatherSDK

WeatherSDK is a Java library for retrieving weather data from the OpenWeather API.

___
## Features
✅ Obtain current weather by city name  
✅ Data caching (10 minutes)  
✅ Two modes of operation:
- `ON_DEMAND` — data update only on request
- `POLLING` — automatic update every 10 minutes

✅ Flexibility: you can create several SDK instances with different API keys.

___

## Quick start

**Step 1.** Add the JitPack repository to your build file

Add to `pom.xml`
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
**Step 2.** Add the dependency
```xml
<dependency>
    <groupId>com.github.pipipuncheck</groupId>
    <artifactId>WeatherSDK</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```
___
## Usage Example
+ Creating an SDK instance:

    + if you want "ON_DEMAND" mode
    ```java
    WeatherSDK sdk = WeatherSDK.addInstance("YOUR_API_KEY", Mode.ON_DEMAND);
    ```
    + "POLLING" mode
    ```java
    WeatherSDK sdk = WeatherSDK.addInstance("YOUR_API_KEY", Mode.POLLING);
    ```
+ Obtaining weather data:
    + as java object:
    ```java
    WeatherData data = sdk.getWeather("City");
    ```
    + as json object:
    ```java
    String json = sdk.getWeatherAsJson("City");
    ```
+ Deleting an instance:
```java
WeatherSDK.removeInstance("Your_API_Key");
```
