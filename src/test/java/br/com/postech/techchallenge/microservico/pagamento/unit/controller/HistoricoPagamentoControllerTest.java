package br.com.postech.techchallenge.microservico.pagamento.unit.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.postech.techchallenge.microservico.pagamento.controller.HistoricoPagamentoController;
import br.com.postech.techchallenge.microservico.pagamento.handler.RestHandlerException;
import br.com.postech.techchallenge.microservico.pagamento.model.response.HistoricoPagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.service.HistoricoPagamentoService;
import br.com.postech.techchallenge.microservico.pagamento.util.Constantes;

class HistoricoPagamentoControllerTest {

	private MockMvc mockMvc;
	
	@Mock
	private HistoricoPagamentoService historicoPagamentoService;
	
	AutoCloseable openMocks;
	
	@BeforeEach
	void setUp() {
		openMocks = MockitoAnnotations.openMocks(this);
		HistoricoPagamentoController historicoPagamentoController = new HistoricoPagamentoController(historicoPagamentoService);
		mockMvc = MockMvcBuilders.standaloneSetup(historicoPagamentoController)
				.setControllerAdvice(new RestHandlerException())
				.addFilter((request, response, chain) -> {
					response.setCharacterEncoding(Constantes.UTF_8);
					chain.doFilter(request, response);
				}, "/*")
				.build();
	}
	
	@AfterEach
	void close() throws Exception {
		openMocks.close();
	}
	
	@Test
	void devePermitirConsultarHistoricoPagamentoPorPedido() throws Exception {
		var historicoPagamentoResponse1 = HistoricoPagamentoResponse.builder()
				.numeroPagamento(1L)
				.numeroPedido(1L)
				.dataHistorico(LocalDateTime.now().toString())
				.descricao(Constantes.SUCESS_MAKE_PAYMENT)
				.valor(BigDecimal.valueOf(100))
				.build();
		
		var historicoPagamentoResponse2 = HistoricoPagamentoResponse.builder()
				.numeroPagamento(2L)
				.numeroPedido(2L)
				.dataHistorico(LocalDateTime.now().toString())
				.descricao(Constantes.SUCESS_MAKE_PAYMENT)
				.valor(BigDecimal.valueOf(100))
				.build();
		
		var historicosPagamentos = Arrays.asList(historicoPagamentoResponse1, historicoPagamentoResponse2);
		
		when(historicoPagamentoService.listarHistoricoPagamentosPorPedido(anyLong())).thenReturn(historicosPagamentos);
		
		mockMvc.perform(get("/v1/pagamentos/historico/{numeroPedido}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.[0].numeroPagamento").value(historicoPagamentoResponse1.getNumeroPagamento()))
        .andExpect(jsonPath("$.[0].numeroPedido").value(historicoPagamentoResponse1.getNumeroPedido()))
        .andExpect(jsonPath("$.[0].descricao").value(historicoPagamentoResponse1.getDescricao()))
        .andExpect(jsonPath("$.[0].valor").value(historicoPagamentoResponse1.getValor()))
        .andExpect(jsonPath("$.[0].dataHistorico").exists());
		
		verify(historicoPagamentoService, times(1)).listarHistoricoPagamentosPorPedido(anyLong());		
	}
}
