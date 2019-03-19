package com.sequenceiq.it.cloudbreak.newway.cloud.v2.yarn;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.mappable.CloudPlatform;
import com.sequenceiq.cloudbreak.api.endpoint.v4.credentials.parameters.yarn.YarnCredentialV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.network.YarnNetworkV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.stack.YarnStackV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.template.YarnInstanceTemplateV4Parameters;
import com.sequenceiq.it.cloudbreak.newway.entity.environment.EnvironmentTestDto;
import com.sequenceiq.it.cloudbreak.newway.cloud.v2.AbstractCloudProvider;
import com.sequenceiq.it.cloudbreak.newway.cloud.v2.CommonCloudParameters;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.InstanceTemplateDto;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.NetworkV2Dto;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.StackAuthenticationDto;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.StackDtoBase;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.VolumeDto;
import com.sequenceiq.it.cloudbreak.newway.entity.credential.CredentialTestDto;

@Component
public class YarnCloudProvider extends AbstractCloudProvider {

    public static final String DEFAULT_LOCATION = "Frankfurt";

    public static final String DEFAULT_QUEUE = "HDP_2_6_0_0-integration-tests";

    public static final String DEFAULT_VOLUME_SIZE = "0";

    public static final String DEFAULT_VOLUME_COUNT = "0";

    public static final String DEFAULT_CPU_COUNT = "4";

    public static final String DEFAULT_MEMORY_SIZE = "8192";

    @Override
    public CloudPlatform getCloudPlatform() {
        return CloudPlatform.YARN;
    }

    @Override
    public CredentialTestDto credential(CredentialTestDto credential) {
        return credential
                .withDescription(CommonCloudParameters.CREDENTIAL_DEFAULT_DESCRIPTION)
                .withCloudPlatform(CloudPlatform.YARN.name())
                .withYarnParameters(yarnCredentialParameters());
    }

    @Override
    public EnvironmentTestDto environment(EnvironmentTestDto environment) {
        return environment
                .withLocation(DEFAULT_LOCATION);
    }

    @Override
    public String region() {
        return getTestParameter().getWithDefault(YarnParameters.REGION, null);
    }

    public String location() {
        return getTestParameter().getWithDefault(YarnParameters.LOCATION, DEFAULT_LOCATION);
    }

    @Override
    public InstanceTemplateDto template(InstanceTemplateDto template) {
        return template.withYarn(instanceParameters());
    }

    @Override
    public StackDtoBase stack(StackDtoBase stack) {
        return stack.withYarn(stackParameters());
    }

    @Override
    public YarnStackV4Parameters stackParameters() {
        YarnStackV4Parameters yarnStackV4Parameters = new YarnStackV4Parameters();
        yarnStackV4Parameters.setYarnQueue(getQueue());
        return yarnStackV4Parameters;
    }

    @Override
    public VolumeDto attachedVolume(VolumeDto volume) {
        return volume.withSize(Integer.parseInt(getTestParameter().getWithDefault(YarnParameters.Instance.VOLUME_SIZE, DEFAULT_VOLUME_SIZE)))
                .withCount(Integer.parseInt(getTestParameter().getWithDefault(YarnParameters.Instance.VOLUME_COUNT, DEFAULT_VOLUME_COUNT)));
    }

    @Override
    public NetworkV2Dto network(NetworkV2Dto network) {
        return network.withYarn(networkParameters()).withSubnetCIDR(getSubnetCIDR());
    }

    @Override
    public String availabilityZone() {
        return getTestParameter().getWithDefault(YarnParameters.AVAILABILITY_ZONE, null);
    }

    @Override
    public StackAuthenticationDto stackAuthentication(StackAuthenticationDto stackAuthenticationDto) {
        String sshPublicKey = getTestParameter().getWithDefault(CommonCloudParameters.SSH_PUBLIC_KEY, CommonCloudParameters.DEFAULT_SSH_PUBLIC_KEY);
        return stackAuthenticationDto.withPublicKey(sshPublicKey);
    }

    @Override
    public String getDefaultClusterDefinitionName() {
        return YarnParameters.DEFAULT_CLUSTER_DEFINTION_NAME;
    }

    public String getQueue() {
        return getTestParameter().getWithDefault(YarnParameters.YARN_QUEUE, DEFAULT_QUEUE);
    }

    public Integer getCPUCount() {
        return Integer.parseInt(getTestParameter().getWithDefault(YarnParameters.Instance.CPU_COUNT, DEFAULT_CPU_COUNT));
    }

    public Integer getMemorySize() {
        return Integer.parseInt(getTestParameter().getWithDefault(YarnParameters.Instance.MEMORY_SIZE, DEFAULT_MEMORY_SIZE));
    }

    public YarnCredentialV4Parameters yarnCredentialParameters() {
        YarnCredentialV4Parameters yarnCredentialV4Parameters = new YarnCredentialV4Parameters();
        yarnCredentialV4Parameters.setEndpoint(getTestParameter().get(YarnParameters.Credential.ENDPOINT));
        yarnCredentialV4Parameters.setAmbariUser(getTestParameter().get(YarnParameters.Credential.AMBARI_USER));
        return yarnCredentialV4Parameters;
    }

    private YarnInstanceTemplateV4Parameters instanceParameters() {
        YarnInstanceTemplateV4Parameters yarnInstanceTemplateV4Parameters = new YarnInstanceTemplateV4Parameters();
        yarnInstanceTemplateV4Parameters.setCpus(getCPUCount());
        yarnInstanceTemplateV4Parameters.setMemory(getMemorySize());
        return yarnInstanceTemplateV4Parameters;
    }

    private YarnNetworkV4Parameters networkParameters() {
        YarnNetworkV4Parameters yarnNetworkV4Parameters = new YarnNetworkV4Parameters();
        yarnNetworkV4Parameters.getCloudPlatform();
        return yarnNetworkV4Parameters;
    }
}