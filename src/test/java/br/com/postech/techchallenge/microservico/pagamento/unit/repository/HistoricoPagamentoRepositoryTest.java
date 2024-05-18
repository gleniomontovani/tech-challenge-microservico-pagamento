package br.com.postech.techchallenge.microservico.pagamento.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.postech.techchallenge.microservico.pagamento.repository.HistoricoPagamentoRepository;
import br.com.postech.techchallenge.microservico.pagamento.util.ObjectCreatorHelper;

class HistoricoPagamentoRepositoryTest {

	@Mock
	private HistoricoPagamentoRepository historicoPagamentoRepository;
	
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
	void deveBuscarHistoricoPagamentoPorNumeroPedido() {
		var historicoModel1 = ObjectCreatorHelper.obterHistoricoPagamento();
		historicoModel1.setId(1L);
		
		var historicoModel2 = ObjectCreatorHelper.obterHistoricoPagamento();
		historicoModel2.setId(2L);
		
		var historicoPagamentos = Arrays.asList(historicoModel1, historicoModel2);
		
		when(historicoPagamentoRepository.findByPagamentoNumeroPedido(anyLong())).thenReturn(historicoPagamentos);
		
		var listaHistoricoPagamentos = historicoPagamentoRepository.findByPagamentoNumeroPedido(1L);
		
		verify(historicoPagamentoRepository, times(1)).findByPagamentoNumeroPedido(anyLong());
		
		assertThat(listaHistoricoPagamentos).hasSize(2).containsExactlyInAnyOrder(historicoModel1, historicoModel2);
	}
}
