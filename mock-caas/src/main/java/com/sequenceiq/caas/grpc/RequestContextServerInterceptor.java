// Copyright (c) 2019 Cloudera, Inc. All rights reserved.
package com.sequenceiq.caas.grpc;

import static com.google.common.base.Preconditions.checkNotNull;

import io.grpc.Context;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class RequestContextServerInterceptor implements ServerInterceptor {

  private static final Key<String> REQUEST_ID_METADATA_KEY =
      Key.of("requestId", Metadata.ASCII_STRING_MARSHALLER);
  private static final Key<String> ACTOR_CRN_METADATA_KEY =
      Key.of("actorCrn", Metadata.ASCII_STRING_MARSHALLER);

  @Override
  public <ReqT, RespT> Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> serverCall,
      Metadata metadata,
      ServerCallHandler<ReqT, RespT> serverCallHandler) {

    String requestId = metadata.get(REQUEST_ID_METADATA_KEY);
    checkNotNull(requestId);
    String actorCrn = metadata.get(ACTOR_CRN_METADATA_KEY);
    checkNotNull(actorCrn);

    GrpcRequestContext requestContext =
        new GrpcRequestContext(requestId);
    GrpcActorContext actorContext =
        new GrpcActorContext(actorCrn);
    Context context = Context.current().withValues(
        GrpcRequestContext.REQUEST_CONTEXT,
        requestContext,
        GrpcActorContext.ACTOR_CONTEXT,
        actorContext);
    Context previous = context.attach();
    try {
      return new ContextualizedServerCallListener<>(
          serverCallHandler.startCall(serverCall, metadata),
          context);
    } finally {
      context.detach(previous);
    }
  }

  private static class ContextualizedServerCallListener<ReqT> extends
          SimpleForwardingServerCallListener<ReqT> {

    private final Context context;

    ContextualizedServerCallListener(
        Listener<ReqT> delegate,
        Context context) {
      super(delegate);
      this.context = checkNotNull(context);
    }

    @Override
    public void onMessage(ReqT message) {
      Context previous = context.attach();
      try {
        super.onMessage(message);
      } finally {
        context.detach(previous);
      }
    }

    @Override
    public void onHalfClose() {
      Context previous = context.attach();
      try {
        super.onHalfClose();
      } finally {
        context.detach(previous);
      }
    }

    @Override
    public void onCancel() {
      Context previous = context.attach();
      try {
        super.onCancel();
      } finally {
        context.detach(previous);
      }
    }
  }
}
