package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.network.AwsNetworkV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.network.AzureNetworkV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.network.GcpNetworkV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.network.MockNetworkV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.network.OpenStackNetworkV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.network.YarnNetworkV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.network.NetworkV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.network.NetworkV4Response;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
public class NetworkV2Dto extends AbstractCloudbreakDto<NetworkV4Request, NetworkV4Response, NetworkV2Dto> {
    public static final String NETWORK = "NETWORK";

    public NetworkV2Dto(NetworkV4Request request, TestContext testContext) {
        super(request, testContext);
    }

    public NetworkV2Dto(TestContext testContext) {
        super(new NetworkV4Request(), testContext);
    }

    public NetworkV2Dto() {
        super(NetworkV2Dto.class.getSimpleName().toUpperCase());
    }

    public NetworkV2Dto valid() {
        return getCloudProvider().network(this);
    }

    public NetworkV2Dto withAzure(AzureNetworkV4Parameters azure) {
        getRequest().setAzure(azure);
        return this;
    }

    public NetworkV2Dto withAws(AwsNetworkV4Parameters aws) {
        getRequest().setAws(aws);
        return this;
    }

    public NetworkV2Dto withGcp(GcpNetworkV4Parameters gcp) {
        getRequest().setGcp(gcp);
        return this;
    }

    public NetworkV2Dto withOpenStack(OpenStackNetworkV4Parameters openStack) {
        getRequest().setOpenstack(openStack);
        return this;
    }

    public NetworkV2Dto withMock(MockNetworkV4Parameters param) {
        getRequest().setMock(param);
        return this;
    }

    public NetworkV2Dto withYarn(YarnNetworkV4Parameters yarn) {
        getRequest().setYarn(yarn);
        return this;
    }

    public NetworkV2Dto withSubnetCIDR(String subnetCIDR) {
        getRequest().setSubnetCIDR(subnetCIDR);
        return this;
    }
}
