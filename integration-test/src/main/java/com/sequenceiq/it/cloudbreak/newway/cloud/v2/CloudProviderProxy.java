package com.sequenceiq.it.cloudbreak.newway.cloud.v2;

import static com.sequenceiq.it.cloudbreak.newway.cloud.v2.CommonCloudParameters.CLOUD_PROVIDER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.mappable.CloudPlatform;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.stack.StackV4ParameterBase;
import com.sequenceiq.it.cloudbreak.newway.ImageSettingsEntity;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.InstanceTemplateDto;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.NetworkV2Dto;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.PlacementSettingsDto;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.StackAuthenticationDto;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.StackDtoBase;
import com.sequenceiq.it.cloudbreak.newway.entity.stack.VolumeDto;
import com.sequenceiq.it.cloudbreak.newway.entity.credential.CredentialTestDto;
import com.sequenceiq.it.cloudbreak.newway.entity.environment.EnvironmentTestDto;
import com.sequenceiq.it.cloudbreak.newway.entity.imagecatalog.ImageCatalogTestDto;

@Component
public class CloudProviderProxy implements CloudProvider {

    private CloudProvider delegate;

    @Value("${" + CLOUD_PROVIDER + ":MOCK}")
    private CloudPlatform cloudPlatform;

    @Inject
    private List<CloudProvider> cloudProviders;

    private final Map<CloudPlatform, CloudProvider> cloudProviderMap = new HashMap<>();

    @PostConstruct
    private void init() {
        Map<CloudPlatform, CloudProvider> cloudProviderMap = new HashMap<>();
        cloudProviders.forEach(cloudProvider -> {
            cloudProviderMap.put(cloudProvider.getCloudPlatform(), cloudProvider);
        });
        delegate = cloudProviderMap.get(cloudPlatform);
    }

    @Override
    public String availabilityZone() {
        return delegate.availabilityZone();
    }

    @Override
    public String region() {
        return delegate.region();
    }

    @Override
    public String location() {
        return delegate.location();
    }

    @Override
    public ImageCatalogTestDto imageCatalog(ImageCatalogTestDto imageCatalog) {
        return delegate.imageCatalog(imageCatalog);
    }

    @Override
    public ImageSettingsEntity imageSettings(ImageSettingsEntity imageSettings) {
        return delegate.imageSettings(imageSettings);
    }

    @Override
    public InstanceTemplateDto template(InstanceTemplateDto template) {
        return delegate.template(template);
    }

    @Override
    public StackDtoBase stack(StackDtoBase stack) {
        return delegate.stack(stack);
    }

    @Override
    public VolumeDto attachedVolume(VolumeDto volume) {
        return delegate.attachedVolume(volume);
    }

    @Override
    public NetworkV2Dto network(NetworkV2Dto network) {
        return delegate.network(network);
    }

    @Override
    public String getSubnetCIDR() {
        return delegate.getSubnetCIDR();
    }

    @Override
    public CloudPlatform getCloudPlatform() {
        return delegate.getCloudPlatform();
    }

    @Override
    public CredentialTestDto credential(CredentialTestDto credential) {
        return delegate.credential(credential);
    }

    @Override
    public EnvironmentTestDto environment(EnvironmentTestDto environment) {
        return delegate.environment(environment);
    }

    @Override
    public PlacementSettingsDto placement(PlacementSettingsDto placement) {
        return delegate.placement(placement);
    }

    @Override
    public StackAuthenticationDto stackAuthentication(StackAuthenticationDto stackAuthenticationDto) {
        return delegate.stackAuthentication(stackAuthenticationDto);
    }

    @Override
    public Integer gatewayPort(StackDtoBase stackEntity) {
        return delegate.gatewayPort(stackEntity);
    }

    @Override
    public String getDefaultClusterDefinitionName() {
        return delegate.getDefaultClusterDefinitionName();
    }

    @Override
    public StackV4ParameterBase stackParameters()  {
        return delegate.stackParameters();
    }
}
