package com.example.smartstorageorganizer.model;

import android.os.Build;


import com.example.smartstorageorganizer.BuildConfig;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;

public class KeyCloakAccessToken {
    private static final  String serverUrl = "http://10.0.2.2:8080/auth";
    private static final String realm = "myrealm";
    private static final String clientId = "Login";
    private static final String client= BuildConfig.clientSecret;



    public static AccessTokenResponse getAccessToken(String username, String password) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .clientSecret(client)
                .username(username)
                .password(password)
                .build();
        return keycloak.tokenManager().getAccessToken();
    }
}
