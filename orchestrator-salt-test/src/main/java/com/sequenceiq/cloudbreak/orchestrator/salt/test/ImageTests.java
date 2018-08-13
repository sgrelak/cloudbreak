package com.sequenceiq.cloudbreak.orchestrator.salt.test;

import static com.sequenceiq.cloudbreak.orchestrator.salt.test.ImageTestContants.clientCert;
import static com.sequenceiq.cloudbreak.orchestrator.salt.test.ImageTestContants.clientKey;
import static com.sequenceiq.cloudbreak.orchestrator.salt.test.ImageTestContants.saltPassword;
import static com.sequenceiq.cloudbreak.orchestrator.salt.test.ImageTestContants.saltSignPrivateKey;
import static com.sequenceiq.cloudbreak.orchestrator.salt.test.ImageTestContants.saltSignPublicKey;
import static com.sequenceiq.cloudbreak.orchestrator.salt.test.ImageTestContants.serverCert;
import static com.sequenceiq.cloudbreak.orchestrator.salt.test.ImageTestContants.signatureKey;
import static com.sequenceiq.cloudbreak.orchestrator.salt.test.ImageTestContants.saltBootPassword;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.sequenceiq.cloudbreak.api.model.DatabaseVendor;
import com.sequenceiq.cloudbreak.api.model.RecipeType;
import com.sequenceiq.cloudbreak.api.model.rds.RdsType;
import com.sequenceiq.cloudbreak.api.model.stack.cluster.gateway.SSOType;
import com.sequenceiq.cloudbreak.blueprint.template.views.RdsView;
import com.sequenceiq.cloudbreak.cloud.model.AmbariRepo;
import com.sequenceiq.cloudbreak.concurrent.MDCCleanerTaskDecorator;
import com.sequenceiq.cloudbreak.orchestrator.exception.CloudbreakOrchestratorException;
import com.sequenceiq.cloudbreak.orchestrator.exception.CloudbreakOrchestratorFailedException;
import com.sequenceiq.cloudbreak.orchestrator.executor.ParallelOrchestratorComponentRunner;
import com.sequenceiq.cloudbreak.orchestrator.model.BootstrapParams;
import com.sequenceiq.cloudbreak.orchestrator.model.GatewayConfig;
import com.sequenceiq.cloudbreak.orchestrator.model.Node;
import com.sequenceiq.cloudbreak.orchestrator.model.RecipeModel;
import com.sequenceiq.cloudbreak.orchestrator.model.SaltConfig;
import com.sequenceiq.cloudbreak.orchestrator.model.SaltPillarProperties;
import com.sequenceiq.cloudbreak.orchestrator.salt.SaltOrchestrator;
import com.sequenceiq.cloudbreak.orchestrator.state.ExitCriteria;
import com.sequenceiq.cloudbreak.orchestrator.state.ExitCriteriaModel;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(properties = {
        "rest.debug=true",
})
public class ImageTests extends AbstractTestNGSpringContextTests {

    public static final String SCRIPT = "#!/bin/bash -e\n"
            + "\n"
            + "create_user_home() {\n"
            + "  export DOMAIN=$(dnsdomainname)\n"
            + "su hdfs<<EOF\n"
            + "  if [ -d /etc/security/keytabs ]; then\n"
            + "    echo \"kinit using realm: ${DOMAIN^^}\"\n"
            + "  \tkinit -V -kt /etc/security/keytabs/dn.service.keytab dn/$(hostname -f)@${DOMAIN^^}\n"
            + "  fi\n"
            + "\n"
            + "  if ! hadoop fs -ls /user/$1 2> /dev/null; then\n"
            + "    hadoop fs -mkdir /user/$1 2> /dev/null\n"
            + "    hadoop fs -chown $1:hdfs /user/$1 2> /dev/null\n"
            + "    hadoop dfsadmin -refreshUserToGroupsMappings\n"
            + "    echo \"created /user/$1\"\n"
            + "  else\n"
            + "    echo \"/user/$1 already exists, skipping...\"\n"
            + "  fi\n"
            + "EOF\n"
            + "}\n"
            + "\n"
            + "main(){\n"
            + "  create_user_home yarn\n"
            + "  create_user_home admin\n"
            + "}\n"
            + "\n"
            + "[[ \"$0\" == \"$BASH_SOURCE\" ]] && main \"$@\"\n";

    public static final String AMBARI_DB_PASSWORD = "119mebph99j2bcau2hkrhisqea";

    public static final String HIVE_DB_PASSWORD = "1u43n29lm3ll9me196u78avp3o";

    public static final String AWS_PLATFORM = "AWS";

    public static final String HOST_GROUP = "master";

    public static final String REPO_ID = "HDP-2.6";

    public static final String HDP_VERSION = "2.6.5.0-292";

    public static final String REDHAT_HDP_REPO_URL = "http://public-repo-1.hortonworks.com/HDP/centos6/2.x/updates/2.6.5.0";

    public static final String REDHAT_HDP_VDF_URL = "http://public-repo-1.hortonworks.com/HDP/centos6/2.x/updates/2.6.5.0/HDP-2.6.5.0-292.xml";

