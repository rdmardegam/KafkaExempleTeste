package com.example.kafka.producer.model;

import lombok.Data;

@Data
public class AssincronoDTO {

	private int index;
	private String valor;
	private Boolean executeCallSucess;
	private String statusChamada;
	private String resposta;
	private int tentativa;
	
}