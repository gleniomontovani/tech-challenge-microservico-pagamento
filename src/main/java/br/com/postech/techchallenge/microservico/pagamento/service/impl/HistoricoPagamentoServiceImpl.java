package br.com.postech.techchallenge.microservico.pagamento.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import br.com.postech.techchallenge.microservico.pagamento.configuration.ModelMapperConfiguration;
import br.com.postech.techchallenge.microservico.pagamento.entity.HistoricoPagamento;
import br.com.postech.techchallenge.microservico.pagamento.model.response.HistoricoPagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.repository.HistoricoPagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.service.HistoricoPagamentoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class HistoricoPagamentoServiceImpl implements HistoricoPagamentoService {
	private static final ModelMapper MAPPER = ModelMapperConfiguration.getModelMapper();
	
	private final HistoricoPagamentoRepository historicoPagamentoJpaRepository;
	
	@Override
	public List<HistoricoPagamentoResponse> listarHistoricoPagamentosPorPedido(Long numeroPedido) {
		List<HistoricoPagamento> historicoPagamentosEntity = historicoPagamentoJpaRepository.findByPagamentoNumeroPedido(numeroPedido);
		
		MAPPER.typeMap(HistoricoPagamento.class, HistoricoPagamentoResponse.class)
		  	.addMappings(mapperA -> 
		  		  mapperA.map(src ->src.getDataPagamento(), HistoricoPagamentoResponse::setDataPagamento))
			.addMappings(mapperB -> 
				  mapperB.map(src -> src.getPagamento().getNumeroPedido(), HistoricoPagamentoResponse::setNumeroPedido))
			.addMappings(mapperC -> 
			  	  mapperC.map(src -> src.getPagamento().getValor(), HistoricoPagamentoResponse::setValor))
			.addMappings(mapperD -> {
				  mapperD.map(src -> src.getPagamento().getId(), HistoricoPagamentoResponse::setNumeroPagamento);
		});
		
		return MAPPER.map(historicoPagamentosEntity, new TypeToken<List<HistoricoPagamentoResponse>>() {
		}.getType());
	}
}