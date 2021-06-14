package com.example.kafka.producer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.example.kafka.producer.exception.BusinessException;
import com.example.kafka.producer.exception.TechnicalException;
import com.example.kafka.producer.service.CardService;

@RestController
public class MasterCardController {

	private static final Logger log = LoggerFactory.getLogger(MasterCardController.class);
	
	@Autowired
	CardService cardService;
	
	@Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
	
	@GetMapping(value = "masterCard")
	public String teste() throws Exception {
		try {

			cardService.ativarToken("5412345678901234", "50042");
			
			
		} catch (Exception e) {
			log.error("ERROR ATIVAR TOKEN PAN: {} - CORRELATIONID: {}");
			log.error(e.getMessage());
			
			// Caso ocorreu um erro tecnico, tenta novamente
			if (e instanceof TechnicalException) {
				this.logTechnicalExeption(e);

				
			} else if (e instanceof BusinessException) {
				// Erro de negocio joga para o Deadletter para futura analise
				
			}

		}
		return "OK";
	}

	private void logTechnicalExeption(Exception e) {
		
		// Verifica se é um erro de conexao 
		if (e.getCause() instanceof HttpClientErrorException) {
			HttpClientErrorException httpError = (HttpClientErrorException) e.getCause();
			
			// Erro ao chamar do nosso cliente (Itau)  com a MasterCard
			if(httpError.getStatusCode().is4xxClientError()) {
				
			// Erro ao de processamento na masterCard, masterCard Mandou um erro 	
			} else if(httpError.getStatusCode().is5xxServerError()) {
				
			}

		// Caso contrario o erro deve ser de certificado ou ligado ao Header ou Chave com a MasterCard	
		} else {
			
		}
	}
	
	@GetMapping(value = "startKafka")
	public String startKafka() throws Exception {
		kafkaListenerEndpointRegistry.getListenerContainer("MASTER_CARD_TOKEN_ACTIVATION").start();
		return "OK";
	}
	
}
