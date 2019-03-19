package com.sequenceiq.it.cloudbreak.newway.cloud.v2.azure;

import static com.sequenceiq.it.cloudbreak.newway.cloud.v2.CommonCloudParameters.CREDENTIAL_DEFAULT_DESCRIPTION;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.mappable.CloudPlatform;
import com.sequenceiq.cloudbreak.api.endpoint.v4.credentials.parameters.azure.AppBased;
import com.sequenceiq.cloudbreak.api.endpoint.v4.credentials.parameters.azure.AzureCredentialV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.network.AzureNetworkV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.stack.AzureStackV4Parameters;
import com.sequenceiq.it.cloudbreak.newway.cloud.v2.AbstractCloudProvider;
import com.sequenceiq.it.cloudbreak.newway.cloud.v2.CommonCloudParameters;
import com.sequenceiq.it.cloudbreak.newway.cloud.v2.aws.AwsParameters;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.InstanceTemplateDto;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.NetworkV2Dto;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.StackAuthenticationDto;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.StackDtoBase;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.VolumeDto;
import com.sequenceiq.it.cloudbreak.newway.entity.credential.CredentialTestDto;

@Component
public class AzureCloudProvider extends AbstractCloudProvider {

    @Override
    public CredentialTestDto credential(CredentialTestDto credential) {
        AzureCredentialV4Parameters parameters = new AzureCredentialV4Parameters();
        parameters.setSubscriptionId(getTestParameter().getRequired(AzureParameters.Credential.SUBSCRIPTION_ID));
        parameters.setTenantId(getTestParameter().getRequired(AzureParameters.Credential.TENANT_ID));
        AppBased appBased = new AppBased();
        appBased.setAccessKey(getTestParameter().getRequired(AzureParameters.Credential.APP_ID));
        appBased.setSecretKey(getTestParameter().getRequired(AzureParameters.Credential.APP_PASSWORD));
        parameters.setAppBased(appBased);
        return credential.withAzureParameters(parameters)
                .withCloudPlatform(CloudPlatform.AZURE.name())
                .withDescription(CREDENTIAL_DEFAULT_DESCRIPTION);
    }

    @Override
    public StackDtoBase stack(StackDtoBase stack) {
        return stack.withAzure(stackParameters());
    }

    @Override
    public AzureStackV4Parameters stackParameters() {
        return new AzureStackV4Parameters();
    }

    @Override
    public String region() {
        return getTestParameter().getWithDefault(AzureParameters.REGION, "West Europe");
    }

    @Override
    public String location() {
        return getTestParameter().getWithDefault(AzureParameters.LOCATION, "West Europe");
    }

    @Override
    public InstanceTemplateDto template(InstanceTemplateDto template) {
        return template.withInstanceType(getTestParameter().getWithDefault(AzureParameters.Instance.TYPE, "Standard_D12_v2"));
    }

    @Override
    public VolumeDto attachedVolume(VolumeDto volume) {
        int attachedVolumeSize = Integer.parseInt(getTestParameter().getWithDefault(AwsParameters.Instance.VOLUME_SIZE, "100"));
        int attachedVolumeCount = Integer.parseInt(getTestParameter().getWithDefault(AwsParameters.Instance.VOLUME_COUNT, "1"));
        String attachedVolumeType = getTestParameter().getWithDefault(AwsParameters.Instance.VOLUME_TYPE, "Standard_LRS");
        return volume.withSize(attachedVolumeSize)
                .withCount(attachedVolumeCount)
                .withType(attachedVolumeType);
    }

    @Override
    public NetworkV2Dto network(NetworkV2Dto network) {
        AzureNetworkV4Parameters parameters = new AzureNetworkV4Parameters();
        parameters.setNoPublicIp(false);
        parameters.setNoFirewallRules(false);
        return network.withAzure(parameters)
                .withSubnetCIDR(getSubnetCIDR());
    }

    @Override
    public CloudPlatform getCloudPlatform() {
        return CloudPlatform.AZURE;
    }

    @Override
    public String availabilityZone() {
        return getTestParameter().getWithDefault(AzureParameters.AVAILABILITY_ZONE, null);
    }

    @Override
    public StackAuthenticationDto stackAuthentication(StackAuthenticationDto stackAuthenticationDto) {
        String sshPublicKey = getTestParameter().getWithDefault(CommonCloudParameters.SSH_PUBLIC_KEY, CommonCloudParameters.DEFAULT_SSH_PUBLIC_KEY);
        return stackAuthenticationDto.withPublicKey(sshPublicKey);
    }

    @Override
    public String getDefaultClusterDefinitionName() {
        return AzureParameters.DEFAULT_CLUSTER_DEFINTION_NAME;
    }
}
