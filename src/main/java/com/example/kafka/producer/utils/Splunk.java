package com.example.kafka.producer.utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@JsonPropertyOrder(value = {"key","time", "customMessage", "startTime","endTime", "payload", "moreInfo"})
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
	private Long elapseTime;
	
	private String key;
	
	@JsonIgnore
	private Throwable exception;
			
	private String customMessage;
	private Object payload;
	
	/*@Setter
	private Map<String,Object> request;*/
	
	@Setter
	private Map<String,Object> moreInfo;
	
	
	public Splunk addMoreInfo(Map<String, Object> more) {
		if(this.moreInfo == null) {
			moreInfo = new HashMap<String, Object>();
		}
		more.forEach((k,v) -> {
			this.moreInfo.put(k,v);
		});
		return this;
	}
	
}