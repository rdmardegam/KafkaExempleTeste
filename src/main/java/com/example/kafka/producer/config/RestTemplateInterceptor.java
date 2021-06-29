package com.example.kafka.producer.config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
import com.example.kafka.producer.utils.LogSplunk;
import com.example.kafka.producer.utils.Splunk;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;



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
		
		Splunk splunk =  Splunk.builder().key("master.request.info")
						  .customMessage("call request/response")
						  .starTime(OffsetDateTime.now()).build();
		
		// Chama serviço para gerar o header com Oauth para a requisição especifica
		String authHeader = authorizationCardService.gerarHeaderToken(request.getURI().toString(),new String(body, "UTF-8"), request.getMethod());
		request.getHeaders().set(HttpHeaders.AUTHORIZATION, authHeader);
		request.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		logRequest(request, body, splunk);
		ClientHttpResponse response = execution.execute(request, body);
		logResponse(response, splunk);
		
		splunk.setEndTime(OffsetDateTime.now());
		LogSplunk.info(splunk);
		
		return response;
	}

	private void logRequest(HttpRequest request, byte[] body, Splunk splunk) throws IOException {

		/*log.info("===========================request begin================================================");
		log.info("URI         : {}", request.getURI());
		log.info("Method      : {}", request.getMethod());
		log.info("Headers     : {}", request.getHeaders());
		log.info("Request body: {}", new String(body, "UTF-8"));
		log.info("==========================request end================================================");*/
		
		String logRequest = "{\r\n"
				+ "  \"uri\" : \"%s\",\r\n"
				//+ "  \"header\" : %s,\r\n"
				+ "  \"method\" : \"%s\",\r\n"
				+ "  \"body\" : %s\r\n"
				+ "}";
		
		logRequest =  String.format(logRequest, request.getURI(), request.getMethod(),new String(body, "UTF-8"));
		
		// String json to map
		ObjectMapper mapper = new ObjectMapper();
		Map<String,Object> mapRequest = mapper.readValue(logRequest, new TypeReference<HashMap>(){});

		// Add reader
		mapRequest.put("header", request.getHeaders().toString());
		
		splunk.addMoreInfo(Collections.singletonMap("request", mapRequest));
		splunk.addMoreInfo(Collections.singletonMap("requestUri", request.getURI()));
		
	}

	private void logResponse(ClientHttpResponse response, Splunk splunk) throws IOException {
		/*log.info("");	
		log.info("============================response begin==========================================");
		log.info("Status code  : {}", response.getStatusCode());
		log.info("Status text  : {}", response.getStatusText());
		log.info("Headers      : {}", response.getHeaders());
		log.info("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
		log.info("=======================response end=================================================");*/
		
		String logResponse = "{\r\n"
				+ "  \"code\" : \"%s\",\r\n"
				+ "  \"status text\" : \"%s\",\r\n"
				+ "  \"body\" : %s\r\n"
				+ "}";
		
		logResponse =  String.format(logResponse, response.getStatusCode(), response.getStatusText(), StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
		
		// String json to map
		ObjectMapper mapper = new ObjectMapper();
		Map<String,Object> mapResponse = mapper.readValue(logResponse, new TypeReference<HashMap>() {});
		mapResponse.put("header", response.getHeaders());
		
		splunk.addMoreInfo(Collections.singletonMap("response", mapResponse));
		// Add response na raiz
		splunk.addMoreInfo(Collections.singletonMap("responseStatus", response.getRawStatusCode()));
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