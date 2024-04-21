package br.com.postech.techchallenge.microservico.pagamento.service.impl;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import br.com.postech.techchallenge.microservico.pagamento.configuration.ModelMapperConfiguration;
import br.com.postech.techchallenge.microservico.pagamento.converts.StatusPagamentoParaInteiroConverter;
import br.com.postech.techchallenge.microservico.pagamento.entity.HistoricoPagamento;
import br.com.postech.techchallenge.microservico.pagamento.entity.Pagamento;
import br.com.postech.techchallenge.microservico.pagamento.enums.StatusPagamentoEnum;
import br.com.postech.techchallenge.microservico.pagamento.exception.NotFoundException;
import br.com.postech.techchallenge.microservico.pagamento.model.response.HistoricoPagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.model.response.PagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.repository.HistoricoPagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.repository.PagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.service.PagamentoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PagamentoServiceImpl implements PagamentoService {	
	private static final ModelMapper MAPPER = ModelMapperConfiguration.getModelMapper();
	
	private final PagamentoRepository pagamentoJpaRepository;
	private final HistoricoPagamentoRepository historicoPagamentoJpaRepository;

	@Override
	public PagamentoResponse consultarStatusPagamentoPorPedido(Long numeroPedido) {
		Pagamento pagamentoEntity = pagamentoJpaRepository.findByNumeroPedido(numeroPedido)
				.orElseThrow(() -> new NotFoundException("Pedido não encontrado!"));
		
		return MAPPER.map(pagamentoEntity, PagamentoResponse.class);
	}

	@Override
	public List<PagamentoResponse> listarPagamentosPendentes() {
		List<Pagamento> pagamentosPendentes = pagamentoJpaRepository
				.findByStatusPagamentoIn(Arrays.asList(StatusPagamentoEnum.PENDENTE));
		
		MAPPER.typeMap(Pagamento.class, PagamentoResponse.class)
		  	.addMappings(mapperA -> mapperA.using(new StatusPagamentoParaInteiroConverter())
					.map(Pagamento::getStatusPagamento, PagamentoResponse::setStatusPagamento))
			.addMappings(mapperB -> 
				  mapperB.map(src -> src.getNumeroPedido(), PagamentoResponse::setNumeroPedido))
			.addMappings(mapperC -> {
				  mapperC.map(src -> src.getId(), PagamentoResponse::setNumeroPagamento);
		});
		
		return MAPPER.map(pagamentosPendentes, new TypeToken<List<PagamentoResponse>>() {
		}.getType());
	}

	@Override
	public List<HistoricoPagamentoResponse> listarHistoricoPagamentosPorPedido(Long numeroPedido) {
		List<HistoricoPagamento> historicoPagamentosEntity = historicoPagamentoJpaRepository.findByPagamentoId(numeroPedido);
		
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