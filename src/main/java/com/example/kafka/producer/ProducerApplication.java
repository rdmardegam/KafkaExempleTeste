package com.example.kafka.producer;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ProducerApplication extends RuntimeException implements ExitCodeGenerator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 690766885143727874L;

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}

	@Override
	public int getExitCode() {
		return 42;
	}
}