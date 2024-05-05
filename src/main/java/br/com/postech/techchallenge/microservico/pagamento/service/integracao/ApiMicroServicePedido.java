package br.com.postech.techchallenge.microservico.pagamento.service.integracao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.postech.techchallenge.microservico.pagamento.configuration.ControllerMappingConfig;
import br.com.postech.techchallenge.microservico.pagamento.exception.BusinessException;
import br.com.postech.techchallenge.microservico.pagamento.service.integracao.request.PedidoRequest;

@FeignClient(url = "${api.client.pedido.uri}", path = ControllerMappingConfig.PATH_API_PEDIDO, name = "pedidos")
public interface ApiMicroServicePedido {
	
	@RequestMapping(method = RequestMethod.PUT)
	void atualizarPedido(@RequestBody PedidoRequest pedidoRequest)throws BusinessException;
}
