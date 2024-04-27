package br.com.postech.techchallenge.microservico.pagamento.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.postech.techchallenge.microservico.pagamento.enums.StatusPagamentoEnum;
import br.com.postech.techchallenge.microservico.pagamento.util.Constantes;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pagamento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Generated 
public class Pagamento implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "pedido_id", nullable = false)
	private Long numeroPedido;
		
	@Column(name = "data_pagamento", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime dataPagamento;
	
	@Type(value = br.com.postech.techchallenge.microservico.pagamento.enums.AssociacaoType.class,
            parameters = {@Parameter(name = Constantes.ENUM_CLASS_NAME, value = "StatusPagamentoEnum")})
    @Column(name = "status_pagamento_id")
	private StatusPagamentoEnum statusPagamento;
	
	@Column(name = "valor", nullable = false, precision = 10, scale = 2)
	private BigDecimal valor;
	
	@Column(name = "qr_code_pix", nullable = false)
	private String qrCodePix;
	
	@OneToMany(mappedBy = "pagamento", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<HistoricoPagamento> historicoPagamento;
	
	public void adicionaHistorico(HistoricoPagamento historicoPagamento) {
		if (Objects.isNull(this.historicoPagamento)) {
			this.historicoPagamento = new ArrayList<HistoricoPagamento>();
		}
		
		this.historicoPagamento.add(historicoPagamento);
	}
}
