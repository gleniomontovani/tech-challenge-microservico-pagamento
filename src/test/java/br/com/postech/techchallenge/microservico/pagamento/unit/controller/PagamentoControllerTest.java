package br.com.postech.techchallenge.microservico.pagamento.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.postech.techchallenge.microservico.pagamento.controller.PagamentoController;
import br.com.postech.techchallenge.microservico.pagamento.handler.RestHandlerException;
import br.com.postech.techchallenge.microservico.pagamento.model.request.PagamentoRequest;
import br.com.postech.techchallenge.microservico.pagamento.model.response.PagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.service.PagamentoService;
import br.com.postech.techchallenge.microservico.pagamento.util.Constantes;
import br.com.postech.techchallenge.microservico.pagamento.util.ObjectCreatorHelper;
import br.com.postech.techchallenge.microservico.pagamento.util.Utilitario;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

class PagamentoControllerTest {

	private MockMvc mockMvc;	

	@Mock
	private PagamentoService pagamentoService;
	
	AutoCloseable openMocks;
	
	@BeforeEach
	void setUp() {
		openMocks = MockitoAnnotations.openMocks(this);
		PagamentoController pagamentoController = new PagamentoController(pagamentoService);
		mockMvc = MockMvcBuilders.standaloneSetup(pagamentoController)
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
	void devePermitirCriarPagamento() throws Exception {
		var pagamentoRequest = ObjectCreatorHelper.obterRequisicaoPagamento(null);
		
		when(pagamentoService.criarPagamento(any(PagamentoRequest.class))).thenReturn(PagamentoResponse.builder().build());
		
		mockMvc.perform(post("/v1/pagamentos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Utilitario.asJsonString(pagamentoRequest)))
		.andExpect(status().isCreated());
		
		verify(pagamentoService, times(1)).criarPagamento(any(PagamentoRequest.class));
	}

	@Test
	void devePermitirAtualizaPagamento() throws Exception {
		var pagamentoRequest = ObjectCreatorHelper.obterRequisicaoPagamento(Utilitario.gerarQrCodePix(BigDecimal.valueOf(100L)));
		var pagamentoResponse = PagamentoResponse.builder()
				.numeroPagamento(pagamentoRequest.numeroPagamento())
				.numeroPedido(pagamentoRequest.numeroPedido())
				.statusPagamento(pagamentoRequest.statusPagamento())
				.qrCodePix(pagamentoRequest.qrCodePix())
				.valor(pagamentoRequest.valor())
				.build();
		
		
		when(pagamentoService.atualizaPagamento(any(PagamentoRequest.class))).thenReturn(pagamentoResponse);
		
		mockMvc.perform(put("/v1/pagamentos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Utilitario.asJsonString(pagamentoRequest)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.numeroPagamento").value(pagamentoResponse.getNumeroPagamento()))
        .andExpect(jsonPath("$.numeroPedido").value(pagamentoResponse.getNumeroPedido()))
        .andExpect(jsonPath("$.statusPagamento").value(pagamentoResponse.getStatusPagamento()))
        .andExpect(jsonPath("$.valor").value(pagamentoResponse.getValor()))
        .andExpect(jsonPath("$.qrCodePix").value(pagamentoResponse.getQrCodePix()));
		
		verify(pagamentoService, times(1)).atualizaPagamento(any(PagamentoRequest.class));
	}
	
	@Test
	void devePermitirConsultarPagamentoPorPedido() throws Exception {
		var pagamentoResponse = PagamentoResponse.builder()
				.numeroPagamento(1L)
				.numeroPedido(1L)
				.statusPagamento(1)
				.qrCodePix(Utilitario.gerarQrCodePix(BigDecimal.valueOf(100L)))
				.valor(BigDecimal.valueOf(100L))
				.build();
		
		when(pagamentoService.consultarStatusPagamentoPorPedido(anyLong())).thenReturn(pagamentoResponse);
		
		mockMvc.perform(get("/v1/pagamentos/{numeroPedido}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.numeroPagamento").value(pagamentoResponse.getNumeroPagamento()))
        .andExpect(jsonPath("$.numeroPedido").value(pagamentoResponse.getNumeroPedido()))
        .andExpect(jsonPath("$.statusPagamento").value(pagamentoResponse.getStatusPagamento()))
        .andExpect(jsonPath("$.valor").value(pagamentoResponse.getValor()))
        .andExpect(jsonPath("$.qrCodePix").value(pagamentoResponse.getQrCodePix()));
		
		verify(pagamentoService, times(1)).consultarStatusPagamentoPorPedido(anyLong());		
	}
}
