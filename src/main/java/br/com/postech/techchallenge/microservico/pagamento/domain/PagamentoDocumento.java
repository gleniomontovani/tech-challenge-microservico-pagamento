package br.com.postech.techchallenge.microservico.pagamento.domain;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "pagamento")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoDocumento {

	@Id
	private Long numeroPagamento;
	private Long numeroPedido;
	private String dataPagamento;
	private Integer statusPagamento;
	private BigDecimal valor;
	private String qrCodePix;
}
