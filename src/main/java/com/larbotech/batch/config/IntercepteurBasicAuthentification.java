package com.larbotech.batch.config;

import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class IntercepteurBasicAuthentification implements ClientHttpRequestInterceptor {

  private String username;
  private String password;

  public IntercepteurBasicAuthentification(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
      ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
    HttpHeaders headers = httpRequest.getHeaders();
    headers.add(HttpHeaders.AUTHORIZATION,
        encodeCredentialsForBasicAuthentification(username, password));

    return clientHttpRequestExecution.execute(httpRequest, bytes);
  }

  private static String encodeCredentialsForBasicAuthentification(String username,
      String password) {
    return "Basic " + new Base64().encodeToString((username + ":" + password).getBytes());
  }
}
