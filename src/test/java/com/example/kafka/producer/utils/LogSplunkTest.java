package com.example.kafka.producer.utils;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.kafka.producer.model.MasterCardDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LogSplunkTest {

	
	@Test
	public void testLogSplunk() throws JsonProcessingException {
	    
		
		MasterCardDTO masterCardDTO =  new MasterCardDTO();
		masterCardDTO.setNumeroCartao("12345678901234567");
		masterCardDTO.setNumeroCarteira("618");
		masterCardDTO.setIdCartao(UUID.randomUUID().toString());
		masterCardDTO.setIdDispositivo(UUID.randomUUID().toString());
		masterCardDTO.setCodigoBandeira("M");
		masterCardDTO.setTipoCartao(List.of("D","M", "C").get(new Random().nextInt(3)));
		masterCardDTO.setOrigemInformacao(List.of("BC","VQ").get(new Random().nextInt(2)));
		
		
		
		LogSplunk.info(Splunk.builder().payload(masterCardDTO).customMessage(new ObjectMapper().writeValueAsString(masterCardDTO)).build());
	    
	}
}
