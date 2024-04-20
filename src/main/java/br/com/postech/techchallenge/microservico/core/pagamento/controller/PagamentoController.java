package br.com.postech.techchallenge.microservico.core.pagamento.controller;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.postech.techchallenge.microservico.core.pagamento.model.response.HistoricoPagamentoResponse;
import br.com.postech.techchallenge.microservico.core.pagamento.model.response.PagamentoResponse;
import br.com.postech.techchallenge.microservico.core.pagamento.service.PagamentoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {	
	private final PagamentoService pagamentoService;
	
    @GetMapping(path = "/{numeroPedido}", produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<PagamentoResponse> consultarPagamentoPorPedido(@PathVariable Long numeroPedido) throws Exception {
    	PagamentoResponse pagamento = pagamentoService.consultarStatusPagamentoPorPedido(numeroPedido);
        
    	return new ResponseEntity<>(pagamento, HttpStatus.OK);
    } 
    
    @GetMapping(path = "/historico/{numeroPedido}", produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<List<HistoricoPagamentoResponse>> consultarHistoricoPagamentoPorPedido(@PathVariable Long numeroPedido) throws Exception {
    	List<HistoricoPagamentoResponse> historicoPagamento = pagamentoService.listarHistoricoPagamentosPorPedido(numeroPedido);

        return new ResponseEntity<>(historicoPagamento, HttpStatus.OK);
    }
}
