package org.example;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class ApacheHttpClientProxyTest {

    private static final String TARGET_HOST = "https://example.com";
    private static final int EXPECTED_RESPONSE_CODE = 200;
    private static final String EXPECTED_STRING = "Example Domain";

    @Container
    private static final HttpDContainer httpd = new HttpDContainer();

    @Test
    public void testConnectionThroughInsecurePort() throws IOException, ParseException {
        // Given
        HttpClientBuilder clientBuilder = HttpClientBuilder.create()
            .setProxy(new HttpHost(URIScheme.HTTP.id, httpd.getProxyHost(), httpd.getInsecurePort()));

        HttpGet request = new HttpGet(TARGET_HOST);

        // When
        int responseCode;
        String responseBody;

        try (CloseableHttpClient httpClient = clientBuilder.build()) {
            CloseableHttpResponse response = httpClient.execute(request);

            responseCode = response.getCode();
            responseBody = EntityUtils.toString(response.getEntity());
        }

        // Then
        assertEquals(EXPECTED_RESPONSE_CODE, responseCode);
        assertTrue(responseBody.contains(EXPECTED_STRING));
    }

    @Test
    public void testConnectionThroughSecurePort() throws IOException, ParseException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        // Given
        HttpClientBuilder clientBuilder = HttpClientBuilder.create()
            // Ignore self-signed certificate
            .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(SSLContextBuilder.create()
                        .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                        .build())
                    .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build())
                .build())
            .setProxy(new HttpHost(URIScheme.HTTPS.id, httpd.getProxyHost(), httpd.getSecurePort()));

        HttpGet request = new HttpGet(TARGET_HOST);

        // When
        int responseCode;
        String responseBody;

        try (CloseableHttpClient httpClient = clientBuilder.build()) {
            CloseableHttpResponse response = httpClient.execute(request);

            responseCode = response.getCode();
            responseBody = EntityUtils.toString(response.getEntity());
        }

        // Then
        assertEquals(EXPECTED_RESPONSE_CODE, responseCode);
        assertTrue(responseBody.contains(EXPECTED_STRING));
    }
}
