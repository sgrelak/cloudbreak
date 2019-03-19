package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.gateway.GatewayV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.cluster.gateway.GatewayV4Response;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
public class GatewayDto extends AbstractCloudbreakDto<GatewayV4Request, GatewayV4Response, GatewayDto> {
    public static final String NETWORK = "NETWORK";

    public GatewayDto(TestContext testContext) {
        super(new GatewayV4Request(), testContext);
    }

    public GatewayDto valid() {
        return this;
    }

    public GatewayDto withTopologies(String... keys) {
        getRequest().setTopologies(Stream.of(keys).map(key -> {
            GatewayTopologyDto gatewayEntity = getTestContext().get(key);
            return gatewayEntity.getRequest();
        }).collect(Collectors.toList()));
        return this;
    }
}
