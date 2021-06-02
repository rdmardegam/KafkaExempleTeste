package com.example.kafka.producer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import com.mastercard.developer.signers.SpringHttpRequestSigner;
import com.mastercard.developer.utils.AuthenticationUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.PrivateKey;

public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		try {
			request.getHeaders().set(HttpHeaders.AUTHORIZATION, getHeaderOath());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		logRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		logResponse(response);

		return response;
	}

	private void logRequest(HttpRequest request, byte[] body) throws IOException {

		System.out.println("===========================request begin================================================");
		System.out.println("URI         : {}" + request.getURI());
		System.out.println("Method      : {}" + request.getMethod());
		System.out.println("Headers     : {}" + request.getHeaders());
		System.out.println("Request body: {}" + new String(body, "UTF-8"));
		System.out.println("==========================request end================================================");

	}

	private void logResponse(ClientHttpResponse response) throws IOException {

		System.out.println("============================response begin==========================================");
		System.out.println("Status code  : {}" + response.getStatusCode());
		System.out.println("Status text  : {}" + response.getStatusText());
		System.out.println("Headers      : {}" + response.getHeaders());
		System.out
				.println("Response body: {}" + StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
		System.out.println("=======================response end=================================================");

	}

	private String getHeaderOath() throws Exception {
		PrivateKey signingKey = AuthenticationUtils.loadSigningKey("./keyalias-sandbox.p12", "keyalias",
				"keystorepassword1");

		HttpHeaders header = new HttpHeaders();

		HttpRequest request = new HttpRequest() {
			@Override
			public HttpMethod getMethod() {
				return HttpMethod.POST;
			}

			@Override
			public String getMethodValue() {
				return getMethod().toString();
			}

			@Override
			public URI getURI() {
				try {
					return new URI("https://api.mastercard.com/service");
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public HttpHeaders getHeaders() {
				return header;
			}
		};

		SpringHttpRequestSigner instanceUnderTest = new SpringHttpRequestSigner(
				"6ynVPNsWkA59SZXvieIRJ7tbapzylu9jxaq3te4re9dca799!5f9bae87b9fa4b6faee7cfacfdce5e9f0000000000000000",
				signingKey);
		instanceUnderTest.sign(request, null);

		System.out.println(header.getFirst(HttpHeaders.AUTHORIZATION));

		return header.toString();

	}
}