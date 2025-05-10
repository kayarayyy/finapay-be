package com.bcaf.bcapay.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier() {
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(List.of("630482567435-4ck2eqarg55ua42v9g2d2gmj5kgphfv5.apps.googleusercontent.com"))
                .build();
    }

    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            return idToken != null ? idToken.getPayload() : null;
        } catch (Exception e) {
            return null;
        }
    }
}