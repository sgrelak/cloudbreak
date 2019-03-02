package com.sequenceiq.it.cloudbreak.newway.entity.udaptestack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.cloudbreak.api.endpoint.v4.autoscales.request.InstanceGroupAdjustmentV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.autoscales.request.UpdateStackV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.StatusRequest;
import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakEntity;

@Prototype
public class UpdateStackTestDto extends AbstractCloudbreakEntity<UpdateStackV4Request, Object, UpdateStackTestDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateStackTestDto.class);

    private Long id;

    private String userId;

    private UpdateStackV4Request updateRequest;

    protected UpdateStackTestDto(TestContext testContext) {
        super(new UpdateStackV4Request(), testContext);
    }

    @Override
    public UpdateStackTestDto valid() {
        var updateRequest = new UpdateStackV4Request();
        updateRequest.setStatus(StatusRequest.SYNC);
        updateRequest.setInstanceGroupAdjustment(new InstanceGroupAdjustmentV4Request());
        updateRequest.setWithClusterEvent(false);
        return withId(1L).withUpdateRequest(updateRequest).withUserId("1");
    }

    public Long getId() {
        return id;
    }

    public UpdateStackTestDto withId(Long id) {
        this.id = id;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public UpdateStackTestDto withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public UpdateStackV4Request getUpdateRequest() {
        return updateRequest;
    }

    public UpdateStackTestDto withUpdateRequest(UpdateStackV4Request updateRequest) {
        this.updateRequest = updateRequest;
        return this;
    }

    @Override
    public void cleanUp(TestContext context, CloudbreakClient cloudbreakClient) {
        LOGGER.debug("this entry point does not have any clean up operation");
    }

    @Override
    public int order() {
        return 500;
    }

}
