package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.cm.repository.ClouderaManagerRepositoryV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.cluster.clouderamanager.ClouderaManagerRepositoryV4Response;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;
import com.sequenceiq.it.cloudbreak.newway.entity.CloudbreakEntity;

@Prototype
public class ClouderaManagerRepositoryDto
        extends AbstractCloudbreakDto<ClouderaManagerRepositoryV4Request, ClouderaManagerRepositoryV4Response, ClouderaManagerRepositoryDto> {

    protected ClouderaManagerRepositoryDto(TestContext testContext) {
        super(new ClouderaManagerRepositoryV4Request(), testContext);
    }

    @Override
    public CloudbreakEntity valid() {
        return withVersion("6.1.0");
    }

    public ClouderaManagerRepositoryDto withVersion(String version) {
        getRequest().setVersion(version);
        return this;
    }
}
