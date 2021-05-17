package com.example.kafka.producer.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.producer.model.Evento;
import com.example.kafka.producer.model.TipoEventoEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

@RestController
public class ProducerController {

	@Autowired
	private KafkaTemplate<String, Evento> kafkaTemplate;
	
	
	@Autowired
    private Jackson2ObjectMapperBuilder builder;

	
	@GetMapping(value = "/produzir")
	public Evento getTest() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
//		//TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
//		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//		
//		
//		System.out.println(LocalDate.now());
//		System.out.println(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
//		System.out.println(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS));
//		
//		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
//		System.out.println(LocalDate.now());
//		System.out.println(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
//		System.out.println(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS));
		

		ObjectMapper mapper = this.builder
                /*.serializers(new LocalDateSerializer(new DateTimeFormatterBuilder()
                .appendPattern("dd-MM-yyyy").toFormatter()))*/
            /*.deserializers(new LocalDateDeserializer(new DateTimeFormatterBuilder()
                .appendPattern("dd/MM/yyyy").toFormatter())
            		)*/
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .propertyNamingStrategy(PropertyNamingStrategy.SnakeCaseStrategy.SNAKE_CASE)
            .build();
		
		try{
			
			Evento evento = new Evento();
			evento.setUui(UUID.randomUUID());
			evento.setValor(new BigDecimal("510.25"));
			evento.setTipoCartao("PLATIUM");
			evento.setEvento(TipoEventoEnum.PAGAMENTO);
			evento.setDataHoraEvento(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS));
			
			System.out.println("VALOR:");
			System.out.println(mapper.writeValueAsString(evento));
			System.out.println("--------");
			
			Message<Evento> event = MessageBuilder.
             withPayload(evento).
             setHeader(KafkaHeaders.TOPIC, "EVENTO-CARTAO").
             setHeader(KafkaHeaders.TIMESTAMP, Instant.now().toEpochMilli()).
             build();
			
			// Garante a entrega atraves do GET
			SendResult<String, Evento> result =  
					kafkaTemplate.send("EVENTO-CARTAO-2", evento.getTipoCartao(), evento).get();
			
//			SendResult<String, Evento> result =  
//					kafkaTemplate.send("EVENTO-CARTAO", evento.getTipoCartao(), event).get();
			
			System.out.println("RESULT: "); 
			System.out.println(result.getProducerRecord());
			
			return evento;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}	
	
	
	
	
}