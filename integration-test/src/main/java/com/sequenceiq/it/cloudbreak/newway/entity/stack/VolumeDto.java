package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.template.volume.VolumeV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.instancegroup.template.volume.VolumeV4Response;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;
import com.sequenceiq.it.cloudbreak.newway.entity.CloudbreakEntity;

@Prototype
public class VolumeDto extends AbstractCloudbreakDto<VolumeV4Request, VolumeV4Response, VolumeDto> {

    protected VolumeDto(TestContext testContext) {
        super(new VolumeV4Request(), testContext);
    }

    @Override
    public CloudbreakEntity valid() {
        return getCloudProvider().attachedVolume(this);
    }

    public VolumeDto withSize(int size) {
        getRequest().setSize(size);
        return this;
    }

    public VolumeDto withType(String type) {
        getRequest().setType(type);
        return this;
    }

    public VolumeDto withCount(int count) {
        getRequest().setCount(count);
        return this;
    }
}
