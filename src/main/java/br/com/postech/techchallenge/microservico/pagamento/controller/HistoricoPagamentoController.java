package br.com.postech.techchallenge.microservico.pagamento.controller;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.postech.techchallenge.microservico.pagamento.model.response.HistoricoPagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.service.HistoricoPagamentoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/pagamentos/historico")
@RequiredArgsConstructor
public class HistoricoPagamentoController {
	private final HistoricoPagamentoService historicoPagamentoService;
	
	@GetMapping(path = "/{numeroPedido}", produces = MediaType.APPLICATION_JSON)
	public ResponseEntity<List<HistoricoPagamentoResponse>> consultarHistoricoPagamentoPorPedido(
			@PathVariable Long numeroPedido) throws Exception {
		List<HistoricoPagamentoResponse> historicoPagamento = historicoPagamentoService
				.listarHistoricoPagamentosPorPedido(numeroPedido);

		return new ResponseEntity<>(historicoPagamento, HttpStatus.OK);
	}
}
