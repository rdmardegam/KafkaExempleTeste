package com.example.kafka.producer.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.kafka.producer.model.Evento;

@Component
public class MessageConsumer {
	private static final Logger log = LogManager.getLogger(MessageConsumer.class);
    private static final String TOPIC = "EVENTO-CARTAO-2";

    @KafkaListener(topics = TOPIC, groupId ="consumer_group1")
    public void messageListener(ConsumerRecord consumerRecord, @Payload Evento event, Acknowledgment ack) {

        /*String key = consumerRecord.key();
        Message value = consumerRecord.value();
        int partition = consumerRecord.partition();*/

        
        System.out.println("*** CONSUMINDOOO");
        log.info("key: " + consumerRecord.key());
        log.info("Headers: " + consumerRecord.headers());
        log.info("Partion: " + consumerRecord.partition());
        log.info("Order: " + consumerRecord.value());
        
        log.info("Event = {}", event);
        
        ack.acknowledge();
    }
}