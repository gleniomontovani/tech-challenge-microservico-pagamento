package br.com.postech.techchallenge.microservico.core.pagamento.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.postech.techchallenge.microservico.core.comum.enums.StatusPagamentoEnum;
import br.com.postech.techchallenge.microservico.core.pagamento.entity.Pagamento;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long>{

	Optional<Pagamento> findByNumeroPedido(Long numeroPedido);
	
	List<Pagamento> findByStatusPagamentoIn(List<StatusPagamentoEnum> statusPagamentos);
}
