package br.com.postech.techchallenge.microservico.pagamento.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.postech.techchallenge.microservico.pagamento.entity.Pagamento;
import br.com.postech.techchallenge.microservico.pagamento.enums.StatusPagamentoEnum;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long>{

	Optional<Pagamento> findByNumeroPedido(Long numeroPedido);
	
	List<Pagamento> findByStatusPagamentoIn(List<StatusPagamentoEnum> statusPagamentos);
	
	Optional<Pagamento> findByNumeroPedidoAndQrCodePixAndDataPagamentoIsNull(Long numeroPedido, String qrCodePix);
}
