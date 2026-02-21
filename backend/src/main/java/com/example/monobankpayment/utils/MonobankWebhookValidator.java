package com.example.monobankpayment.utils;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class MonobankWebhookValidator {

    public void validate(byte[] bodyBytes, String signatureBase64, String publicKeyBase64Pem) {
        if (bodyBytes == null) {
            throw new IllegalArgumentException("Request body is null");
        }
        if (signatureBase64 == null || signatureBase64.isBlank()) {
            throw new IllegalArgumentException("X-Sign header is missing");
        }
        if (publicKeyBase64Pem == null || publicKeyBase64Pem.isBlank()) {
            throw new IllegalStateException("Monobank public key is not configured");
        }

        final PublicKey publicKey;
        try {
            publicKey = loadEcPublicKeyFromBase64Pem(publicKeyBase64Pem);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid Monobank public key format/config", e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to build Monobank public key", e);
        }

        final byte[] signatureDer;
        try {
            signatureDer = Base64.getDecoder().decode(signatureBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("X-Sign is not valid Base64", e);
        }

        final boolean ok;
        try {
            Signature verifier = Signature.getInstance("SHA256withECDSA");
            verifier.initVerify(publicKey);
            verifier.update(bodyBytes);
            ok = verifier.verify(signatureDer);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("JVM does not support SHA256withECDSA", e);
        } catch (InvalidKeyException e) {
            throw new IllegalStateException("Invalid EC public key for signature verification", e);
        } catch (SignatureException e) {
            throw new IllegalStateException("Signature verification failed due to crypto error", e);
        }

        if (!ok) {
            throw new InvalidWebhookSignatureException("Invalid Monobank webhook signature");
        }
    }

    /**
     * Input: base64(PEM text) where PEM contains:
     * -----BEGIN PUBLIC KEY-----
     * ...
     * -----END PUBLIC KEY-----
     */
    private PublicKey loadEcPublicKeyFromBase64Pem(String pubKeyBase64) throws GeneralSecurityException {
        byte[] pemBytes;
        try {
            pemBytes = Base64.getDecoder().decode(pubKeyBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("publicKeyBase64 is not valid Base64", e);
        }

        String pem = new String(pemBytes, StandardCharsets.UTF_8);

        String base64Body = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] spkiDer;
        try {
            spkiDer = Base64.getDecoder().decode(base64Body);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("PUBLIC KEY PEM body is not valid Base64", e);
        }

        KeyFactory kf = KeyFactory.getInstance("EC");
        return kf.generatePublic(new X509EncodedKeySpec(spkiDer));
    }

    public static class InvalidWebhookSignatureException extends RuntimeException {
        public InvalidWebhookSignatureException(String message) {
            super(message);
        }
    }
}
