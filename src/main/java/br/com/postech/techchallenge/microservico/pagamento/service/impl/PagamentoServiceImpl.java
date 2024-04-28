package br.com.postech.techchallenge.microservico.pagamento.service.impl;

import java.time.LocalDateTime;
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
import br.com.postech.techchallenge.microservico.pagamento.exception.BusinessException;
import br.com.postech.techchallenge.microservico.pagamento.exception.NotFoundException;
import br.com.postech.techchallenge.microservico.pagamento.model.request.PagamentoRequest;
import br.com.postech.techchallenge.microservico.pagamento.model.response.PagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.repository.HistoricoPagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.repository.PagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.service.PagamentoService;
import br.com.postech.techchallenge.microservico.pagamento.util.Constantes;
import br.com.postech.techchallenge.microservico.pagamento.util.Utilitario;
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
		
		MAPPER.typeMap(Pagamento.class, PagamentoResponse.class)
		  	.addMappings(mapperA -> mapperA.using(new StatusPagamentoParaInteiroConverter())
					.map(Pagamento::getStatusPagamento, PagamentoResponse::setStatusPagamento))
			.addMappings(mapperB -> 
				  mapperB.map(src -> src.getNumeroPedido(), PagamentoResponse::setNumeroPedido))
			.addMappings(mapperC -> {
				  mapperC.map(src -> src.getId(), PagamentoResponse::setNumeroPagamento);
		});
		
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
	public PagamentoResponse criarPagamento(PagamentoRequest pagamentoRequest) throws BusinessException {	
		var pagamento = MAPPER.map(pagamentoRequest, Pagamento.class);
		pagamento.setStatusPagamento(StatusPagamentoEnum.get(pagamentoRequest.statusPagamento()));
		pagamento.setId(pagamentoRequest.numeroPagamento());

		Integer tentativas = historicoPagamentoJpaRepository.findByPagamentoNumeroPedido(pagamento.getNumeroPedido()).stream()
				.map(HistoricoPagamento::getNumeroTentativas).max(Integer::compare).orElse(0);
		
		// Obtem os valores para caso foi a terceira tentativa de pagamento,
		String descricaoHistorico = ((tentativas < 2) ? Constantes.FAIL_TRY_PAYMENT : Constantes.SUCESS_MAKE_PAYMENT);
		LocalDateTime dataPagamento = ((tentativas < 2) ? null : LocalDateTime.now());
		Pagamento pagamentoEntity = null;
		
		// Faz a atualizacao do pagamento
		pagamento.setDataPagamento(dataPagamento);
		pagamento.adicionaHistorico(HistoricoPagamento.adicionaHistorico(descricaoHistorico,
				pagamento, dataPagamento, tentativas));

        pagamento.setQrCodePix(Utilitario.gerarQrCodePix(pagamento.getValor()));
        
		pagamentoEntity = pagamentoJpaRepository.save(pagamento);
				
		MAPPER.typeMap(Pagamento.class, PagamentoResponse.class)
		  	.addMappings(mapperA -> mapperA.using(new StatusPagamentoParaInteiroConverter())
					.map(Pagamento::getStatusPagamento, PagamentoResponse::setStatusPagamento))
			.addMappings(mapperB -> {
				  mapperB.map(src -> src.getId(), PagamentoResponse::setNumeroPagamento);
		});
		
		return MAPPER.map(pagamentoEntity, PagamentoResponse.class);
	}

	@Override
	public PagamentoResponse atualizaPagamento(PagamentoRequest pagamentoRequest) throws BusinessException {
		var pagamento = MAPPER.map(pagamentoRequest, Pagamento.class);
		pagamento.setStatusPagamento(StatusPagamentoEnum.get(pagamentoRequest.statusPagamento()));
		pagamento.setId(pagamentoRequest.numeroPagamento());

		Integer tentativas = historicoPagamentoJpaRepository.findByPagamentoNumeroPedido(pagamento.getNumeroPedido()).stream()
				.map(HistoricoPagamento::getNumeroTentativas).max(Integer::compare).orElse(0);
		
		// Faz a atualizacao do pagamento		
		Pagamento pagamentoEntity = pagamentoJpaRepository
				.findByNumeroPedidoAndQrCodePixAndDataPagamentoIsNull(pagamento.getNumeroPedido(), pagamento.getQrCodePix())
				.map(pagEntity -> {
					pagEntity.setDataPagamento(LocalDateTime.now());
					pagEntity.setStatusPagamento(pagamento.getStatusPagamento());
					pagEntity.getHistoricoPagamento().add(HistoricoPagamento.adicionaHistorico(Constantes.SUCESS_MAKE_PAYMENT,
							pagEntity, LocalDateTime.now(), tentativas));

					return pagamentoJpaRepository.save(pagEntity);
				}).orElseThrow(()-> new BusinessException("Pagamento não encontrado ou já processado!"));
		
		//TODO - Aqui vou ter que fazer a requisição do microserviço de produção para alterar o status da produção do pedido.
		
		MAPPER.typeMap(Pagamento.class, PagamentoResponse.class)
			.addMappings(mapperA -> mapperA.using(new StatusPagamentoParaInteiroConverter())
					.map(Pagamento::getStatusPagamento, PagamentoResponse::setStatusPagamento))
			.addMappings(mapperC -> {
				mapperC.map(src -> src.getId(), PagamentoResponse::setNumeroPagamento);
		});
		
		return MAPPER.map(pagamentoEntity, PagamentoResponse.class);
	}
}