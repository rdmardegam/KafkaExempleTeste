//package com.example.kafka.producer.listener;
//
//import java.util.concurrent.TimeUnit;
//
//import javax.management.RuntimeErrorException;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.listener.ContainerProperties.AckMode;
//import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//import org.springframework.util.backoff.FixedBackOff;
//import org.springframework.web.client.HttpClientErrorException;
//
//import com.example.kafka.producer.controller.MasterCardController;
//import com.example.kafka.producer.exception.TechnicalException;
//import com.example.kafka.producer.model.MasterCardDTO;
//import com.example.kafka.producer.service.CardService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import io.github.resilience4j.circuitbreaker.CircuitBreaker;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
//import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
//
//@Component
//public class MasterCardConsumer_old {
//
//	private static final Logger log = LoggerFactory.getLogger(MasterCardController.class);
//
//	@Autowired
//	private CardService cardService;
//
//	@Autowired
//	ObjectMapper mapper;
//	
//	@Autowired
//	private KafkaTemplate<String, MasterCardDTO> kafkaTemplate;
//	
//	private final CircuitBreaker circuitBreaker;
//	
//	public MasterCardConsumer_old(CircuitBreakerRegistry circuitBreakerRegistry) {
//		/*CircuitBreakerConfig config = CircuitBreakerConfig
//				.custom()
//				.slidingWindowType(SlidingWindowType.COUNT_BASED)
//				.slidingWindowSize(1)
//				.failureRateThreshold(1.0f)
//				.waitDurationInOpenState(Duration.ofSeconds(10))
//				.permittedNumberOfCallsInHalfOpenState(4)
//				.build();
//		CircuitBreakerRegistry.of(config);*/
//		
//		//this.circuitBreaker = CircuitBreakerRegistry.of(config).circuitBreaker("masterCircuit");
//		this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("masterCircuit");
//		this.circuitBreaker.getEventPublisher().onStateTransition(this::onStateChange);
//	}
//	
//	
//	private static int x=0;
//	
//	@KafkaListener(topics = "MASTER_CARD_TOKEN_ACTIVATION", groupId = "consumer_group1")
//	//public void messageListener(ConsumerRecord consumerRecord, @Payload MasterCardDTO event, Acknowledgment ack)
//	public void messageListener(ConsumerRecord consumerRecord, @Payload MasterCardDTO event, Acknowledgment ack) throws Exception{
//		
//		log(consumerRecord, event);
//		
//		if(circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
//			// Stop consume 30 segundos
//			// ack.nack(5000);
// 			   System.out.println("******     BREAK         ******* "+ event.getUuid());
//			   return;
//		}
//		
//		// Solicita ativacao do Token do cartao
//		try {			
//			
//			cardService.ativarToken(event.getAccountPan(), event.getCorrelationId());
//			ack.acknowledge();
//			System.out.println("******     SUCESSSOOOOO         ******* " + event.getUuid());
//		
//		} catch (Exception e) {
//			log.error(e.getMessage());
//			log.error("ERROR NA MSG: {}" + consumerRecord.value());
//			ack.nack(0);
//			
//			 // Trata-se de um erro recuperável?
//		    final boolean recuperavel = isRecuperavel(e);
//			
//		    // Caso erro não seja recuperavel
//		    if(!recuperavel) {
//		    	// Envia para fila de DeadLeatter
//		    	kafkaTemplate.send("MASTER_CARD_TOKEN_ACTIVATION-DLQ", event.getAccountPan(), event).get(5, TimeUnit.SECONDS);
//		    	ack.acknowledge();
//		    }
//		}
//	}
//
//	
//	
//	
//	private boolean isRecuperavel(Exception e) {
//		boolean isRecuperavel = false;
//		
//		if (e instanceof TechnicalException) {
//			
//			// Verifica se é um erro de conexao
//			if (e.getCause() instanceof HttpClientErrorException) {
//				HttpClientErrorException httpError = (HttpClientErrorException) e.getCause();
//
//				// Erro ao chamar do nosso cliente (Itau) com a MasterCard
//				if (httpError.getStatusCode().is4xxClientError()) {
//
//					// Erro ao de processamento na masterCard, masterCard Mandou um erro
//				} else if (httpError.getStatusCode().is5xxServerError()) {
//					isRecuperavel = true;
//				}
//
//				// Caso contrario o erro deve ser de certificado ou ligado ao Header ou Chave
//				// com a MasterCard
//			} else {
//				System.out.println("*********-----**** ERRO GRAVISSIMO");	
//			}
//		
//		}
//		
//		return isRecuperavel;
//	}
//	
//	
//	private void logTechnicalExeption(Exception e) {
//
//		// Verifica se é um erro de conexao
//		if (e.getCause() instanceof HttpClientErrorException) {
//			HttpClientErrorException httpError = (HttpClientErrorException) e.getCause();
//
//			// Erro ao chamar do nosso cliente (Itau) com a MasterCard
//			if (httpError.getStatusCode().is4xxClientError()) {
//
//				// Erro ao de processamento na masterCard, masterCard Mandou um erro
//			} else if (httpError.getStatusCode().is5xxServerError()) {
//
//			}
//
//			// Caso contrario o erro deve ser de certificado ou ligado ao Header ou Chave
//			// com a MasterCard
//		} else {
//
//		}
//	}
//
//	
//
//	private void onStateChange(CircuitBreakerOnStateTransitionEvent event) {
//		
//		CircuitBreaker.State state =  event.getStateTransition().getToState();
//		switch (state) {
//			case OPEN:
//				System.out.println("ABERTO ------------/-----");
//				break;
//			case CLOSED, HALF_OPEN:
//				System.out.println("FECHADO ----------x--x-----");
//			break;
//		}
//	}
//	
//	
////	
////	@Bean
////	public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
////			ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
////			ConsumerFactory<Object, Object> kafkaConsumerFactory) {
////
////		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
////		configurer.configure(factory, kafkaConsumerFactory);
////		
////		// Não falhar, caso ainda não existam os tópicos para consumo
////		factory.getContainerProperties().setMissingTopicsFatal(false);
////		
////		// Commit manual do offset
////		factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
////		
////		// Commits síncronos
////		factory.getContainerProperties().setSyncCommits(Boolean.TRUE);
////		//factory.getContainerProperties().setSyncCommits(false);
////		
////		
////		//factory.setErrorHandler(new SeekToCurrentErrorHandler(new FixedBackOff(0L, 20L)));
////		return factory; 
////	}
//	
//	
////	@Autowired
////	KafkaProperties properties;
////	
////	 @Bean
////	  public ConsumerFactory<String, String> consumerFactory() {
////	      return new DefaultKafkaConsumerFactory<>(
////	              properties.buildConsumerProperties());
////	  }
////
////	  @Bean
////	  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
////	      kafkaListenerContainerFactory() {
////
////	      ConcurrentKafkaListenerContainerFactory<String, String> listener = 
////	            new ConcurrentKafkaListenerContainerFactory<>();
////
////	      listener.setConsumerFactory(consumerFactory());
////
////	      // Não falhar, caso ainda não existam os tópicos para consumo
////	      listener.getContainerProperties()
////	          .setMissingTopicsFatal(false);
////
////	      // ### AQUI
////	      // Commit manual do offset
////	      listener.getContainerProperties().setAckMode(AckMode.MANUAL);
////
////	      // ### AQUI
////	      // Commits síncronos
////	      listener.getContainerProperties().setSyncCommits(Boolean.TRUE);
////
////	      return listener;
////	    }
//	
//
//	
//	
//	private void log(ConsumerRecord consumerRecord, MasterCardDTO event) {
//		System.out.println("******     INICIADO         ******* " + event.getUuid());
////		System.out.println("*** *****CONSUMINDOOO ******");
////		log.info("key: " + consumerRecord.key());
////		log.info("Headers: " + consumerRecord.headers());
////		log.info("Partion: " + consumerRecord.partition());
////		log.info("Payload: " + consumerRecord.value());
////		
////		//consumerRecord.
////		
////		System.out.println("******              *******");
////		
////		//MasterCardDTO event = mapper.readValue(consumerRecord.value().toString(), MasterCardDTO.class);
////		
////		Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
////		//log.info("Event = {}", gson.toJson(event.getPayload().getAccountPan()));
////		log.info("Event = {}", gson.toJson(event));
////		System.out.println("******              *******");
//	}
//}
