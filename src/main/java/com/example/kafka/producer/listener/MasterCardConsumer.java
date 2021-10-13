package com.example.kafka.producer.listener;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.example.kafka.producer.enums.TopicEnum;
import com.example.kafka.producer.exception.TechnicalException;
import com.example.kafka.producer.model.MasterCardDTO;
import com.example.kafka.producer.service.CardService;
import com.example.kafka.producer.utils.LogSplunk;
import com.example.kafka.producer.utils.Splunk;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;

@Component
public class MasterCardConsumer {

	//private static final Logger log = LoggerFactory.getLogger(MasterCardController.class);

	@Autowired
	private CardService cardService;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private KafkaTemplate<String, Map<String,Object>> kafkaTemplate;
	
	/*@Autowired
	private KafkaTemplate<String, String> kafkaTemplateRetry;*/
	
	@Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
	
	@Value("${time.circuite.open}")
	private int timeCircuiteOpen;
	
	@Value("${master.walletsToProcess}")
	private List<String> carteirasPermitidasAtivacao;
	
	private final CircuitBreaker circuitBreaker;
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	public MasterCardConsumer(CircuitBreakerRegistry circuitBreakerRegistry) {
		this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("masterCircuit");
		this.circuitBreaker.getEventPublisher().onStateTransition(this::onStateChange);
	}
	
	@KafkaListener(id = "MASTER_CARD_TOKEN_ACTIVATION", topics = "MASTER_CARD_TOKEN_ACTIVATION", groupId = "consumer_group1")
	public void messageListener(ConsumerRecord<String, String> consumerRecord, /*@Payload String payload,*/ Acknowledgment ack) throws Exception {
		System.out.println("\n\n");
		System.out.println("+++++++++++++++++++++++  CONSUMINDO ORIGINAL");
		System.out.println("\n\n");

		processMsg(consumerRecord, ack);
	}

	//@Header("dataHoraProximaTentativa") long dataHoraProximaTentativa
	@KafkaListener(id = "MASTER_CARD_TOKEN_RETRY1", topics = "MASTER_CARD_TOKEN_ACTIVATION_PRIMEIRO_RETRY", groupId = "consumer_group1")
	public void messageListenerRetry(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack ,  @Header(KafkaHeaders.RECEIVED_TIMESTAMP)  long ts ) throws Exception {
		System.out.println("\n\n");
		System.out.println("CONSUMINDO ************** RETRY 1");
		
		if(System.currentTimeMillis() >= ts + (1000 * 10) ) {
			System.out.println(" PROCESSANDO O RETRY 1 AGORA ");
			processMsg(consumerRecord, ack);	
		} else {
			System.out.println("Falta RETRY 1: " + (ts + (1000 * 10) - System.currentTimeMillis() ));
			ack.nack( (1000 * 5));
		}
	}
	
	@KafkaListener(id = "MASTER_CARD_TOKEN_RETRY2", topics = "MASTER_CARD_TOKEN_ACTIVATION_SEGUNDO_RETRY", groupId = "consumer_group1")
	public void messageListenerRetry2(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack, @org.springframework.messaging.handler.annotation.Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts ) throws Exception {
		System.out.println("\n\n");
		System.out.println("CONSUMINDO ************** RETRY 2");

		//LogSplunk.info(Splunk.builder().key("RETRY 2").customMessage("RETRY 2").build());
		
		if(System.currentTimeMillis() >= ts + (1000 * 40)) {
			System.out.println(" PROCESSANDO O RETRY 2 AGORA ");
			processMsg(consumerRecord, ack);
		} else {
			System.out.println("Falta RETRY 2 : " + (ts + (1000 * 40) - System.currentTimeMillis() ));
			//ack.nack(ts + (1000 * 10) - System.currentTimeMillis());
			ack.nack((1000 * 10));
		}
	}
	
	
//	private long getNextExecutionByHeader(Headers headers, String headerName) {
//		long value = 0L;
//		
//		try {
//			for (Header header : headers) {
//	            if (header.key().equals(headerName)) {
//	               String s = new String(header.value(), StandardCharsets.UTF_8);
//	               System.out.println(s);
//	               value =  Long.valueOf(s);
//	               break;
//	            }
//			 }
//		} catch (Exception e) {
//			value = 0L;
//		}
//
//		
//		return value;
//	}
	
