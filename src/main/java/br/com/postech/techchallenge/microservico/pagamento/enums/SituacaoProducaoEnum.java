package br.com.postech.techchallenge.microservico.pagamento.enums;

public enum SituacaoProducaoEnum {

	DESCONHECIDO(0, "Desconhecido"),
	RECEBIDO(1, "Recebido"),
	EM_PREPARACAO(2, "Em preparação"),
	PRONTO(3, "Pronto"),
	CANCELADO(4, "Cancelado");
	
	private Integer value;
	private String descricao;
	
	private SituacaoProducaoEnum(Integer value, String descricao) {
		this.value = value;
		this.descricao = descricao;
	}

	public Integer getValue() {
		return value;
	}
	

	public String getDescricao() {
		return descricao;
	}

	public static SituacaoProducaoEnum get(Integer value) {
		for (SituacaoProducaoEnum status : SituacaoProducaoEnum.values()) {
			if(status.getValue() == value ) {
				return status;
			}
		}
		return SituacaoProducaoEnum.DESCONHECIDO;
	}
	
	public static SituacaoProducaoEnum getByDescricao(String descricao) {
		for (SituacaoProducaoEnum status : SituacaoProducaoEnum.values()) {
			if(status.getDescricao().equals(descricao)) {
				return status;
			}
		}
		return SituacaoProducaoEnum.DESCONHECIDO;
	}
}
