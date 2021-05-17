package com.example.kafka.producer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@Configuration
public class KafkaConfig {

	/**
	 * Consegue sempre receber a msg, mesmo que o json esteja com erro
	 * */
	@Bean
	public RecordMessageConverter converter() {
		return new StringJsonMessageConverter();
	}
}
