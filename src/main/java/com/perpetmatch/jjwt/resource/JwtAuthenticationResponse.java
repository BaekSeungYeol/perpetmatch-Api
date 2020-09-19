package com.perpetmatch.jjwt.resource;

public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String nickname = "null";

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
    public JwtAuthenticationResponse(String accessToken, String nickname) {
        this.accessToken = accessToken;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
