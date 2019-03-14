package com.sequenceiq.cloudbreak.api.endpoint.v4.util.responses;

public class TrustedProxyPublicKeyResponse {

    private String publicKey;

    public TrustedProxyPublicKeyResponse() {
    }

    public TrustedProxyPublicKeyResponse(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
