package com.example.kafka.producer.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import com.example.kafka.producer.model.MasterCardDTO;

@Configuration
public class KafkaConfig {

	/**
	 * Consegue sempre receber a msg, mesmo que o json esteja com erro
	 * */
	@Bean
	public RecordMessageConverter converter() {
		return new StringJsonMessageConverter();
	}
	
//	@Autowired
//	private ConsumerFactory consumerFactory;

//	@Bean
//	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MasterCardDTO>> kafkaListenerContainerFactoryFilter(ConsumerFactory consumerFactory) {
//	    ConcurrentKafkaListenerContainerFactory<String, MasterCardDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
//	    factory.setConsumerFactory(consumerFactory);
//	    
//	    //factory.setRecordFilterStrategy(record -> record.value().contains("ignored"));
//	    //factory.setAckDiscarded(true);
//	    factory.setRecordFilterStrategy(record -> {
//	    	return record.value().getCodigoBandeira().contains("216");
//	    });
//	    
////	    factory.setRecordFilterStrategy(new RecordFilterStrategy<String, String>() {
////
////            @Override
////            public boolean filter(ConsumerRecord<String, String> consumerRecord) {
////                if(consumerRecord.key().equals("ETEST")) {
////                return false;
////                }
////            else {
////                return true;
////                 }
////            }   
////        });
//	    
//	    
//	    return factory;
//	  }

}
