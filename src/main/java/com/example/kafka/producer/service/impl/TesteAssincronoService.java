package com.example.kafka.producer.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.kafka.producer.exception.BaseException;
import com.example.kafka.producer.exception.TechnicalException;
import com.example.kafka.producer.model.AssincronoDTO;
import com.example.kafka.producer.utils.LogSplunk;
import com.example.kafka.producer.utils.Splunk;

@Service
public class TesteAssincronoService {

	
	@Retryable(value = TechnicalException.class, maxAttempts = 5, backoff = @Backoff(delay = 500) )
	public List<AssincronoDTO> teste(List<AssincronoDTO> listAssincrono) throws BaseException {
		System.out.println("STARTANDO TESTE");
		
		List<String> listExceptions = new ArrayList<String>();
		
		Long threadIdToUseLog = Thread.currentThread().getId();
		
		listAssincrono.parallelStream().forEach(elem -> {
			try {
				if(!elem.getExecuteCallSucess()) {
					elem.setTentativa(elem.getTentativa()+1);
					efetivaAtivacaoMastercard(elem,threadIdToUseLog);
				}	
			}catch (Exception e) {
				elem.setExecuteCallSucess(false);
				listExceptions.add(e.getMessage());
			}
		});
		
		if(!listExceptions.isEmpty()) {
			throw new TechnicalException(listExceptions.toString());
		}
		
		
		return listAssincrono;
		
	}

	
	
	
	private void efetivaAtivacaoMastercard(AssincronoDTO assincronoDTO, Long threadUseLog) throws BaseException {
		System.out.println("Iniciando teste async: "+ assincronoDTO.getValor());
		RestTemplate restTemplate = new RestTemplate();
		
		LogSplunk.info(Splunk.builder().customMessage("Chamando Efetiva Async - " + assincronoDTO.getValor()).build(), threadUseLog);
		
		
		//String url = callSucess ? "http://localhost:8080/teste200" : "http://localhost:8080/teste500";
		//String url = assincronoDTO.getIndex() == 7 ||  assincronoDTO.getIndex() == 4 ? "http://localhost:8080/teste200" : "http://localhost:8080/teste500";
		String url = new Random().nextInt(2) == 1 ? "http://localhost:8080/teste200" : "http://localhost:8080/teste500";
		
		
		
		try {
			// Gerando payload da ativacao do token
			HttpEntity<String> entity = new HttpEntity<String>("");
			
			// Efetua chamada
			ResponseEntity<String> response = 
					restTemplate.exchange(url, HttpMethod.GET,entity,String.class);
			
			assincronoDTO.setExecuteCallSucess(true);
			
			if(response.getStatusCode().is2xxSuccessful()) {
				System.out.println("Solicitação sucesso async test: {} "+response.getBody());	
			}
			
			assincronoDTO.setResposta(response.getBody());
			
		} catch (Exception e) {
			assincronoDTO.setExecuteCallSucess(false);
			throw new TechnicalException(e);
		}finally {
			System.out.println("FIM async test " +assincronoDTO.toString());
		}
	}	
	
}