package com.caerus.identity.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class RsaKeyConfig {

    @Value("${jwt.keystore.location}")
    private String keyStorePath;

    @Value("${jwt.keystore.password}")
    private String keyStorePassword;

    @Value("${jwt.key.alias}")
    private String keyAlias;

    @Value("${jwt.key.password}")
    private String keyPassword;

    @Value("${jwt.key.id:caerus-key-1}")
    private String keyId;

    @Bean
    public KeyPair keyPair() throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream(
                keyStorePath.replace("classpath:", "/"))) {

            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(inputStream, keyStorePassword.toCharArray());

            Key key = ks.getKey(keyAlias, keyPassword.toCharArray());
            if (key instanceof PrivateKey privateKey) {
                Certificate cert = ks.getCertificate(keyAlias);
                PublicKey publicKey = cert.getPublicKey();
                return new KeyPair(publicKey, privateKey);
            }
            throw new IllegalStateException("No private key found in keystore");
        }
    }

    @Bean
    public RSAPublicKey rsaPublicKey(KeyPair keyPair) {
        return (RSAPublicKey) keyPair.getPublic();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(keyId)
                .build();

        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }


    @Bean
    public String keyId() {
        return keyId;
    }
}
