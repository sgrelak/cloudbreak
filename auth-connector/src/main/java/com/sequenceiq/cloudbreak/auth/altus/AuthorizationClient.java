package com.sequenceiq.cloudbreak.auth.altus;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang.StringUtils;

import com.cloudera.thunderhead.service.authorization.AuthorizationGrpc;
import com.cloudera.thunderhead.service.authorization.AuthorizationProto;

import io.grpc.ManagedChannel;

/**
 * A simple wrapper to the GRPC user management service. This handles setting up
 * the appropriate context-propogatinng interceptors and hides some boilerplate.
 */
public class AuthorizationClient {

    private final ManagedChannel channel;

    private final String actorCrn;

    /**
     * Constructor.
     *
     * @param channel  the managed channel.
     * @param actorCrn the actor CRN.
     */
    AuthorizationClient(ManagedChannel channel,
            String actorCrn) {
        this.channel = checkNotNull(channel);
        this.actorCrn = checkNotNull(actorCrn);
    }

    /**
     * Wraps a call to check right.
     *
     * @param requestId the request ID for the request
     * @param actorCrn  the user CRN
     * @param right     right to check
     * @param resource  the resource to check if any.
     * @return the user
     */
    public Boolean hasRight(String requestId, String actorCrn, String right, String resource) {
        checkNotNull(requestId);
        checkNotNull(actorCrn);
        checkNotNull(right);
        AuthorizationProto.RightCheck.Builder rightCheckBuilder = AuthorizationProto.RightCheck.newBuilder().setRight(right);
        if (StringUtils.isNotEmpty(resource)) {
            rightCheckBuilder.setResource(resource);
        }
        return newStub(requestId).hasRights(
                AuthorizationProto.HasRightsRequest.newBuilder()
                        .setActorCrn(actorCrn)
                        .setCheck(0, rightCheckBuilder.build())
                        .build()
        ).getResult(0); // what is this index?
    }

    /**
     * Creates a new stub with the appropriate metadata injecting interceptors.
     *
     * @param requestId the request ID
     * @return the stub
     */
    private AuthorizationGrpc.AuthorizationBlockingStub newStub(String requestId) {
        checkNotNull(requestId);
        return AuthorizationGrpc.newBlockingStub(channel)
                .withInterceptors(new AltusMetadataInterceptor(requestId, actorCrn));
    }
}
