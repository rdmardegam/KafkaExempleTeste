package com.example.kafka.producer.enums;

import java.util.Arrays;
import java.util.List;

public enum TopicEnum {

	PRIMEIRO_RETRY("MASTER_CARD_TOKEN_ACTIVATION_PRIMEIRO_RETRY", 10L, 1, 2),
	SEGUNDO_RETRY("MASTER_CARD_TOKEN_ACTIVATION_SEGUNDO_RETRY", 120L, 3, 4),
	TERCEIRO_RETRY("MASTER_CARD_TOKEN_ACTIVATION_TERCEIRO_RETRY", 240L, 5, 6),
	DLQ("MASTER_CARD_TOKEN_ACTIVATION-DLQ", 0L);
	
	// Nome do Topico
	private String topicName;
	private long secondsAwait;
	private List<Integer> executionAttempt;
	
	TopicEnum(String topicName, long secondsAwait, Integer... executionAttempt) {
		this.topicName = topicName;
		this.secondsAwait = secondsAwait;
		this.executionAttempt = Arrays.asList(executionAttempt);		
	}
	
	public static TopicEnum getRetryTopicFromAttempt(Integer attempt) {
		// Topico Default caso nao ache correspondente
		TopicEnum retry =  TopicEnum.DLQ;
		//  Veririca se encontra perante retry
		for (TopicEnum retryTopic: values()) {
			if(retryTopic.getExecutionAttempt().contains(attempt)) {
				retry = retryTopic;
				break;
			}
	    }
		return retry;
	}
	
	public static TopicEnum getTopicByName(String topicName) {
		// Topico Default caso nao ache correspondente
		TopicEnum topicEnum =  TopicEnum.DLQ;
		//  Veririca se encontra perante retry
		for (TopicEnum retryTopic: values()) {
			if(retryTopic.getTopicName().equalsIgnoreCase(topicName)) {
				topicEnum = retryTopic;
				break;
			}
	    }
		return topicEnum;
	} 
	

	public String getTopicName() {
		return topicName;
	}

	public long getSecondsAwait() {
		return secondsAwait;
	}
	
	public long getSecondsAwaitInMillis() {
		return secondsAwait * 1000; 		
	}


	public List<Integer> getExecutionAttempt() {
		return executionAttempt;
	}	

}