    public static final String HIVE_DB = "hive";

    public static final String HIVE_USER = "hive";

    public static final String AMBARI_DB = "ambari";

    public static final String AMBARI_USER = "ambari";

    public static final String REDHAT_AMBARI_REPO_URL = "http://public-repo-1.hortonworks.com/ambari/centos6/2.x/updates/2.6.2.0";

    public static final String AMBARI_VERSION = "2.6.2.0";

    public static final ExitCriteria EXIT_CRITERIA = new ExitCriteria() {
        @Override
        public boolean isExitNeeded(ExitCriteriaModel exitCriteriaModel) {
            return false;
        }

        @Override
        public String exitMessage() {
            return "No more questions";
        }
    };

    private String connectionAddress = "34.243.243.8";

    private String publicAddress = connectionAddress;

    private String privateAddress = "172.31.0.213";

    private String hostname = "ip-172-31-0-213.eu-west-1.compute.internal";

    private GatewayConfig gatewayConfig;

    private Node node;

    private ExitCriteriaModel exitModel;

    @Inject
    private SaltOrchestrator saltOrchestratorUnderTest;

    public ImageTests() {
        Integer gatewayPort = 9443;

        //GatewayConfig gatewayConfig = new GatewayConfig(connectionAddress,publicAddress,privateAddress,gatewayPort, false);
        gatewayConfig = new GatewayConfig(connectionAddress, publicAddress, privateAddress, hostname, gatewayPort, serverCert, clientCert, clientKey,
                saltPassword, saltBootPassword, signatureKey, false, true, saltSignPrivateKey, saltSignPublicKey);
        node = new Node(privateAddress, publicAddress, hostname, HOST_GROUP);
        exitModel = new MyExitCriteriaModel();
    }

    @Test
    void testImage() throws CloudbreakOrchestratorException {
        testIsBootstrapApiAvailable();
        testBootstrap();
        testUploadRecipes();
        testInitServiceRun();
    }

    public void testIsBootstrapApiAvailable() {
        assertTrue(saltOrchestratorUnderTest.isBootstrapApiAvailable(gatewayConfig));
    }

    public void testGetStateConfigZip() throws IOException {
        assertNotNull(saltOrchestratorUnderTest.getStateConfigZip());
    }

    public void testBootstrap() throws CloudbreakOrchestratorException {
        BootstrapParams params = new BootstrapParams();
        params.setCloud(AWS_PLATFORM);
        params.setOs("amazonlinux");
        saltOrchestratorUnderTest.bootstrap(List.of(gatewayConfig), Set.of(node), params, exitModel);
    }

    public void testGetMembers() throws CloudbreakOrchestratorException {
        saltOrchestratorUnderTest.getMissingNodes(gatewayConfig, Set.of(node));
        saltOrchestratorUnderTest.getMembers(gatewayConfig, List.of(privateAddress));
    }

    public void testUploadRecipes() throws CloudbreakOrchestratorFailedException {
        RecipeModel recipe = new RecipeModel("hdfs-home", RecipeType.POST_CLUSTER_INSTALL, SCRIPT);
        Map<String, List<RecipeModel>> recipes = Map.of(HOST_GROUP, List.of(recipe));
        saltOrchestratorUnderTest.uploadRecipes(List.of(gatewayConfig), recipes, exitModel);
    }

