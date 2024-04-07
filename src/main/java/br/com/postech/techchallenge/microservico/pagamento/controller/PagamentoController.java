package br.com.postech.techchallenge.microservico.pagamento.controller;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.postech.techchallenge.microservico.pagamento.configuration.ModelMapperConfiguration;
import br.com.postech.techchallenge.microservico.pagamento.model.response.HistoricoPagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.model.response.PagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.service.PagamentoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {
	private static final ModelMapper MAPPER = ModelMapperConfiguration.getModelMapper();
	
	private final PagamentoService pagamentoService;
	
    @GetMapping(path = "/{numeroPedido}", produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<PagamentoResponse> consultarPagamentoPorPedido(@PathVariable Long numeroPedido) throws Exception {
//    	Pagamento pagamento = pagamentoService.consultarStatusPagamentoPorPedido(numeroPedido);
//    	
//    	PagamentoResponse response = MAPPER.map(pagamento, PagamentoResponse.class);
//    	
//        return new ResponseEntity<>(response, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.OK);
    } 
    
    @GetMapping(path = "/historico/{numeroPedido}", produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<List<HistoricoPagamentoResponse>> consultarHistoricoPagamentoPorPedido(@PathVariable Long numeroPedido) throws Exception {
//    	List<HistoricoPagamento> historicoPagamento = pagamentoService.listarHistoricoPagamentosPorPedido(numeroPedido);
//    	
//		List<HistoricoPagamentoResponse> response = MAPPER.map(historicoPagamento,
//				new TypeToken<List<HistoricoPagamentoResponse>>() {
//				}.getType());
//    	
//        return new ResponseEntity<>(response, HttpStatus.OK);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
