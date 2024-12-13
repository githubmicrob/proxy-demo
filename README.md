# HTTP Clients with HTTPS Proxy Example

This project demonstrates how different HTTP clients interact with an HTTPS proxy. It sets up an Apache HTTP Server as a proxy and provides example test cases for two different HTTP clients.

---

## What This Project Does

1. **Launches an Apache HTTP Server as a Proxy**:
    - Configured to act as a proxy using a provided `httpd.conf` file.
    - Runs in a Docker container.
    - Exposes:
        - Port `80` for HTTP proxy.
        - Port `443` for HTTPS proxy (using a self-signed SSL certificate).

2. **Includes Two Test Files**:
    - One for Java's built-in HTTP client.
    - One for Apache HTTP client.
    - Both test files demonstrate connections through HTTP and HTTPS proxy ports.

3. **Simple Execution**:
    - The entire project, including setup and tests, can be executed with:
      ```bash
      mvn clean test
      ```

---

## Generating Your Own Self-Signed Certificate

The HTTPS proxy requires a self-signed SSL certificate. If you wish to generate your own, use the following command:

```bash
openssl req -x509 -nodes -days 1826 -newkey rsa:2048 -keyout server.key -out server.crt
```

---

## Steps to Configure the `httpd.conf` File

### 1. Generate the Initial `httpd.conf` File
Run the following command to extract the default configuration from the official Apache HTTP Server Docker image:

```bash
docker run --rm httpd:2.4.59 cat /usr/local/apache2/conf/httpd.conf > httpd.conf
```

This command creates a base configuration file named `httpd.conf` in your current directory.

### 2. Modify the Configuration File

#### **Uncomment Required Modules**

Open the `httpd.conf` file and uncomment the following modules by removing the `#` at the beginning of their lines:

- `mod_xml2enc.so`
- `mod_proxy_html.so`
- `mod_socache_shmcb.so`
- `mod_proxy.so`
- `mod_proxy_connect.so`
- `mod_proxy_http.so`
- `mod_ssl.so`
- `mod_proxy_http2.so`

#### **Enable SSL Configuration**

Uncomment the following line to include SSL-related configurations:

```
Include conf/extra/httpd-ssl.conf
```

#### **Enable Proxying**

Add the following line to enable proxy requests:

```
ProxyRequests On
```

