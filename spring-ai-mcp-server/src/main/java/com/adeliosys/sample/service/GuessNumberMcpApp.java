package com.adeliosys.sample.service;

import com.adeliosys.sample.model.GuessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.MetaProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GuessNumberMcpApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuessNumberMcpApp.class);

    private static final int MIN = 1;
    private static final int MAX = 1000;

    // Single shared secret number to guess (good enough for a sample application)
    private final AtomicInteger secretNumber = new AtomicInteger(newSecret());

    private static int newSecret() {
        int secret = ThreadLocalRandom.current().nextInt(MIN, MAX + 1);
        LOGGER.info("New secret number is {}", secret);
        return secret;
    }

    // Step 1 - The resource that serves the MCP App UI

    @Value("classpath:/app/guess-number-app.html")
    private Resource guessNumberAppResource;

    @McpResource(name = "Guess Number App Resource", uri = "ui://guess-number/guess-number-app.html",
            mimeType = "text/html;profile=mcp-app", metaProvider = CspMetaProvider.class)
    public String getGuessNumberAppResource() throws IOException {
        LOGGER.info("Serving 'guess-number-app' resource");
        return guessNumberAppResource.getContentAsString(StandardCharsets.UTF_8);
    }

    public static final class CspMetaProvider implements MetaProvider {
        @Override
        public Map<String, Object> getMeta() {
            return Map.of("ui", Map.of("csp", Map.of("resourceDomains", List.of("https://unpkg.com"))));
        }
    }

    // Step 2 - The tool that points to the UI resource

    @McpTool(title = "Open Guess the Number UI", name = "guess-number-ui",
            description = """
                    Display the interactive 'Guess the Number' game UI where the user must find a number.
                    Use this tool when the user wants to play guess the number.
                    """,
            metaProvider = GuessNumberMetaProvider.class)
    public String openGuessNumberApp() {
        LOGGER.info("Executing 'guess-number-ui' tool");
        return "Guess the Number game loaded.";
    }

    public static final class GuessNumberMetaProvider implements MetaProvider {
        @Override
        public Map<String, Object> getMeta() {
            return Map.of("ui", Map.of("resourceUri", "ui://guess-number/guess-number-app.html"));
        }
    }

    // Step 3 - The tool that checks the user's guess

    @McpTool(title = "Check a Guess", name = "check-guess",
            description = """
                    Check if a guess for the secret number is correct.
                    Returns TOO_LOW, TOO_HIGH, WON (a new secret number is then chosen) or INVALID (number out of range).
                    """)
    public GuessResult checkGuess(@McpToolParam(description = "The user guess") int number) {
        LOGGER.info("Executing 'check-guess' tool with {}", number);
        if (number < MIN || number > MAX) {
            return GuessResult.INVALID;
        }
        int secret = secretNumber.get();
        if (number > secret) {
            return GuessResult.TOO_HIGH;
        }
        if (number < secret) {
            return GuessResult.TOO_LOW;
        }
        secretNumber.set(newSecret());
        return GuessResult.WON;
    }
}
