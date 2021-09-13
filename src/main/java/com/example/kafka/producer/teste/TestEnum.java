package com.example.kafka.producer.teste;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.example.kafka.producer.enums.TopicEnum;
import com.example.kafka.producer.model.MasterCardDTO;

public class TestEnum {

	
	public static void main(String[] args) throws InterruptedException {
//		System.out.println(	TopicEnum.getRetryTopicFromAttempt(0));
//		System.out.println(	TopicEnum.getRetryTopicFromAttempt(1));
//		System.out.println(	TopicEnum.getRetryTopicFromAttempt(2));
//		System.out.println(	TopicEnum.getRetryTopicFromAttempt(3));
//		System.out.println(	TopicEnum.getRetryTopicFromAttempt(4));
//		System.out.println(	TopicEnum.getRetryTopicFromAttempt(5));
//		System.out.println(	TopicEnum.getRetryTopicFromAttempt(6));
//		System.out.println(	TopicEnum.getRetryTopicFromAttempt(7));
		
		
		MasterCardDTO masterCard = new MasterCardDTO();
		TopicEnum topicoDestinoInfo =  TopicEnum.PRIMEIRO_RETRY;
		
		// Data Hora
		System.out.println(LocalDateTime.now());
		masterCard.setDataHoraProximaTentativa(LocalDateTime.now().plusSeconds(topicoDestinoInfo.getSecondsAwait()));
		System.out.println(masterCard.getDataHoraProximaTentativa());
		
		System.out.println("**********");

		while(true) {
			Thread.sleep(2000); 
			
			long miliSecondsToExecute =  masterCard.getDataHoraProximaTentativa().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			long miliSecondsNow = System.currentTimeMillis();
			
			System.out.println(miliSecondsToExecute - miliSecondsNow);
			
			if(LocalDateTime.now().isAfter(masterCard.getDataHoraProximaTentativa())) {
				System.out.println("TEMPO FOI ALCANÇADO");
				System.out.println("AGORA:" + LocalDateTime.now() + " - CHEGO:" + masterCard.getDataHoraProximaTentativa());
				break;
			} else {
				System.out.println("AGUARDANDO :" + LocalDateTime.now());
			}
		}
		
		System.out.println("**********");
		
	}
}