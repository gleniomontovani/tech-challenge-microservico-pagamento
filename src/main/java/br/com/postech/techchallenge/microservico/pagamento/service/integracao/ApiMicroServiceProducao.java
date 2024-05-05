package br.com.postech.techchallenge.microservico.pagamento.service.integracao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.postech.techchallenge.microservico.pagamento.configuration.ControllerMappingConfig;
import br.com.postech.techchallenge.microservico.pagamento.exception.BusinessException;
import br.com.postech.techchallenge.microservico.pagamento.service.integracao.request.ProducaoRequest;

@FeignClient(url = "${api.client.producao.uri}", path = ControllerMappingConfig.PATH_API_PRODUCAO, name = "producao")
public interface ApiMicroServiceProducao {

	@RequestMapping(method = RequestMethod.POST)
	void salvarProducao(@RequestBody ProducaoRequest producaoRequest)throws BusinessException;
	
	@RequestMapping(method = RequestMethod.PUT)
	void atualizarProducao(@RequestBody ProducaoRequest producaoRequest)throws BusinessException;
}
