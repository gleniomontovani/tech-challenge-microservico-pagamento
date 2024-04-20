package br.com.postech.techchallenge.microservico.core.pagamento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.postech.techchallenge.microservico.core.pagamento.entity.HistoricoPagamento;

@Repository
public interface HistoricoPagamentoRepository extends JpaRepository<HistoricoPagamento, Long>{
	
	List<HistoricoPagamento> findByPagamentoId(Long numeroPagamento);
}