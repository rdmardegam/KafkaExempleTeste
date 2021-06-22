//package com.example.kafka.producer;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.util.concurrent.TimeUnit;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.test.annotation.DirtiesContext;
//
//import com.example.kafka.producer.listener.MasterCardConsumer;
//import com.example.kafka.producer.model.MasterCardDTO;
//
//@SpringBootTest
//@DirtiesContext
//@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
//public class MasterCardConsumerTest {
//
//	@Autowired
//	private MasterCardConsumer consumer;
//
//	@Autowired
//	private KafkaTemplate<String, MasterCardDTO> kafkaTemplate;
//	
//	@Test
//    public void givenKafkaDockerContainer_whenSendingtoSimpleProducer_thenMessageReceived() 
//      throws Exception {
//        
//		MasterCardDTO master = new MasterCardDTO();
//		master.setCorrelationId("123");
//		master.setIdCartao("123123");
//		
//		kafkaTemplate.send("MASTER_CARD_TOKEN_ACTIVATION", master);
//        consumer.getLatch().await(10000, TimeUnit.MILLISECONDS);
//        
//        assertEquals(consumer.getLatch().getCount(), 0L);
//        
//    }
//}
