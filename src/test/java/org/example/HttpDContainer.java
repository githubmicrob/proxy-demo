package org.example;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

public class HttpDContainer extends GenericContainer<HttpDContainer> {

    private static final String HTTPD_VERSION = "httpd:2.4.59";

    private static final String SERVER_CERTIFICATE_RESOURCE = "/server.crt";
    private static final String SERVER_KEY_RESOURCE = "/server.key";
    private static final String HTTPD_CONFIG_RESOURCE = "/httpd.conf";

    private static final int INSECURE_PORT = 80;
    private static final int SECURE_PORT = 443;
    private static final String PROXY_HOST = "localhost";

    public HttpDContainer() {
        super(HTTPD_VERSION);
    }

    @Override
    protected void configure() {
        withExposedPorts(INSECURE_PORT, SECURE_PORT);
        withClasspathResourceMapping(SERVER_KEY_RESOURCE, "/usr/local/apache2/conf/server.key", BindMode.READ_ONLY);
        withClasspathResourceMapping(SERVER_CERTIFICATE_RESOURCE, "/usr/local/apache2/conf/server.crt", BindMode.READ_ONLY);
        withClasspathResourceMapping(HTTPD_CONFIG_RESOURCE, "/usr/local/apache2/conf/httpd.conf", BindMode.READ_ONLY);
    }

    public String getProxyHost() {
        return PROXY_HOST;
    }

    public Integer getInsecurePort() {
        return getMappedPort(INSECURE_PORT);
    }

    public Integer getSecurePort() {
        return getMappedPort(SECURE_PORT);
    }
}
