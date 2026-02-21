package com.example.monobankpayment.utils;

import com.example.monobankpayment.config.MonobankConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class MonobankSignUtil {

    private final MonobankConfig config;

    private final String privateKeyPem = readPem("keys/private_pkcs8.pem");

    public String sign(byte[] payloadBytes) throws Exception {
        PrivateKey privateKey = loadPkcs8EcPrivateKey(privateKeyPem);

        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initSign(privateKey);
        sig.update(payloadBytes);

        byte[] signatureDer = sig.sign();
        return Base64.getEncoder().encodeToString(signatureDer);
    }

    private PrivateKey loadPkcs8EcPrivateKey(String pkcs8Pem) throws Exception {
        String base64 = pkcs8Pem
                .replaceAll("-----BEGIN [^-]+-----", "")
                .replaceAll("-----END [^-]+-----", "")
                .trim();

        byte[] keyBytes = Base64.getMimeDecoder().decode(base64);

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private String readPem(String classpathLocation) {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read PEM from classpath: " + classpathLocation, e);
        }
    }
}
