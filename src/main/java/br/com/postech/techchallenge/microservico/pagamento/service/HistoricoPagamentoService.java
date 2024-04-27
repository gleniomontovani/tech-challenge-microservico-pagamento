package br.com.postech.techchallenge.microservico.pagamento.service;

import java.util.List;

import br.com.postech.techchallenge.microservico.pagamento.model.response.HistoricoPagamentoResponse;

public interface HistoricoPagamentoService {
	
	List<HistoricoPagamentoResponse> listarHistoricoPagamentosPorPedido(Long numeroPedido);

}