package br.com.postech.techchallenge.microservico.pagamento.util;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.postech.techchallenge.microservico.pagamento.model.DadosEnvioPix;

public class Utilitario {
	
	public static String getHashDateTimeUnique() {
        Instant instant = Instant.now();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // Creating a unique UUID
        UUID uuid = UUID.randomUUID();

        // Creating a unique identifier incorporating timestamp and UUID
        return localDateTime + Constantes.STRING_UNDERLINE + uuid;
    }
	
	public static String gerarQrCodePix(BigDecimal valorPix) {
		final var dadosPix = new DadosEnvioPix(
				Constantes.NOME_DESTINATARIO_PIX_QRCODE,
				Constantes.CHAVE_DESTINATARIO_PIX_QRCODE, 
				valorPix,
				Constantes.CIDADE_DESTINATARIO_PIX_QRCODE, 
				String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli()));

        final var qrCodePix = new QRCodePix(dadosPix);
        qrCodePix.save(Path.of(Constantes.IMAGEM_QRCODE_PATH));
        
        return qrCodePix.toString();
	}

	public static String asJsonString(final Object obj) throws Exception {
		return new ObjectMapper().writeValueAsString(obj);
	}
}
