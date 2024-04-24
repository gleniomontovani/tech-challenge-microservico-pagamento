package br.com.postech.techchallenge.microservico.pagamento.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class Utilitario {
	
	public static String getHashDateTimeUnique() {
        Instant instant = Instant.now();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // Creating a unique UUID
        UUID uuid = UUID.randomUUID();

        // Creating a unique identifier incorporating timestamp and UUID
        return localDateTime + Constantes.STRING_UNDERLINE + uuid;
    }

}
