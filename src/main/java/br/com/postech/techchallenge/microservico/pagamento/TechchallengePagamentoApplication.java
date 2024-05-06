package br.com.postech.techchallenge.microservico.pagamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TechchallengePagamentoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechchallengePagamentoApplication.class, args);
	}
}