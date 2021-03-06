package com.sequenceiq.it.cloudbreak.newway.action.v4.stack;

import static com.sequenceiq.it.cloudbreak.newway.log.Log.log;
import static com.sequenceiq.it.cloudbreak.newway.log.Log.logJSON;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.GeneratedClusterDefinitionV4Response;
import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.action.Action;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.dto.stack.StackTestDto;

public class StackClusterDefinitionRequestAction implements Action<StackTestDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackClusterDefinitionRequestAction.class);

    @Override
    public StackTestDto action(TestContext testContext, StackTestDto testDto, CloudbreakClient client) throws Exception {
        log(LOGGER, format(" Name: %s", testDto.getRequest().getName()));
        logJSON(LOGGER, " Stack get cluster definition:\n", testDto.getRequest());
        GeneratedClusterDefinitionV4Response bp = client.getCloudbreakClient().stackV4Endpoint().postStackForClusterDefinition(
                client.getWorkspaceId(),
                testDto.getName(),
                testDto.getRequest());
        testDto.withGeneratedClusterDefinition(bp);
        logJSON(LOGGER, " get cluster definition was successfully:\n", testDto.getGeneratedClusterDefinition());
        return testDto;
    }
}
