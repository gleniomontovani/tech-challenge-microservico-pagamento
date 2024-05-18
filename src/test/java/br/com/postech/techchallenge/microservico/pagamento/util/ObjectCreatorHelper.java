package br.com.postech.techchallenge.microservico.pagamento.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import br.com.postech.techchallenge.microservico.pagamento.entity.HistoricoPagamento;
import br.com.postech.techchallenge.microservico.pagamento.entity.Pagamento;
import br.com.postech.techchallenge.microservico.pagamento.enums.StatusPagamentoEnum;
import br.com.postech.techchallenge.microservico.pagamento.model.request.PagamentoRequest;

public class ObjectCreatorHelper {

	public static Pagamento obterPagamento() {
		return Pagamento.builder()
				.dataPagamento(LocalDateTime.now())
				.numeroPedido(1L)
				.qrCodePix(Utilitario.gerarQrCodePix(BigDecimal.valueOf(100L)))
				.historicoPagamento(new ArrayList<>())
				.statusPagamento(StatusPagamentoEnum.PENDENTE)
				.valor(BigDecimal.valueOf(100L))
				.build();
	}
	
	public static PagamentoRequest obterRequisicaoPagamento(String qrCodePix) {
		return new PagamentoRequest(1L, 1L, 1, BigDecimal.valueOf(100), qrCodePix);
	}
	
	public static PagamentoRequest obterRequisicaoPagamentoSemID() {
		return new PagamentoRequest(null, 755L, 1, BigDecimal.valueOf(100), Utilitario.gerarQrCodePix(BigDecimal.valueOf(100L)));
	}
	
	public static HistoricoPagamento obterHistoricoPagamento() {
		var pagamento = obterPagamento();
		pagamento.setId(1L);
		return HistoricoPagamento.builder()
				.dataHistorico(LocalDateTime.now())
				.descricao(Constantes.SUCESS_MAKE_PAYMENT)
				.numeroTentativas(Constantes.INT_ZERO)
				.pagamento(pagamento)
				.build();
	}	
}
