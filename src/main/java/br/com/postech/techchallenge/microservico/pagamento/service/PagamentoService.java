package br.com.postech.techchallenge.microservico.pagamento.service;

import java.util.List;

import br.com.postech.techchallenge.microservico.pagamento.exception.BusinessException;
import br.com.postech.techchallenge.microservico.pagamento.model.request.PagamentoRequest;
import br.com.postech.techchallenge.microservico.pagamento.model.response.PagamentoResponse;

public interface PagamentoService {
	
	PagamentoResponse consultarStatusPagamentoPorPedido(Long numeroPedido);
	
	List<PagamentoResponse> listarPagamentosPendentes();
	
	PagamentoResponse criarPagamento(PagamentoRequest pagamentoRequest) throws BusinessException;
	
	PagamentoResponse atualizaPagamento(PagamentoRequest pagamentoRequest) throws BusinessException;
}
