# language: pt

Funcionalidade: API Microserviço de Pagamentos
		
	Cenário: Criar pagamento do pedido
		Quando submeter uma nova requisição de criação de pagamento
		Então o pagamento é registrado com sucesso	
			
	Cenário: Buscar um pagamento existente
		Dado que o pagamento desse pedido já foi cadastrado
		Quando requisitar a busca de um pagamento pelo número do pedido
		Então o pagamento é exibido com sucesso
				
#	Cenário: Atualizar um pagamento de um pedido
#		Dado que o pagamento desse pedido já foi cadastrado
#		Quando requisitar um alteração do status do pagamento
#		Então o pagamento é atualizado com sucesso
		