package com.example.kafka.producer.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.example.kafka.producer.exception.BaseException;
import com.example.kafka.producer.model.Account;

public interface CardService  extends Serializable {

//	public void ativarToken(String accountPan, String correlationId) throws BaseException;
//	public Optional<List<Account>> listaContaToken (String accountPan)  throws BaseException;
	
	public void ativarToken(String accountPan, String correlationId);
	public Optional<List<Account>> listaContaToken (String accountPan);
	
	public String gerarBodyPesquisaTokens(String accountPan);
	
}