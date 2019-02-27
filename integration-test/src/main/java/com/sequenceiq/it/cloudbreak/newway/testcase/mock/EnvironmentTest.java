package com.sequenceiq.it.cloudbreak.newway.testcase.mock;

import static com.sequenceiq.it.cloudbreak.newway.context.RunningParameter.key;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sequenceiq.cloudbreak.api.endpoint.v4.database.base.DatabaseV4Base;
import com.sequenceiq.cloudbreak.api.endpoint.v4.database.responses.DatabaseV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.environment.responses.SimpleEnvironmentV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.ldaps.responses.LdapV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.proxies.responses.ProxyV4Response;
import com.sequenceiq.it.cloudbreak.exception.TestFailException;
import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.Environment;
import com.sequenceiq.it.cloudbreak.newway.EnvironmentEntity;
import com.sequenceiq.it.cloudbreak.newway.action.credential.CredentialTestAction;
import com.sequenceiq.it.cloudbreak.newway.assertion.CheckEnvironmentCredential;
import com.sequenceiq.it.cloudbreak.newway.context.Description;
import com.sequenceiq.it.cloudbreak.newway.context.MockedTestContext;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.CloudbreakEntity;
import com.sequenceiq.it.cloudbreak.newway.entity.credential.CredentialTestDto;
import com.sequenceiq.it.cloudbreak.newway.entity.database.DatabaseEntity;
import com.sequenceiq.it.cloudbreak.newway.entity.ldap.LdapConfigTestDto;
import com.sequenceiq.it.cloudbreak.newway.entity.proxy.ProxyConfigEntity;
import com.sequenceiq.it.cloudbreak.newway.testcase.AbstractIntegrationTest;

public class EnvironmentTest extends AbstractIntegrationTest {
    private static final String FORBIDDEN_KEY = "forbiddenPost";

    private static final Set<String> INVALID_REGION = new HashSet<>(Collections.singletonList("MockRegion"));

    private static final Set<String> INVALID_PROXY = new HashSet<>(Collections.singletonList("InvalidProxy"));

    private static final Set<String> INVALID_LDAP = new HashSet<>(Collections.singletonList("InvalidLdap"));

    private static final Set<String> INVALID_RDS = new HashSet<>(Collections.singletonList("InvalidRds"));

    private Set<String> mixedProxy = new HashSet<>();

    private Set<String> mixedLdap = new HashSet<>();

    private Set<String> mixedRds = new HashSet<>();

