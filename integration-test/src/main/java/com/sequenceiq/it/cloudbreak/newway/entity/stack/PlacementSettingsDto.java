package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.environment.placement.PlacementSettingsV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.environment.placement.PlacementSettingsV4Response;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
public class PlacementSettingsDto extends AbstractCloudbreakDto<PlacementSettingsV4Request, PlacementSettingsV4Response, PlacementSettingsDto> {

    private static final String PLACEMENT = "PLACEMENT";

    public PlacementSettingsDto() {
        super(PLACEMENT);
    }

    protected PlacementSettingsDto(TestContext testContext) {
        super(new PlacementSettingsV4Request(), testContext);
    }

    @Override
    public PlacementSettingsDto valid() {
        return getCloudProvider().placement(this);
    }

    public PlacementSettingsDto withRegion(String region) {
        getRequest().setRegion(region);
        return this;
    }

    public PlacementSettingsDto withAvailabilityZone(String availabilityZone) {
        getRequest().setAvailabilityZone(availabilityZone);
        return this;
    }
}
