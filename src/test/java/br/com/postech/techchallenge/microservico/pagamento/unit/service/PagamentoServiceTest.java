package br.com.postech.techchallenge.microservico.pagamento.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.postech.techchallenge.microservico.pagamento.entity.Pagamento;
import br.com.postech.techchallenge.microservico.pagamento.model.response.PagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.repository.HistoricoPagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.repository.PagamentoMongoRepository;
import br.com.postech.techchallenge.microservico.pagamento.repository.PagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.service.PagamentoService;
import br.com.postech.techchallenge.microservico.pagamento.service.impl.PagamentoServiceImpl;
import br.com.postech.techchallenge.microservico.pagamento.service.integracao.ApiMicroServiceProducao;
import br.com.postech.techchallenge.microservico.pagamento.util.ObjectCreatorHelper;

class PagamentoServiceTest {
	
	private PagamentoService pagamentoService;
	@Mock
	private PagamentoRepository pagamentoRepository;
	@Mock
	private HistoricoPagamentoRepository historicoPagamentoRepository;
	@Mock
	private PagamentoMongoRepository pagamentoMongoRepository;
	@Mock
	private ApiMicroServiceProducao apiMicroServiceProducao;

	AutoCloseable openMocks;
	
	@BeforeEach
	void setUp() {
		openMocks = MockitoAnnotations.openMocks(this);
		pagamentoService = new PagamentoServiceImpl(pagamentoRepository, historicoPagamentoRepository,
				pagamentoMongoRepository, apiMicroServiceProducao);
	}
	
	@AfterEach
	void close() throws Exception {
		openMocks.close();
	}
	
	@Nested
	class ManipularPagamento {
		@Test
		void devePermitirCriarPagamento() throws Exception {
			var pagamentoRequestModel = ObjectCreatorHelper.obterRequisicaoPagamento(null);
			
			var historicoModel1 = ObjectCreatorHelper.obterHistoricoPagamento();
			historicoModel1.setId(1L);
			
			var historicoModel2 = ObjectCreatorHelper.obterHistoricoPagamento();
			historicoModel2.setId(2L);
			
			var historicoPagamentos = Arrays.asList(historicoModel1, historicoModel2);
			
			when(historicoPagamentoRepository.findByPagamentoNumeroPedido(anyLong())).thenReturn(historicoPagamentos);

			when(pagamentoRepository.save(any(Pagamento.class)))
				.thenAnswer(p -> p.getArgument(0));
			
			
			var pagamentoSave = pagamentoService.criarPagamento(pagamentoRequestModel);
				
			assertThat(pagamentoSave).isInstanceOf(PagamentoResponse.class).isNotNull();
			assertThat(pagamentoSave.getNumeroPedido()).isEqualTo(pagamentoRequestModel.numeroPedido());
			assertThat(pagamentoSave.getStatusPagamento()).isEqualTo(pagamentoRequestModel.statusPagamento());
			assertThat(pagamentoSave.getValor()).isEqualTo(pagamentoRequestModel.valor());
			assertThat(pagamentoSave.getQrCodePix()).isNotNull();		
			
			verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
			verify(historicoPagamentoRepository, times(1)).findByPagamentoNumeroPedido(anyLong());
		}
		
		@Test
		void devePermitirAtualizaPagamento() throws Exception {
			var pagamentoModel = ObjectCreatorHelper.obterPagamento();
			var pagamentoRequestModel = ObjectCreatorHelper.obterRequisicaoPagamento(pagamentoModel.getQrCodePix());
			
			var historicoModel1 = ObjectCreatorHelper.obterHistoricoPagamento();
			historicoModel1.setId(1L);
			
			var historicoModel2 = ObjectCreatorHelper.obterHistoricoPagamento();
			historicoModel2.setId(2L);
			
			var historicoPagamentos = Arrays.asList(historicoModel1, historicoModel2);
					
			when(historicoPagamentoRepository.findByPagamentoNumeroPedido(anyLong())).thenReturn(historicoPagamentos);
			
			when(pagamentoRepository.findByNumeroPedidoAndQrCodePixAndDataPagamentoIsNull(anyLong(), anyString())).thenReturn(Optional.of(pagamentoModel));
			when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(p -> p.getArgument(0));
			
			var pagamentoSave = pagamentoService.atualizaPagamento(pagamentoRequestModel);
			
			assertThat(pagamentoSave).isInstanceOf(PagamentoResponse.class).isNotNull();
			assertThat(pagamentoSave.getNumeroPedido()).isEqualTo(pagamentoRequestModel.numeroPedido());
			assertThat(pagamentoSave.getStatusPagamento()).isEqualTo(pagamentoRequestModel.statusPagamento());
			assertThat(pagamentoSave.getValor()).isEqualTo(pagamentoRequestModel.valor());
			assertThat(pagamentoSave.getQrCodePix()).isNotNull();	
			assertThat(pagamentoSave.getQrCodePix()).isEqualTo(pagamentoRequestModel.qrCodePix());
			
			verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
			verify(pagamentoRepository, times(1)).findByNumeroPedidoAndQrCodePixAndDataPagamentoIsNull(anyLong(), anyString());
			verify(historicoPagamentoRepository, times(1)).findByPagamentoNumeroPedido(anyLong());		
		}
	}
	
	@Nested
	class ObterPagamento {
		@Test
		void devePermitirConsultarStatusPagamentoPorPedido() {
			var pagamentoModel = ObjectCreatorHelper.obterPagamento();
			pagamentoModel.setId(1L);
			
			when(pagamentoRepository.findByNumeroPedido(anyLong())).thenReturn(Optional.of(pagamentoModel));
			
			var pagamento = pagamentoService.consultarStatusPagamentoPorPedido(1L);
			
			verify(pagamentoRepository, times(1)).findByNumeroPedido(1L);
			
			assertThat(pagamento.getNumeroPagamento()).isEqualTo(pagamentoModel.getId());
			assertThat(pagamento.getNumeroPedido()).isEqualTo(pagamentoModel.getNumeroPedido());
			assertThat(pagamento.getStatusPagamento()).isEqualTo(pagamentoModel.getStatusPagamento().getValue());
			assertThat(pagamento.getValor()).isEqualTo(pagamentoModel.getValor());
			assertThat(pagamento.getQrCodePix()).isEqualTo(pagamentoModel.getQrCodePix());				
		}

		@Test
		void devePermitirListarPagamentosPendentes() {
			var pagamentoModel1 = ObjectCreatorHelper.obterPagamento();
			pagamentoModel1.setId(1L);
			
			var pagamentoModel2 = ObjectCreatorHelper.obterPagamento();
			pagamentoModel2.setId(2L);
			
			List<Pagamento> pagamentos = Arrays.asList(pagamentoModel1, pagamentoModel2);
			
			when(pagamentoRepository.findByStatusPagamentoIn(anyList())).thenReturn(pagamentos);
			
			var pagamentoSalvos = pagamentoService.listarPagamentosPendentes();
			
			verify(pagamentoRepository, times(1)).findByStatusPagamentoIn(anyList());
			
			assertThat(pagamentoSalvos).hasSize(2);
			assertThat(pagamentoSalvos)
				.asList()
				.allSatisfy(pagamento ->{
					assertThat(pagamento).isNotNull();
					assertThat(pagamento).isInstanceOf(PagamentoResponse.class);
				});
		}
	}
}
