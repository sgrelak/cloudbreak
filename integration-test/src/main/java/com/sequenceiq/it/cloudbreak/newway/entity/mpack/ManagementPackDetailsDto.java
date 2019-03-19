package com.sequenceiq.it.cloudbreak.newway.entity.mpack;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.ambari.stackrepository.mpack.ManagementPackDetailsV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.cluster.ambari.stackrepository.mpack.ManagementPackDetailsV4Response;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
public class ManagementPackDetailsDto extends AbstractCloudbreakDto<ManagementPackDetailsV4Request, ManagementPackDetailsV4Response,
        ManagementPackDetailsDto> {

    public ManagementPackDetailsDto(TestContext testContext) {
        super(new ManagementPackDetailsV4Request(), testContext);
    }

    public ManagementPackDetailsDto valid() {
        return this;
    }

    public ManagementPackDetailsDto withName(String name) {
        getRequest().setName(name);
        return this;
    }

    public ManagementPackDetailsDto withPreInstalled(Boolean preInstalled) {
        getRequest().setPreInstalled(preInstalled);
        return this;
    }
}
