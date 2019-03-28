package com.sequenceiq.cloudbreak.auth.altus.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.netty.util.internal.StringUtil;

@Configuration
public class IamConfig {

    @Value("${altus.iam.host:}")
    private String endpoint;

    @Value("${altus.iam.port:8982}")
    private int port;

    public String getEndpoint() {
        return endpoint;
    }

    public int getPort() {
        return port;
    }

    public boolean isConfigured() {
        return !StringUtil.isNullOrEmpty(endpoint);
    }
}
