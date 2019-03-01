package com.sequenceiq.it.cloudbreak.newway.testcase.mock;

import static com.sequenceiq.cloudbreak.cloud.model.InstanceStatus.STARTED;
import static com.sequenceiq.cloudbreak.cloud.model.InstanceStatus.STOPPED;
import static com.sequenceiq.it.cloudbreak.newway.Mock.gson;
import static com.sequenceiq.it.cloudbreak.newway.context.RunningParameter.key;
import static com.sequenceiq.it.spark.ITResponse.MOCK_ROOT;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.OK;

import org.testng.annotations.Test;

import com.sequenceiq.it.cloudbreak.newway.Stack;
import com.sequenceiq.it.cloudbreak.newway.action.stack.StackTestAction;
import com.sequenceiq.it.cloudbreak.newway.context.Description;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.StackTestDto;
import com.sequenceiq.it.cloudbreak.newway.context.MockedTestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AmbariEntity;
import com.sequenceiq.it.cloudbreak.newway.entity.ClusterEntity;
import com.sequenceiq.it.cloudbreak.newway.entity.clusterdefinition.ClusterDefinitionTestDto;
import com.sequenceiq.it.cloudbreak.newway.mock.model.SPIMock;
import com.sequenceiq.it.spark.StatefulRoute;
import com.sequenceiq.it.spark.spi.CloudVmInstanceStatuses;

public class ClouderaManagerStartStopTest extends AbstractClouderaManagerTest {

    private static final String CLOUD_INSTANCE_STATUSES = MOCK_ROOT + SPIMock.CLOUD_INSTANCE_STATUSES;

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK, enabled = false)
    @Description(
        given = "a Cloudera Manager cluster",
        when = "the cluster is stoppend and started",
        then = "the cluster should be available")
    public void createRegularClouderaManagerClusterThenWaitForAvailableThenStopThenStartThenWaitForAvailableThenNoExceptionOccurs(
            MockedTestContext testContext) {
        mockSpi(testContext);
        String ambariKey = getNameGenerator().getRandomNameForResource();
        String clusterName = testContext.get(ClusterDefinitionTestDto.class).getRequest().getName();
        testContext
                .given(ambariKey, AmbariEntity.class)
                .withClusterDefinitionName(clusterName)
                .withValidateClusterDefinition(Boolean.FALSE)
                .given(clusterName, ClusterEntity.class)
                .withAmbari(ambariKey)
                .given(StackTestDto.class)
                .withCluster(clusterName)
                .when(Stack.postV4(), key(ambariKey))
                .await(STACK_AVAILABLE, key(ambariKey))
                .when(StackTestAction::stop, key(ambariKey))
                .await(STACK_STOPPED, key(ambariKey))
                .when(StackTestAction::start, key(ambariKey))
                .await(STACK_AVAILABLE, key(ambariKey))
                .validate();
    }

    private void mockSpi(MockedTestContext testContext) {
        StatefulRoute okState = (request, response, model) -> {
            String resultJson = gson().toJson(new CloudVmInstanceStatuses(model.getInstanceMap()).createCloudVmInstanceStatuses());
            response.body(resultJson);
            response.type(TEXT_PLAIN);
            response.status(OK.getStatusCode());
            return "";
        };

        StatefulRoute stoppedStateSpi = (request, response, model) -> {
            String resultJson = gson().toJson(new CloudVmInstanceStatuses(model.getInstanceMap()).createCloudVmInstanceStatuses());
            response.body(resultJson.replaceAll(STARTED.name(), STOPPED.name()));
            response.type(TEXT_PLAIN);
            response.status(OK.getStatusCode());
            return "";
        };

        StatefulRoute startedStateSpi = (request, response, model) -> {
            String resultJson = gson().toJson(new CloudVmInstanceStatuses(model.getInstanceMap()).createCloudVmInstanceStatuses());
            response.body(resultJson.replaceAll(STOPPED.name(), STARTED.name()));
            response.type(TEXT_PLAIN);
            response.status(OK.getStatusCode());
            return "";
        };

        testContext.getModel().getSpiMock().getDynamicRouteStack().clearPost(CLOUD_INSTANCE_STATUSES);
        testContext.getModel().getSpiMock().getDynamicRouteStack().post(CLOUD_INSTANCE_STATUSES, okState);
        testContext.getModel().getSpiMock().getDynamicRouteStack().post(CLOUD_INSTANCE_STATUSES, startedStateSpi);
        testContext.getModel().getSpiMock().getDynamicRouteStack().post(CLOUD_INSTANCE_STATUSES, stoppedStateSpi);
    }
}
