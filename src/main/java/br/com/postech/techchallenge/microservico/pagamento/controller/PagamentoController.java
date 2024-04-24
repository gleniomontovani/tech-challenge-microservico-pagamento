package br.com.postech.techchallenge.microservico.pagamento.controller;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.postech.techchallenge.microservico.pagamento.exception.BusinessException;
import br.com.postech.techchallenge.microservico.pagamento.model.request.PagamentoRequest;
import br.com.postech.techchallenge.microservico.pagamento.model.response.HistoricoPagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.model.response.PagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/pagamentos")
@RequiredArgsConstructor
@Slf4j
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
    
    @PostMapping
	public ResponseEntity<PagamentoResponse> criarPagamento(@RequestBody PagamentoRequest pagamentoRequest) {	
    	PagamentoResponse response = null;
    	try {
			response = pagamentoService.criarPagamento(pagamentoRequest);			
		}catch (Exception e) {
	        // Verifica se a exceção é uma DataIntegrityViolationException
	        if (e instanceof DataIntegrityViolationException) {
	            // Lidar com a exceção DataIntegrityViolationException
	        	log.error("Já existe este registro.");	            
	        } else {
	            // Lidar com outras exceções, se necessário
	        	log.error("Pagamento não encontrado!");
	        }
	        throw new BusinessException("Não foi possivel atualizar o pagamento!");
		}
    	
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}