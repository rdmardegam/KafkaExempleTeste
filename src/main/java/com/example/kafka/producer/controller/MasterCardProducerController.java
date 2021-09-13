package com.example.kafka.producer.controller;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.producer.model.MasterCardDTO;
import com.example.kafka.producer.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RestController
public class MasterCardProducerController {
	
//	@Autowired
//	private KafkaTemplate<String, MasterCardDTO> kafkaTemplate;
	
	@Autowired
	private KafkaTemplate<String, Map<?, ?>> kafkaTemplate;
	
	@Autowired
	private CardService cardService;
	
	@Autowired
	private ObjectMapper mapper;
	
	private static final Logger logger = LoggerFactory.getLogger(MasterCardProducerController.class);
	
	@GetMapping(value = "/produzMasterCard", produces = MediaType.APPLICATION_JSON_VALUE)
	public String produzMasterCard(	@RequestParam(name= "accountPan",required = false) String accountPan, 
									@RequestParam(name = "correlationId",required = false)  String correlationId) throws Exception {
		
		//TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		logger.info("INICIANDO CONTROLLER PRODUZMARTERCARD");
		
		MasterCardDTO masterCardDTO = new MasterCardDTO();
		masterCardDTO.setNumeroCartao(accountPan);
		masterCardDTO.setCorrelationId(correlationId);
		masterCardDTO.setDataEvento(OffsetDateTime.now());
		
		//618 GOOGLE
		masterCardDTO.setNumeroCarteira(List.of("216","217","103","618").get(new Random().nextInt(4)));
		//masterCardDTO.setNumeroCarteira("618");
		
		masterCardDTO.setIdCartao(UUID.randomUUID().toString());
		masterCardDTO.setIdDispositivo(UUID.randomUUID().toString());
		
		//masterCardDTO.setCodigoBandeira(List.of("M","V").get(new Random().nextInt(2)));
		masterCardDTO.setCodigoBandeira("M");
		
		masterCardDTO.setTipoCartao(List.of("D","M", "C").get(new Random().nextInt(3)));
		masterCardDTO.setOrigemInformacao(List.of("BC","VQ").get(new Random().nextInt(2)));
		
		//var future = kafkaTemplate.send("MASTER_CARD_TOKEN_ACTIVATION", masterCardDTO.getAccountPan(), masterCardDTO);
		
		Message<MasterCardDTO> record = MessageBuilder.
	             withPayload(masterCardDTO).
	             setHeader(KafkaHeaders.MESSAGE_KEY, masterCardDTO.getNumeroCartao()).
	             setHeader(KafkaHeaders.TOPIC, "MASTER_CARD_TOKEN_ACTIVATION").
	             setHeader(KafkaHeaders.TIMESTAMP, Instant.now().toEpochMilli()).
	             build();
		
		
//		var message = kafkaTemplate.send("MASTER_CARD_TOKEN_ACTIVATION", masterCardDTO.getNumeroCartao(), masterCardDTO).get(5, TimeUnit.SECONDS);
//      var message = kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);
		
		var mapPayload = Collections.singletonMap("payload", masterCardDTO);
		
		
		var message = kafkaTemplate.send("MASTER_CARD_TOKEN_ACTIVATION", masterCardDTO.getNumeroCartao(), mapPayload).get(5, TimeUnit.SECONDS);
		
		
		
		System.out.println(message.getProducerRecord().value());
		
		//return message.getProducerRecord().value().toString();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		return gson.toJson(mapPayload);
		//return jsonToSend;
		
	}	
	
	@GetMapping(value = "/testeRetry", produces = MediaType.APPLICATION_JSON_VALUE)
	public String testeRetry()  {
		cardService.ativarToken("123123123", "123123123123");
		
//		cardService.listaContaToken("123123123");
		
		return "OK";
	}
	
}