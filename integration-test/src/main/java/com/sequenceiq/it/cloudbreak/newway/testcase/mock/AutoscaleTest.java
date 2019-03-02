package com.sequenceiq.it.cloudbreak.newway.testcase.mock;

import javax.inject.Inject;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sequenceiq.it.cloudbreak.newway.client.UpdateStackTestClient;
import com.sequenceiq.it.cloudbreak.newway.context.MockedTestContext;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.udaptestack.UpdateStackTestDto;
import com.sequenceiq.it.cloudbreak.newway.testcase.AbstractIntegrationTest;

public class AutoscaleTest extends AbstractIntegrationTest {

    @Inject
    private UpdateStackTestClient updateStackTestClient;

    @BeforeMethod
    public void beforeMethod(Object[] data) {
        minimalSetupForClusterCreation((MockedTestContext) data[0]);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(Object[] data) {
        ((MockedTestContext) data[0]).cleanupTestContextEntity();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    public void testPutStack(TestContext testContext) {
        testContext
                .given(UpdateStackTestDto.class)
                .when(updateStackTestClient.put())
                .validate();
    }

}
