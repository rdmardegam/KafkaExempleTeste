package com.example.kafka.producer.config;

import java.util.Collection;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;

@Component
public class KafkaRebalanceListener implements ConsumerRebalanceListener {

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        System.out.println("********** Partições atribuídas: " + partitions);
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        System.out.println("*********** Partições revogadas: " + partitions);
    }
}