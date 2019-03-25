package com.sequenceiq.it.cloudbreak.newway.testcase.e2e.azure;

import static com.sequenceiq.it.cloudbreak.newway.cloud.v2.azure.AzureParameters.CloudStorage.Account.STORAGE_ACCOUNT_KEY;
import static com.sequenceiq.it.cloudbreak.newway.cloud.v2.azure.AzureParameters.CloudStorage.Account.STORAGE_ACCOUNT_NAME;
import static com.sequenceiq.it.cloudbreak.newway.util.storagelocation.StorageComponent.HIVE;
import static com.sequenceiq.it.cloudbreak.newway.util.storagelocation.StorageComponent.RANGER;
import static com.sequenceiq.it.cloudbreak.newway.util.storagelocation.StorageComponent.SPARK2;
import static com.sequenceiq.it.cloudbreak.newway.util.storagelocation.StorageComponent.TEZ;
import static com.sequenceiq.it.cloudbreak.newway.util.storagelocation.StorageComponent.YARN;
import static com.sequenceiq.it.cloudbreak.newway.util.storagelocation.StorageComponent.ZEPPELIN;

import javax.inject.Inject;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.mappable.CloudPlatform;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.storage.AdlsGen2CloudStorageV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.storage.CloudStorageV4Request;
import com.sequenceiq.it.cloudbreak.newway.client.StackTestClient;
import com.sequenceiq.it.cloudbreak.newway.context.Description;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.dto.ClusterTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.stack.StackTestDto;
import com.sequenceiq.it.cloudbreak.newway.testcase.e2e.AbstractE2ETest;
import com.sequenceiq.it.cloudbreak.newway.util.storagelocation.AzureTestStorageLocation;
import com.sequenceiq.it.cloudbreak.newway.util.storagelocation.StorageComponent;

public class AzureAdlsGen2Tests extends AbstractE2ETest {

    private static final StorageComponent[] LOCATION_STORAGE_COMPONENTS = {HIVE, SPARK2, TEZ, RANGER, YARN, ZEPPELIN};

    @Inject
    private StackTestClient stackTestClient;

    @BeforeMethod
    public void beforeMethod(Object[] data) {
        TestContext testContext = (TestContext) data[0];
        minimalSetupForClusterCreation(testContext);
    }

    @Test(dataProvider = TEST_CONTEXT)
    @Description(
            given = "there is a running cloudbreak",
            when = "a valid azure stack create request with attached ADLS Gen2 cloud storage is sent AND the stack is stoppend",
            and = "the stack is started",
            then = "the stack should be available AND deletable")
    public void testCreateStopAndStartClusterWithAdlsGen2CloudStorage(TestContext testContext) {
        checkCloudPlatform(CloudPlatform.AZURE);

        String clusterName = getNameGenerator().getRandomNameForResource();

        testContext
                .given("clusterWithAdlsGen2", ClusterTestDto.class)
                .withCloudStorage(adlsGen2CloudStorageV4RequestWithoutStorageLocations())

                .given(StackTestDto.class)
                .withCluster("clusterWithAdlsGen2")
                .withName(clusterName)

                .when(stackTestClient.createV4())
                .await(STACK_AVAILABLE)

                .then(stackTestClient.deleteV4()::action)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT)
    @Description(
            given = "there is a running cloudbreak",
            when = "a valid azure stack create request with attached ADLS Gen2 cloud storage and defined storage location is sent",
            and = "the stack is started",
            then = "the stack should be available AND deletable")
    public void testCreateStopAndStartClusterWithAdlsGen2AndCloudStorage(TestContext testContext) {
        checkCloudPlatform(CloudPlatform.AZURE);

        String clusterName = getNameGenerator().getRandomNameForResource();

        testContext
                .given("clusterWithAdlsGen2", ClusterTestDto.class)
                .withCloudStorage(adlsGen2CloudStorageV4RequestWithStorageLocations(clusterName))

                .given(StackTestDto.class)
                .withCluster("clusterWithAdlsGen2")

                .when(stackTestClient.createV4())
                .await(STACK_AVAILABLE)

                .then(stackTestClient.deleteV4()::action)
                .validate();
    }

    @AfterMethod(alwaysRun = true)
    public void teardown(Object[] data) {
        TestContext testContext = (TestContext) data[0];
        testContext.cleanupTestContext();
    }

    private CloudStorageV4Request adlsGen2CloudStorageV4RequestWithStorageLocations(String clusterName) {
        CloudStorageV4Request request = adlsGen2CloudStorageV4RequestWithoutStorageLocations();
        AzureTestStorageLocation azureStorageLocation = new AzureTestStorageLocation(getTestParameter().getRequired(STORAGE_ACCOUNT_NAME), clusterName,
                "someBaseLocation");
        request.setLocations(azureStorageLocation.getAdlsGen2(LOCATION_STORAGE_COMPONENTS));
        return request;
    }

    private CloudStorageV4Request adlsGen2CloudStorageV4RequestWithoutStorageLocations() {
        CloudStorageV4Request request = new CloudStorageV4Request();
        AdlsGen2CloudStorageV4Parameters adlsgen2 = new AdlsGen2CloudStorageV4Parameters();
        adlsgen2.setAccountKey(getTestParameter().getRequired(STORAGE_ACCOUNT_KEY));
        adlsgen2.setAccountName(getTestParameter().getRequired(STORAGE_ACCOUNT_NAME));
        request.setAdlsGen2(adlsgen2);
        return request;
    }

}
