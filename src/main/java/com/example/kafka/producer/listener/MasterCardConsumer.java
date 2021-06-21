package com.example.kafka.producer.listener;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.example.kafka.producer.exception.TechnicalException;
import com.example.kafka.producer.model.MasterCardDTO;
import com.example.kafka.producer.service.CardService;
import com.example.kafka.producer.utils.LogSplunk;
import com.example.kafka.producer.utils.Splunk;
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
	private KafkaTemplate<String, Map<String,String>> kafkaTemplate;
	
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
	public void messageListener(ConsumerRecord<String, ?> consumerRecord, /*@Payload String payload,*/ Acknowledgment ack)throws Exception {
		long startTime = System.currentTimeMillis();
		
		// Send log inicio de processamento msg
		LogSplunk.info(Splunk.builder().key("token.ativacao")
				 .customMessage("Iniciando ativacao token")
				 .payload(consumerRecord.toString()).build());

		MasterCardDTO masterCard = null;
		
		String payload = (String) consumerRecord.value().toString();
		
		try {
			// Tenta transformar o payload em Objeto
			masterCard = mapper.readValue(payload, MasterCardDTO.class);

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
						.payload(payload).build());
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
				Map<String, String> deadLeatterMsg = new HashMap<String, String>();
				deadLeatterMsg.put("payload", payload);
				deadLeatterMsg.put("customMsg", e.getMessage());
				
				// Envia para fila de DeadLeatter
				kafkaTemplate.send("MASTER_CARD_TOKEN_ACTIVATION-DLQ",
						masterCard != null ? masterCard.getNumeroCartao() : payload, 
								deadLeatterMsg).get();

			} else {
				// Log Erro Recuperavel
				LogSplunk.error(Splunk.builder().key("token.ativacao.erro.recuperavel")
						.customMessage(e.getMessage())
						.payload(payload).exception(e).build());
				
				// Pausa o kafka por 0, apenas para não mover o offset da msg atual e reprocessar msg
				ack.nack(0);
				
				System.out.println("TEMPO DECORRIDO - " + (System.currentTimeMillis() - startTime));
				return;
			}
		}

		// Commita leitura move o offSet
		ack.acknowledge();

		System.out.println("TEMPO DECORRIDO - " + (System.currentTimeMillis() - startTime));
	}
	
	
	
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
					}
					
				} else {
					HttpServerErrorException httpError = (HttpServerErrorException) e.getCause();
					statusCode = httpError.getStatusCode();
				}
				
				// Erro ao chamar do nosso cliente (Itau) com a MasterCard
				if (statusCode.is4xxClientError()) {
					if(statusCode == HttpStatus.TOO_MANY_REQUESTS) {
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