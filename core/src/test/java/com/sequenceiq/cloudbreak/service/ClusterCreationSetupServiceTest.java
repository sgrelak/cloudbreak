package com.sequenceiq.cloudbreak.service;

import static com.sequenceiq.cloudbreak.RepoTestUtil.getDefaultCDHInfo;
import static com.sequenceiq.cloudbreak.RepoTestUtil.getDefaultHDPInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Sets;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.InstanceGroupType;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.ClusterV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.util.responses.StackMatrixV4Response;
import com.sequenceiq.cloudbreak.cloud.model.AmbariRepo;
import com.sequenceiq.cloudbreak.cloud.model.ClouderaManagerRepo;
import com.sequenceiq.cloudbreak.cloud.model.Image;
import com.sequenceiq.cloudbreak.cloud.model.component.DefaultCDHEntries;
import com.sequenceiq.cloudbreak.cloud.model.component.DefaultCDHInfo;
import com.sequenceiq.cloudbreak.cloud.model.component.DefaultHDPEntries;
import com.sequenceiq.cloudbreak.cloud.model.component.DefaultHDPInfo;
import com.sequenceiq.cloudbreak.clusterdefinition.utils.ClusterDefinitionUtils;
import com.sequenceiq.cloudbreak.cloud.model.component.RepositoryInfo;
import com.sequenceiq.cloudbreak.cluster.api.ClusterPreCreationApi;
import com.sequenceiq.cloudbreak.common.type.ComponentType;
import com.sequenceiq.cloudbreak.controller.exception.BadRequestException;
import com.sequenceiq.cloudbreak.core.CloudbreakImageNotFoundException;
import com.sequenceiq.cloudbreak.domain.ClusterDefinition;
import com.sequenceiq.cloudbreak.domain.KerberosConfig;
import com.sequenceiq.cloudbreak.domain.json.Json;
import com.sequenceiq.cloudbreak.domain.stack.Component;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.cluster.Cluster;
import com.sequenceiq.cloudbreak.domain.stack.cluster.ClusterComponent;
import com.sequenceiq.cloudbreak.domain.workspace.User;
import com.sequenceiq.cloudbreak.domain.workspace.Workspace;
import com.sequenceiq.cloudbreak.service.cluster.ClusterApiConnectors;
import com.sequenceiq.cloudbreak.service.cluster.ClusterService;
import com.sequenceiq.cloudbreak.service.clusterdefinition.ClusterDefinitionService;
import com.sequenceiq.cloudbreak.service.decorator.ClusterDecorator;

public class ClusterCreationSetupServiceTest {

    public static final String REDHAT_7 = "redhat7";

    public static final String HDP_VERSION = "3.0";

    public static final String CDH_VERSION = "6.1.0";

    @Mock
    private ClusterDecorator clusterDecorator;

    @Mock
    private ComponentConfigProvider componentConfigProvider;

    @Mock
    private ClusterDefinitionUtils clusterDefinitionUtils;

    @Mock
    private ClusterDefinitionService clusterDefinitionService;

    @Mock
    private DefaultHDPEntries defaultHDPEntries;

    @Mock
    private DefaultCDHEntries defaultCDHEntries;

    @Mock
    private StackMatrixService stackMatrixService;

    @Mock
    private ClusterService clusterService;

    @Mock
    private ClusterApiConnectors clusterApiConnectors;

    @Mock
    private DefaultAmbariRepoService defaultAmbariRepoService;

    @Mock
    private DefaultClouderaManagerRepoService defaultClouderaManagerRepoService;

    @Spy
    private ClusterPreCreationApi clusterPreCreationApi;

    @Captor
    private ArgumentCaptor<ArrayList<ClusterComponent>> clusterComponentCaptor;

    @InjectMocks
    private ClusterCreationSetupService underTest;

    private ClusterV4Request clusterRequest;

    private Stack stack;

    private ClusterDefinition clusterDefinition;

    private User user;

    private Workspace workspace;

    private Cluster cluster;

