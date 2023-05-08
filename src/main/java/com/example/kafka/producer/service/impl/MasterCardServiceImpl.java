package com.example.kafka.producer.service.impl;

import java.net.ConnectException;
import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
import com.example.kafka.producer.service.CardService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import com.mastercard.developer.encryption.FieldLevelEncryption;
import com.mastercard.developer.encryption.FieldLevelEncryptionConfig;
import com.mastercard.developer.encryption.FieldLevelEncryptionConfig.FieldValueEncoding;
import com.mastercard.developer.encryption.FieldLevelEncryptionConfigBuilder;
import com.mastercard.developer.encryption.JsonParser;
import com.mastercard.developer.json.JacksonJsonEngine;
import com.mastercard.developer.utils.EncryptionUtils;

import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;

@Service
public class MasterCardServiceImpl implements CardService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5865501114268560700L;
	private static final Logger log = LogManager.getLogger(MasterCardServiceImpl.class);
	
	// URLS
	private static final String URL_SEARCH = "https://sandbox.api.mastercard.com/mdes/csapi/v2/search";
	private static final String URL_ACTIVE = "https://sandbox.api.mastercard.com/mdes/csapi/v2/token/activate";
	
	private int tentativa=0;
	
	// 
	ObjectMapper objectMapper;
	RestTemplate restTemplate;
	
	@Autowired
	private RetryRegistry registry;
	
	@Autowired
	public MasterCardServiceImpl(ObjectMapper objectMapper, RestTemplate restTemplate) {
		super();
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplate;
		
		 tentativa = 0;
//		registry
//        .retry("masterCircuit")
//        .getEventPublisher()
//        .onRetry(System.out::println);
	}


	@Override
	@io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name="masterCircuit")
	@Retry(name="masterCircuit")
	@Timed("metrica_ativarToken")
	public void ativarToken(String accountPan, String correlationId) throws BaseException {
		
		//Long a = Long.parseLong("a");
		//if(true) throw new  TechnicalException(new ConnectException("TESTE"));
		
		if(correlationId.equals("111")) throw new TechnicalException(new ConnectException("Teste Para retry/dlq"));
		
		// valida campos de entrada
		validaCamposAtivacaoToken(accountPan, correlationId);
		
		
		// Pesquisa contas e token
		Optional<List<Account>> opAccount = this.listaContaToken(accountPan);
		
		// Verifica se existe informações, senao lança exeção
		List<Account> listAccount = opAccount
				.orElseThrow(() -> new BusinessException("Pan informado não localizado no retorno da masterCard PAN {} ", accountPan));
		
		// localiza o token baseado na listagem retornada
		String tokenUniqueReference = findTokenUniqueReference(listAccount, correlationId, accountPan);
		
		// Efetiva ativacao na master card
		this.efetivaAtivacaoMastercard(tokenUniqueReference);
	}


	
