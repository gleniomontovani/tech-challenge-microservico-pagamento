package br.com.postech.techchallenge.microservico.pagamento.service;

import java.util.List;

import br.com.postech.techchallenge.microservico.pagamento.model.request.PagamentoRequest;
import br.com.postech.techchallenge.microservico.pagamento.model.response.HistoricoPagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.model.response.PagamentoResponse;

public interface PagamentoService {
	
	PagamentoResponse consultarStatusPagamentoPorPedido(Long numeroPedido);
	
	List<PagamentoResponse> listarPagamentosPendentes();
	
	List<HistoricoPagamentoResponse> listarHistoricoPagamentosPorPedido(Long numeroPedido);
	
	PagamentoResponse criarPagamento(PagamentoRequest pagamento) throws Exception;
}
