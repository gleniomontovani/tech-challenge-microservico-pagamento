package br.com.postech.techchallenge.microservico.pagamento.bdd;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import br.com.postech.techchallenge.microservico.pagamento.configuration.ControllerMappingConfig;
import br.com.postech.techchallenge.microservico.pagamento.enums.StatusPagamentoEnum;
import br.com.postech.techchallenge.microservico.pagamento.model.request.PagamentoRequest;
import br.com.postech.techchallenge.microservico.pagamento.model.response.PagamentoResponse;
import br.com.postech.techchallenge.microservico.pagamento.util.ObjectCreatorHelper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;

public class StepDefinitionPagamento {

	private Response response;
	private PagamentoResponse pagamentoResponse;
	
	// - ###################################################
	// - Criar um novo pagamento para um determinado pedido
	// - ###################################################
	@Quando("submeter uma nova requisição de criação de pagamento")
	public PagamentoResponse submeter_uma_nova_requisição_de_criação_de_pagamento() {
	    var pagamentoRequest = ObjectCreatorHelper.obterRequisicaoPagamentoSemID();
	    
	    response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(pagamentoRequest)
				.when()
				.post(ControllerMappingConfig.ENDPOINT_MICRO_SERVICE_PAGAMENTO_LOCAL);	
	    
	    return response.then().extract().as(PagamentoResponse.class);
	}
	
	@Então("o pagamento é registrado com sucesso")
	public void o_pagamento_é_registrado_com_sucesso() {
		response.then()
	        .statusCode(HttpStatus.CREATED.value())
	        .body(matchesJsonSchemaInClasspath("./schemas/PagamentoResponseSchema.json"));
	}
	
	// - ###################################################
	// - Buscar um pagamento de um determinado pedido
	// - ###################################################
	@Dado("que o pagamento desse pedido já foi cadastrado")
	public void que_o_pagamento_desse_pedido_já_foi_cadastrado() {
	    pagamentoResponse = submeter_uma_nova_requisição_de_criação_de_pagamento();
	}
	
	@Quando("requisitar a busca de um pagamento pelo número do pedido")
	public void requisitar_a_busca_de_um_pagamento_pelo_número_do_pedido() {
		response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(ControllerMappingConfig.ENDPOINT_MICRO_SERVICE_PAGAMENTO_LOCAL+"/{numeroPedido}", 
                		pagamentoResponse.getNumeroPedido());
	}
	
	@Então("o pagamento é exibido com sucesso")
	public void o_pagamento_é_exibido_com_sucesso() {
		response.then()
	    	.statusCode(HttpStatus.OK.value())
	    	.body(matchesJsonSchemaInClasspath("./schemas/PagamentoResponseSchema.json"));
	}
	
	// - ###################################################
	// - Atualizar um pagamento de um determinado pedido
	// - ###################################################
	@Quando("requisitar um alteração do status do pagamento")
	public void requisitar_um_alteração_do_status_do_pagamento() {
		var pagamentoRequest = new PagamentoRequest(pagamentoResponse.getNumeroPagamento(), 
				  pagamentoResponse.getNumeroPedido(),
				  StatusPagamentoEnum.APROVADO.getValue(), 
				  pagamentoResponse.getValor(),
				  pagamentoResponse.getQrCodePix());

		response = given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(pagamentoRequest)
			.when()
			.put(ControllerMappingConfig.ENDPOINT_MICRO_SERVICE_PAGAMENTO_LOCAL);
	}
	
	@Então("o pagamento é atualizado com sucesso")
	public void o_pagamento_é_atualizado_com_sucesso() {
		response.then()
			.statusCode(HttpStatus.OK.value())
			.body(matchesJsonSchemaInClasspath("./schemas/PagamentoResponseSchema.json"));
	}
}