    @Before
    public void init() throws CloudbreakImageNotFoundException, JsonProcessingException {
        MockitoAnnotations.initMocks(this);
        workspace = new Workspace();
        clusterRequest = new ClusterV4Request();
        stack = new Stack();
        stack.setId(1L);
        stack.setWorkspace(workspace);
        clusterDefinition = new ClusterDefinition();
        clusterDefinition.setClusterDefinitionText("{}");
        user = new User();
        Map<InstanceGroupType, String> userData = new HashMap<>();
        userData.put(InstanceGroupType.CORE, "userdata");
        Image image = new Image("imagename", userData, "centos7", REDHAT_7, "url", "imgcatname",
                "id", Collections.emptyMap());
        Component ambariRepoComponent = spy(new Component(ComponentType.AMBARI_REPO_DETAILS,
                ComponentType.AMBARI_REPO_DETAILS.name(), new Json(getAmbariRepo()), stack));
        Component imageComponent = new Component(ComponentType.IMAGE, ComponentType.IMAGE.name(), new Json(image), stack);

        cluster = new Cluster();
        stack.setCluster(cluster);
        KerberosConfig kerberosConfig = new KerberosConfig();
        kerberosConfig.setDomain("domain");
        cluster.setKerberosConfig(kerberosConfig);
        when(clusterDecorator.decorate(any(), any(), any(), any(), any(), any())).thenReturn(cluster);
        when(componentConfigProvider.getAllComponentsByStackIdAndType(any(), any())).thenReturn(Sets.newHashSet(ambariRepoComponent, imageComponent));
        when(clusterDefinitionUtils.getBlueprintStackVersion(any())).thenReturn(HDP_VERSION);
        when(clusterDefinitionUtils.getBlueprintStackName(any())).thenReturn("HDP");

        DefaultHDPInfo defaultHDPInfo = getDefaultHDPInfo("2.7.0", HDP_VERSION);
        DefaultCDHInfo defaultCDHInfo = getDefaultCDHInfo("6.1.0", CDH_VERSION);
        setupAmbariEntries();
        setupClouderaManagerEntries();

        when(defaultHDPEntries.getEntries()).thenReturn(Collections.singletonMap(HDP_VERSION, defaultHDPInfo));
        when(defaultCDHEntries.getEntries()).thenReturn(Collections.singletonMap(CDH_VERSION, defaultCDHInfo));
        when(componentConfigProvider.getImage(anyLong())).thenReturn(image);
        StackMatrixV4Response stackMatrixV4Response = new StackMatrixV4Response();
        stackMatrixV4Response.setHdp(Collections.singletonMap(HDP_VERSION, null));
        when(stackMatrixService.getStackMatrix()).thenReturn(stackMatrixV4Response);
        when(clusterService.save(any(Cluster.class))).thenReturn(cluster);
    }

    @Test
    public void testDomainIsSet() throws CloudbreakImageNotFoundException, IOException, TransactionService.TransactionExecutionException {
        underTest.prepare(clusterRequest, stack, clusterDefinition, user, workspace);

        assertEquals(cluster.getKerberosConfig().getDomain(), stack.getCustomDomain());
    }

    @Test
    public void testMissingKerberosConfig() throws CloudbreakImageNotFoundException, IOException, TransactionService.TransactionExecutionException {
        cluster.setKerberosConfig(null);

        underTest.prepare(clusterRequest, stack, clusterDefinition, user, workspace);

        assertNull(stack.getCustomDomain());
    }

    @Test
    public void testMissingDomain() throws CloudbreakImageNotFoundException, IOException, TransactionService.TransactionExecutionException {
        cluster.getKerberosConfig().setDomain(null);

        underTest.prepare(clusterRequest, stack, clusterDefinition, user, workspace);

        assertNull(stack.getCustomDomain());
    }

    @Test
    public void testIncorrectKerberosSettingForWorkloadCluster() {
        stack.setDatalakeResourceId(1L);
        clusterRequest.setKerberosName("attached_kerberos_which_not_allowed");

        assertThrows(BadRequestException.class, () -> underTest.validate(clusterRequest, stack, user, workspace));
    }