    public void testInitServiceRun() throws CloudbreakOrchestratorException {
        SaltPillarProperties ambarigpl =
                new SaltPillarProperties("/ambari/gpl.sls", Map.of("ambari", Map.of("gpl", Map.of("enabled", false))));
        SaltPillarProperties metadata =
                new SaltPillarProperties("/metadata/init.sls", Map.of("cluster", Map.of("name", "testcluster")));
        SaltPillarProperties hdp =
                new SaltPillarProperties("/hdp/repo.sls", Map.of("hdp",
                        Map.of("redhat6", REDHAT_HDP_REPO_URL,
                                "repoid", REPO_ID,
                                "repository-version", HDP_VERSION,
                                "vdf-url", REDHAT_HDP_VDF_URL)));
        SaltPillarProperties discovery =
                new SaltPillarProperties("/discovery/init.sls", Map.of("platform", AWS_PLATFORM));
        SaltPillarProperties postgres =
                new SaltPillarProperties("/postgresql/postgre.sls", Map.of("postgres", Map.of(
                        "hive", Map.of(
                                "database", HIVE_DB,
                                "password", HIVE_DB_PASSWORD,
                                "user", HIVE_USER
                        ),
                        "database", HIVE_DB,
                        "password", HIVE_DB_PASSWORD,
                        "user", HIVE_USER,
                        "ambari", Map.of(
                                "database", AMBARI_DB,
                                "password", AMBARI_DB_PASSWORD,
                                "user", AMBARI_USER
                        )
                )));
        com.sequenceiq.cloudbreak.domain.RDSConfig rdsConfig =
                new com.sequenceiq.cloudbreak.domain.RDSConfig();
        rdsConfig.setConnectionURL("jdbc:postgresql://" + privateAddress + ":5432/" + AMBARI_DB);
        rdsConfig.setDatabaseEngine(DatabaseVendor.POSTGRES);
        rdsConfig.setConnectionPassword(AMBARI_DB_PASSWORD);
        rdsConfig.setConnectionUserName(AMBARI_USER);
        rdsConfig.setConnectionDriver("org.postgresql.Driver");
        rdsConfig.setName(AMBARI_DB);
        rdsConfig.setType(RdsType.AMBARI.name());

        SaltPillarProperties ambariDb =
                new SaltPillarProperties("/ambari/database.sls", Map.of("ambari", Map.of(
                        "database", new RdsView(rdsConfig)
                )));
        SaltPillarProperties ambariCredentials
                = new SaltPillarProperties("/ambari/credentials.sls", Map.of("ambari", Map.of(
                "password", "24evrqe59hfi5doctlf3df949n",
                "securityMasterKey", "bigdata",
                "username", "cloudbreak"
        )));
        AmbariRepo ambariRepo = new AmbariRepo();
        ambariRepo.setVersion(AMBARI_VERSION);
        ambariRepo.setBaseUrl(REDHAT_AMBARI_REPO_URL);
        ambariRepo.setPredefined(true);
        SaltPillarProperties ambariRepository
                = new SaltPillarProperties("/ambari/repo.sls", Map.of("ambari", Map.of("repo", ambariRepo)));
        SaltPillarProperties gateway
                = new SaltPillarProperties("/gateway/init.sls", Map.of("gateway", Map.of(
                "mastersecret", "5tpevchd8m73m7v9mni9borm8k",
                "password", "admin123",
                "address", publicAddress,
                "ssotype", SSOType.NONE,
                "location", Map.of(
                        "HIVE_SERVER", List.of(hostname),
                        "HISTORYSERVER", List.of(hostname),
                        "SPARK_JOBHISTORYSERVER", List.of(hostname),
                        "NAMENODE", List.of(hostname),
                        "ZEPPELIN_MASTER", List.of(hostname),
                        "RESOURCEMANAGER", List.of(hostname)
                ),
                "kerberos", false,
                "username", "admin"
        )));
        SaltPillarProperties docker =
                new SaltPillarProperties("/docker/init.sls", Map.of("docker", Map.of("enableContainerExecutor", false)));
        Map<String, SaltPillarProperties> servicePillarConfig = Map.of(
                "ambari-gpl-repo", ambarigpl,
                "metadata", metadata,
                "hdp", hdp,
                "discovery", discovery,
                "postgresql-server", postgres,
                "ambari-database", ambariDb,
                "ambari-credentials", ambariCredentials,
                "ambari-repo", ambariRepository,
                "gateway", gateway,
                "docker", docker
        );
        Map<String, Map<String, String>> grainsProperites = Map.of(privateAddress, Map.of("gateway-address", publicAddress));
        SaltConfig saltConfig = new SaltConfig(servicePillarConfig, grainsProperites);
        saltOrchestratorUnderTest.initServiceRun(List.of(gatewayConfig), Set.of(node), saltConfig, exitModel);
        saltOrchestratorUnderTest.runService(List.of(gatewayConfig), Set.of(node), saltConfig, exitModel);
    }

    @Configuration
    @ComponentScan("com.sequenceiq.cloudbreak.orchestrator.salt")
    public static class SpringConfig {

        //@Value("${cb.container.threadpool.core.size:}")
        private int containerCorePoolSize = 2;

        //@Value("${cb.container.threadpool.capacity.size:}")
        private int containerteQueueCapacity = 2;

        @Bean
        public SaltOrchestrator saltOrchestrator() {
            SaltOrchestrator saltOrchestrator = new SaltOrchestrator();
            saltOrchestrator.init(simpleParallelContainerRunnerExecutor(), clusterDeletionBasedExitCriteria());
            return saltOrchestrator;
        }

        @Bean
        public ParallelOrchestratorComponentRunner simpleParallelContainerRunnerExecutor() {
            return new TestOrchestratorComponentRunner(containerBootstrapBuilderExecutor());
        }

        @Bean
        public ExitCriteria clusterDeletionBasedExitCriteria() {
            return EXIT_CRITERIA;
        }

        @Bean
        public AsyncTaskExecutor containerBootstrapBuilderExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(containerCorePoolSize);
            executor.setQueueCapacity(containerteQueueCapacity);
            executor.setThreadNamePrefix("containerBootstrapBuilderExecutor-");
            executor.setTaskDecorator(new MDCCleanerTaskDecorator());
            executor.initialize();
            return executor;
        }
    }

    private static class MyExitCriteriaModel extends ExitCriteriaModel {
    }

    static class TestOrchestratorComponentRunner implements ParallelOrchestratorComponentRunner {

        private final AsyncTaskExecutor asyncTaskExecutor;

        TestOrchestratorComponentRunner(AsyncTaskExecutor asyncTaskExecutor) {
            this.asyncTaskExecutor = asyncTaskExecutor;
        }

        @Override
        public Future<Boolean> submit(Callable<Boolean> callable) {
            return asyncTaskExecutor.submit(callable);
        }
    }
}
