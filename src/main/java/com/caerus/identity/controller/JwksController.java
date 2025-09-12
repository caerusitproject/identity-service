package com.caerus.identity.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final RSAPublicKey publicKey;
    private final String keyId;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        RSAKey jwk = new RSAKey.Builder(publicKey)
                .keyID(keyId)
                .build();

        return new JWKSet(jwk).toJSONObject();
    }
}
