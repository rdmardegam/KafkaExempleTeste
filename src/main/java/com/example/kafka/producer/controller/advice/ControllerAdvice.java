package com.example.kafka.producer.controller.advice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.kafka.producer.exception.ApiErrorException;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
	
	@ExceptionHandler(value = ApiErrorException.class)
    protected ResponseEntity<String> handleApiErrorException(ApiErrorException ex) {
		System.out.println(ex.getMessage());
		HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.set("Content-Type", "application/json");
	    ResponseEntity<String> responseEntity = new ResponseEntity<String>(ex.getErrorBody(), responseHeaders,ex.getStatusCode());
		return responseEntity;
    }
	
//	@ExceptionHandler(value = RuntimeException.class)
//    protected ResponseEntity<String> handleApiErrorException(RuntimeException ex) {
//		System.out.println(ex.getMessage());
//		
//	    ResponseEntity<String> responseEntity = new ResponseEntity<String>("ERRO API ORQUESTRADORA",HttpStatus.INTERNAL_SERVER_ERROR);
//		return responseEntity;
//    }
}
	