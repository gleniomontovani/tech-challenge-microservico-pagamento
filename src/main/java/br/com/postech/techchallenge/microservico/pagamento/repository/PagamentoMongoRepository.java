package br.com.postech.techchallenge.microservico.pagamento.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.postech.techchallenge.microservico.pagamento.domain.PagamentoDocumento;

@Repository
public interface PagamentoMongoRepository extends MongoRepository<PagamentoDocumento, Long>{

	@Query("{ 'numeroPedido' : ?0 }")
	Optional<PagamentoDocumento> findByNumeroPedido(Long numeroPedido);
}
