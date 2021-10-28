package com.example.kafka.producer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.producer.model.AssincronoDTO;
import com.example.kafka.producer.service.impl.TesteAssincronoService;
import com.example.kafka.producer.utils.LogSplunk;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.GsonBuilder;

@RestController
public class TesteAssincronoController {

	@Autowired
	private TesteAssincronoService resteAssincronoService; 
	
//	@Autowired 
//	VaultOperations vaultOperations;
	
	@GetMapping(value = "/testeAsync")
	public String test() throws JsonProcessingException {
		
		 //VaultResponseSupport<String> response = vaultOperations.read("secret/credentials", String.class);
		 //VaultResponseSupport<String> response = vaultOperations.read("teste/teste1", String.class);
         //System.out.println(response.getData());
		LogSplunk.initLog("KEY_TESTE","KEY_TESTE", "TOPICO_TESTE");
		
		
		List<AssincronoDTO> listAssincrono =  new ArrayList<AssincronoDTO>();
		for(int x=0;x<10;x++) {
			AssincronoDTO assincronoDTO =  new AssincronoDTO();
			assincronoDTO.setIndex(x);
			assincronoDTO.setValor("Valor "+x);
			assincronoDTO.setTentativa(0);
			//assincronoDTO.setExecuteCallSucess(x==5 ? false:true);
			listAssincrono.add(assincronoDTO);
		}		
		
		try {
			listAssincrono = resteAssincronoService.teste(listAssincrono);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("FIM FIM FIM");
		
		
		LogSplunk.finalizeLog();
		
		return new GsonBuilder().setPrettyPrinting().create().toJson(listAssincrono);
	}
	
	@GetMapping(value = "/teste200")
	public  ResponseEntity<String> test200() throws InterruptedException {
		Thread.sleep(500l);
		return ResponseEntity.ok("{ \"status\": \"sucesso\"}");
	}
	
	@GetMapping(value = "/teste500")
	public  ResponseEntity<String> test500() throws InterruptedException {
		Thread.sleep(1000l);
		
		ResponseEntity<String> responseEntity = 
				new ResponseEntity<String>("{ \"status\": \"error\"}", HttpStatus.INTERNAL_SERVER_ERROR);
		
		return responseEntity;
	}
}
