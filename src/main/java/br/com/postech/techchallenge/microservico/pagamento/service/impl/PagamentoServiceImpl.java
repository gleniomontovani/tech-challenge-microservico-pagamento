package br.com.postech.techchallenge.microservico.pagamento.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import br.com.postech.techchallenge.microservico.pagamento.configuration.ModelMapperConfiguration;
import br.com.postech.techchallenge.microservico.pagamento.converts.StatusPagamentoParaInteiroConverter;
import br.com.postech.techchallenge.microservico.pagamento.domain.PagamentoDocumento;
import br.com.postech.techchallenge.microservico.pagamento.entity.HistoricoPagamento;
import br.com.postech.techchallenge.microservico.pagamento.entity.Pagamento;
import br.com.postech.techchallenge.microservico.pagamento.enums.SituacaoProducaoEnum;
import br.com.postech.techchallenge.microservico.pagamento.enums.StatusPagamentoEnum;
import br.com.postech.techchallenge.microservico.pagamento.exception.BusinessException;
import br.com.postech.techchallenge.microservico.pagamento.exception.NotFoundException;
import br.com.postech.techchallenge.microservico.pagamento.model.request.PagamentoRequest;
import br.com.postech.techchallenge.microservico.pagamento.model.response.PagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.repository.HistoricoPagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.repository.PagamentoMongoRepository;
import br.com.postech.techchallenge.microservico.pagamento.repository.PagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.service.PagamentoService;
import br.com.postech.techchallenge.microservico.pagamento.service.integracao.ApiMicroServiceProducao;
import br.com.postech.techchallenge.microservico.pagamento.service.integracao.request.ProducaoRequest;
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
	private final PagamentoMongoRepository pagamentoMongoRepository;
	private final ApiMicroServiceProducao apiMicroServiceProducao;

	@Override
	public PagamentoResponse consultarStatusPagamentoPorPedido(Long numeroPedido) {
		var pagamentoDocumento = pagamentoMongoRepository.findByNumeroPedido(numeroPedido);
		if(!pagamentoDocumento.isPresent()) {
			Pagamento pagamentoEntity = pagamentoJpaRepository.findByNumeroPedido(numeroPedido)
					.orElseThrow(() -> new NotFoundException("Pedido não encontrado!"));

			MAPPER.typeMap(Pagamento.class, PagamentoResponse.class)
					.addMappings(mapperA -> mapperA.using(new StatusPagamentoParaInteiroConverter())
							.map(Pagamento::getStatusPagamento, PagamentoResponse::setStatusPagamento))
					.addMappings(mapperB -> mapperB.map(src -> src.getNumeroPedido(), PagamentoResponse::setNumeroPedido))
					.addMappings(mapperC -> {
						mapperC.map(src -> src.getId(), PagamentoResponse::setNumeroPagamento);
					});

			return MAPPER.map(pagamentoEntity, PagamentoResponse.class);
		}
		
		return MAPPER.map(pagamentoDocumento, PagamentoResponse.class);
	}

	@Override
	public List<PagamentoResponse> listarPagamentosPendentes() {
		List<Pagamento> pagamentosPendentes = pagamentoJpaRepository
				.findByStatusPagamentoIn(Arrays.asList(StatusPagamentoEnum.PENDENTE));

		MAPPER.typeMap(Pagamento.class, PagamentoResponse.class)
				.addMappings(mapperA -> mapperA.using(new StatusPagamentoParaInteiroConverter())
						.map(Pagamento::getStatusPagamento, PagamentoResponse::setStatusPagamento))
				.addMappings(mapperB -> mapperB.map(src -> src.getNumeroPedido(), PagamentoResponse::setNumeroPedido))
				.addMappings(mapperC -> {
					mapperC.map(src -> src.getId(), PagamentoResponse::setNumeroPagamento);
				});

		return MAPPER.map(pagamentosPendentes, new TypeToken<List<PagamentoResponse>>() {
		}.getType());
	}

	@Override
	public PagamentoResponse criarPagamento(PagamentoRequest pagamentoRequest) throws BusinessException {
		Pagamento pagamentoEntity = pagamentoJpaRepository
			.findByNumeroPedido(pagamentoRequest.numeroPedido())
			.orElse(null);
		
		if (Objects.isNull(pagamentoEntity)) {
			var pagamento = MAPPER.map(pagamentoRequest, Pagamento.class);
			pagamento.setStatusPagamento(StatusPagamentoEnum.get(pagamentoRequest.statusPagamento()));
			pagamento.setId(pagamentoRequest.numeroPagamento());
	
			Integer tentativas = historicoPagamentoJpaRepository.findByPagamentoNumeroPedido(pagamento.getNumeroPedido())
					.stream().map(HistoricoPagamento::getNumeroTentativas).max(Integer::compare).orElse(0);
	
			// Faz a atualizacao do pagamento
			pagamento.adicionaHistorico(
					HistoricoPagamento.adicionaHistorico(Constantes.AWAITING_PAYMENT, pagamento, null, tentativas));
	
			pagamento.setQrCodePix(Utilitario.gerarQrCodePix(pagamento.getValor()));
	
			pagamentoEntity = pagamentoJpaRepository.save(pagamento);
	
			apiMicroServiceProducao.salvarProducao(new ProducaoRequest(pagamentoEntity.getNumeroPedido(),
					Constantes.ORDER_OBSERVATION, SituacaoProducaoEnum.RECEBIDO.getValue()));
		}
		
		MAPPER.typeMap(Pagamento.class, PagamentoResponse.class)
				.addMappings(mapperA -> mapperA.using(new StatusPagamentoParaInteiroConverter())
						.map(Pagamento::getStatusPagamento, PagamentoResponse::setStatusPagamento))
				.addMappings(mapperB -> {
					mapperB.map(src -> src.getId(), PagamentoResponse::setNumeroPagamento);
				});
		
		var pagamentoResponse = MAPPER.map(pagamentoEntity, PagamentoResponse.class);
		var pagamentoDocumento = MAPPER.map(pagamentoResponse, PagamentoDocumento.class);

		pagamentoMongoRepository.save(pagamentoDocumento);
		
		return pagamentoResponse;
	}

	@Override
	public PagamentoResponse atualizaPagamento(PagamentoRequest pagamentoRequest) throws BusinessException {
		var pagamento = MAPPER.map(pagamentoRequest, Pagamento.class);
		pagamento.setStatusPagamento(StatusPagamentoEnum.get(pagamentoRequest.statusPagamento()));
		pagamento.setId(pagamentoRequest.numeroPagamento());

		Integer tentativas = historicoPagamentoJpaRepository.findByPagamentoNumeroPedido(pagamento.getNumeroPedido())
				.stream().map(HistoricoPagamento::getNumeroTentativas).max(Integer::compare).orElse(0);

		// Faz a atualizacao do pagamento
		Pagamento pagamentoEntity = pagamentoJpaRepository
				.findByNumeroPedidoAndQrCodePixAndDataPagamentoIsNull(pagamento.getNumeroPedido(),
						pagamento.getQrCodePix())
				.map(pagEntity -> {
					pagEntity.setDataPagamento(LocalDateTime.now());
					pagEntity.setStatusPagamento(pagamento.getStatusPagamento());
					pagEntity.getHistoricoPagamento().add(HistoricoPagamento.adicionaHistorico(
							Constantes.SUCESS_MAKE_PAYMENT, pagEntity, LocalDateTime.now(), tentativas));

					return pagamentoJpaRepository.save(pagEntity);
				}).orElseThrow(() -> new BusinessException("Pagamento não encontrado ou já processado!"));

		apiMicroServiceProducao.atualizarProducao(new ProducaoRequest(pagamentoEntity.getNumeroPedido(),
				Constantes.ORDER_OBSERVATION, SituacaoProducaoEnum.EM_PREPARACAO.getValue()));

		MAPPER.typeMap(Pagamento.class, PagamentoResponse.class)
				.addMappings(mapperA -> mapperA.using(new StatusPagamentoParaInteiroConverter())
						.map(Pagamento::getStatusPagamento, PagamentoResponse::setStatusPagamento))
				.addMappings(mapperC -> {
					mapperC.map(src -> src.getId(), PagamentoResponse::setNumeroPagamento);
				});
		
		var pagamentoResponse = MAPPER.map(pagamentoEntity, PagamentoResponse.class);
		var pagamentoDocumento = MAPPER.map(pagamentoResponse, PagamentoDocumento.class);

		pagamentoMongoRepository.save(pagamentoDocumento);

		return pagamentoResponse;
	}

}