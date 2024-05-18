package br.com.postech.techchallenge.microservico.pagamento.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.postech.techchallenge.microservico.pagamento.entity.Pagamento;
import br.com.postech.techchallenge.microservico.pagamento.enums.StatusPagamentoEnum;
import br.com.postech.techchallenge.microservico.pagamento.repository.PagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.util.ObjectCreatorHelper;
import br.com.postech.techchallenge.microservico.pagamento.util.Utilitario;

class PagamentoRepositoryTest {
	
	@Mock
	private PagamentoRepository pagamentoRepository;

	AutoCloseable openMocks;
	
	@BeforeEach
	void setUp() {
		openMocks = MockitoAnnotations.openMocks(this);
	}
	
	@AfterEach
	void close() throws Exception {
	    openMocks.close();
	}
	
	@Test
	void devePoderSalvarPagamento() {
		var pagamentoModel = ObjectCreatorHelper.obterPagamento();
		pagamentoModel.setId(1L);

		when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoModel);

		var pagamento = pagamentoRepository.save(pagamentoModel);

		verify(pagamentoRepository, times(1)).save(pagamentoModel);

		assertThat(pagamento).isInstanceOf(Pagamento.class).isNotNull().isEqualTo(pagamentoModel);

		assertThat(pagamento).extracting(Pagamento::getId).isEqualTo(pagamentoModel.getId());

		assertThat(pagamento).extracting(Pagamento::getNumeroPedido).isEqualTo(pagamentoModel.getNumeroPedido());

		assertThat(pagamento).extracting(Pagamento::getStatusPagamento).isEqualTo(pagamentoModel.getStatusPagamento());

		assertThat(pagamento).extracting(Pagamento::getValor).isEqualTo(pagamentoModel.getValor());

		assertThat(pagamento).extracting(Pagamento::getQrCodePix).isEqualTo(pagamentoModel.getQrCodePix());		
	}
	
	@Test
	void deveBuscarPedidoPeloNumero() {
		var pagamentoModel = ObjectCreatorHelper.obterPagamento();
		pagamentoModel.setId(1L);

		when(pagamentoRepository.findByNumeroPedido(anyLong())).thenReturn(Optional.of(pagamentoModel));

		var pagamento = pagamentoRepository.findByNumeroPedido(1L);

		verify(pagamentoRepository, times(1)).findByNumeroPedido(1L);

		assertThat(pagamento).isPresent().containsSame(pagamentoModel);

		pagamento.ifPresent(pagamentoSavo -> {
			assertThat(pagamentoSavo.getId()).isEqualTo(pagamentoModel.getId());
			assertThat(pagamentoSavo.getNumeroPedido()).isEqualTo(pagamentoModel.getNumeroPedido());
			assertThat(pagamentoSavo.getStatusPagamento()).isEqualTo(pagamentoModel.getStatusPagamento());
			assertThat(pagamentoSavo.getValor()).isEqualTo(pagamentoModel.getValor());
			assertThat(pagamentoSavo.getQrCodePix()).isEqualTo(pagamentoModel.getQrCodePix());
		});		
	}

	@Test
	void deveBuscarPagamentoPorListaDeStatusPagamento() {
		var pagamentoModel1 = ObjectCreatorHelper.obterPagamento();
		var pagamentoModel2 = ObjectCreatorHelper.obterPagamento();
		pagamentoModel1.setId(1L);
		pagamentoModel2.setId(2L);

		List<Pagamento> pagamentos = Arrays.asList(pagamentoModel1, pagamentoModel2);
		
		when(pagamentoRepository.findByStatusPagamentoIn(anyList())).thenReturn(pagamentos);

		var pagamentosByStatus = pagamentoRepository.findByStatusPagamentoIn(Arrays.asList(StatusPagamentoEnum.PENDENTE));

		verify(pagamentoRepository, times(1)).findByStatusPagamentoIn(anyList());

		assertThat(pagamentosByStatus).hasSize(2).containsExactlyInAnyOrder(pagamentoModel1, pagamentoModel2);	
	}

	@Test
	void deveBuscarPagamentoPorNumeroPedidoEQrCodePix_QuandoDataPagamentoForNulo() {
		var pagamentoModel = ObjectCreatorHelper.obterPagamento();
		pagamentoModel.setId(1L);

		when(pagamentoRepository.findByNumeroPedidoAndQrCodePixAndDataPagamentoIsNull(anyLong(), anyString())).thenReturn(Optional.of(pagamentoModel));

		var pagamento = pagamentoRepository.findByNumeroPedidoAndQrCodePixAndDataPagamentoIsNull(1L, Utilitario.gerarQrCodePix(BigDecimal.valueOf(100L)));

		verify(pagamentoRepository, times(1)).findByNumeroPedidoAndQrCodePixAndDataPagamentoIsNull(anyLong(), anyString());

		assertThat(pagamento).isPresent().containsSame(pagamentoModel);

		pagamento.ifPresent(pagamentoSavo -> {
			assertThat(pagamentoSavo.getId()).isEqualTo(pagamentoModel.getId());
			assertThat(pagamentoSavo.getNumeroPedido()).isEqualTo(pagamentoModel.getNumeroPedido());
			assertThat(pagamentoSavo.getStatusPagamento()).isEqualTo(pagamentoModel.getStatusPagamento());
			assertThat(pagamentoSavo.getValor()).isEqualTo(pagamentoModel.getValor());
			assertThat(pagamentoSavo.getQrCodePix()).isEqualTo(pagamentoModel.getQrCodePix());
		});		
	}
}