    @Test
    public void testAmbariClusterComponents() throws CloudbreakImageNotFoundException, IOException, TransactionService.TransactionExecutionException {
        when(clusterDefinitionService.isAmbariBlueprint(clusterDefinition)).thenReturn(true);
        when(clusterApiConnectors.getConnector(any(Cluster.class))).thenReturn(clusterPreCreationApi);
        when(clusterPreCreationApi.isVdfReady(any(AmbariRepo.class))).thenReturn(false);
        underTest.prepare(clusterRequest, stack, clusterDefinition, user, workspace);
        verify(clusterService, times(1)).create(any(Stack.class), any(Cluster.class), clusterComponentCaptor.capture(), any(User.class));

        ArrayList<ClusterComponent> clusterComponents = clusterComponentCaptor.getValue();
        assertEquals(2, clusterComponents.size());
        assertTrue(clusterComponents.stream().anyMatch(component -> ComponentType.AMBARI_REPO_DETAILS.equals(component.getComponentType())));
        assertTrue(clusterComponents.stream().anyMatch(component -> ComponentType.HDP_REPO_DETAILS.equals(component.getComponentType())));
    }

    @Test
    public void testClouderaManagerClusterComponents() throws CloudbreakImageNotFoundException, IOException, TransactionService.TransactionExecutionException {
        when(clusterDefinitionService.isClouderaManagerTemplate(clusterDefinition)).thenReturn(true);
        when(clusterApiConnectors.getConnector(any(Cluster.class))).thenReturn(clusterPreCreationApi);
        underTest.prepare(clusterRequest, stack, clusterDefinition, user, workspace);
        verify(clusterService, times(1)).create(any(Stack.class), any(Cluster.class), clusterComponentCaptor.capture(), any(User.class));

        ArrayList<ClusterComponent> clusterComponents = clusterComponentCaptor.getValue();
        assertEquals(2, clusterComponents.size());
        assertTrue(clusterComponents.stream().anyMatch(component -> ComponentType.CM_REPO_DETAILS.equals(component.getComponentType())));
        assertTrue(clusterComponents.stream().anyMatch(component -> ComponentType.CDH_PRODUCT_DETAILS.equals(component.getComponentType())));
    }

    private AmbariRepo getAmbariRepo() {
        AmbariRepo ambariRepo = new AmbariRepo();
        ambariRepo.setBaseUrl("http://public-repo-1.hortonworks.com/ambari/centos7/2.x/updates/2.6.2.2");
        ambariRepo.setVersion("2.6.2.2");
        return ambariRepo;
    }

    private ClouderaManagerRepo getClouderaManagerRepo() {
        ClouderaManagerRepo clouderaManagerRepo = new ClouderaManagerRepo();
        clouderaManagerRepo.setBaseUrl("http://public-repo-1.hortonworks.com/cm/centos7/6.1.0/updates/6.1.0");
        clouderaManagerRepo.setVersion("6.1.0");
        return clouderaManagerRepo;
    }

    private void setupAmbariEntries() {
        Map<String, RepositoryInfo> ambariEntries = new HashMap<>();
        Mockito.when(defaultAmbariRepoService.getEntries()).thenReturn(ambariEntries);
        AmbariRepo ambariRepo = getAmbariRepo();
        Mockito.when(defaultAmbariRepoService.getDefault(REDHAT_7)).thenReturn(ambariRepo);
    }

    private void setupClouderaManagerEntries() {
        Map<String, RepositoryInfo> clouderaManagerEntries = new HashMap<>();
        Mockito.when(defaultClouderaManagerRepoService.getEntries()).thenReturn(clouderaManagerEntries);
        ClouderaManagerRepo clouderaManagerRepo = getClouderaManagerRepo();
        Mockito.when(defaultClouderaManagerRepoService.getDefault(REDHAT_7)).thenReturn(clouderaManagerRepo);
    }
}