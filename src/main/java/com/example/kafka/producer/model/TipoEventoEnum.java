package com.example.kafka.producer.model;

public enum TipoEventoEnum {

	PAGAMENTO(1, "PAGAMENTO_1"),
	CANCELAMENTO(2, "CANCELAMENTO_2"),
	PARCELAMENTO(3, "PARCELAMENTO_3");

	private Integer codigo;
	private String descricao;
	
	TipoEventoEnum(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}