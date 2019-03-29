package com.sequenceiq.caas.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sequenceiq.caas.grpc.GrpcServer;
import com.sequenceiq.caas.grpc.service.MockAuthorizationService;
import com.sequenceiq.caas.grpc.service.MockUserManagementService;

@Configuration
public class GrpcServerConfig {

    @Inject
    private MockUserManagementService mockUserManagementService;

    @Inject
    private MockAuthorizationService mockAuthorizationService;

    @Bean
    public GrpcServer grpcServer() {
        return new GrpcServer(
                8982,
                mockUserManagementService.bindService(),
                mockAuthorizationService.bindService());
    }
}
