package com.example.kafka.producer.model;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class MasterCardDTO {
	// Id que representa o cartao
	private String idCartao;
	
	// AccountPan
	private String numeroCartao;
	
	// correlationId
	private String correlationId;
	
	// Numero da carteira digital Wallet: 216-Google | 217-Samsung | 618-Whatsapp | 103-Apple|
	private String numeroCarteira;
	
	// M - Master | V- Visa
	private String codigoBandeira;
	
	// D-Debito | M-Multiplo | C-Puro Credito
	private String tipoCartao;
	
	// BC ou VQ
	private String origemInformacao;
	
	//DeviceID
	private String idDispositivo;
	
	// Campo utilizado internamente para log
	private OffsetDateTime dataEvento;
	
	// Utilizados para retry
	private Integer numeroTentativa;
	private LocalDateTime dataHoraProximaTentativa;
		
	public Integer getNumeroTentativa() {
		if(numeroTentativa==null) {
			numeroTentativa = 0;
		}
		return numeroTentativa;
	}
	
	
	
}