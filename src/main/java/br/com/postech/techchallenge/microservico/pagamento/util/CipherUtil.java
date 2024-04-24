package br.com.postech.techchallenge.microservico.pagamento.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import br.com.postech.techchallenge.microservico.pagamento.exception.BusinessException;

/**
 * Utility class for encrypting data.
 */
public class CipherUtil {

    /**
     * Encrypts a string value in a given algorithm.
     * 
     * @param value - The value to be encrypted.
     * @param algorithm - The type of encryption that will be used.
     * 
     * @return - The encrypted value.
     */
    public static String cipherValueToSomeAlgorithm(String value, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(value.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException("Error generating password hash");
        }
    }
}
