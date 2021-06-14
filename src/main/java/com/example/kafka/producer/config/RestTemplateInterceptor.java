package com.example.kafka.producer.config;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.example.kafka.producer.service.AuthorizationOauthHeaderService;

@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger log = LoggerFactory.getLogger(RestTemplateInterceptor.class);

	@Autowired
	AuthorizationOauthHeaderService authorizationCardService;

	public static int x=0;
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		 
		log.info("[RestTemplateInterceptor]- Acionado");

		System.out.println("****");
		System.out.println("-- " +x++);
		System.out.println("****");
		
		// Chama serviço para gerar o header com Oauth para a requisição especifica
		String authHeader = authorizationCardService.gerarHeaderToken(request.getURI().toString(),new String(body, "UTF-8"), request.getMethod());
		
		request.getHeaders().set(HttpHeaders.AUTHORIZATION, authHeader);
		request.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		//logRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		logResponse(response);
		
		return response;
	}

	private void logRequest(HttpRequest request, byte[] body) throws IOException {

		log.info("===========================request begin================================================");
		log.info("URI         : {}", request.getURI());
		log.info("Method      : {}", request.getMethod());
		log.info("Headers     : {}", request.getHeaders());
		log.info("Request body: {}", new String(body, "UTF-8"));
		log.info("==========================request end================================================");

	}

	private void logResponse(ClientHttpResponse response) throws IOException {
		log.info("");	
		log.info("============================response begin==========================================");
		log.info("Status code  : {}", response.getStatusCode());
		log.info("Status text  : {}", response.getStatusText());
		log.info("Headers      : {}", response.getHeaders());
		log.info("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
		log.info("=======================response end=================================================");

	}

//	private String getHeaderOath() throws Exception {
//		PrivateKey signingKey = AuthenticationUtils.loadSigningKey("./keyalias-sandbox.p12", "keyalias",
//				"keystorepassword1");
//
//		HttpHeaders header = new HttpHeaders();
//
//		HttpRequest request = new HttpRequest() {
//			@Override
//			public HttpMethod getMethod() {
//				return HttpMethod.POST;
//			}
//
//			@Override
//			public String getMethodValue() {
//				return getMethod().toString();
//			}
//
//			@Override
//			public URI getURI() {
//				try {
//					return new URI("https://api.mastercard.com/service");
//				} catch (URISyntaxException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				return null;
//			}
//
//			@Override
//			public HttpHeaders getHeaders() {
//				return header;
//			}
//		};
//
//		SpringHttpRequestSigner instanceUnderTest = new SpringHttpRequestSigner(
//				"6ynVPNsWkA59SZXvieIRJ7tbapzylu9jxaq3te4re9dca799!5f9bae87b9fa4b6faee7cfacfdce5e9f0000000000000000",
//				signingKey);
//		instanceUnderTest.sign(request, null);
//
//		System.out.println(header.getFirst(HttpHeaders.AUTHORIZATION));
//
//		return header.toString();
//
//	}
}