package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import static com.sequenceiq.it.cloudbreak.newway.cloud.HostGroupType.COMPUTE;
import static com.sequenceiq.it.cloudbreak.newway.cloud.HostGroupType.MASTER;
import static com.sequenceiq.it.cloudbreak.newway.cloud.HostGroupType.WORKER;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.mappable.CloudPlatform;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.stack.AwsStackV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.stack.AzureStackV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.stack.GcpStackV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.stack.MockStackV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.stack.OpenStackStackV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.stack.YarnStackV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.StackV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.authentication.StackAuthenticationV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.ClusterV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.sharedservice.SharedServiceV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.environment.EnvironmentSettingsV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.InstanceGroupV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.tags.TagsV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.instancegroup.InstanceGroupV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.instancegroup.instancemetadata.InstanceMetaDataV4Response;
import com.sequenceiq.it.cloudbreak.newway.SecurityRulesEntity;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;
import com.sequenceiq.it.cloudbreak.newway.entity.environment.EnvironmentSettingsDto;
import com.sequenceiq.it.cloudbreak.newway.entity.mpack.ManagementPackDetailsDto;
import com.sequenceiq.it.cloudbreak.newway.entity.environment.EnvironmentTestDto;
import com.sequenceiq.it.cloudbreak.newway.ImageSettingsEntity;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.imagecatalog.ImageCatalogTestDto;

public abstract class StackDtoBase<T extends StackDtoBase<T>> extends AbstractCloudbreakDto<StackV4Request, StackV4Response, T> {

    public StackDtoBase(String newId) {
        super(newId);
        StackV4Request r = new StackV4Request();
        setRequest(r);
    }

    public StackDtoBase(TestContext testContext) {
        super(new StackV4Request(), testContext);
    }

    public StackDtoBase<T> valid() {
        String name = getNameCreator().getRandomNameForResource();
        withName(name)
                .withImageSettings(getCloudProvider().imageSettings(getTestContext().given(ImageSettingsEntity.class)))
                .withPlacement(getTestContext().given(PlacementSettingsDto.class))
                .withInstanceGroupsEntity(InstanceGroupDto.defaultHostGroup(getTestContext()))
                .withNetwork(getTestContext().given(NetworkV2Dto.class))
                .withStackAuthentication(getCloudProvider().stackAuthentication(given(StackAuthenticationDto.class)))
                .withGatewayPort(getCloudProvider().gatewayPort(this))
                .withCluster(getTestContext().given(ClusterEntity.class).withName(name));
        return getCloudProvider().stack(this);
    }

    public StackDtoBase<T> withEveryProperties() {
        ImageCatalogTestDto imgCat = getTestContext().get(ImageCatalogTestDto.class);
        getTestContext()
                .given("network", NetworkV2Dto.class).withSubnetCIDR("10.10.0.0/16")
                .given("securityRulesWorker", SecurityRulesEntity.class).withPorts("55", "66", "77").withProtocol("ftp").withSubnet("10.0.0.0/32")
                .given("securityGroupMaster", SecurityGroupDto.class).withSecurityGroupIds("scgId1", "scgId2")
                .given("securityGroupWorker", SecurityGroupDto.class).withSecurityRules("securityRulesWorker")
                .given("master", InstanceGroupDto.class).withHostGroup(MASTER).withRecipes("mock-test-recipe").withSecurityGroup("securityGroupMaster")
                .given("worker", InstanceGroupDto.class).withHostGroup(WORKER).withSecurityGroup("securityGroupWorker")
                .given("compute", InstanceGroupDto.class).withHostGroup(COMPUTE)
                .given("mpackDetails", ManagementPackDetailsDto.class).withName("mock-test-mpack")
                .given("ambariStack", StackRepositoryDto.class).withMpacks("mpackDetails")
                .given("ambariRepo", AmbariRepositoryDto.class)
                .given("gatewayTopology", GatewayTopologyDto.class).withExposedServices("AMBARI").withTopologyName("proxy-name")
                .given("gateway", GatewayDto.class).withTopologies("gatewayTopology")
                .given("ambari", AmbariDto.class).withAmbariRepoDetails("ambariRepo").withStackRepository("ambariStack")
                .given("cluster", ClusterEntity.class).withRdsConfigNames("mock-test-rds").withLdapConfigName("mock-test-ldap").withAmbari("ambari")
                .withGateway("gateway")
                .given("imageSettings", ImageSettingsEntity.class).withImageId("f6e778fc-7f17-4535-9021-515351df3691").withImageCatalog(imgCat.getName());

        return withNetwork("network")
                .withInstanceGroups("master", "worker", "compute")
                .withCluster("cluster")
                .withUserDefinedTags(Map.of("some-tag", "custom-tag"))
                .withInputs(Map.of("some-input", "custom-input"))
                .withImageSettings("imageSettings");
    }

