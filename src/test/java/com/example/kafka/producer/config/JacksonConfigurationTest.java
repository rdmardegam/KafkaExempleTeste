package com.example.kafka.producer.config;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class JacksonConfigurationTest {

	@Test
	public void testeIniciaMapper() {
		JacksonConfiguration jacksonConfiguration =  new JacksonConfiguration();
		assertNotNull(jacksonConfiguration.objectMapper()); 
	}
}