//	@Retry(name="masterCircuit")
	public Optional<List<Account>> listaContaToken(String accountPan) throws BaseException {
		List<Account> listAccount = null;
		
		System.out.println("Tentativa = " + tentativa++);
		
//		if(true) throw new  TechnicalException(new ConnectException("TESTE"));
		
		try {
			// Gerando payload
			HttpEntity<String> entity = new HttpEntity<String>(gerarBodyPesquisaTokens(accountPan));
/*
			RestTemplate  t = new RestTemplate();
			
			ResponseEntity<Object> response2 = 
					t.exchange("https://teste123.free.beeceptor.com/teste502", 
							HttpMethod.GET, entity, Object.class);
			
			System.out.println(response2.getStatusCodeValue());
			*/
			
			// Efetua chamada
			ResponseEntity<String> response = restTemplate.exchange("https://sandbox.api.mastercard.com/mdes/csapi/v2/search",
													 HttpMethod.POST, 
													 entity, 
													 String.class);

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
	
	
	private String findTokenUniqueReference(final List<Account> listAccount , final String correlationId, String accountPan) throws BaseException {
		Optional<Token> opTokenFinded = Optional.ofNullable(null);
		
		for(Account account: listAccount) {
			// Localiza o token que tenha o correlation ID
			opTokenFinded  = 
							account.getTokens().token.stream()
							.filter(acc -> acc.getCorrelationId()!= null && acc.getCorrelationId().equals(correlationId))
							.findFirst();
		}
		
		//TODO VERIFICAR SE O TOKEN TEM QUE ESTAR EM ALGUMA CONDICAO
		Token token = opTokenFinded.orElseThrow(() -> new BusinessException("CORRELATIONID:" + correlationId+ " - não localizado na MasterCard para o AccounPan:" + accountPan));
		
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
	 
	 


	//@Async("threadPoolTaskExecutor")
	public CompletableFuture<Integer> findUser(Integer numero)  {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(numero == 20) {
			throw new BusinessException("ERRO BUSS");
		}
		
		System.out.println("EXECUTOU " +numero);
		
		return CompletableFuture.completedFuture(numero);
	}
	
	
	
	
	
	public String listEligibleTokenRequestors(String accountRanger) throws BaseException {
		
		String body = String.format("{\r\n"
				+ "  \"requestId\": \"%s\",\r\n"
				+ "  \"accountRanges\": [\r\n"
				+ "    \"5123456789\"\r\n"
				+ "  ],\r\n"
				+ "  \"supportsTokenConnect\": true\r\n"
				+ "}"	, accountRanger);
		
			// Gerando payload
			HttpEntity<String> entity = new HttpEntity<String>(body);
			
			// Efetua chamada
			ResponseEntity<String> response = restTemplate.exchange("https://sandbox.api.mastercard.com/mdes/connect/1/0/getEligibleTokenRequestors",
													 HttpMethod.POST, 
													 entity, 
													 String.class);

			return response.getBody();
		
	}


	@Override
	public String findTokenRequestorAssetInfo(String assetId) throws BaseException {
			
			// Efetua chamada
			ResponseEntity<String> response = restTemplate.getForEntity("https://sandbox.api.mastercard.com/mdes/issuer-assets/1/0/asset/tokenrequestor/"+assetId,
					 String.class); 
			
			
			return response.getBody();
	}
	
	@Override
	public String pushTeste() throws Exception {
		// Selecting a JSON Engine
		JsonParser.withJsonEngine(new JacksonJsonEngine());
		
		// Loading the Encryption Certificate
		Certificate encryptionCertificate = EncryptionUtils.loadEncryptionCertificate("./token-connect-request-encryption-sandbox.crt");

		// Carregando perante o payload oque vai ser criptografado
		FieldLevelEncryptionConfig config  = FieldLevelEncryptionConfigBuilder.aFieldLevelEncryptionConfig()
		        .withEncryptionPath("$.pushFundingAccounts.encryptedPayload.encryptedData", "$.pushFundingAccounts.encryptedPayload")
		        .withEncryptionCertificate(encryptionCertificate)
		        .withOaepPaddingDigestAlgorithm("SHA-512")
		        .withEncryptedValueFieldName("encryptedData")
		        .withEncryptedKeyFieldName("encryptedKey")
		        .withIvFieldName("iv")
		        .withOaepPaddingDigestAlgorithmFieldName("oaepHashingAlgorithm")
		        .withEncryptionCertificateFingerprintFieldName("publicKeyFingerprint")
		        .withFieldValueEncoding(FieldValueEncoding.HEX)
		        .build();
		
		
		String requestPayload = this.getRequestPushPayload2();
		String encryptedRequestPayload = FieldLevelEncryption.encryptPayload(requestPayload, config);
		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(encryptedRequestPayload)));
		
		// fazendo a requisicao
		

		// Efetua chamada
		HttpEntity<String> entity = new HttpEntity<String>(encryptedRequestPayload);
		ResponseEntity<String> response = restTemplate.exchange("https://sandbox.api.mastercard.com/mdes/connect/1/0/pushMultipleAccounts",
												 HttpMethod.POST, 
												 entity, 
												 String.class);
		return response.getBody();
	}

	private String getRequestPushPayload() {
		return "{\r\n"
				+ "  \"requestId\": \"123456\",\r\n"
				+ "  \"pushFundingAccounts\": {\r\n"
				+ "    \"encryptedPayload\": {\r\n"
				+ "      \"encryptedData\": [\r\n"
				+ "        {\r\n"
				+ "          \"pushAccountId\": \"896e845c-828f-11eb-8dcd-0242ac130003\",\r\n"
				+ "          \"fundingAccountData\": {\r\n"
				+ "            \"cardAccountData\": {\r\n"
				+ "              \"accountNumber\": \"5123456789012345\",\r\n"
				+ "              \"expiryMonth\": \"12\",\r\n"
				+ "              \"expiryYear\": \"21\"\r\n"
				+ "            },\r\n"
				+ "            \"financialAccountData\": {\r\n"
				+ "              \"financialAccountId\": \"5412345678901234\",\r\n"
				+ "              \"interbankCardAssociationId\": \"1234\",\r\n"
				+ "              \"countryCode\": \"GBR\"\r\n"
				+ "            },\r\n"
				+ "            \"accountHolderData\": {\r\n"
				+ "              \"accountHolderName\": \"John Doe\",\r\n"
				+ "              \"accountHolderAddress\": {\r\n"
				+ "                \"line1\": \"100 1st Street\",\r\n"
				+ "                \"line2\": \"Apt. 4B\",\r\n"
				+ "                \"city\": \"St. Louis\",\r\n"
				+ "                \"countrySubdivision\": \"MO\",\r\n"
				+ "                \"postalCode\": \"61000\",\r\n"
				+ "                \"country\": \"USA\"\r\n"
				+ "              },\r\n"
				+ "              \"accountHolderEmailAddress\": \"john.doe@anymail.com\",\r\n"
				+ "              \"accountHolderMobilePhoneNumber\": {\r\n"
				+ "                \"countryDialInCode\": \"1\",\r\n"
				+ "                \"phoneNumber\": \"7181234567\"\r\n"
				+ "              }\r\n"
				+ "            }\r\n"
//				+ "            \",dataValidUntilTimestamp\": \"2022-05-06T12:09:56.123-07:00\"\r\n"
				+ "          }\r\n"
				+ "        }\r\n"
				+ "      ],\r\n"
				+ "      \"publicKeyFingerprint\": \"8fc11150a7508f14baca07285703392a399cc57c\",\r\n"
				+ "      \"encryptedKey\": \"s\",\r\n"
				+ "      \"oaepHashingAlgorithm\": \"SHA512\",\r\n"
				+ "      \"iv\": \"1b9396c98ab2bfd195de661d70905a45\"\r\n"
				+ "    }\r\n"
				+ "  },\r\n"
				+ "  \"tokenRequestorId\": \"50123197928\",\r\n"
				+ "  \"signatureData\": {\r\n"
				+ "    \"callbackURL\": \"http://www.tokenIssuer1.com/pushtoken\",\r\n"
				+ "    \"completeIssuerAppActivation\": false,\r\n"
				+ "    \"completeWebsiteActivation\": false,\r\n"
				+ "    \"accountHolderDataSupplied\": false,\r\n"
				+ "    \"locale\": \"en_US\"\r\n"
				+ "  },\r\n"
				+ "  \"requestIssuerInitiatedDigitizationData\": false,\r\n"
				+ "  \"pushAccountReceiptsValidityPeriod\": 15\r\n"
				+ "}";
	}
	
	// Formatar a data no formato ISO 8601
    DateTimeFormatter formatterIso8601 = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	
    private String getRequestPushPayload2() {
    	String expireEncrypt = LocalDateTime.now().atZone(ZoneId.of("GMT")).plusMinutes(30).format(formatterIso8601);
		System.out.println(expireEncrypt);
		
		
		String r =  "{\r\n"
				+ "  \"requestId\": \"123456\",\r\n"
				+ "  \"pushFundingAccounts\": {\r\n"
				+ "    \"encryptedPayload\": {\r\n"
				+ "      \"encryptedData\": [\r\n"

				
				+ "        {\r\n"
				+ "          \"pushAccountId\": \"1\",\r\n"
				+ "          \"fundingAccountData\": {\r\n"
				+ "            \"cardAccountData\": {\r\n"
				+ "              \"accountNumber\": \"5123456789012345\"\r\n"
				+ "            },\r\n"
				+ "            \"accountHolderData\": {\r\n"
				+ "              \"accountHolderName\": \"John Doe\",\r\n"
				+ "              \"accountHolderEmailAddress\": \"john.doe@anymail.com\"\r\n"
				+ "            }"
//				+ ",\r\n"
//				+ "            \"dataValidUntilTimestamp\":  \""+expireEncrypt+"\"\r\n"
				+ "          }\r\n"
				+ "        }\r\n"
				
				
				+ "       , {\r\n"
				+ "          \"pushAccountId\": \"2\",\r\n"
				+ "          \"fundingAccountData\": {\r\n"
				+ "            \"cardAccountData\": {\r\n"
				+ "              \"accountNumber\": \"5123456789065478\"\r\n"
				+ "            },\r\n"
				+ "            \"accountHolderData\": {\r\n"
				+ "              \"accountHolderName\": \"John Doe\",\r\n"
				+ "              \"accountHolderEmailAddress\": \"john.doe@anymail.com\"\r\n"
				+ "            }"
//				+ ",\r\n"
//				+ "            \"dataValidUntilTimestamp\":  \""+expireEncrypt+"\"\r\n"
				+ "          }\r\n"
				+ "        }\r\n"
				
				
				
				
				
				
				+ "      ]\r\n"
				+ "    }\r\n"
				+ "  },\r\n"
				+ "  \"tokenRequestorId\": \"50123456790\",\r\n"
				+ "  \"signatureData\": {\r\n"
				+ "    \"callbackURL\": \"http://www.tokenIssuer1.com/pushtoken\",\r\n"
				+ "    \"completeIssuerAppActivation\": false,\r\n"
				+ "    \"completeWebsiteActivation\": false,\r\n"
				+ "    \"accountHolderDataSupplied\": false\r\n"
				+ "  },\r\n"
				+ "  \"requestIssuerInitiatedDigitizationData\": false\r\n"
				+ "}";
		
		System.out.println(r);
		
		
		return r;
	}
	
	
	private String getRequestPushPayload3() {
		return "[\r\n"
				+ "  {\r\n"
				+ "    \"pushAccountId\": \"37fa3300-828f-11eb-8dcd-0242ac130003\",\r\n"
				+ "    \"fundingAccountData\": {\r\n"
				+ "      \"cardAccountData\": {\r\n"
				+ "        \"accountNumber\": \"5123456789012346\",\r\n"
				+ "        \"expiryMonth\": \"12\",\r\n"
				+ "        \"expiryYear\": \"24\",\r\n"
				+ "        \"securityCode\": \"123\"\r\n"
				+ "      },\r\n"
				+ "      \"accountHolderData\": {\r\n"
				+ "        \"accountHolderName\": \"robbin\",\r\n"
				+ "        \"accountHolderEmailAddress\": \"mrobbin@mc.com\",\r\n"
				+ "        \"accountHolderMobilePhoneNumber\": {\r\n"
				+ "          \"countryDialInCode\": \"001\",\r\n"
				+ "          \"phoneNumber\": \"0019898522121212\"\r\n"
				+ "        },\r\n"
				+ "        \"accountHolderAddress\": {\r\n"
				+ "          \"line1\": \"street1\",\r\n"
				+ "          \"line2\": \"street2\",\r\n"
				+ "          \"city\": \"california\",\r\n"
				+ "          \"countrySubdivision\": \"USA\",\r\n"
				+ "          \"postalCode\": \"001234\",\r\n"
				+ "          \"country\": \"USA\"\r\n"
				+ "        }\r\n"
				+ "      }\r\n"
				+ "    }\r\n"
				+ "  },\r\n"
				+ "  {\r\n"
				+ "    \"pushAccountId\": \"896e845c-828f-11eb-8dcd-0242ac130003\",\r\n"
				+ "    \"fundingAccountData\": {\r\n"
				+ "      \"financialAccountData\": {\r\n"
				+ "        \"financialAccountId\": \"5123456789012345\",\r\n"
				+ "        \"interbankCardAssociationId\": \"123456\",\r\n"
				+ "        \"countryCode\": \"USA\"\r\n"
				+ "      }\r\n"
				+ "    }\r\n"
				+ "  }\r\n"
				+ "]";
	}
	

	
	
}
