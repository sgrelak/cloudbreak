package com.sequenceiq.it.cloudbreak.newway;

import java.util.Collections;
import java.util.function.Function;

import com.amazonaws.services.apigateway.model.GatewayResponse;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.SSOType;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.gateway.GatewayV4Request;
import com.sequenceiq.it.IntegrationTestContext;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

public class ClusterGatewayDto extends AbstractCloudbreakDto<GatewayV4Request, GatewayResponse, ClusterGatewayDto> {

    public static final String GATEWAY_REQUEST = "GATEWAY_REQUEST";

    ClusterGatewayDto(String newId) {
        super(newId);
        setRequest(new GatewayV4Request());
    }

    ClusterGatewayDto() {
        this(GATEWAY_REQUEST);
    }

    public ClusterGatewayDto(TestContext testContext) {
        super(new GatewayV4Request(), testContext);
    }

    public ClusterGatewayDto withTopology(GatewayTopology topology) {
        getRequest().setTopologies(Collections.singletonList(topology.getRequest()));
        return this;
    }

    public ClusterGatewayDto withSsoType(SSOType ssoType) {
        getRequest().setSsoType(ssoType);
        return this;
    }

    public ClusterGatewayDto withSsoProvider(String ssoProvider) {
        getRequest().setSsoProvider(ssoProvider);
        return this;
    }

    public ClusterGatewayDto withPath(String path) {
        getRequest().setPath(path);
        return this;
    }

    public static ClusterGatewayDto request(String key) {
        return new ClusterGatewayDto(key);
    }

    public static ClusterGatewayDto request() {
        return new ClusterGatewayDto();
    }

    public static Function<IntegrationTestContext, ClusterGatewayDto> getTestContextGateway(String key) {
        return testContext -> testContext.getContextParam(key, ClusterGatewayDto.class);
    }

    public static Function<IntegrationTestContext, ClusterGatewayDto> getTestContextGateway() {
        return getTestContextGateway(GATEWAY_REQUEST);
    }
}
