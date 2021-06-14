package com.example.kafka.producer.utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@JsonPropertyOrder(value = {"key","customMessage"})
public class Splunk {
	@Builder.Default
	private String id = UUID.randomUUID().toString();
	@Builder.Default
	private String time = OffsetDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	
	@Setter
	private OffsetDateTime starTime;
	@Setter
	private OffsetDateTime endTime;
	@Setter
	private long elapseTime;
	
	private String key;
	
	@JsonIgnore
	private Throwable exception;
			
	private String customMessage;
	private Object payload;
}