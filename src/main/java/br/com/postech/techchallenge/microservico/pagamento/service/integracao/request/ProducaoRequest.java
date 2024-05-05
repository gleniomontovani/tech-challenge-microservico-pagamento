package br.com.postech.techchallenge.microservico.pagamento.service.integracao.request;

public record ProducaoRequest(Long numeroPedido, String observacao, Integer situacaoProducao) {

}
