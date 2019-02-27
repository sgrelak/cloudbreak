package com.sequenceiq.it.cloudbreak.newway.testcase.mock;

import static com.sequenceiq.cloudbreak.api.endpoint.v4.clustertemplate.ClusterTemplateV4Type.SPARK;
import static com.sequenceiq.it.cloudbreak.newway.cloud.v2.provider.MockCloudProvider.LONDON;
import static com.sequenceiq.it.cloudbreak.newway.cloud.v2.provider.MockCloudProvider.VALID_REGION;
import static com.sequenceiq.it.cloudbreak.newway.context.RunningParameter.force;
import static com.sequenceiq.it.cloudbreak.newway.context.RunningParameter.key;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sequenceiq.it.cloudbreak.newway.Environment;
import com.sequenceiq.it.cloudbreak.newway.EnvironmentEntity;
import com.sequenceiq.it.cloudbreak.newway.action.clustertemplate.ClusterTemplateGetAction;
import com.sequenceiq.it.cloudbreak.newway.action.clustertemplate.ClusterTemplateV4CreateAction;
import com.sequenceiq.it.cloudbreak.newway.action.clustertemplate.ClusterTemplateV4DeleteAction;
import com.sequenceiq.it.cloudbreak.newway.action.clustertemplate.ClusterTemplateV4ListAction;
import com.sequenceiq.it.cloudbreak.newway.action.clustertemplate.DeleteClusterFromTemplateAction;
import com.sequenceiq.it.cloudbreak.newway.action.clustertemplate.LaunchClusterFromTemplateAction;
import com.sequenceiq.it.cloudbreak.newway.action.database.DatabaseCreateIfNotExistsAction;
import com.sequenceiq.it.cloudbreak.newway.action.mpack.MpackTestAction;
import com.sequenceiq.it.cloudbreak.newway.action.recipe.RecipeTestClient;
import com.sequenceiq.it.cloudbreak.newway.assertion.CheckClusterTemplateGetResponse;
import com.sequenceiq.it.cloudbreak.newway.assertion.CheckClusterTemplateType;
import com.sequenceiq.it.cloudbreak.newway.assertion.CheckStackTemplateAfterClusterTemplateCreation;
import com.sequenceiq.it.cloudbreak.newway.assertion.CheckStackTemplateAfterClusterTemplateCreationWithProperties;
import com.sequenceiq.it.cloudbreak.newway.client.LdapConfigTestClient;
import com.sequenceiq.it.cloudbreak.newway.cloud.v2.provider.MockCloudProvider;
import com.sequenceiq.it.cloudbreak.newway.context.Description;
import com.sequenceiq.it.cloudbreak.newway.context.MockedTestContext;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.ClusterTemplateEntity;
import com.sequenceiq.it.cloudbreak.newway.entity.EnvironmentSettingsV4Entity;
import com.sequenceiq.it.cloudbreak.newway.entity.PlacementSettingsEntity;
import com.sequenceiq.it.cloudbreak.newway.entity.StackTemplateEntity;
import com.sequenceiq.it.cloudbreak.newway.entity.database.DatabaseEntity;
import com.sequenceiq.it.cloudbreak.newway.entity.ldap.LdapConfigTestDto;
import com.sequenceiq.it.cloudbreak.newway.entity.mpack.MPackTestDto;
import com.sequenceiq.it.cloudbreak.newway.entity.recipe.RecipeTestDto;
import com.sequenceiq.it.cloudbreak.newway.testcase.AbstractIntegrationTest;
import com.sequenceiq.it.util.LongStringGeneratorUtil;

public class ClusterTemplateTest extends AbstractIntegrationTest {

    private static final String SPECIAL_CT_NAME = "@#$|:&* ABC";

    private static final String ILLEGAL_CT_NAME = "Illegal template name ;";

    private static final String INVALID_SHORT_CT_NAME = "";

    @Inject
    private LdapConfigTestClient ldapConfigTestClient;

    @Inject
    private LongStringGeneratorUtil longStringGeneratorUtil;

