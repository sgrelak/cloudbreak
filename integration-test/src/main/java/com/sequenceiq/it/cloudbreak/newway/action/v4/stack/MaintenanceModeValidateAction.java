package com.sequenceiq.it.cloudbreak.newway.action.v4.stack;

import static com.sequenceiq.it.cloudbreak.newway.log.Log.logJSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.MaintenanceModeStatus;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.MaintenanceModeV4Request;
import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.action.Action;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.StackTestDto;

public class MaintenanceModeValidateAction implements Action<StackTestDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceModeValidateAction.class);

    private MaintenanceModeV4Request request;

    public MaintenanceModeValidateAction() {
        this.request = new MaintenanceModeV4Request();
        this.request.setStatus(MaintenanceModeStatus.VALIDATION_REQUESTED);
    }

    @Override
    public StackTestDto action(TestContext testContext, StackTestDto entity, CloudbreakClient client) throws Exception {

        logJSON(" Enable Maintenance Mode post request:\n", request);

        client.getCloudbreakClient()
                .stackV4Endpoint()
                .setClusterMaintenanceMode(client.getWorkspaceId(), entity.getName(), request);

        return entity;
    }
}
