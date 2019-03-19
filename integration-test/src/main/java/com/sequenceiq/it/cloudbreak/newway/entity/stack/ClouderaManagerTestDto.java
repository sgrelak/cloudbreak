package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import javax.ws.rs.core.Response;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.cm.ClouderaManagerV4Request;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
public class ClouderaManagerTestDto extends AbstractCloudbreakDto<ClouderaManagerV4Request, Response, ClouderaManagerTestDto> {

    public ClouderaManagerTestDto(TestContext testContex) {
        super(new ClouderaManagerV4Request(), testContex);
    }

    public ClouderaManagerTestDto() {
        super(ClouderaManagerTestDto.class.getSimpleName().toUpperCase());
    }

    public ClouderaManagerTestDto valid() {
        return this;
    }

    public ClouderaManagerTestDto withClouderaManagerRepository(String key) {
        ClouderaManagerRepositoryDto repositoryEntity = getTestContext().get(key);
        return withStackRepository(repositoryEntity);
    }

    public ClouderaManagerTestDto withStackRepository(ClouderaManagerRepositoryDto clouderaManagerRepositoryDto) {
        getRequest().setRepository(clouderaManagerRepositoryDto.getRequest());
        return this;
    }
}
