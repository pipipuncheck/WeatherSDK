package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    @JsonProperty("weather")
    public List<Weather> weather;

    @JsonProperty("main")
    public Main main;

    @JsonProperty("visibility")
    public Long visibility;

    @JsonProperty("wind")
    public Wind wind;

    @JsonProperty("dt")
    public Long dt;

    @JsonProperty("sys")
    public Sys sys;

    @JsonProperty("timezone")
    public Long timezone;

    @JsonProperty("name")
    public String name;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather{
        @JsonProperty("main")
        public String main;

        @JsonProperty("description")
        public String description;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main{
        @JsonProperty("temp")
        public Double temp;

        @JsonProperty("feels_like")
        public Double feels_like;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind{
        @JsonProperty("speed")
        public Double speed;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys{
        @JsonProperty("sunrise")
        public Long sunrise;

        @JsonProperty("sunset")
        public Long sunset;
    }
}
