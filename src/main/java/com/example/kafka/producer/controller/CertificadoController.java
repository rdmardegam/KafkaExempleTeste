//package com.example.kafka.producer.controller;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.security.PrivateKey;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpRequest;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import com.mastercard.developer.signers.SpringHttpRequestSigner;
//import com.mastercard.developer.utils.AuthenticationUtils;
//
//@RestController
//public class CertificadoController {
//
//	@Autowired
//	RestTemplate restTemplate;
//	
//	@GetMapping("findStatus")
//	public String findStatus () throws Exception {
//		
////		HttpHeaders httpHeaders = new HttpHeaders();
////	    String url = "https://sandbox.api.mastercard.com/mdes/csapi/v2/systemstatus";
////	    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
////	  //  String result = restTemplate.getForObject(uri, String.class);
////	    //System.out.println("Result " + result);
////		
////	    
////	    
////	    PrivateKey signingKey = AuthenticationUtils.loadSigningKey(
////                "./keyalias-sandbox.p12", 
////                "keyalias", 
////                "keystorepassword1");
////		
////		
////		String consumerKey = "6ynVPNsWkA59SZXvieIRJ7tbapzylu9jxaq3te4re9dca799!5f9bae87b9fa4b6faee7cfacfdce5e9f0000000000000000";
////		URI uri = URI.create(url);
////		String method = "GET";
////		String payload = "Hello world!";
////		Charset charset = StandardCharsets.UTF_8;
////		String authHeader = OAuth.getAuthorizationHeader(uri, method, payload, charset, consumerKey, signingKey);
////		
////		System.out.println(authHeader);
////	    //----
////		
////		httpHeaders.add(OAuth.AUTHORIZATION_HEADER_NAME, authHeader);
////		
////		
////		HttpEntity entity = new HttpEntity(httpHeaders);
////
////		ResponseEntity<String> response = restTemplate.exchange(
////		    url, HttpMethod.GET, entity, String.class);
////		
////		System.out.println(response.getStatusCode());
////		System.out.println(response.getBody());
////		
////		return response.getBody();
//		
//	  PrivateKey signingKey = AuthenticationUtils.loadSigningKey(
//      "./keyalias-sandbox.p12", 
//      "keyalias", 
//      "keystorepassword1");
//		
//	    HttpHeaders header = new HttpHeaders();
//	    
//	    HttpRequest request = new HttpRequest() {
//			@Override
//			public HttpMethod getMethod(){
//				return HttpMethod.POST;
//			}
//			@Override
//			public String getMethodValue(){
//				return getMethod().toString();
//			}
//			@Override
//			public URI getURI(){
//				try {
//					return new URI("https://api.mastercard.com/service");
//				} catch (URISyntaxException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				return null;
//			}
//			@Override
//			public HttpHeaders getHeaders(){
//				return header;
//			}
//		};
//		
//		SpringHttpRequestSigner instanceUnderTest = new SpringHttpRequestSigner("6ynVPNsWkA59SZXvieIRJ7tbapzylu9jxaq3te4re9dca799!5f9bae87b9fa4b6faee7cfacfdce5e9f0000000000000000", signingKey);
//		instanceUnderTest.sign(request, null);
//		
//		System.out.println(header.getFirst(HttpHeaders.AUTHORIZATION));
//		
//		return header.toString();
//	}
//	
//	@GetMapping("findStatus2")
//	public String findStatus2 () throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//		HttpEntity entity = new HttpEntity(headers);
//		
//		
//		String url = "https://sandbox.api.mastercard.com/mdes/csapi/v2/systemstatus";	
//		ResponseEntity<String> response = restTemplate.exchange(
//	    url, HttpMethod.GET, entity, String.class);
//		
//		return response.getBody();
//	}
//}
