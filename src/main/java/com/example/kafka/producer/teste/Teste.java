package com.example.kafka.producer.teste;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.mastercard.developer.oauth.OAuth;
import com.mastercard.developer.utils.AuthenticationUtils;

public class Teste {

	public static void main(String[] args) throws Exception {
		/*System.setProperty("proxyHost", "proxyad.itau");
		System.setProperty("proxyPort", "8080");
		System.setProperty("javax.net.ssl.trustStore", "8080");
		System.setProperty("javax.net.ssl.trustStorePassword", "C:\\Users\\RDMARUI\\OneDrive - Banco Itaú SA\\Área de Trabalho\\CERTI_DIGITAL\\itau-rede-interna-truststore.jks");
		System.setProperty("javax.net.ssl.trustStoreType", "JDK");*/
		
		
		
		
		RestTemplate restTemplate = new RestTemplate();
		/*PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(100);
		connectionManager.setDefaultMaxPerRoute(6);
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClients.custom().setConnectionManager(connectionManager).build()));*/
		
		
		
		System.out.println("-----------");
		String url = "https://sandbox.api.mastercard.com/mdes/csapi/v2/systemstatus";
		String header = generateHeader(url, "", HttpMethod.GET);
		System.out.println("-----------");
		
		
		HttpHeaders  headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(OAuth.AUTHORIZATION_HEADER_NAME, header);
		
		HttpEntity entity = new HttpEntity(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(
			    url, HttpMethod.GET, entity, String.class);
		
		System.out.println("-----------");
		System.out.println(response.getStatusCode());
		System.out.println(response.getBody());
		System.out.println("-----------");
		
		System.out.println("-----------");
		
		String payload = "{\r\n" + 
				"  \"SearchRequest\": {\r\n" + 
				"    \"TokenUniqueReference\": \"DWSPMC00000000010906a349d9ca4eb1a4d53e3c90a11d9c\",\r\n" + 
				"    \"AuditInfo\": {\r\n" + 
				"      \"UserId\": \"A1435477\",\r\n" + 
				"      \"UserName\": \"John Smith\",\r\n" + 
				"      \"Organization\": \"Any Bank\",\r\n" + 
				"      \"Phone\": \"5555551234\"\r\n" + 
				"    }\r\n" + 
				"  }\r\n" + 
				"}";
		
		url = "https://sandbox.api.mastercard.com/mdes/csapi/v2/search";
		header = generateHeader(url, payload, HttpMethod.POST);
		
		
		
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(OAuth.AUTHORIZATION_HEADER_NAME, header);
		entity = new HttpEntity<String>(payload, headers);
		
		/*String personResultAsJsonStr = 
			      restTemplate.postForObject(url, request, String.class);*/
		
		response = restTemplate.exchange(
			    url, HttpMethod.POST, entity, String.class);
				
		System.out.println("-----------");
		System.out.println(response.getStatusCode());
		System.out.println(response.getBody());
		System.out.println("-----------");
		
		//System.out.println(personResultAsJsonStr);
	}
	
	
	private static String generateHeader(String url, String payload, HttpMethod method) throws Exception {
		String header = "";
		
		HttpHeaders httpHeaders = new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	   
	    PrivateKey signingKey = AuthenticationUtils.loadSigningKey("./keyalias-sandbox.p12", 
                "keyalias", 
                "keystorepassword1");
		
	    
		String consumerKey = "6ynVPNsWkA59SZXvieIRJ7tbapzylu9jxaq3te4re9dca799!5f9bae87b9fa4b6faee7cfacfdce5e9f0000000000000000";
		URI uri = URI.create(url);
		Charset charset = StandardCharsets.UTF_8;
		
		
		for(int x=0;x<5;x++) {
			header = OAuth.getAuthorizationHeader(uri, method.toString(), payload, charset, consumerKey, signingKey);
			//httpHeaders.add(OAuth.AUTHORIZATION_HEADER_NAME, authHeader);
			//httpHeaders.add("content-type", MediaType.APPLICATION_JSON_VALUE);
			System.out.println(header);
		}
		
		
		return header;
	}
}