	private void processMsg(ConsumerRecord<String, String> consumerRecord,  Acknowledgment ack) throws Exception {
		long startTime = System.currentTimeMillis();
		
		System.out.println("*** TOPIC NAME ***");
		System.out.println(consumerRecord.topic());
		System.out.println("***  ***");
		
		MasterCardDTO masterCard = null;
		String payload = consumerRecord.value();
		
		consumerRecord.offset();
		
		LogSplunk.initLog(consumerRecord.topic() + consumerRecord.offset() + consumerRecord.partition(),
				consumerRecord.topic() + consumerRecord.offset() + consumerRecord.partition() + "_" + UUID.randomUUID(),
				consumerRecord.topic());
		
		try {
			// Tenta transformar o payload em Objeto
			masterCard = mapper.treeToValue(mapper.readTree(payload).get("payload"), MasterCardDTO.class);
			
			
			LogSplunk.info(Splunk.builder().key("token.ativacao2")
					 	   .customMessage("Iniciando ativacao token")
					       .payload(masterCard).build());
			
			// Filtra eventos permitidos
			if (isEventoPermitido(masterCard)) {

				// Verifica se o circuito está aberto
				if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
					// Stop consumo do kafka pelo tempo determinado
					ack.nack(timeCircuiteOpen * 1000);
					return;
				}
				
				// Solicita ativacao do Token do cartao
				cardService.ativarToken(masterCard.getNumeroCartao(), masterCard.getCorrelationId());

				// Send log Sucesso
				LogSplunk.info(Splunk.builder().key("solicita.ativacao.sucesso")
						.customMessage("Token ativado com sucesso - CorrelationId: "+ masterCard.getCorrelationId())
						.payload(masterCard).build());
			}
			
		} catch (Exception e) {
			// Trata-se de um erro recuperável?
			final boolean recuperavel = isRecuperavel(e);
			
			// Caso erro não seja recuperavel
			if (!recuperavel) {
				
				LogSplunk.error(Splunk.builder().key("token.ativacao.erro.dlq")
						.customMessage(e.getMessage())
						.payload(payload).exception(e).build());
								
				// Cria msg de dlq
				Map<String, Object> deadLeatterMsg = new HashMap<String, Object>();
				deadLeatterMsg.put("payload", payload);
				deadLeatterMsg.put("customMsg", e.getMessage());
				
				// Envia para fila de DeadLeatter
				kafkaTemplate.send(TopicEnum.DLQ.getTopicName(),
						masterCard != null ? masterCard.getNumeroCartao() : payload, 
								deadLeatterMsg).get();
				
			} else {
				// Log Erro Recuperavel
				LogSplunk.error(Splunk.builder().key("token.ativacao.erro.recuperavel")
						.customMessage(e.getMessage())
						.payload(payload).exception(e).build());
				
				// Decide o topico de retry ou dlq a ser enviado
				this.decideRetryOrDlq(masterCard, e);
				
			}
		} finally {
			// Commita leitura move o offSet
			ack.acknowledge();
		}
		
