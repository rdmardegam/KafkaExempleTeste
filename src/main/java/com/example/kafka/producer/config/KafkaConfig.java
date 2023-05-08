package com.example.kafka.producer.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

@Configuration
public class KafkaConfig {

	/**
	 * Consegue sempre receber a msg, mesmo que o json esteja com erro
	 * */
	/*@Bean
	public RecordMessageConverter converter() {
		return new StringJsonMessageConverter();
	}*/
	
	/*@Autowired
	private ConsumerFactory consumerFactory;*/

	//@Bean("kafkaListenerContainerFactoryFilter")
	@Bean
	//public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactoryFilter(ConsumerFactory consumerFactory) {
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryFilter(ConsumerFactory consumerFactory, KafkaRebalanceListener kafkaRebalanceListener) {
	    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
	    
	    final Map<?, ?> unmodifiable = consumerFactory.getConfigurationProperties();
	    Map<String, Object> props  = new HashMap(unmodifiable);
	    
	    
	    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);
	    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 10000);
	    //props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName()); 
	    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
	    
	    consumerFactory.updateConfigs(props);
	    
	    factory.setConsumerFactory(consumerFactory);
	    
	    
	    //factory.getContainerProperties().setSyncCommits(true);
	    //factory.getContainerProperties().setAckMode(AckMode.TIME);
	    //factory.getContainerProperties().setAckTime(10000L);
	    
	    //factory.getContainerProperties().setIdleBetweenPolls(100); // 100 miliseconds
	    
	    //factory.setRecordFilterStrategy(record -> record.value().contains("ignored"));
	    //factory.setAckDiscarded(true);
	    
	    /*factory.setRecordFilterStrategy(record -> {
	    	return record.value().getCodigoBandeira().contains("618");
	    });*/
	    
	    //CooperativeStickyAssignor
	    
	    System.out.println(factory.getContainerProperties().getKafkaConsumerProperties());
	    //factory.setBatchListener(true);
	    factory.getContainerProperties().setAckMode(AckMode.MANUAL);
	    
	    factory.getContainerProperties().setConsumerRebalanceListener(kafkaRebalanceListener);
	    
	    
	    
	    //factory.getContainerProperties().setac
	    
	    
	    
//	    factory.setRecordFilterStrategy(new RecordFilterStrategy<String, String>() {
//
//            @Override
//            public boolean filter(ConsumerRecord<String, String> consumerRecord) {
//                if(consumerRecord.key().equals("ETEST")) {
//                return false;
//                }
//            else {
//                return true;
//                 }
//            }   
//        });
	    
	    
	    return factory;
	  }

}
