package com.example.kafka.producer.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Evento {

	private UUID uui;
	private String tipoCartao;
	
	private BigDecimal valor;
	
	/*@JsonSerialize (using = ZonedDateTimeSerializer.class)
    @JsonDeserialize (using = ZonedDateTimeSerializer.class)*/
	private OffsetDateTime dataHoraEvento;
	
	//@JsonProperty("data_hora_evento")
//	
//	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//	@JsonSerialize(using = LocalDateTimeSerializer.class)
//	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	//private LocalDateTime dataHoraEvento;
	
	private TipoEventoEnum evento;
	
	public UUID getUui() {
		return uui;
	}
	public void setUui(UUID uui) {
		this.uui = uui;
	}
	public String getTipoCartao() {
		return tipoCartao;
	}
	public void setTipoCartao(String tipoCartao) {
		this.tipoCartao = tipoCartao;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public OffsetDateTime getDataHoraEvento() {
		return dataHoraEvento;
	}
	public void setDataHoraEvento(OffsetDateTime dataHoraEvento) {
		this.dataHoraEvento = dataHoraEvento;
	}
	public TipoEventoEnum getEvento() {
		return evento;
	}
	public void setEvento(TipoEventoEnum evento) {
		this.evento = evento;
	}
	
}