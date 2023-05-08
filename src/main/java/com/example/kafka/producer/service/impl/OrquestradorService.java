package com.example.kafka.producer.service.impl;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.example.kafka.producer.exception.ApiErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.handler.timeout.ReadTimeoutException;
import reactor.netty.http.client.HttpClient;

@Service
public class OrquestradorService {

	private WebClient webClient;
	private ObjectMapper mapper;
	
	private static List<String> fildsToEncodeDecode = new ArrayList<String>();
	
	static {
		fildsToEncodeDecode =  Arrays.asList("id_token", "type", "card_id", "idCartao", "token_id", "TokenUniqueReference");
	}
	
	
	@Autowired
    public OrquestradorService(ObjectMapper mapper) {
		this.webClient = WebClient.builder().baseUrl("http://localhost:8080")
											.clientConnector(new ReactorClientHttpConnector(	
											HttpClient.create().responseTimeout(Duration.ofSeconds(2))))
				.build();
        this.mapper = mapper;
    }
		
    public String callApi(String apiUrl, HttpMethod httpMethod, Map<String, Object> queryParams, Map<String, Object> paths,
    		Map<String, Object> body, Map<String, Object> headers, boolean externalCall) {

    	// Aplica Encode
    	this.encode(body,queryParams,paths,externalCall);
    	
    	// Aplica paths na url
    	String urlToCall = this.getUrlWhithPaths(apiUrl,paths).replaceFirst("api", "api2").replaceFirst("tokenization", "tokenization2");

    	// Prepara os Headers
    	HttpHeaders headersToSend = getHeaders(headers); 
        
    	// Perar os queryParametros para serem enviados
    	MultiValueMap<String, String> queryParamsToSend = this.getQueryParams(queryParams); 
    	
    	String jsonRetorno = "";
    	
		jsonRetorno = webClient.method(httpMethod)
				.uri(uriBuilder -> uriBuilder.path(urlToCall).queryParams(queryParamsToSend).build())
				.headers(httpHeaders -> httpHeaders.addAll(headersToSend))
				.body(body != null ? BodyInserters.fromValue(body) : null).retrieve()
				.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class).flatMap(errorBody -> {
					throw new ApiErrorException(response.statusCode(), errorBody);
				})).bodyToMono(String.class)
				/*.onErrorResume(Exception.class,
						error -> Mono.error(
								new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO INTERNO WEBCLIENT")))*/
				.doOnError(throwable -> {
		            if(throwable instanceof ApiErrorException) {
		            	throw (ApiErrorException) throwable;
		            }
					if (throwable instanceof WebClientRequestException && throwable.getCause() instanceof ReadTimeoutException) {
		                // Tratar o erro de timeout
		                throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO INTERNO WEBCLIENT - TIMEOUT");
		            } else {
		                // Tratar outros erros de comunicação
		                throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO INTERNO WEBCLIENT");
		            }
		        })
				.block();

		jsonRetorno = this.decode(jsonRetorno);
    	
        
        
        return jsonRetorno;
    } 
	

	private HttpHeaders getHeaders(Map<String, Object> headers) {
		 HttpHeaders headersSend = new HttpHeaders();
		 headers.forEach((key, value) -> headersSend.add(key, value.toString()));
		 return headersSend;	
	}



	private String getUrlWhithPaths(String apiUrl, Map<String, Object> paths) {
		String url = apiUrl;
		// Atribui na url os valores
	    for (Map.Entry<String, Object> entry : paths.entrySet()) {
	    	url = url.replace("{" +  entry.getKey() +"}", entry.getValue().toString());
	    }
	    return url;
	}
	

	private MultiValueMap<String, String> getQueryParams(Map<String, Object> queryParams) {
		MultiValueMap<String, String> multiValueQueryParam = new LinkedMultiValueMap<>();
		queryParams.forEach((key, value) -> multiValueQueryParam.add(key, value.toString()));
		return multiValueQueryParam;
	}

	private void encode(Map<String, Object> body, Map<String, Object> queryParams, Map<String, Object> paths, boolean externalCall) {
		if(externalCall) {
			this.findAndEncode(body, fildsToEncodeDecode, true);
			this.findAndEncode(queryParams, fildsToEncodeDecode,true);
			this.findAndEncode(paths, fildsToEncodeDecode,true);
		}
	}
	
	private String decode(String json) {
		String jsonRetorno = "";
		try {
			Map<String,Object> result = mapper.readValue(json, HashMap.class);
			this.findAndEncode(result, fildsToEncodeDecode, false);
			return mapper.writeValueAsString(result);
			
		} catch (JsonProcessingException e) {
			jsonRetorno = "ERRO NO RETORNO";
			e.printStackTrace();
		}
		return jsonRetorno;
	}
	
	
	private void findAndEncode(Map<String, Object> map, List<String> attributeNames, boolean encode) {
	    if(map != null) {
	    	for (Map.Entry<String, Object> entry : map.entrySet()) {
		        String key = entry.getKey();
		        Object value = entry.getValue();
		        
		        if (value instanceof Map) {
		            findAndEncode((Map<String, Object>) value, attributeNames,encode);
		        } else if (value instanceof List) {
		            for (Object obj : (List) value) {
		                if (obj instanceof Map) {
		                    findAndEncode((Map<String, Object>) obj, attributeNames,encode);
		                }
		            }
		        } else if (attributeNames.contains(key)) {
		            if (value instanceof String) {
		                String encodedValue = encode ? Base64.getEncoder().encodeToString(((String) value).getBytes()) : new String(Base64.getDecoder().decode(((String) value).getBytes()));
		                map.put(key, encodedValue);
		            } else if (value instanceof Integer) {
				        Integer intValue = (Integer) value;
				        intValue = 999999999;
				        map.put(key, intValue);
				        // realizar a conversão do int para base64
				        // retornar o resultado como um Integer
		            } else if (value instanceof Long) {
				    	Long longValue = (Long) value;
				    	longValue = 888888888888L;
					        map.put(key, longValue);
				    }
		        }
		    }
	    }
	}
	
	
	
	
}
