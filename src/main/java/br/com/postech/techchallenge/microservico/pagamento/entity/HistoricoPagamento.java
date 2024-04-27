package br.com.postech.techchallenge.microservico.pagamento.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "historico_pagamento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HistoricoPagamento implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = true, length = 255)
	private String descricao;
	
	@ManyToOne
    @JoinColumn(name = "pagamento_id")
	private Pagamento pagamento;
	
	@Column(name = "data_historico", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime dataHistorico;
	
	@Column(name = "data_pagamento", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime dataPagamento;
	
	@Column(name = "numero_tentativas")
	private Integer numeroTentativas;
	
	public static HistoricoPagamento adicionaHistorico(String descricaoHistorico, Pagamento pagamento, LocalDateTime dataPagamento, Integer tentativas)  {
		return new HistoricoPagamento(null, descricaoHistorico, pagamento, LocalDateTime.now(), dataPagamento, tentativas + 1);
	}
}
