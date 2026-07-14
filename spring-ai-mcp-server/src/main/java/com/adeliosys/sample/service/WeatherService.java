package com.adeliosys.sample.service;

import com.adeliosys.sample.model.WeatherCondition;
import com.adeliosys.sample.model.WeatherReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class WeatherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);

    @McpTool(description = "Return the current weather report for a given city including the condition and temperature in celsius.")
    public WeatherReport getCurrentWeather(@McpToolParam(description = "The name of the city") String city) {
        // The current implementation returns a random report
        WeatherReport report = new WeatherReport(
                WeatherCondition.random(),
                ThreadLocalRandom.current().nextLong(10, 30));

        LOGGER.info("Weather report for '{}': {}", city, report);

        return report;
    }
}
