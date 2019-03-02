package com.sequenceiq.it.cloudbreak.newway.action.udaptestack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.action.Action;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.udaptestack.UpdateStackTestDto;

public class UpdateStackTestAction implements Action<UpdateStackTestDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateStackTestAction.class);

    public UpdateStackTestDto action(TestContext testContext, UpdateStackTestDto entity, CloudbreakClient client) {
        String logInitMessage = "Putting Stack update";
        LOGGER.info("{}", logInitMessage);
        client.getCloudbreakClient().autoscaleEndpoint().putStack(entity.getId(), entity.getUserId(), entity.getUpdateRequest());
        LOGGER.info("{} was successful", logInitMessage);
        return entity;
    }

}
