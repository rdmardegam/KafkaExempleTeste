package com.example.kafka.producer.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.producer.model.Account;
import com.example.kafka.producer.model.Account.Tokens;
import com.example.kafka.producer.model.MasterCardDTO;
import com.example.kafka.producer.model.Token;
import com.example.kafka.producer.service.impl.OrquestradorService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class TesteOrquestradorController2 {

	
	@Autowired
	ObjectMapper mapper;

	@Autowired
	OrquestradorService orquestradorService;
	
//	@RequestMapping(value = {"/api/{idCartao}",
//							 "/tokenization/v2/tokens/{id_token}",
//							 "/tokenization/v2/tokens",
//							 "/tokenization/v3/card/{id_cartao}/encrypt"},
//			method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
//	public ResponseEntity<String> callApi(HttpServletRequest request, 
//			@RequestBody(required = false) Map<String, Object> requestBody /*String requestBody*/,
//			@RequestParam Map<String, Object> requestQueryParams, 
//			@RequestHeader Map<String, Object> requestHeaders)  {
//	    
//		// Metodo requisitado que será a base para o metodo seguinte
//	    String requestUrl = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
//	    
//	    // Recuperado o tipo do metodo chamado GET, POST, PUT, PATCH ...
//	    HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
//	    
//	    // Extrai os path parametros
//	    Map<String,Object> requestPaths = (HashMap<String, Object>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//	    Map<String,Object> path = new HashMap<String, Object>();
//	    requestPaths.forEach((key, value) -> path.put(key, value.toString()));
//	    
//	    
//	    String jsonValue = orquestradorService.callApi(requestUrl, httpMethod, requestQueryParams, path, requestBody, requestHeaders, true);
//	    
//	    // Monta headerResposta
//	    HttpHeaders responseHeaders = new HttpHeaders();
//	    responseHeaders.set("Content-Type", "application/json");
//	    ResponseEntity<String> responseEntity = new ResponseEntity<String>(jsonValue, responseHeaders, HttpStatus.OK);
//	    
//	    return responseEntity;
//	}
	
	public ResponseEntity<?> callApi(@RequestBody(required = false) String  requestBodyString) {
		
		System.out.println(requestBodyString);
		
		
		Account account =  new Account();
		account.setAccountPanSuffix(12234);
		List<Token> listToken =  new ArrayList<Token>();
		Token token =  new Token();
		token.setCorrelationId("AAAXCA121456");
		token.setCurrentStatusCode("A");
		token.setCurrentStatusDateTime(OffsetDateTime.now()); 
		listToken.add(token);
		token =  new Token();
		token.setCurrentStatusCode("C");
		token.setCorrelationId("1234567890");
		token.setCurrentStatusDateTime(OffsetDateTime.now());
		listToken.add(token);
		
		token =  new Token();
		token.setCurrentStatusCode("P");
		token.setCorrelationId("987321564");
		token.setCurrentStatusDateTime(OffsetDateTime.now());
		listToken.add(token);
		
		Tokens tokens =  account.new Tokens();;
		tokens.setToken(listToken);
		account.setTokens(tokens);
		return new ResponseEntity<Account>(account, HttpStatus.OK);
		
	} 
	
	
	@PostMapping("/api/{idCartao}")
	public ResponseEntity<?> apiTeste(@RequestBody MasterCardDTO dto,
			//@RequestBody(required = false) String  requestBodyString,
			@PathVariable(name = "idCartao") String idCartao, @RequestParam(name = "nome") String name,
			@RequestParam(name = "status") List<String> listStatus,  @RequestParam (name="erro") int erro) {
		
		System.out.println(URLDecoder.decode(name, StandardCharsets.UTF_8));
		
		System.out.println("----------");
		System.out.println(dto);
		//System.out.println(requestBodyString);
		
		System.out.println(idCartao);
		System.out.println(name+ "-----??");
		System.out.println(listStatus);
		System.out.println(erro);
		System.out.println("----------");
		
		dto.setCorrelationId(name +"-----??");
		dto.setIdCartao("352145975");
		
		
		if(erro==1) {
		String erroBody = 	"{   \"erro\":\"CLIENTE\",\r\n"
				+ "				\"campo\":\"nome\",\r\n"
				+ "				\"info\": \"nome informado invalido\"\r\n"
				+ "			}";
			return new ResponseEntity<String>(erroBody, HttpStatus.BAD_REQUEST);
		} else if(erro==2) {
			return new ResponseEntity<String>("ERRO NA API EXTERNA", HttpStatus.INTERNAL_SERVER_ERROR);
		} else if(erro==4) {
			Account account =  new Account();
			account.setAccountPanSuffix(12234);
			List<Token> listToken =  new ArrayList<Token>();
			Token token =  new Token();
			token.setCorrelationId("AAAXCA121456");
			token.setCurrentStatusCode("A");
			token.setCurrentStatusDateTime(OffsetDateTime.now()); 
			listToken.add(token);
			token =  new Token();
			token.setCurrentStatusCode("C");
			token.setCorrelationId("1234567890");
			token.setCurrentStatusDateTime(OffsetDateTime.now());
			listToken.add(token);
			
			token =  new Token();
			token.setCurrentStatusCode("P");
			token.setCorrelationId("987321564");
			token.setCurrentStatusDateTime(OffsetDateTime.now());
			listToken.add(token);
			
			Tokens tokens =  account.new Tokens();;
			tokens.setToken(listToken);
			account.setTokens(tokens);
			return new ResponseEntity<Account>(account, HttpStatus.OK);
		}
		
		return new ResponseEntity<MasterCardDTO>(dto, HttpStatus.OK);
	}
}