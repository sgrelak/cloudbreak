package com.sequenceiq.caas.grpc.service;

import static com.cloudera.thunderhead.service.usermanagement.UserManagementProto.GetAccountRequest;
import static com.cloudera.thunderhead.service.usermanagement.UserManagementProto.GetAccountResponse;
import static com.cloudera.thunderhead.service.usermanagement.UserManagementProto.GetUserRequest;
import static com.cloudera.thunderhead.service.usermanagement.UserManagementProto.GetUserResponse;
import static com.cloudera.thunderhead.service.usermanagement.UserManagementProto.User;

import org.springframework.stereotype.Service;

import com.cloudera.thunderhead.service.usermanagement.UserManagementGrpc;
import com.sequenceiq.caas.grpc.GrpcActorContext;

import io.grpc.stub.StreamObserver;

@Service
public class MockUserManagementService
        extends UserManagementGrpc.UserManagementImplBase {

    @Override
    public void getUser(
            GetUserRequest request,
            StreamObserver<GetUserResponse> responseObserver) {
        String userIdOrCrn = request.getUserIdOrCrn();
        String[] splittedCrn = userIdOrCrn.split(":");
        responseObserver.onNext(
                GetUserResponse.newBuilder()
                        .setUser(User.newBuilder()
                                .setCrn(GrpcActorContext.ACTOR_CONTEXT.get().getActorCrn())
                                .setEmail(splittedCrn[6])
                                .build())
                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAccount(
            GetAccountRequest request,
            StreamObserver<GetAccountResponse> responseObserver) {
        responseObserver.onNext(
                GetAccountResponse.newBuilder()
                        .build());
        responseObserver.onCompleted();
    }
}