package com.example.kafka.producer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.kafka.producer.service.CardService;

@Controller
public class MasterCardPushController {
	@Autowired
	private CardService cardService;
	
	@GetMapping(value = "pesquisaTokenCartao")
	public Object pesquisaTokenCartao(@RequestParam(name= "numeroCartao",required = false) String numeroCartao) throws Exception {
		return cardService.listaContaToken(numeroCartao).get();
	}
	
	@GetMapping(value = "listEligibleTokenRequestors")
	public ResponseEntity<String> listEligibleTokenRequestors(@RequestParam(name= "accountRange",required = false) String accountRange) throws Exception {
		String tokenRequestorElegiveis = cardService.listEligibleTokenRequestors(accountRange);

		HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	    
	    return  new ResponseEntity<String>(tokenRequestorElegiveis, responseHeaders, HttpStatus.OK);
	}
	
	@GetMapping(value = "findTokenRequestorAssetInfo")
	public ResponseEntity<String> findTokenRequestorAssetInfo(@RequestParam(name= "assetId",required = false) String assetId) throws Exception {
		String retorno = cardService.findTokenRequestorAssetInfo(assetId);

		HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	    
	    return  new ResponseEntity<String>(retorno, responseHeaders, HttpStatus.OK);
	}
	

	@GetMapping(value = "pushTokenRequestorTeste")
	public ResponseEntity<String> pushTokenRequestorTeste() throws Exception {
		String retorno = cardService.pushTeste();

		HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	    
	    return  new ResponseEntity<String>(retorno, responseHeaders, HttpStatus.OK);
	}
}
