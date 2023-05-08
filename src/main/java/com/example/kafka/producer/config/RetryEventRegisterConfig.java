package com.example.kafka.producer.config;

import org.springframework.context.annotation.Bean;

import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import lombok.extern.log4j.Log4j2;

import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class RetryEventRegisterConfig {

	 @Bean
	    public RegistryEventConsumer<Retry> myRetryRegistryEventConsumer() {

	        return new RegistryEventConsumer<Retry>() {
	            @Override
	            public void onEntryAddedEvent(EntryAddedEvent<Retry> entryAddedEvent) {
	                entryAddedEvent.getAddedEntry().getEventPublisher()
	                   .onEvent(event -> log.info(event.toString()));
	            }

	            @Override
	            public void onEntryRemovedEvent(EntryRemovedEvent<Retry> entryRemoveEvent) {

	            }

	            @Override
	            public void onEntryReplacedEvent(EntryReplacedEvent<Retry> entryReplacedEvent) {

	            }
	        };
	    }
}