    public StackDtoBase<T> withEnvironmentSettings() {
        return withEnvironmentSettings(EnvironmentSettingsDto.class.getSimpleName());
    }

    public StackDtoBase<T> withEnvironmentSettings(EnvironmentSettingsDto environment) {
        getRequest().setEnvironment(environment.getRequest());
        return this;
    }

    public StackDtoBase<T> withCloudPlatform(CloudPlatform cloudPlatform) {
        getRequest().setCloudPlatform(cloudPlatform);
        return this;
    }

    public StackDtoBase<T> withEnvironment(Class<EnvironmentTestDto> environmentKey) {
        return withEnvironmentKey(environmentKey.getSimpleName());
    }

    public StackDtoBase<T> withCatalog(Class<ImageCatalogTestDto> catalogKey) {
        ImageCatalogTestDto catalog = getTestContext().get(catalogKey);
        if (catalog == null) {
            throw new IllegalArgumentException("Catalog is null with given key: " + catalogKey);
        }
        getRequest().getImage().setCatalog(catalog.getName());
        return this;
    }

    public StackDtoBase<T> withEnvironmentKey(String environmentKey) {
        EnvironmentTestDto env = getTestContext().get(environmentKey);
        if (env == null) {
            throw new IllegalArgumentException("Env is null with given key: " + environmentKey);
        }
        return withEnvironmentSettings(getTestContext().init(EnvironmentSettingsDto.class)
                .withName(env.getName()));
    }

    public StackDtoBase<T> withEnvironmentSettings(String environmentKey) {
        EnvironmentSettingsDto environment = getTestContext().get(environmentKey);
        getRequest().setEnvironment(environment.getRequest());
        return this;
    }

    /**
     * @deprecated this is forbidden in newway.testcase
     */
    @Deprecated
    public StackDtoBase<T> withEnvironmentSettings(EnvironmentSettingsV4Request environment) {
        getRequest().setEnvironment(environment);
        return this;
    }

    public StackDtoBase<T> withName(String name) {
        getRequest().setName(name);
        setName(name);
        return this;
    }

    public StackDtoBase<T> withCluster() {
        return withCluster(ClusterEntity.class.getSimpleName());
    }

    public StackDtoBase<T> withCluster(String key) {
        ClusterEntity clusterEntity = getTestContext().get(key);
        return withCluster(clusterEntity);
    }

    public StackDtoBase<T> withCluster(ClusterEntity cluster) {
        getRequest().setCluster(cluster.getRequest());
        return this;
    }

    public StackDtoBase<T> withClusterRequest(ClusterV4Request clusterRequest) {
        getRequest().setCluster(clusterRequest);
        return this;
    }

    public StackDtoBase<T> withImageSettings(String key) {
        ImageSettingsEntity imageSettingsEntity = getTestContext().get(key);
        getRequest().setImage(imageSettingsEntity.getRequest());
        return this;
    }

    public StackDtoBase<T> withImageSettings(ImageSettingsEntity imageSettings) {
        getRequest().setImage(imageSettings.getRequest());
        ImageCatalogTestDto imageCatalogTestDto = getTestContext().get(ImageCatalogTestDto.class);
        if (imageCatalogTestDto != null) {
            getRequest().getImage().setCatalog(imageCatalogTestDto.getName());
        }
        return this;
    }

    public StackDtoBase<T> withInputs(Map<String, Object> inputs) {
        if (inputs == null) {
            getRequest().setInputs(Collections.emptyMap());
        } else {
            getRequest().setInputs(inputs);
        }
        return this;
    }

    public StackDtoBase<T> withInstanceGroups(List<InstanceGroupV4Request> instanceGroups) {
        getRequest().setInstanceGroups(instanceGroups);
        return this;
    }

    public StackDtoBase<T> withInstanceGroupsEntity(Collection<InstanceGroupDto> instanceGroups) {
        getRequest().setInstanceGroups(instanceGroups.stream()
                .map(InstanceGroupDto::getRequest)
                .collect(Collectors.toList()));
        return this;
    }

    public StackDtoBase<T> withDefaultInstanceGroups() {
        return withInstanceGroups(InstanceGroupDto.class.getSimpleName());
    }

