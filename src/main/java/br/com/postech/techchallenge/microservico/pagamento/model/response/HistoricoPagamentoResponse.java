package br.com.postech.techchallenge.microservico.pagamento.model.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoPagamentoResponse {

	private Long numeroPagamento;
	private Long numeroPedido;
	private String descricao;
	private String dataPagamento;
	private String dataHistorico;
	private BigDecimal valor;
	private Integer numeroTentativas;
}
