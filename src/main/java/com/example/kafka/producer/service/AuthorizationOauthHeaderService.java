package com.example.kafka.producer.service;

import java.io.Serializable;

import org.springframework.http.HttpMethod;

import com.example.kafka.producer.exception.BaseException;

public interface AuthorizationOauthHeaderService extends Serializable {

	String gerarHeaderToken(String url, String payload, HttpMethod method) throws BaseException;
	
}