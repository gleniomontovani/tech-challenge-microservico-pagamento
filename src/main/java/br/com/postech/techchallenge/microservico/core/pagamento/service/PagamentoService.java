package br.com.postech.techchallenge.microservico.core.pagamento.service;

import java.util.List;

import br.com.postech.techchallenge.microservico.core.pagamento.model.response.HistoricoPagamentoResponse;
import br.com.postech.techchallenge.microservico.core.pagamento.model.response.PagamentoResponse;

public interface PagamentoService {
	
	PagamentoResponse consultarStatusPagamentoPorPedido(Long numeroPedido);
	
	List<PagamentoResponse> listarPagamentosPendentes();
	
	List<HistoricoPagamentoResponse> listarHistoricoPagamentosPorPedido(Long numeroPedido);

}
