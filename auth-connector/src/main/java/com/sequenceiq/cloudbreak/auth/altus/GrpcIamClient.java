package com.sequenceiq.cloudbreak.auth.altus;


import static io.grpc.internal.GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE;

import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.auth.altus.config.IamConfig;

import io.grpc.ManagedChannelBuilder;

@Component
public class GrpcIamClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcIamClient.class);

    @Inject
    private IamConfig iamConfig;

    private static String newRequestId() {
        return UUID.randomUUID().toString();
    }

    public Boolean hasRight(String actorCrn, String right, String resource) {
        try (ManagedChannelWrapper channelWrapper = new ManagedChannelWrapper(
                ManagedChannelBuilder.forAddress(iamConfig.getEndpoint(), iamConfig.getPort())
                        .usePlaintext()
                        .maxInboundMessageSize(DEFAULT_MAX_MESSAGE_SIZE)
                        .build())) {
            IamClient client = new IamClient(channelWrapper.getChannel(), actorCrn);
            String requestId = newRequestId();
            return client.hasRight(requestId, actorCrn, right, resource);
        }
    }

    public boolean isConfigured() {
        return iamConfig.isConfigured();
    }
}
