package com.sequenceiq.it.cloudbreak.newway.testcase.mock;

import static com.sequenceiq.it.cloudbreak.newway.context.RunningParameter.key;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sequenceiq.it.cloudbreak.newway.action.credential.CredentialTestAction;
import com.sequenceiq.it.cloudbreak.newway.action.encryptionkeys.PlatformEncryptionKeysTestAction;
import com.sequenceiq.it.cloudbreak.newway.context.Description;
import com.sequenceiq.it.cloudbreak.newway.context.MockedTestContext;
import com.sequenceiq.it.cloudbreak.newway.context.TestCaseDescription;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.credential.CredentialTestDto;
import com.sequenceiq.it.cloudbreak.newway.entity.encryption.PlatformEncryptionKeysTestDto;
import com.sequenceiq.it.cloudbreak.newway.testcase.AbstractIntegrationTest;

public class EncryptionKeysTest extends AbstractIntegrationTest {

    @BeforeMethod
    public void beforeMethod(Object[] data) {
        createDefaultUser((TestContext) data[0]);
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
        given = "using a MOCK credential to get encryption keys",
        when = "calling get encryption keys endpoint",
        then = "returns with MOCK related encryption keys")
    public void getPlatformEncryptionKeysWithMockCredentialThenReturnWithPlatformRelatedKeys(MockedTestContext testContext) {
        String credentialName = getNameGenerator().getRandomNameForResource();
        testContext
                .given(CredentialTestDto.class)
                .withName(credentialName)
                .when(CredentialTestAction::create, key(credentialName))
                .given(PlatformEncryptionKeysTestDto.class)
                .withCredentialName(credentialName)
                .when(PlatformEncryptionKeysTestAction::getEncryptionKeys, key(credentialName))
                .validate();
    }

    @Test(dataProvider = "contextWithCredentialNameAndException")
    public void getPlatformEncryptionKeysWithMockCredentialThenReturnWithPlatformRelatedKeys(
            MockedTestContext testContext,
            String credentialName,
            Class<Exception> exception,
            @Description TestCaseDescription testCaseDescription) {
        String generatedKey = getNameGenerator().getRandomNameForResource();
        testContext
                .given(PlatformEncryptionKeysTestDto.class)
                .withCredentialName(credentialName)
                .when(PlatformEncryptionKeysTestAction::getEncryptionKeys, key(generatedKey))
                .expect(exception, key(generatedKey))
                .validate();
    }

    @DataProvider(name = "contextWithCredentialNameAndException", parallel = true)
    public Object[][] provideInvalidAttributes() {
        return new Object[][]{
                {
                    getBean(MockedTestContext.class),
                    "",
                    BadRequestException.class,
                    new TestCaseDescription.TestCaseDescriptionBuilder()
                        .given("using an empty string as MOCK credential to get encryption keys")
                        .when("calling get encryption keys endpoint")
                        .then("returns with BadRequestException because the credential name did not defined")
                },
                {
                    getBean(MockedTestContext.class),
                    null,
                    BadRequestException.class,
                    new TestCaseDescription.TestCaseDescriptionBuilder()
                        .given("using a 'null' string as MOCK credential to get encryption keys")
                        .when("calling get encryption keys endpoint")
                        .then("returns with BadRequestException because the credential name did not defined")
                },
                {
                    getBean(MockedTestContext.class),
                    "andNowForSomethingCompletelyDifferent",
                    ForbiddenException.class,
                    new TestCaseDescription.TestCaseDescriptionBuilder()
                        .given("using a MOCK credential to get encryption keys")
                        .when("calling get encryption keys endpoint")
                        .then("returns with ForbiddenException because the credential does not belong to your account")
                }
        };
    }

}