    public StackDtoBase<T> replaceInstanceGroups(String... keys) {
        Stream.of(keys)
                .map(this::getInstanceGroupV2Request)
                .forEach(ig -> {
                    for (int i = 0; i < getRequest().getInstanceGroups().size(); i++) {
                        InstanceGroupV4Request old = getRequest().getInstanceGroups().get(i);
                        if (old.getName().equals(ig.getName())) {
                            getRequest().getInstanceGroups().remove(i);
                            getRequest().getInstanceGroups().add(i, ig);
                        }
                    }
                });
        return this;
    }

    private InstanceGroupV4Request getInstanceGroupV2Request(String key) {
        InstanceGroupDto instanceGroupDto = getTestContext().get(key);
        if (instanceGroupDto == null) {
            throw new IllegalStateException("Given key is not exists in the test context: " + key);
        }
        return instanceGroupDto.getRequest();
    }

    public StackDtoBase<T> withInstanceGroups(String... keys) {
        getRequest().setInstanceGroups(Stream.of(keys)
                .map(this::getInstanceGroupV2Request)
                .collect(Collectors.toList()));
        return this;
    }

    public StackDtoBase<T> withNetwork(String key) {
        NetworkV2Dto network = getTestContext().get(key);
        return withNetwork(network);
    }

    public StackDtoBase<T> withNetwork(NetworkV2Dto network) {
        getRequest().setNetwork(network.getRequest());
        return this;
    }

    public StackDtoBase<T> withAzure(AzureStackV4Parameters azure) {
        getRequest().setAzure(azure);
        return this;
    }

    public StackDtoBase<T> withAws(AwsStackV4Parameters aws) {
        getRequest().setAws(aws);
        return this;
    }

    public StackDtoBase<T> withGcp(GcpStackV4Parameters gcp) {
        getRequest().setGcp(gcp);
        return this;
    }

    public StackDtoBase<T> withOpenStack(OpenStackStackV4Parameters openStack) {
        getRequest().setOpenstack(openStack);
        return this;
    }

    public StackDtoBase<T> withMock(MockStackV4Parameters mock) {
        getRequest().setMock(mock);
        return this;
    }

    public StackDtoBase<T> withYarn(YarnStackV4Parameters yarn) {
        getRequest().setYarn(yarn);
        return this;
    }

    public StackDtoBase<T> withStackAuthentication(StackAuthenticationV4Request stackAuthentication) {
        getRequest().setAuthentication(stackAuthentication);
        return this;
    }

    public StackDtoBase<T> withStackAuthentication(StackAuthenticationDto stackAuthentication) {
        getRequest().setAuthentication(stackAuthentication.getRequest());
        return this;
    }

    public StackDtoBase<T> withUserDefinedTags(Map<String, String> tags) {
        if (getRequest().getTags() == null) {
            getRequest().setTags(new TagsV4Request());
        }
        getRequest().getTags().setUserDefined(tags);
        return this;
    }

    public StackDtoBase<T> withGatewayPort(Integer port) {
        getRequest().setGatewayPort(port);
        return this;
    }

    public boolean hasCluster() {
        return getRequest().getCluster() != null;
    }

    public List<InstanceGroupV4Response> getInstanceGroups() {
        return getResponse().getInstanceGroups();
    }

    public String getInstanceId(String hostGroupName) {
        Set<InstanceMetaDataV4Response> metadata = getInstanceMetaData(hostGroupName);
        return metadata
                .stream()
                .findFirst()
                .get()
                .getInstanceId();
    }

    public Set<InstanceMetaDataV4Response> getInstanceMetaData(String hostGroupName) {
        return getResponse().getInstanceGroups()
                .stream().filter(im -> im.getName().equals(hostGroupName))
                .findFirst()
                .get()
                .getMetadata();
    }

    public StackDtoBase<T> withPlacement(String key) {
        PlacementSettingsDto placementSettings = getTestContext().get(key);
        return withPlacement(placementSettings);
    }

    public StackDtoBase<T> withPlacement(PlacementSettingsDto placementSettings) {
        getRequest().setPlacement(placementSettings.getRequest());
        return this;
    }

    public StackDtoBase<T> withSharedService(String datalakeClusterName) {
        SharedServiceV4Request sharedServiceRequest = new SharedServiceV4Request();
        sharedServiceRequest.setDatalakeName(datalakeClusterName);
        getRequest().setSharedService(sharedServiceRequest);
        return this;
    }

    @Override
    public String getName() {
        return getRequest().getName();
    }
}
