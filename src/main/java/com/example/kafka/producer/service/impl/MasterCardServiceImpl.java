package com.example.kafka.producer.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.kafka.producer.exception.BaseException;
import com.example.kafka.producer.exception.BusinessException;
import com.example.kafka.producer.exception.TechnicalException;
import com.example.kafka.producer.model.Account;
import com.example.kafka.producer.model.Token;
import com.example.kafka.producer.service.AuthorizationOauthHeaderService;
import com.example.kafka.producer.service.CardService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.resilience4j.retry.annotation.Retry;

@Service
public class MasterCardServiceImpl implements CardService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5865501114268560700L;
	private static final Logger log = LogManager.getLogger(MasterCardServiceImpl.class);
	
	// URLS
	private static final String URL_SEARCH = "/search";
	private static final String URL_ACTIVE = "/token/activate";
	
	// 
	ObjectMapper objectMapper;
	RestTemplate restTemplate;
	
	@Autowired
	public MasterCardServiceImpl(ObjectMapper objectMapper, RestTemplate restTemplate) {
		super();
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplate;
	}


	@Override
	@io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name="masterCircuit")
	public void ativarToken(String accountPan, String correlationId) throws BaseException {
		
		//Long a = Long.parseLong("a");
		//if(true) throw new  TechnicalException(new ConnectException("TESTE"));
		
		// valida campos de entrada
		validaCamposAtivacaoToken(accountPan, correlationId);
				
		// Pesquisa contas e token
		Optional<List<Account>> opAccount = this.listaContaToken(accountPan);
		
		// Verifica se existe informações, senao lança exeção
		List<Account> listAccount = opAccount
				.orElseThrow(() -> new BusinessException("Pan informado não localizado no retorno da masterCard PAN {} ", accountPan));
		
		// localiza o token baseado na listagem retornada
		String tokenUniqueReference = findTokenUniqueReference(listAccount, correlationId);
		
		// Efetiva ativacao na master card
		this.efetivaAtivacaoMastercard(tokenUniqueReference);
	}


	@Override
	@Retry(name="masterCircuit")
	public Optional<List<Account>> listaContaToken(String accountPan) throws BaseException {
		List<Account> listAccount = null;
		try {
			// Gerando payload
			HttpEntity<String> entity = new HttpEntity<String>(gerarBodyPesquisaTokens(accountPan));

			// Efetua chamada
			ResponseEntity<String> response = 
					restTemplate.exchange("/search", HttpMethod.POST, entity, String.class);

			// Recupera na linha do account o json
			JsonNode jsonNode = objectMapper.readTree(response.getBody());
			JsonNode result = jsonNode.get("SearchResponse").get("Accounts").get("Account");
			
			// Transforma no array
			listAccount = Arrays.asList(objectMapper.treeToValue(result, Account[].class));

		} catch (Exception e) {
			//e.printStackTrace();
			throw new TechnicalException(e);
		}

		return Optional.ofNullable(listAccount);
	}
	
	
	private String findTokenUniqueReference(final List<Account> listAccount , final String correlationId) throws BaseException {
		Optional<Token> opTokenFinded = Optional.ofNullable(null);
		
		for(Account account: listAccount) {
			// Localiza o token que tenha o correlation ID
			opTokenFinded  = 
							account.getTokens().token.stream()
							.filter(acc -> acc.getCorrelationId()!= null && acc.getCorrelationId().equals(correlationId))
							.findFirst();
		}
		
		//TODO VERIFICAR SE O TOKEN TEM QUE ESTAR EM ALGUMA CONDICAO
		Token token = opTokenFinded.orElseThrow(() -> new BusinessException("CORRELATIONID:" + correlationId+ " - não localizado na MasterCard, retornoMaster:" + listAccount.toString()));
		
		/*if(!token.getCurrentStatusCode().equals("U")) {
			throw new BusinessException("CORRELATIONID:" +correlationId+ " está com status diferente de 'U'; Status Atual:" +token.getCurrentStatusCode() + " - " + token.getCurrentStatusDescription() );
		}*/
		
		return token.getTokenUniqueReference();
	}
	
	@Retry(name="masterCircuit")
	private void efetivaAtivacaoMastercard(String tokenUniqueReference) throws BaseException {
		try {
			// Gerando payload da ativacao do token
			HttpEntity<String> entity = new HttpEntity<String>(gerarBodyAtivacaoToken(tokenUniqueReference));

			// Efetua chamada
			ResponseEntity<String> response = 
					restTemplate.exchange(URL_ACTIVE, HttpMethod.POST, entity, String.class);
			
			if(response.getStatusCode().is2xxSuccessful()) {
				log.info("Solicitação de ativação Master Token efetado com sucesso para TUR: {}",tokenUniqueReference);	
			}
			
		} catch (Exception e) {
			throw new TechnicalException(e);
		}
	}
	
	
	private void validaCamposAtivacaoToken(String accountPan, String correlationId) throws BaseException {
		// Verifica se os parametros foram enviados
		if (accountPan == null || (accountPan.length() < 9 || accountPan.length() > 19)) {
			throw new BusinessException("Campo accountPan deve ter 9 há 19 caracteres");
		}

		if (correlationId == null || ( correlationId.length() < 3 || correlationId.length() > 14) ) {
			throw new BusinessException("Campos correlationId deve ter de 3 há 14 caracteres");
		}
	}
	
	
	 public String gerarBodyPesquisaTokens(String accountPan) {
	   	 String requestJson = "{\r\n"
	   	 		+ " \"SearchRequest\": {\r\n"
	   	 		+ "   \"AccountPan\": \"%s\",\r\n"
	   	 		+ "   \"ExcludeDeletedIndicator\": \"false\",\r\n"
	   	 		+ "   \"AuditInfo\": {\r\n"
	   	 		+ "      \"UserId\": \"A1435477\",\r\n"
	   	 		+ "      \"UserName\": \"John Smith\",\r\n"
	   	 		+ "      \"Organization\": \"Any Bank\",\r\n"
	   	 		+ "      \"Phone\": \"5555551234\"\r\n"
	   	 		+ "   }\r\n"
	   	 		+ " }\r\n"
	   	 		+ "}";
	        
	   	 return String.format(requestJson, accountPan);
	   }

	 
	 private String gerarBodyAtivacaoToken(String tokenUniqueReference) {
	   	 String requestJson = "{\r\n"
	   	 		+ "		   \"TokenActivateRequest\": {\r\n"
	   	 		+ "		   \"TokenUniqueReference\": \"%s\",\r\n"
	   	 		+ "		   \"CommentText\": \"Confirmed cardholder identity.\",\r\n"
	   	 		+ "		   \"ReasonCode\": \"C\",\r\n"
	   	 		+ "		   \"AuditInfo\": {\r\n"
	   	 		+ "		      \"UserId\": \"A1435477\",\r\n"
	   	 		+ "		      \"UserName\": \"John Smith\",\r\n"
	   	 		+ "		      \"Organization\": \"Any Bank\",\r\n"
	   	 		+ "		      \"Phone\": \"555 1234\"\r\n"
	   	 		+ "		   }\r\n"
	   	 		+ "		 }\r\n"
	   	 		+ "		}";
	        
	   	 return String.format(requestJson, tokenUniqueReference);
	   }
}