    @BeforeMethod
    public void beforeMethod(Method method, Object[] data) {
        MockedTestContext testContext = (MockedTestContext) data[0];

        createDefaultUser(testContext);
        createDefaultCredential(testContext);
        createDefaultImageCatalog(testContext);
        initializeDefaultClusterDefinitions(testContext);
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a prepared environment",
            when = "a valid cluster template create request is sent",
            then = "the cluster template is created and can be deleted"
    )
    public void testClusterTemplateCreateAndGetAndDelete(TestContext testContext) {
        testContext
                .given(EnvironmentEntity.class)
                .when(Environment::post)
                .given("stackTemplate", StackTemplateEntity.class).withEnvironment(EnvironmentEntity.class)
                .given(ClusterTemplateEntity.class).withStackTemplate("stackTemplate")
                .when(new ClusterTemplateV4CreateAction())
                .when(new ClusterTemplateGetAction())
                .then(new CheckClusterTemplateGetResponse())
                .then(new CheckStackTemplateAfterClusterTemplateCreation())
                .capture(ClusterTemplateEntity::count, key("ctSize"))
                .when(new ClusterTemplateV4DeleteAction())
                .capture(ct -> ct.count() - 1, key("ctSize"))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a prepared environment",
            when = "a valid cluster template create request with spark type is sent",
            then = "the new cluster template with spark type is listed in the list cluster templates response"
    )
    public void testClusterTemplateWithType(TestContext testContext) {
        testContext.given("environment", EnvironmentEntity.class).withRegions(VALID_REGION).withLocation(LONDON)
                .when(Environment::post)
                .given("stackTemplate", StackTemplateEntity.class).withEnvironment("environment")
                .given(ClusterTemplateEntity.class).withType(SPARK).withStackTemplate("stackTemplate")
                .capture(ClusterTemplateEntity::count, key("ctSize"))
                .when(new ClusterTemplateV4CreateAction())
                .verify(ct -> ct.count() - 1, key("ctSize"))
                .when(new ClusterTemplateV4ListAction())
                .then(new CheckClusterTemplateType(SPARK))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a prepared cluster template",
            when = "a stack is created from the prepared cluster template",
            then = "the stack starts properly and can be deleted"
    )
    public void testLaunchClusterFromTemplate(TestContext testContext) {
        testContext.given("environment", EnvironmentEntity.class).withRegions(VALID_REGION).withLocation(LONDON)
                .when(Environment::post)
                .given("stackTemplate", StackTemplateEntity.class).withEnvironment("environment")
                .given(ClusterTemplateEntity.class).withStackTemplate("stackTemplate")
                .when(new ClusterTemplateV4CreateAction())
                .when(new LaunchClusterFromTemplateAction("stackTemplate"))
                .await(STACK_AVAILABLE, key("stackTemplate"))
                .when(new DeleteClusterFromTemplateAction("stackTemplate"))
                .await(STACK_DELETED, key("stackTemplate"))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a working Cloudbreak",
            when = "a cluster template create request with missing environment is sent",
            then = "the cluster template is cannot be created"
    )
    public void testCreateClusterTemplateWithoutEnvironment(TestContext testContext) {
        testContext.given("stackTemplate", StackTemplateEntity.class)
                .given(ClusterTemplateEntity.class).withStackTemplate("stackTemplate")
                .when(new ClusterTemplateV4CreateAction(), key("ENVIRONMENT_NULL"))
                .expect(BadRequestException.class, key("ENVIRONMENT_NULL").withExpectedMessage("The environment name cannot be null."))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a working Cloudbreak",
            when = "a cluster template create request with null environment name is sent",
            then = "the cluster template is cannot be created"
    )
    public void testCreateClusterTemplateWithoutEnvironmentName(TestContext testContext) {
        testContext.given(EnvironmentSettingsV4Entity.class).withName(null)
                .given("stackTemplate", StackTemplateEntity.class).withEnvironmentSettings()
                .given(ClusterTemplateEntity.class).withStackTemplate("stackTemplate")
                .when(new ClusterTemplateV4CreateAction(), key("ENVIRONMENT_NULL"))
                .expect(BadRequestException.class, key("ENVIRONMENT_NULL").withExpectedMessage("The environment name cannot be null."))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a prepared cluster template with many properties",
            when = "a stack is created from the prepared cluster template",
            then = "the stack starts properly and can be deleted"
    )
    public void testLaunchClusterFromTemplateWithProperties(MockedTestContext testContext) {
        testContext.getModel().getAmbariMock().putConfigureLdap();
        testContext.getModel().getAmbariMock().postSyncLdap();
        testContext.getModel().getAmbariMock().putConfigureSso();
        testContext
                .given(LdapConfigTestDto.class).withName("mock-test-ldap")
                .when(ldapConfigTestClient.createIfNotExists())
                .given(RecipeTestDto.class).withName("mock-test-recipe")
                .when(RecipeTestClient::postV4)
                .given(DatabaseEntity.class).withName("mock-test-rds")
                .when(new DatabaseCreateIfNotExistsAction())
                .given("mpack", MPackTestDto.class).withName("mock-test-mpack")
                .when(MpackTestAction::create)
                .given("environment", EnvironmentEntity.class).withRegions(VALID_REGION).withLocation(LONDON)
                .when(Environment::post)
                .given("stackTemplate", StackTemplateEntity.class).withEnvironment("environment").withEveryProperties()
                .given(ClusterTemplateEntity.class).withStackTemplate("stackTemplate")
                .capture(ClusterTemplateEntity::count, key("ctSize"))
                .when(new ClusterTemplateV4CreateAction())
                .verify(ct -> ct.count() - 1, key("ctSize"))
                .when(new ClusterTemplateGetAction())
                .then(new CheckStackTemplateAfterClusterTemplateCreationWithProperties())
                .when(new LaunchClusterFromTemplateAction("stackTemplate"))
                .await(STACK_AVAILABLE, key("stackTemplate"))
                .when(new DeleteClusterFromTemplateAction("stackTemplate"), force())
                .await(STACK_DELETED, key("stackTemplate").withSkipOnFail(false))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a prepared environment",
            when = "a cluster template create request is sent with invalid name",
            then = "the cluster template cannot be created"
    )
    public void testCreateInvalidNameClusterTemplate(TestContext testContext) {
        testContext.given(ClusterTemplateEntity.class).withName(ILLEGAL_CT_NAME)
                .when(new ClusterTemplateV4CreateAction(), key("illegalCtName"))
                .expect(BadRequestException.class, key("illegalCtName").withExpectedMessage("\"post.arg1.name\":"
                        + "\"The length of the cluster template's name has to be in range of 1 to 100 and should not contain semicolon\""))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a prepared environment",
            when = "a cluster template create request is sent with a special name",
            then = "the cluster template creation should be successful"
    )
    public void testCreateSpecialNameClusterTemplate(TestContext testContext) {
        String name = StringUtils.substring(getNameGenerator().getRandomNameForResource(), 0, 40 - SPECIAL_CT_NAME.length()) + SPECIAL_CT_NAME;
        testContext
                .given(EnvironmentEntity.class)
                .when(Environment::post)
                .given("stackTemplate", StackTemplateEntity.class).withEnvironment(EnvironmentEntity.class)
                .given(ClusterTemplateEntity.class).withStackTemplate("stackTemplate").withName(name)
                .when(new ClusterTemplateV4CreateAction())
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a working cloudbreak",
            when = "a cluster template create request is sent with a too short name",
            then = "the cluster template cannot be created"
    )
    public void testCreateInvalidShortNameClusterTemplate(TestContext testContext) {
        testContext.given(ClusterTemplateEntity.class).withName(longStringGeneratorUtil.stringGenerator(2))
                .when(new ClusterTemplateV4CreateAction(), key("illegalCtName"))
                .expect(BadRequestException.class, key("illegalCtName")
                        .withExpectedMessage("The length of the cluster's name has to be in range of 5 to 40")
                )
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a prepared environment and cluster template",
            when = "the cluster template create request is sent again",
            then = "a BadRequest should be returned"
    )
    public void testCreateAgainClusterTemplate(TestContext testContext) {
        testContext.given("environment", EnvironmentEntity.class).withRegions(VALID_REGION).withLocation(LONDON)
                .when(Environment::post)
                .given("placementSettings", PlacementSettingsEntity.class).withRegion(MockCloudProvider.EUROPE)
                .given("stackTemplate", StackTemplateEntity.class).withEnvironment("environment").withPlacement("placementSettings")
                .given(ClusterTemplateEntity.class).withStackTemplate("stackTemplate")
                .when(new ClusterTemplateV4CreateAction())
                .when(new ClusterTemplateV4CreateAction(), key("againCtName"))
                .expect(BadRequestException.class, key("againCtName").withExpectedMessage("^clustertemplate already exists with name.*"))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a prepared environment",
            when = "a create cluster template request is sent with too long description",
            then = "the a cluster template should not be created"
    )
    public void testCreateLongDescriptionClusterTemplate(TestContext testContext) {
        String invalidLongDescripton = longStringGeneratorUtil.stringGenerator(1001);
        testContext.given("environment", EnvironmentEntity.class).withRegions(VALID_REGION).withLocation(LONDON)
                .when(Environment::post)
                .given(ClusterTemplateEntity.class).withDescription(invalidLongDescripton)
                .when(new ClusterTemplateV4CreateAction(), key("longCtDescription"))
                .expect(BadRequestException.class, key("longCtDescription").withExpectedMessage("\"post.arg1.description\":"
                        + "\"size must be between 0 and 1000\""))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a prepared environment",
            when = "a cluster template create request without stack template is sent",
            then = "the a cluster template should not be created"
    )
    public void testCreateEmptyStackTemplateClusterTemplateException(TestContext testContext) {
        testContext.given(ClusterTemplateEntity.class).withoutStackTemplate()
                .when(new ClusterTemplateV4CreateAction(), key("emptyStack"))
                .expect(BadRequestException.class, key("emptyStack").withExpectedMessage("\"post.arg1.stackTemplate\":\"must not be null\""))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "a prepared environment",
            when = "a cluster tempalte create request with null name is sent",
            then = "the a cluster template should not be created"
    )
    public void testCreateEmptyClusterTemplateNameException(TestContext testContext) {
        testContext
                .given(ClusterTemplateEntity.class).withName(null)
                .when(new ClusterTemplateV4CreateAction(), key("nullTemplateName"))
                .given(ClusterTemplateEntity.class).withName("")
                .when(new ClusterTemplateV4CreateAction(), key("emptyTemplateName").withSkipOnFail(false))
                .expect(BadRequestException.class, key("nullTemplateName").withExpectedMessage("\"post.arg1.name\":\"must not be null\""))
                .expect(BadRequestException.class, key("emptyTemplateName")
                        .withExpectedMessage("\"post.arg1.name\":\"The length of the cluster's name has to be in range of 5 to 40\""))
                .validate();
    }

    @AfterMethod(alwaysRun = true)
    public void tear(Object[] data) {
        MockedTestContext testContext = (MockedTestContext) data[0];
        testContext.cleanupTestContextEntity();
    }
}
