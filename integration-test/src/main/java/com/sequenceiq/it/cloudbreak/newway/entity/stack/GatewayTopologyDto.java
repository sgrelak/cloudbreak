package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import java.util.Arrays;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.gateway.topology.GatewayTopologyV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.cluster.gateway.topology.GatewayTopologyV4Response;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
public class GatewayTopologyDto extends AbstractCloudbreakDto<GatewayTopologyV4Request, GatewayTopologyV4Response, GatewayTopologyDto> {
    public static final String NETWORK = "NETWORK";

    public GatewayTopologyDto(TestContext testContext) {
        super(new GatewayTopologyV4Request(), testContext);
    }

    public GatewayTopologyDto valid() {
        return this;
    }

    public GatewayTopologyDto withExposedServices(String... exposedServices) {
        getRequest().setExposedServices(Arrays.asList(exposedServices));
        return this;
    }

    public GatewayTopologyDto withTopologyName(String topologyName) {
        getRequest().setTopologyName(topologyName);
        return this;
    }
}
