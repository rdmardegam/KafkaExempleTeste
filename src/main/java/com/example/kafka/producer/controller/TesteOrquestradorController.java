//package com.example.kafka.producer.controller;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.HandlerMapping;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.JsonNodeFactory;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//
//import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
//
//@Controller
//public class TesteOrquestradorController {
//
////    // percorre os valores do requestBody e verifica se os atributos correspondem à condição definida
////    findAndEncode(requestBody, "id_texto1");
////    findAndEncode(requestBody, "id_conta1");
////    
////    // percorre os valores do requestParams e verifica se os atributos correspondem à condição definida
////    findAndEncode(requestParams, "id_texto1");
////    findAndEncode(requestParams, "id_conta1");
////    
////    // percorre os valores do headers e verifica se os atributos correspondem à condição definida
////    findAndEncode(headers, "id_texto1");
////    findAndEncode(headers, "id_conta1");
//
//	
//	// Recupera os Headers
//    /*HttpHeaders httpHeaders = new HttpHeaders();
//    headers.forEach(httpHeaders::set);*/
//	
//	@Autowired
//	ObjectMapper mapper;
//	
//	
//	@RequestMapping(value = {"/api/{cartao}",
//							 "/api2/**",
//							 "/tokenization-app-bff/v2/tokens/{id_token}",
//							 "/tokenization-app-bff/v3/card/{id_cartao}/encrypt"},
//			method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE })
//	public ResponseEntity<String> callApi(HttpServletRequest request, @RequestBody(required = false) /*Map<String, Object> requestBody*/ String requestBody, @RequestParam Map<String, Object> requestParams, @RequestHeader Map<String, String> headers) {
//	    
//		// Metodo requisitado que será a base para o metodo seguinte
//	    String requestUrl = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
//	    							 /*request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)
//	    							 request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)*/
//	    
//	    // Recuperado o tipo do metodo chamado GET, POST, PUT, PATCH ...
//	    HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
//	    
//	    Map<String, String> headersOriginal = headers;
//	    
//	    // Extrai os path parametros
//	    Map<String,String> requestPaths = (HashMap<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//	    
//	    // Request Param
//	    Map<String, Object> requestParamsOriginal = requestParams;
//	    
//	    // Body
//	    String requestBodyOriginal = requestBody;
//	    
//	    // Atribui a api a ser roteada
//	    String apiUrl = "http://sua-api-segunda.com" + requestUrl;
//	    
//	    /***************************/
//	    /***************************/
//	    /***************************/
//	    
//	    
//	    // Efetua mascara no json recebido
//	    if(!StringUtils.isEmpty(requestBody)) {
//	    	requestBody = findAndEncode(requestBody, Arrays.asList("id_token", "type","card_id"));
//	    }
//	    
//	    // Efetua mascara no requestPaths
//	    
//	    
//	    
//	    
//	    
//	    //requestBody.replaceAll((key, value) -> findAndEncode(value, Arrays.asList("id_token", "type")));;
//	    
//	    // Headers
//	    System.out.println(headers);
//	    
//	    // Body
//	    System.out.println(requestBody);
//	    	    
//	    
//	    
//	    //requestParams.replaceAll( (key, value) -> findAndEncode(value, Arrays.asList("id_token", "type","card_id","card_status")) );
//	    
//	    System.out.println(requestParams);
//	    
//	    
//	    
//	    //this.ofuscarEntrada(headers,requestPaths, requestParams,requestBody);
//	    
//	    
//	    
//	    // Atribui na url os valores
//	    for (Map.Entry<String, String> entry : requestPaths.entrySet()) {
//	    	apiUrl = apiUrl.replace("{" +  entry.getKey() +"}", entry.getValue());
//	    }
//	    System.out.println(apiUrl);
//	    
//	    
//	    //ResponseEntity<String> responseEntity = apiService.callApi(apiUrl, httpMethod, httpHeaders, requestBody, requestParams);
//	    //extractPathVariables(request);
//	    
//	    
//	    ResponseEntity<String> responseEntity = new ResponseEntity<String>("OK", HttpStatus.OK);
//	    
//	    return responseEntity;
//	}
//
//	
//	private String findAndEncode(String json, List<String> attributeNames) {
//	        try {
//	            JsonNode root = mapper.readTree(json);
//	            for (String attributeName : attributeNames) {
//	                findAndEncode(root, attributeName);
//	            }
//	            return mapper.writeValueAsString(root);
//	        } catch (IOException e) {
//	            throw new RuntimeException("Error while encoding request body", e);
//	        }
//	 }
//	 
//	 private void findAndEncode(JsonNode node, String attributeName) {
//	        if (node.isObject()) {
//	            Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
//	            while (iterator.hasNext()) {
//	                Map.Entry<String, JsonNode> entry = iterator.next();
//	                if (entry.getKey().equals(attributeName)) {
//	                	Object encodedValue = encodeValue(entry.getValue());
//	                	  ((ObjectNode) node).replace(entry.getKey(), JsonNodeFactory.instance.pojoNode(encodedValue));
//	                } else {
//	                    findAndEncode(entry.getValue(), attributeName);
//	                }
//	            }
//	        } else if (node.isArray()) {
//	            for (JsonNode arrayNode : node) {
//	                findAndEncode(arrayNode, attributeName);
//	            }
//	        }
//	    }
//	 
//	 /*private String encodeValue(String value) {
//	        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
//     }*/
//	 
//	 private Object encodeValue(JsonNode value) {
//		    if (value.isTextual()) {
//		        String stringValue = value.textValue();
//		        return Base64.getEncoder().encodeToString(stringValue.getBytes(StandardCharsets.UTF_8));
//		    } else if (value.isInt()) {
//		        Integer intValue = value.intValue();
//		        intValue = 999999999;
//		        return  intValue;
//		        // realizar a conversão do int para base64
//		        // retornar o resultado como um Integer
//		    } else if (value.isLong()) {
//		    	Long longValue = value.longValue();
//		    	longValue = 999999999999L;
//		        return longValue;
//		        
//		    } else if (value.isFloat()) {
//		        Float floatValue =  value.floatValue();
//		        return floatValue + 1F;
//		        // realizar a conversão do float para base64
//		        // retornar o resultado como um Float
//		    } else {
//		        // caso não seja nenhum dos tipos esperados, retornar o próprio valor
//		        return value;
//		    }
//		}
//
//
//
//
//
//	 
//	
//	private void ofuscarEntrada(Map<String, String> headers, Map<String, String> requestPaths, Map<String, String> requestParams, Map<String, Object> requestBody) {
//		
//		
//		
//	}
//	
//	
//	
//	
//	
////	private Map<String, String> extractPathVariables(HttpServletRequest request) {
////        Map<String, String> pathVariables = new HashMap<>();
////        //String requestUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
////        String requestUrl = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
////        
////        Map map = new TreeMap<>((Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
////        
////        
////        
////        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(requestUrl);
////        /*builder.build().getPathSegments().forEach(segment -> {
////            Map<String, String> variables = segment.getVariables();
////            variables.forEach((name, value) -> pathVariables.put(name, value));
////        });*/
////        
////        List<String> x = builder.build().getPathSegments();
////        
////        Map pathVariables2 = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
////        
////        
////        return pathVariables;
////    }
//}
