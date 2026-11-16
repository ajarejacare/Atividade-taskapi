package br.edu.ifsp.web.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class Pbkdf2CredentialHasher {

    private static final String PREFIX = "pbkdf2_sha256";
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600_000;
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;

    private final SecureRandom secureRandom;

    public Pbkdf2CredentialHasher() {
        this(new SecureRandom());
    }

    Pbkdf2CredentialHasher(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public String hash(String senha) throws GeneralSecurityException {
        if (senha == null) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }

        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        byte[] derivedKey = deriveKey(senha, salt);

        return PREFIX + "$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(derivedKey);
    }

    public boolean verificar(String senha, String credencial)
            throws GeneralSecurityException {
        if (senha == null || credencial == null) {
            return false;
        }

        String[] partes = credencial.split("\\$", -1);
        if (partes.length != 4
                || !PREFIX.equals(partes[0])
                || !Integer.toString(ITERATIONS).equals(partes[1])) {
            return false;
        }

        try {
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[3]);
            if (salt.length != SALT_LENGTH || hashEsperado.length != HASH_LENGTH
                    || !Base64.getEncoder().encodeToString(salt).equals(partes[2])
                    || !Base64.getEncoder().encodeToString(hashEsperado).equals(partes[3])) {
                return false;
            }

            byte[] hashCalculado = deriveKey(senha, salt);
            return MessageDigest.isEqual(hashEsperado, hashCalculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private byte[] deriveKey(String senha, byte[] salt)
            throws GeneralSecurityException {
        char[] caracteres = senha.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(caracteres, salt, ITERATIONS, HASH_LENGTH * 8);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM)
                    .generateSecret(spec)
                    .getEncoded();
        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(caracteres, '\0');
        }
    }
}
