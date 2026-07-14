package com.adeliosys.sample.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * This interceptor logs some information about the HTTP calls executed by RestClient.
 * It is used to trace HTTP requests to LLM services called using RestClient (e.g. Ollama).
 * LLM services called without using RestClient (e.g. Azure OpenAI) are not covered.
 * Note that this interceptor logs the headers, hence may reveal sensitive information such as API keys.
 */
@Configuration
public class RestClientLogger implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RestClientLogger.class);

    @Bean
    public RestClientCustomizer loggingRestClientCustomizer() {
        return restClientBuilder -> restClientBuilder.requestInterceptor(new RestClientLogger());
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logger.debug("-> Request: {} {}", request.getMethod(), request.getURI());
        logger.debug("-> Headers: {}", request.getHeaders());
        logger.debug("-> Body: {}", new String(body, StandardCharsets.UTF_8));

        long duration = -System.currentTimeMillis();
        ClientHttpResponse response = execution.execute(request, body);
        duration += System.currentTimeMillis();

        // Wrap response to enable multiple reads
        BufferedResponseWrapper responseWrapper = new BufferedResponseWrapper(response);

        logger.debug("<- Response: {} {} in {} ms", responseWrapper.getStatusCode(), responseWrapper.getStatusText(), duration);
        logger.debug("<- Headers: {}", responseWrapper.getHeaders());
        String responseBody = StreamUtils.copyToString(responseWrapper.getBody(), StandardCharsets.UTF_8);
        logger.debug("<- Body: {}", responseBody);

        return responseWrapper;
    }

    /**
     * Buffer the response body so it can be read twice (once for logging, once for actual processing).
     */
    private static class BufferedResponseWrapper implements ClientHttpResponse {

        private final ClientHttpResponse original;
        private final byte[] body;

        public BufferedResponseWrapper(ClientHttpResponse response) throws IOException {
            this.original = response;
            this.body = StreamUtils.copyToByteArray(response.getBody());
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public org.springframework.http.HttpHeaders getHeaders() {
            return original.getHeaders();
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return original.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return original.getStatusText();
        }

        @Override
        public void close() {
            original.close();
        }
    }
}
