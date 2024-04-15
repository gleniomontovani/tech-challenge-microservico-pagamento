package br.com.postech.techchallenge.microservico.pagamento.model.converters;

import org.modelmapper.AbstractConverter;

import br.com.postech.techchallenge.microservico.pagamento.enums.StatusPagamentoEnum;

public class InteiroParaStatusPagamentoConverter extends AbstractConverter<Integer, StatusPagamentoEnum> {

	@Override
	protected StatusPagamentoEnum convert(Integer source) {
		return StatusPagamentoEnum.get(source);
	}
}