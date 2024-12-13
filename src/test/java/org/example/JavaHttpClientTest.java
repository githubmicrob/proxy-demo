package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class JavaHttpClientTest {

    private static final String TARGET_HOST = "https://example.com";
    private static final int EXPECTED_RESPONSE_CODE = 200;
    private static final String EXPECTED_STRING = "Example Domain";

    @Container
    private static final HttpDContainer httpd = new HttpDContainer();

    @Test
    public void testConnectionThroughInsecurePort() throws IOException {
        // Given
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpd.getProxyHost(), httpd.getInsecurePort()));
        URL url = new URL(TARGET_HOST);

        // When
        int responseCode;
        String responseBody;

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
        try (InputStream inputStream = httpURLConnection.getInputStream()) {
            responseCode = httpURLConnection.getResponseCode();
            responseBody = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } finally {
            httpURLConnection.disconnect();
        }

        // Then
        assertEquals(EXPECTED_RESPONSE_CODE, responseCode);
        assertTrue(responseBody.contains(EXPECTED_STRING));
    }

    @Test
    public void testConnectionThroughSecurePort() throws IOException {
        // Given
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpd.getProxyHost(), httpd.getSecurePort()));
        URL url = new URL(TARGET_HOST);

        // When
        // Then
        int responseCode;
        String responseBody;

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
        try (InputStream inputStream = httpURLConnection.getInputStream()) {
            responseCode = httpURLConnection.getResponseCode();
            responseBody = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } finally {
            httpURLConnection.disconnect();
        }

        assertEquals(EXPECTED_RESPONSE_CODE, responseCode);
        assertTrue(responseBody.contains(EXPECTED_STRING));
    }
}
