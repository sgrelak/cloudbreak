package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.template.volume.RootVolumeV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.instancegroup.template.volume.RootVolumeV4Response;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
public class RootVolumeDto extends AbstractCloudbreakDto<RootVolumeV4Request, RootVolumeV4Response, RootVolumeDto> {

    protected RootVolumeDto(TestContext testContext) {
        super(new RootVolumeV4Request(), testContext);
    }

    @Override
    public RootVolumeDto valid() {
        return withSize(50);
    }

    public RootVolumeDto withSize(int size) {
        getRequest().setSize(size);
        return this;
    }
}