    @BeforeMethod
    public void beforeMethod(Object[] data) {
        MockedTestContext testContext = (MockedTestContext) data[0];
        createDefaultUser(testContext);
        createDefaultCredential(testContext);
        initializeDefaultClusterDefinitions(testContext);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(Object[] data) {
        ((MockedTestContext) data[0]).cleanupTestContextEntity();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment valid request",
            then = "should be created")
    public void testCreateEnvironment(TestContext testContext) {
        testContext
                .given(EnvironmentEntity.class)
                .when(Environment::post)
                .then(EnvironmentTest::checkCredentialAttachedToEnv)
                .when(Environment::getAll)
                .then(EnvironmentTest::checkEnvIsListed)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment valid request and attach a proxy to environment",
            then = "should environment and proxy listed")
    public void testCreateEnvironmenWithProxy(TestContext testContext) {
        createDefaultProxyConfig(testContext);
        Set<String> validProxy = new HashSet<>();
        validProxy.add(testContext.get(ProxyConfigEntity.class).getName());
        testContext
                .given(EnvironmentEntity.class)
                .withProxyConfigs(validProxy)
                .when(Environment::post)
                .then(EnvironmentTest::checkCredentialAttachedToEnv)
                .when(Environment::getAll)
                .then(EnvironmentTest::checkEnvIsListed)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment valid request and attach an ldap to environment",
            then = "should environment and proxy listed")
    public void testCreateEnvironmenWithLdap(TestContext testContext) {
        createDefaultLdapConfig(testContext);
        Set<String> validLdap = new HashSet<>();
        validLdap.add(testContext.get(LdapConfigTestDto.class).getName());
        testContext
                .given(EnvironmentEntity.class)
                .withLdapConfigs(validLdap)
                .when(Environment::post)
                .then(EnvironmentTest::checkCredentialAttachedToEnv)
                .when(Environment::getAll)
                .then(EnvironmentTest::checkEnvIsListed)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment valid request and attach a database to environment",
            then = "should environment and proxy listed")
    public void testCreateEnvironmenWithDatabase(TestContext testContext) {
        createDefaultRdsConfig(testContext);
        Set<String> validRds = new HashSet<>();
        validRds.add(testContext.get(DatabaseEntity.class).getName());
        testContext
                .given(EnvironmentEntity.class)
                .withRdsConfigs(validRds)
                .when(Environment::post)
                .then(EnvironmentTest::checkCredentialAttachedToEnv)
                .when(Environment::getAll)
                .then(EnvironmentTest::checkEnvIsListed)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment with invalid region",
            then = "should throw BadRequestException")
    public void testCreateEnvironmentInvalidRegion(TestContext testContext) {
        String forbiddenKey = getNameGenerator().getRandomNameForResource();
        testContext
                .init(EnvironmentEntity.class)
                .withRegions(INVALID_REGION)
                .when(Environment::post, key(forbiddenKey))
                .expect(BadRequestException.class, key(forbiddenKey))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment with 'null' region",
            then = "should throw BadRequestException")
    public void testCreateEnvironmentNoRegion(TestContext testContext) {
        String forbiddenKey = getNameGenerator().getRandomNameForResource();
        testContext
                .init(EnvironmentEntity.class)
                .withRegions(null)
                .when(Environment::post, key(forbiddenKey))
                .expect(BadRequestException.class, key(forbiddenKey))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment with non existing credential",
            then = "should throw BadRequestException")
    public void testCreateEnvironmentNotExistCredential(TestContext testContext) {
        String forbiddenKey = getNameGenerator().getRandomNameForResource();
        testContext
                .init(EnvironmentEntity.class)
                .withCredentialName("notexistingcredendital")
                .when(Environment::post, key(forbiddenKey))
                .expect(BadRequestException.class, key(forbiddenKey))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment with non existing proxy",
            then = "should throw BadRequestException")
    public void testCreateEnvironmentNotExistProxy(TestContext testContext) {
        String forbiddenKey = getNameGenerator().getRandomNameForResource();
        testContext
                .init(EnvironmentEntity.class)
                .withProxyConfigs(INVALID_PROXY)
                .when(Environment::post, key(forbiddenKey))
                .expect(BadRequestException.class, key(forbiddenKey))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment with non existing ldap",
            then = "should throw BadRequestException")
    public void testCreateEnvironmentNotExistLdap(TestContext testContext) {
        String forbiddenKey = getNameGenerator().getRandomNameForResource();
        testContext
                .init(EnvironmentEntity.class)
                .withLdapConfigs(INVALID_LDAP)
                .when(Environment::post, key(forbiddenKey))
                .expect(BadRequestException.class, key(forbiddenKey))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment with non existing database",
            then = "should throw BadRequestException")
    public void testCreateEnvironmentNotExistRds(TestContext testContext) {
        String forbiddenKey = getNameGenerator().getRandomNameForResource();
        testContext
                .init(EnvironmentEntity.class)
                .withRdsConfigs(INVALID_RDS)
                .when(Environment::post, key(forbiddenKey))
                .expect(BadRequestException.class, key(forbiddenKey))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment with non existing database and existing one",
            then = "should throw BadRequestException")
    public void testCreateEnvWithExistingAndNotExistingRds(TestContext testContext) {
        String forbiddenKey = getNameGenerator().getRandomNameForResource();
        createDefaultRdsConfig(testContext);
        mixedRds.add(testContext.get(DatabaseEntity.class).getName());
        mixedRds.add("invalidRds");
        testContext
                .init(EnvironmentEntity.class)
                .withRdsConfigs(mixedRds)
                .when(Environment::post, key(forbiddenKey))
                .expect(BadRequestException.class, key(forbiddenKey))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment with non existing proxy and existing one",
            then = "should throw BadRequestException")
    public void testCreateEnvWithExistingAndNotExistingProxy(TestContext testContext) {
        String forbiddenKey = getNameGenerator().getRandomNameForResource();
        createDefaultProxyConfig(testContext);
        mixedProxy.add(testContext.get(ProxyConfigEntity.class).getName());
        mixedProxy.add("invalidProxy");
        testContext
                .init(EnvironmentEntity.class)
                .withProxyConfigs(mixedProxy)
                .when(Environment::post, key(forbiddenKey))
                .expect(BadRequestException.class, key(forbiddenKey))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Create one environment",
            when = "create environment with non existing ldap and existing one",
            then = "should throw BadRequestException")
    public void testCreateEnvWithExistingAndNotExistingLdap(TestContext testContext) {
        String forbiddenKey = getNameGenerator().getRandomNameForResource();
        createDefaultLdapConfig(testContext);
        mixedLdap.add(testContext.get(LdapConfigTestDto.class).getName());
        mixedLdap.add("invalidLdap");
        testContext
                .init(EnvironmentEntity.class)
                .withLdapConfigs(mixedLdap)
                .when(Environment::post, key(forbiddenKey))
                .expect(BadRequestException.class, key(forbiddenKey))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "delete specified environment",
            then = "should be deleted")
    public void testDeleteEnvironment(TestContext testContext) {
        testContext
                .init(EnvironmentEntity.class)
                .when(Environment::post)
                .then(EnvironmentTest::checkCredentialAttachedToEnv)
                .when(Environment::getAll)
                .then(EnvironmentTest::checkEnvIsListed)
                .when(Environment::delete)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment with proxy",
            when = "delete specified environment",
            then = "should be deleted")
    public void testDeleteEnvWithProxy(TestContext testContext) {
        createDefaultProxyConfig(testContext);
        Set<String> validProxy = new HashSet<>();
        validProxy.add(testContext.get(ProxyConfigEntity.class).getName());
        testContext
                .init(EnvironmentEntity.class)
                .withProxyConfigs(validProxy)
                .when(Environment::post)
                .then(EnvironmentTest::checkCredentialAttachedToEnv)
                .when(Environment::getAll)
                .then(EnvironmentTest::checkEnvIsListed)
                .when(Environment::delete)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment with ldap",
            when = "delete specified environment",
            then = "should be deleted")
    public void testDeleteEnvWithLdap(TestContext testContext) {
        createDefaultLdapConfig(testContext);
        Set<String> validLdap = new HashSet<>();
        validLdap.add(testContext.get(LdapConfigTestDto.class).getName());
        testContext
                .init(EnvironmentEntity.class)
                .withLdapConfigs(validLdap)
                .when(Environment::post)
                .then(EnvironmentTest::checkCredentialAttachedToEnv)
                .when(Environment::getAll)
                .then(EnvironmentTest::checkEnvIsListed)
                .when(Environment::delete)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment with database",
            when = "delete specified environment",
            then = "should be deleted")
    public void testDeleteEnvWithRds(TestContext testContext) {
        createDefaultRdsConfig(testContext);
        Set<String> validRds = new HashSet<>();
        validRds.add(testContext.get(DatabaseEntity.class).getName());
        testContext
                .init(EnvironmentEntity.class)
                .withRdsConfigs(validRds)
                .when(Environment::post)
                .then(EnvironmentTest::checkCredentialAttachedToEnv)
                .when(Environment::getAll)
                .then(EnvironmentTest::checkEnvIsListed)
                .when(Environment::delete)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "delete specified environment which does not exist",
            then = "should throw ForbiddenException")
    public void testDeleteEnvironmentNotExist(TestContext testContext) {
        String forbiddenKey = getNameGenerator().getRandomNameForResource();
        testContext
                .init(EnvironmentEntity.class)
                .when(Environment::delete, key(forbiddenKey))
                .expect(ForbiddenException.class, key(forbiddenKey))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "attach database to it",
            then = "should be attached")
    public void testCreateEnvAttachRds(TestContext testContext) {
        createDefaultRdsConfig(testContext);
        Set<String> validRds = new HashSet<>();
        validRds.add(testContext.get(DatabaseEntity.class).getName());
        testContext
                .given(EnvironmentEntity.class)
                .withName("int-rds-attach")
                .when(Environment::post)
                .when(Environment::getAll)
                .given(EnvironmentEntity.class)
                .withRdsConfigs(validRds)
                .when(Environment::putAttachResources)
                .then(EnvironmentTest::checkRdsAttachedToEnv)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "attach ldap to it",
            then = "should be attached")
    public void testCreateEnvAttachLdap(TestContext testContext) {
        String env = getNameGenerator().getRandomNameForResource();
        createDefaultLdapConfig(testContext);
        Set<String> validLdap = new HashSet<>();
        validLdap.add(testContext.get(LdapConfigTestDto.class).getName());
        testContext
                .given(EnvironmentEntity.class)
                .withName(env)
                .when(Environment::post)
                .given(EnvironmentEntity.class)
                .withLdapConfigs(validLdap)
                .when(Environment::putAttachResources)
                .then(EnvironmentTest::checkLdapAttachedToEnv)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "attach proxy to it",
            then = "should be attached")
    public void testCreateEnvAttachProxy(TestContext testContext) {
        String env = getNameGenerator().getRandomNameForResource();
        createDefaultProxyConfig(testContext);
        Set<String> validProxy = new HashSet<>();
        validProxy.add(testContext.get(ProxyConfigEntity.class).getName());
        testContext
                .given(EnvironmentEntity.class)
                .withName(env)
                .when(Environment::post)
                .given(EnvironmentEntity.class)
                .withProxyConfigs(validProxy)
                .when(Environment::putAttachResources)
                .then(EnvironmentTest::checkProxyAttachedToEnv)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "detach database from it",
            then = "should be detached")
    public void testCreateEnvDetachRds(TestContext testContext) {
        String env = getNameGenerator().getRandomNameForResource();
        createDefaultRdsConfig(testContext);
        Set<String> validRds = new HashSet<>();
        validRds.add(testContext.get(DatabaseEntity.class).getName());
        testContext
                .given(EnvironmentEntity.class)
                .withName(env)
                .when(Environment::post)
                .when(Environment::getAll)
                .given(EnvironmentEntity.class)
                .withRdsConfigs(validRds)
                .when(Environment::putAttachResources)
                .then(EnvironmentTest::checkRdsAttachedToEnv)
                .when(Environment::putDetachResources)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "detach ldap from it",
            then = "should be detached")
    public void testCreateEnvDetachLdap(TestContext testContext) {
        String env = getNameGenerator().getRandomNameForResource();
        createDefaultLdapConfig(testContext);
        Set<String> validLdap = new HashSet<>();
        validLdap.add(testContext.get(LdapConfigTestDto.class).getName());
        testContext
                .given(EnvironmentEntity.class)
                .withName(env)
                .when(Environment::post)
                .when(Environment::getAll)
                .given(EnvironmentEntity.class)
                .withLdapConfigs(validLdap)
                .when(Environment::putAttachResources)
                .then(EnvironmentTest::checkLdapAttachedToEnv)
                .when(Environment::putDetachResources)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "detach proxy from it",
            then = "should be detached")
    public void testCreateEnvDetachProxy(TestContext testContext) {
        String env = getNameGenerator().getRandomNameForResource();
        createDefaultProxyConfig(testContext);
        Set<String> validProxy = new HashSet<>();
        validProxy.add(testContext.get(ProxyConfigEntity.class).getName());
        testContext
                .given(EnvironmentEntity.class)
                .withName(env)
                .when(Environment::post)
                .when(Environment::getAll)
                .given(EnvironmentEntity.class)
                .withProxyConfigs(validProxy)
                .when(Environment::putAttachResources)
                .then(EnvironmentTest::checkProxyAttachedToEnv)
                .when(Environment::putDetachResources)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environments",
            when = "attach database to it",
            then = "should be attached")
    public void testAttachRdsToMoreEnvs(TestContext testContext) {
        String rds1 = getNameGenerator().getRandomNameForResource();
        String rds2 = getNameGenerator().getRandomNameForResource();
        createDefaultRdsConfig(testContext);
        Set<String> validRds = new HashSet<>();
        validRds.add(testContext.get(DatabaseEntity.class).getName());
        attachRdsToEnv(testContext, rds1, validRds);
        attachRdsToEnv(testContext, rds2, validRds);
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environments",
            when = "attach ldap to it",
            then = "should be attached")
    public void testAttachLdapToMoreEnvs(TestContext testContext) {
        String ldap1 = getNameGenerator().getRandomNameForResource();
        String ldap2 = getNameGenerator().getRandomNameForResource();
        createDefaultLdapConfig(testContext);
        Set<String> validLdap = new HashSet<>();
        validLdap.add(testContext.get(LdapConfigTestDto.class).getName());
        attachLdapToEnv(testContext, ldap1, validLdap);
        attachLdapToEnv(testContext, ldap2, validLdap);
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environments",
            when = "attach proxy to it",
            then = "should be attached")
    public void testAttachProxyToMoreEnvs(TestContext testContext) {
        String proxy1 = getNameGenerator().getRandomNameForResource();
        String proxy2 = getNameGenerator().getRandomNameForResource();
        createDefaultProxyConfig(testContext);
        Set<String> validProxy = new HashSet<>();
        validProxy.add(testContext.get(ProxyConfigEntity.class).getName());
        attachProxyToEnv(testContext, proxy1, validProxy);
        attachProxyToEnv(testContext, proxy2, validProxy);
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "attach database to a not existing environment",
            then = "should throw ForbiddenException")
    public void testAttachRdsToNotExistEnv(TestContext testContext) {
        String env = getNameGenerator().getRandomNameForResource();
        String forbiddenKey = getNameGenerator().getRandomNameForResource();
        createDefaultRdsConfig(testContext);
        Set<String> validRds = new HashSet<>();
        validRds.add(testContext.get(DatabaseEntity.class).getName());
        testContext
                .init(EnvironmentEntity.class)
                .withName(env)
                .withRdsConfigs(validRds)
                .when(Environment::putAttachResources, key(forbiddenKey))
                .expect(ForbiddenException.class, key(forbiddenKey))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "attach database to a not existing environment",
            then = "should throw ForbiddenException")
    public void testRdsAttachDetachOther(TestContext testContext) {
        String notExistingRds = getNameGenerator().getRandomNameForResource();
        String rds1 = getNameGenerator().getRandomNameForResource();
        createDefaultRdsConfig(testContext);
        Set<String> validRds = new HashSet<>();
        Set<String> notValidRds = new HashSet<>();
        validRds.add(testContext.get(DatabaseEntity.class).getName());
        notValidRds.add(notExistingRds);
        testContext
                .given(EnvironmentEntity.class)
                .withName(rds1)
                .when(Environment::post)
                .when(Environment::getAll)
                .given(EnvironmentEntity.class)
                .withRdsConfigs(validRds)
                .when(Environment::putAttachResources)
                .then(EnvironmentTest::checkRdsAttachedToEnv)
                .given(EnvironmentEntity.class)
                .withName(rds1)
                .withRdsConfigs(notValidRds)
                .when(Environment::putDetachResources)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "modify credential",
            then = "should use the new credential on environment")
    public void testCreateEnvironmentChangeCredWithCredName(TestContext testContext) {
        String cred1 = getNameGenerator().getRandomNameForResource();
        testContext
                .given(EnvironmentEntity.class)
                .when(Environment::post)
                .given(cred1, CredentialTestDto.class)
                .withName(cred1)
                .when(CredentialTestAction::create)
                .given(EnvironmentEntity.class)
                .withCredentialName(cred1)
                .then(Environment::changeCredential)
                .then(new CheckEnvironmentCredential(cred1))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "modify credential",
            then = "should use the new credentialRequest on environment")
    public void testCreateEnvironmentChangeCredWithCredRequest(TestContext testContext) {
        String cred1 = getNameGenerator().getRandomNameForResource();
        testContext
                .given(EnvironmentEntity.class)
                .when(Environment::post)

                .given(cred1, CredentialTestDto.class)
                .withName(cred1)
                .given(EnvironmentEntity.class)
                .withCredentialName(null)
                .withCredential(cred1)
                .then(Environment::changeCredential)
                .then(new CheckEnvironmentCredential(cred1))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "modify credential for the same credential",
            then = "should use the same credential on environment")
    public void testCreateEnvironmentChangeCredForSame(TestContext testContext) {
        testContext
                .given(EnvironmentEntity.class)
                .when(Environment::post)
                .withCredentialName(testContext.get(CredentialTestDto.class).getName())
                .then(Environment::changeCredential)
                .then(EnvironmentTest::checkCredentialAttachedToEnv)
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "Created environment",
            when = "modify credential for a not existing credential",
            then = "should throw ForbiddenException")
    public void testCreateEnvironmentChangeCredNotExistingName(TestContext testContext) {
        String notExistingCred = getNameGenerator().getRandomNameForResource();
        String forbiddenKey = getNameGenerator().getRandomNameForResource();
        testContext
                .given(EnvironmentEntity.class)
                .when(Environment::post)
                .given(EnvironmentEntity.class)
                .withCredentialName(notExistingCred)
                .then(Environment::changeCredential, key(forbiddenKey))
                .expect(ForbiddenException.class, key(forbiddenKey))
                .validate();
    }

    private static void attachRdsToEnv(TestContext testContext, String name, Set<String> validRds) {
        testContext
                .given(name, EnvironmentEntity.class)
                .withName(name)
                .when(Environment::post)

                .given(name, EnvironmentEntity.class)
                .withRdsConfigs(validRds)
                .when(Environment::putAttachResources)
                .then(EnvironmentTest::checkRdsAttachedToEnv)
                .validate();
    }

    private static void attachLdapToEnv(TestContext testContext, String name, Set<String> validLdap) {
        testContext
                .given(name, EnvironmentEntity.class)
                .withName(name)
                .when(Environment::post)

                .given(name, EnvironmentEntity.class)
                .withLdapConfigs(validLdap)
                .when(Environment::putAttachResources)
                .then(EnvironmentTest::checkLdapAttachedToEnv)
                .validate();
    }

    private static void attachProxyToEnv(TestContext testContext, String name, Set<String> validLdap) {
        testContext
                .given(name, EnvironmentEntity.class)
                .withName(name)
                .when(Environment::post)

                .given(name, EnvironmentEntity.class)
                .withProxyConfigs(validLdap)
                .when(Environment::putAttachResources)
                .then(EnvironmentTest::checkProxyAttachedToEnv)
                .validate();
    }

    private static EnvironmentEntity checkCredentialAttachedToEnv(TestContext testContext, EnvironmentEntity environment, CloudbreakClient cloudbreakClient) {
        String credentialName = environment.getResponse().getCredentialName();
        if (!credentialName.equals(testContext.get(CredentialTestDto.class).getName())) {
            throw new TestFailException("Credential is not attached to environment");
        }
        return environment;
    }

    static EnvironmentEntity checkRdsAttachedToEnv(TestContext testContext, EnvironmentEntity environment, CloudbreakClient cloudbreakClient) {
        Set<String> rdsConfigs = new HashSet<>();
        Set<DatabaseV4Response> rdsConfigResponseSet = environment.getResponse().getDatabases();
        for (DatabaseV4Response rdsConfigResponse : rdsConfigResponseSet) {
            rdsConfigs.add(rdsConfigResponse.getName());
        }
        if (!rdsConfigs.contains(testContext.get(DatabaseEntity.class).getName())) {
            throw new TestFailException("Rds is not attached to environment");
        }
        return environment;
    }

    public static EnvironmentEntity checkRdsDetachedFromEnv(TestContext testContext, EnvironmentEntity environment,
            String rdsKey, CloudbreakClient cloudbreakClient) {
        String rdsName = testContext.get(rdsKey).getName();
        return checkRdsDetachedFromEnv(environment, rdsName);
    }

    static <T extends CloudbreakEntity> EnvironmentEntity checkRdsDetachedFromEnv(TestContext testContext,
            EnvironmentEntity environment, Class<T> rdsKey, CloudbreakClient cloudbreakClient) {
        String rdsName = testContext.get(rdsKey).getName();
        return checkRdsDetachedFromEnv(environment, rdsName);
    }

    private static EnvironmentEntity checkRdsDetachedFromEnv(EnvironmentEntity environment, String rdsName) {
        Set<DatabaseV4Response> rdsConfigs = environment.getResponse().getDatabases();
        boolean attached = rdsConfigs.stream().map(DatabaseV4Base::getName)
                .anyMatch(rds -> rds.equals(rdsName));

        if (attached) {
            throw new TestFailException("Rds is attached to environment");
        }
        return environment;
    }

    private static EnvironmentEntity checkLdapAttachedToEnv(TestContext testContext, EnvironmentEntity environment, CloudbreakClient cloudbreakClient) {
        Set<String> ldapConfigs = new HashSet<>();
        Set<LdapV4Response> ldapV4ResponseSet = environment.getResponse().getLdaps();
        for (LdapV4Response ldapV4Response : ldapV4ResponseSet) {
            ldapConfigs.add(ldapV4Response.getName());
        }
        if (!ldapConfigs.contains(testContext.get(LdapConfigTestDto.class).getName())) {
            throw new TestFailException("Ldap is not attached to environment");
        }
        return environment;
    }

    private static EnvironmentEntity checkProxyAttachedToEnv(TestContext testContext, EnvironmentEntity environment, CloudbreakClient cloudbreakClient) {
        Set<String> proxyConfigs = new HashSet<>();
        Set<ProxyV4Response> proxyV4ResponseSet = environment.getResponse().getProxies();
        for (ProxyV4Response proxyV4Response : proxyV4ResponseSet) {
            proxyConfigs.add(proxyV4Response.getName());
        }
        if (!proxyConfigs.contains(testContext.get(ProxyConfigEntity.class).getName())) {
            throw new TestFailException("Proxy is not attached to environment");
        }
        return environment;
    }

    private static EnvironmentEntity checkEnvIsListed(TestContext testContext, EnvironmentEntity environment, CloudbreakClient cloudbreakClient) {
        Collection<SimpleEnvironmentV4Response> simpleEnvironmentV4Respons = environment.getResponseSimpleEnvSet();
        List<SimpleEnvironmentV4Response> result = simpleEnvironmentV4Respons.stream()
                .filter(env -> environment.getName().equals(env.getName()))
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new TestFailException("Environment is not listed");
        }
        return environment;
    }
}