		LogSplunk.finalizeLog();
		System.out.println("TEMPO DECORRIDO - " + (System.currentTimeMillis() - startTime));
	}
	
	private void decideRetryOrDlq(MasterCardDTO masterCard, Exception e) throws InterruptedException, ExecutionException, JsonMappingException, JsonProcessingException {
		// Atribui o numero da proxima
		masterCard.setNumeroTentativa(masterCard.getNumeroTentativa() + 1);
		
		// Recupera o topico destino e o tempo de espera
		TopicEnum topicoDestinoInfo = TopicEnum.getRetryTopicFromAttempt(masterCard.getNumeroTentativa());
		
		// Calcula o tempo de espera para a proxima execucao, adiciona os segundos de espera
		masterCard.setDataHoraProximaTentativa(LocalDateTime.now().plusSeconds(topicoDestinoInfo.getSecondsAwait()));
		
		Map<String, Object> retryOrDLQrMsg = new HashMap<String, Object>();
		retryOrDLQrMsg.put("payload", masterCard);
		retryOrDLQrMsg.put("customMsg", e.getMessage());
		
		
		/*JsonNode jsonNode = mapper.readTree(mapper.valueToTree(retryOrDLQrMsg));*/
		
		// Monta informações do topico e destino
		Message<Map<String, Object>> record = MessageBuilder.
	             withPayload(retryOrDLQrMsg).
	             setHeader(KafkaHeaders.MESSAGE_KEY, masterCard.getNumeroCartao()).
	             setHeader(KafkaHeaders.TOPIC, topicoDestinoInfo.getTopicName()).
//	             setHeader(KafkaHeaders.TIMESTAMP, Instant.now().toEpochMilli()).
//	             setHeader("dataHoraProximaTentativa", masterCard.getDataHoraProximaTentativa().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).
	             build();
		
		/*if(!topicoDestinoInfo.equals(TopicEnum.DLQ)) {
			record.getHeaders().put("dataHoraProximaTentativa", masterCard.getDataHoraProximaTentativa().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}*/
		
		// Envia para o topico de destino
		kafkaTemplate.send(record).get();
		
		//kafkaTemplate.send(topicoDestinoInfo.getTopicName(), masterCard.getNumeroCartao(), retryOrDLQrMsg).get();
	}

