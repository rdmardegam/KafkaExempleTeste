//package com.example.kafka.producer.listener;
//
//import java.net.ConnectException;
//import java.net.UnknownHostException;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.ResourceAccessException;
//
//import com.example.kafka.producer.controller.MasterCardController;
//import com.example.kafka.producer.exception.TechnicalException;
//import com.example.kafka.producer.model.MasterCardDTO;
//import com.example.kafka.producer.service.CardService;
//import com.example.kafka.producer.utils.LogSplunk;
//import com.example.kafka.producer.utils.LogSplunk.TypeLog;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import io.github.resilience4j.circuitbreaker.CircuitBreaker;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
//import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
//
//@Component
//public class MasterCardConsumer_old2 {
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
//	@Autowired
//    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
//
//	@Value("${time.circuite.open}")
//	private int timeCircuiteOpen;
//	
//	private final CircuitBreaker circuitBreaker;
//	
//	public MasterCardConsumer_old2(CircuitBreakerRegistry circuitBreakerRegistry) {
//		this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("masterCircuit");
//		this.circuitBreaker.getEventPublisher().onStateTransition(this::onStateChange);
//	}
//	
//	/*@KafkaHandler
//	void listener(String message) {
//		log.info("Kafka Handler - {}", message);
//	}*/
//	
//	
//	@KafkaListener(topics = "MASTER_CARD_TOKEN_ACTIVATION", groupId = "consumer_group1", id = "MASTER_CARD_TOKEN_ACTIVATION")
//	public void messageListener(ConsumerRecord<String,?> consumerRecord, @Payload MasterCardDTO event, Acknowledgment ack)throws Exception {
//		long startTime = System.currentTimeMillis();
//		//log(consumerRecord, event);
//		
//		// Send log inicio de processamento msg
//		LogSplunk.builder().customMessage("token.ativacao").payload(consumerRecord.value()).build().sendLog();
//		
//		try {
//			// Verifica se o circuito está aberto
//			if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
//				// Stop consumo do kafka pelo tempo determinado
//				ack.nack(timeCircuiteOpen*1000);
//				System.out.println("******     BREAK         ******* " + event.getUuid());
//				return;
//			}
//			
//			// Solicita ativacao do Token do cartao			
//			cardService.ativarToken(event.getAccountPan(), event.getCorrelationId());
//			
//			// Commita leitura move o offSet
//			ack.acknowledge();
//			
//			// Send log Sucesso
//			LogSplunk.builder().
//				customMessage("solicita.ativacao.sucesso").
//				payload(event).
//				build().sendLog();
//			
//			
//			//log.info("Account Pan {}  - CorrelationId {} ativado com sucesso", event.getAccountPan(), event.getCorrelationId());
//
//		} catch (Exception e) {
//			//log.error("ERROR NA MSG: {} - EX {}", consumerRecord.value(), e);
//			// Log ERRO
//			 LogSplunk.builder(TypeLog.ERROR)
//			 	.customMessage("token.ativacao.erro")
//			 	.payload(consumerRecord.value()).exception(e.toString())
//			 	.build()
//			 	.sendLog();
//
//			// Trata-se de um erro recuperável?
//			final boolean recuperavel = isRecuperavel(e);
//
//			// Caso erro não seja recuperavel
//			if (!recuperavel) {
//				// LOG
//				LogSplunk.builder(TypeLog.ERROR).customMessage("token.ativacao.erro.dlq").payload(consumerRecord.value()).build().sendLog();
//				
//				// Envia para fila de DeadLeatter
//				kafkaTemplate.send("MASTER_CARD_TOKEN_ACTIVATION-DLQ", event.getAccountPan(), event).get();
//				// Commita leitura move o offSet
//				ack.acknowledge();
//				
//			} else {
//				// Log Erro Recuperavel
//				LogSplunk.builder(TypeLog.ERROR).customMessage("token.ativacao.erro.recuperavel").payload(consumerRecord.value()).build().sendLog();
//				
//				// Pausa o kafka por 0, apenas para não mover o offset da msg atual e reprocessar msg
//				ack.nack(0);
//			}
//		}
//		
//		System.out.println("TEMPO DECORRIDO - "+ (System.currentTimeMillis() - startTime));
//	}
//	
//	
////	@KafkaListener(topics = "MASTER_CARD_TOKEN_ACTIVATION", groupId = "consumer_group1")
////	public void messageListener(/*List<ConsumerRecord<String,?>> consumerRecord,*/ @Payload List<MasterCardDTO> events, Acknowledgment ack)throws Exception {
////		
////		//log(consumerRecord, events);
////		System.out.println("Size"+events.size());
////		
////		events.parallelStream().forEach(event -> {
////		
////			try {
////				// Verifica se o circuito está aberto
////				if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
////					// Stop consumo do kafka pelo tempo determinado
////					ack.nack(timeCircuiteOpen*1000);
////					System.out.println("******     BREAK         ******* " + event.getUuid());
////					return;
////				}
////				
////				// Solicita ativacao do Token do cartao			
////				cardService.ativarToken(event.getAccountPan(), event.getCorrelationId());
////				
////				// Commita leitura move o offSet
////				ack.acknowledge();
////				
////				log.info("Account Pan {}  - CorrelationId {} ativado com sucesso", event.getAccountPan(), event.getCorrelationId());
////	
////			} catch (Exception e) {
////				log.error("ERROR NA MSG: {} - EX {}", event, e);
////	
////				// Trata-se de um erro recuperável?
////				final boolean recuperavel = isRecuperavel(e);
////	
////				// Caso erro não seja recuperavel
////				if (!recuperavel) {
////					log.info("ERRO NAO RECUPERAVEL.. ENVIANDO PARA DLQ");
////					// Envia para fila de DeadLeatter
////					try {
////						kafkaTemplate.send("MASTER_CARD_TOKEN_ACTIVATION-DLQ", event.getAccountPan(), event).get();
////					} catch (InterruptedException e1) {
////						// TODO Auto-generated catch block
////						e1.printStackTrace();
////					} catch (ExecutionException e1) {
////						// TODO Auto-generated catch block
////						e1.printStackTrace();
////					}
////				
////				} else {
////					// Pausa o kafka por 0, apenas para não mover o offset da msg atual
////					//ack.nack(0);
////				}
////			}
////			// Commita leitura move o offSet
////			ack.acknowledge();
////		});
////	}
//
////	/*int x=1;
////	@KafkaListener(topics = "MASTER_CARD_TOKEN_ACTIVATION", groupId = "consumer_group1")
////	public void messageListener(ConsumerRecord<String,?> consumerRecord, @Payload MasterCardDTO event, Acknowledgment ack)throws Exception {
////		x++;
////		log(consumerRecord, event);
////		ack.acknowledge();
////		//Thread.sleep(200);
////		
////		/*if(x%2!=0) {
////			ack.acknowledge(); 
////			//System.out.println("COMMITADO"); 
////		}*/
////
////	}*/
//	
////	@KafkaListener(topics = "MASTER_CARD_TOKEN_ACTIVATION", groupId = "consumer_group1")
////	public void messageListener(List<ConsumerRecord<String,String>> consumerRecords, /*@Payload List<MasterCardDTO> events,*/ Acknowledgment ack)throws Exception {
////		
////		//log(consumerRecord, events);
////		System.out.println("Size- "+ consumerRecords.size());
////		
////		Thread.sleep(500);
////		
////		// Commita leitura move o offSet
////		ack.acknowledge();
////		
//////		consumerRecords.forEach(consumerRecord -> {
//////			
//////			MasterCardDTO event = null;
//////			
//////			try {
//////				
//////				event = mapper.readValue(consumerRecord.value().getBytes(), MasterCardDTO.class);
//////				
//////				
//////				// Verifica se o circuito está aberto
//////				if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
//////					System.out.println((int) consumerRecord.offset());
//////					
//////					// Stop consumo do kafka pelo tempo determinado
//////					ack.nack(timeCircuiteOpen*1000);
//////					System.out.println("******     BREAK         ******* " + event.getUuid());
//////					return;
//////				}
//////				
//////				// Solicita ativacao do Token do cartao			
//////				cardService.ativarToken(event.getAccountPan(), event.getCorrelationId());
//////				
//////				// Commita leitura move o offSet
//////				ack.acknowledge();
//////				
//////				log.info("Account Pan {}  - CorrelationId {} ativado com sucesso", event.getAccountPan(), event.getCorrelationId());
//////	
//////			} catch (Exception e) {
//////				log.error("ERROR NA MSG: {} - EX {}", event, e);
//////	
//////				// Trata-se de um erro recuperável?
//////				final boolean recuperavel = isRecuperavel(e);
//////	
//////				// Caso erro não seja recuperavel
//////				if (!recuperavel) {
//////					log.info("ERRO NAO RECUPERAVEL.. ENVIANDO PARA DLQ");
//////					// Envia para fila de DeadLeatter
//////					try {
//////						kafkaTemplate.send("MASTER_CARD_TOKEN_ACTIVATION-DLQ", event.getAccountPan(), event).get();
//////					} catch (InterruptedException e1) {
//////						// TODO Auto-generated catch block
//////						e1.printStackTrace();
//////					} catch (ExecutionException e1) {
//////						// TODO Auto-generated catch block
//////						e1.printStackTrace();
//////					}
//////				
//////				} else {
//////					// Pausa o kafka por 0, apenas para não mover o offset da msg atual
//////					//ack.nack(0);
//////				}
//////			}
//////			// Commita leitura move o offSet
//////			ack.acknowledge();
//////		});
////	}
//	
//	private boolean isRecuperavel(Exception e) {
//		boolean isRecuperavel = false;
//		
//		if (e instanceof TechnicalException) {
//			// Problema com conexao ou timeout
//			if(e.getCause() instanceof  ResourceAccessException || e.getCause() instanceof UnknownHostException || e.getCause() instanceof ConnectException) {
//				isRecuperavel = true;
//			
//			} else if (e.getCause() instanceof HttpClientErrorException || e.getCause() instanceof HttpServerErrorException) {
//				HttpStatus statusCode = null;
//				if(e.getCause() instanceof HttpClientErrorException) {
//					HttpClientErrorException httpError = (HttpClientErrorException) e.getCause();
//					statusCode = httpError.getStatusCode();
//					
//					// ERRO NO CERTIFICADO, O CONSUMIDOR DEVE SER PARADO E CORRIGIDO
//					if(httpError.getMessage().contains("INVALID_OAUTH_SBS")) {
//						log.error("ERRO FALTAL: ERRO NO CERTIFICADO DE COMUNICACAO COM MASTERCARD - FOI SOLICITADO O STOP DO CONSUMO DO KAFKA",e);
//						
//						LogSplunk.builder().customMessage("token.ativacao.fatal")
//						.customMessage("Erro Fatal - Consumo do kafka parado")
//						.exception(e.getMessage())
//						.build().sendLog();
//						
//						this.stopKafkaListener();
//						isRecuperavel = true;
//					}
//					
//				} else {
//					HttpServerErrorException httpError = (HttpServerErrorException) e.getCause();
//					statusCode = httpError.getStatusCode();
//				}
//				
//				// Erro ao chamar do nosso cliente (Itau) com a MasterCard
//				if (statusCode.is4xxClientError()) {
//					// 
//					if(statusCode == HttpStatus.TOO_MANY_REQUESTS) {
//						isRecuperavel = true;	
//					}
//				// Erro de processamento na masterCard, masterCard Mandou um erro
//				} else if (statusCode.is5xxServerError()) {
//					isRecuperavel = true;
//				}
//			} else {
//				System.out.println("*********-----**** ERRO GRAVISSIMO");
//			}
//		}
//		
//		return isRecuperavel;
//	}
//	
//	
//	/**
//	 *  Para o consumo do Kafka por algum erro grave e reporta a parada
//	 **/
//	private void stopKafkaListener() {
//		kafkaListenerEndpointRegistry.getListenerContainer("MASTER_CARD_TOKEN_ACTIVATION").stop();
//	}
//
//	private void onStateChange(CircuitBreakerOnStateTransitionEvent event) {
//		CircuitBreaker.State state =  event.getStateTransition().getToState();
//		switch (state) {
//			case OPEN:
//				//System.out.println("ABERTO ------------/-----");
//				LogSplunk.builder().customMessage("token.ativacao.circuit.open").customMessage("CircuitBreak Aberto").build().sendLog();
//				break;
//			case CLOSED, HALF_OPEN:
//				//System.out.println("FECHADO ----------x--x-----");
//				LogSplunk.builder().customMessage("token.ativacao.circuit.close").customMessage("CircuitBreak Fechado").build().sendLog();
//			break;
//		default:
//			break;
//		}
//	}
//	
//
//	
//	
//	private void log(ConsumerRecord<String, ?> consumerRecord, MasterCardDTO event) {
//		System.out.println("******     INICIADO         ******* " + event.getUuid());
//		System.out.println("           OFFSET                   " + consumerRecord.offset());
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
