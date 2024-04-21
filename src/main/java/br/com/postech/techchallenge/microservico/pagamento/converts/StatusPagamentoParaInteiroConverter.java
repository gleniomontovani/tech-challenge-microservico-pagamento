package br.com.postech.techchallenge.microservico.pagamento.converts;

import org.modelmapper.AbstractConverter;

import br.com.postech.techchallenge.microservico.pagamento.enums.StatusPagamentoEnum;

public class StatusPagamentoParaInteiroConverter extends AbstractConverter<StatusPagamentoEnum, Integer>{

	@Override
	protected Integer convert(StatusPagamentoEnum source) {
		return source == null ? null : source.getValue();
	}

}