//	private void decideRetryOrDlq(ConsumerRecord<String, ?> consumerRecord) {
//		// Envia para fila de DeadLeatter
//		try{
//			String payload = (String) consumerRecord.value().toString();
//			kafkaTemplateRetry.send("MASTER_CARD_TOKEN_ACTIVATION-RETRY-1", consumerRecord.key(), payload);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	@RetryableTopic(attempts = "2", backoff = @Backoff(delay = 10000, multiplier = 2.0), autoCreateTopics = "true",
//			topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
//	@KafkaListener(topics = "MASTER_CARD_TOKEN_ACTIVATION-RETRY-1", groupId = "consumer_group1")
//	public void listen(ConsumerRecord<String, ?> consumerRecord, Acknowledgment ack) {
//		System.out.println("**** RETRY ****");
//		System.out.println(consumerRecord.topic());
//		System.out.println(consumerRecord.value());
//		
//		MasterCardDTO masterCard = null;
//		String payload = (String) consumerRecord.value().toString();
//		
//		//char[] escaped = JsonStringEncoder.getInstance().quoteAsString(payload);
//		String escaped = JSONValue.escape(payload);
//		// Tenta transformar o payload em Objeto
//		try {
//			
//			masterCard = mapper.readValue(escaped.toString(), MasterCardDTO.class);
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//			
//		if(masterCard.getIdCartao().equals("e6271375-0471-450b-803a-d0a22b937572")) {
//			ack.acknowledge(); 
//		}
//
//		
//		
//		//log.info(consumerRecord.value() + " from " + topic);
//		throw new RuntimeException("test");
//	}
	
	

	private boolean isEventoPermitido(MasterCardDTO masterCard) {
		boolean carteiraPermitida = false;
		
		// Verifica se esta diferente de null e se possui bandeira MasterCard
		if(masterCard.getCodigoBandeira() != null && "M".equals(masterCard.getCodigoBandeira().toUpperCase())) {
			if(carteirasPermitidasAtivacao.contains(masterCard.getNumeroCarteira())) {
				carteiraPermitida = true;
			}
		}
		
		
		if(!carteiraPermitida) {
			// Send log inicio de processamento msg
			LogSplunk.info(Splunk.builder().key("token.ativacao.carteira.desprezada")
					.customMessage("Carteiras Permitidas p/ ativacao: " + carteirasPermitidasAtivacao.toString() + " da bandeira MasterCard")
					.payload(masterCard)
					.build());
		}
		
		
		return carteiraPermitida;
	}

	/**
	 * Verifica se a excecao é recuperavel ou nao ou se ainda se trata de uma excecao fatal que irá para 
	 * o listener do Kafka
	 * */
	private boolean isRecuperavel(Exception e) {
		boolean isRecuperavel = false;
		
		if (e instanceof TechnicalException) {
			// Problema com conexao ou timeout
			if (e.getCause() instanceof ResourceAccessException || e.getCause() instanceof UnknownHostException
					|| e.getCause() instanceof ConnectException
					
					|| e.getCause() instanceof ConfigurationPropertiesBindException
					|| e.getCause() instanceof IllegalStateException
					) {
				isRecuperavel = true;
			
			} else if (e.getCause() instanceof HttpClientErrorException || e.getCause() instanceof HttpServerErrorException) {
				HttpStatus statusCode = null;
				if(e.getCause() instanceof HttpClientErrorException) {
					HttpClientErrorException httpError = (HttpClientErrorException) e.getCause();
					statusCode = httpError.getStatusCode();
					
					// ERRO NO CERTIFICADO, O CONSUMIDOR DEVE SER PARADO E CORRIGIDO
					if(httpError.getMessage().contains("INVALID_OAUTH_SBS")) {
						LogSplunk.error(Splunk.builder().key("token.ativacao.fatal").customMessage("Erro Fatal - Consumo do kafka parado").exception(e).build());
						// Para o listener do kafka
						this.stopKafkaListener();
						isRecuperavel = true;
						return isRecuperavel;
					}
					
				} else {
					HttpServerErrorException httpError = (HttpServerErrorException) e.getCause();
					statusCode = httpError.getStatusCode();
				}
				
				// Erro ao chamar do nosso cliente (Itau) com a MasterCard
				if (statusCode.is4xxClientError()) {
					if(statusCode == HttpStatus.TOO_MANY_REQUESTS ||
						statusCode == 	HttpStatus.REQUEST_TIMEOUT) {
						isRecuperavel = true;	
					}
				// Erro de processamento na masterCard, masterCard Mandou um erro
				} else if (statusCode.is5xxServerError()) {
					isRecuperavel = true;
				}
			}
		}
		
		return isRecuperavel;
	}
	
	
	/**
	 *  Para o consumo do Kafka por algum erro grave e reporta a parada
	 **/
	private void stopKafkaListener() {
		kafkaListenerEndpointRegistry.getListenerContainer("MASTER_CARD_TOKEN_ACTIVATION").stop();
	}

	/**
	 * Listener contendo o status do circuiteBreak
	 * */	
	private void onStateChange(CircuitBreakerOnStateTransitionEvent event) {
		CircuitBreaker.State state =  event.getStateTransition().getToState();
		switch (state) {
			case OPEN:
				LogSplunk.info(Splunk.builder().key("token.ativacao.circuit.open").customMessage("CircuitBreak Aberto - Tempo "+ timeCircuiteOpen +"s").build());
				break;
			case HALF_OPEN:
				LogSplunk.info(Splunk.builder().key("token.ativacao.circuit.half.open").customMessage("CircuitBreak Meio Aberto").build());
			break;
			case CLOSED :
				LogSplunk.info(Splunk.builder().key("token.ativacao.circuit.close").customMessage("CircuitBreak Fechado").build());
			break;
		default:
			break;
		}
	}

	
	public CountDownLatch getLatch() {
        return latch;
    }
}