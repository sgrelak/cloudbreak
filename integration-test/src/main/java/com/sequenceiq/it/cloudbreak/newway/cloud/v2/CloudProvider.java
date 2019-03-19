package com.sequenceiq.it.cloudbreak.newway.cloud.v2;

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

public interface CloudProvider {

    String availabilityZone();

    String region();

    String location();

    ImageCatalogTestDto imageCatalog(ImageCatalogTestDto imageCatalog);

    ImageSettingsEntity imageSettings(ImageSettingsEntity imageSettings);

    InstanceTemplateDto template(InstanceTemplateDto template);

    VolumeDto attachedVolume(VolumeDto volume);

    NetworkV2Dto network(NetworkV2Dto network);

    StackDtoBase stack(StackDtoBase stack);

    String getSubnetCIDR();

    CloudPlatform getCloudPlatform();

    CredentialTestDto credential(CredentialTestDto credential);

    EnvironmentTestDto environment(EnvironmentTestDto environment);

    PlacementSettingsDto placement(PlacementSettingsDto placement);

    StackAuthenticationDto stackAuthentication(StackAuthenticationDto stackAuthenticationDto);

    Integer gatewayPort(StackDtoBase stackEntity);

    String getDefaultClusterDefinitionName();

    StackV4ParameterBase stackParameters();
}
