package br.com.postech.techchallenge.microservico.pagamento.service.impl;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
import br.com.postech.techchallenge.microservico.pagamento.model.DadosEnvioPix;
import br.com.postech.techchallenge.microservico.pagamento.model.request.PagamentoRequest;
import br.com.postech.techchallenge.microservico.pagamento.model.response.HistoricoPagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.model.response.PagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.repository.HistoricoPagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.repository.PagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.service.PagamentoService;
import br.com.postech.techchallenge.microservico.pagamento.util.Constantes;
import br.com.postech.techchallenge.microservico.pagamento.util.QRCodePix;
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

	@Override
	public PagamentoResponse criarPagamento(PagamentoRequest pagamentoRequest) throws Exception {	
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
		pagamento.setStatusPagamento(pagamento.getStatusPagamento());
		pagamento.getHistoricoPagamento().add(HistoricoPagamento.adicionaHistorico(descricaoHistorico,
				pagamento, dataPagamento, tentativas));

		final var dadosPix = new DadosEnvioPix(
				Constantes.NOME_DESTINATARIO_PIX_QRCODE,
				Constantes.CHAVE_DESTINATARIO_PIX_QRCODE, 
				pagamento.getValor(),
				Constantes.CIDADE_DESTINATARIO_PIX_QRCODE, 
				String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli()));

        final var qrCodePix = new QRCodePix(dadosPix);
        qrCodePix.save(Path.of(Constantes.IMAGEM_QRCODE_PATH));
		
        pagamento.setQrCodePix(qrCodePix.toString());
        
		pagamentoEntity = pagamentoJpaRepository.save(pagamento);
				
		MAPPER.typeMap(Pagamento.class, PagamentoResponse.class)
			.addMappings(mapperA -> mapperA.using(new StatusPagamentoParaInteiroConverter())
					.map(Pagamento::getStatusPagamento, PagamentoResponse::setStatusPagamento))
			.addMappings(mapperC -> {
				mapperC.map(src -> src.getId(), PagamentoResponse::setNumeroPagamento);
		});
		
		return MAPPER.map(pagamentoEntity, PagamentoResponse.class);
	}
}