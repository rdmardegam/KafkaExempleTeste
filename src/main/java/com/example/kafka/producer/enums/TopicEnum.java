package com.example.kafka.producer.enums;

import java.util.Arrays;
import java.util.List;

public enum TopicEnum {

	PRIMEIRO_RETRY("MASTER_CARD_TOKEN_ACTIVATION_PRIMEIRO_RETRY", 5, 1, 2),
	SEGUNDO_RETRY("MASTER_CARD_TOKEN_ACTIVATION_SEGUNDO_RETRY", 10, 3, 4),
	TERCEIRO_RETRY("MASTER_CARD_TOKEN_ACTIVATION_TERCEIRO_RETRY", 15, 5, 6),
	DLQ("MASTER_CARD_TOKEN_ACTIVATION-DLQ", 0);
	
	// Nome do Topico
	private String topicName;
	private int secondsAwait;
	private List<Integer> executionAttempt;
	
	TopicEnum(String topicName, int secondsAwait, Integer... executionAttempt) {
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
	

	public String getTopicName() {
		return topicName;
	}

	public Integer getSecondsAwait() {
		return secondsAwait;
	}


	public List<Integer> getExecutionAttempt() {
		return executionAttempt;
	}	

}
