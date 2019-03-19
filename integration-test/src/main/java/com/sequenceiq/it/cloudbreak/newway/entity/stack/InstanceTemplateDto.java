package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.template.AwsInstanceTemplateV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.template.AzureInstanceTemplateV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.template.GcpInstanceTemplateV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.template.OpenStackInstanceTemplateV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.template.YarnInstanceTemplateV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.template.InstanceTemplateV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.template.volume.VolumeV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.instancegroup.template.InstanceTemplateV4Response;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
public class InstanceTemplateDto extends AbstractCloudbreakDto<InstanceTemplateV4Request, InstanceTemplateV4Response, InstanceTemplateDto> {

    public InstanceTemplateDto(InstanceTemplateV4Request request, TestContext testContext) {
        super(request, testContext);
    }

    public InstanceTemplateDto(TestContext testContext) {
        super(new InstanceTemplateV4Request(), testContext);
    }

    public InstanceTemplateDto() {
        super(InstanceTemplateDto.class.getSimpleName().toUpperCase());
    }

    public InstanceTemplateDto valid() {
        return getCloudProvider().template(withRootVolume(getTestContext().given(RootVolumeDto.class))
                .withAttachedVolume(getTestContext().init(VolumeDto.class)));
    }

    public InstanceTemplateDto withAttachedVolumes(Set<VolumeV4Request> volumes) {
        getRequest().setAttachedVolumes(volumes);
        return this;
    }

    public InstanceTemplateDto withAwsParameters(AwsInstanceTemplateV4Parameters awsParameters) {
        getRequest().setAws(awsParameters);
        return this;
    }

    public InstanceTemplateDto withGcpParameters(GcpInstanceTemplateV4Parameters gcpParameters) {
        getRequest().setGcp(gcpParameters);
        return this;
    }

    public InstanceTemplateDto withAzureParameters(AzureInstanceTemplateV4Parameters azureParameters) {
        getRequest().setAzure(azureParameters);
        return this;
    }

    public InstanceTemplateDto withOpenStackParameters(OpenStackInstanceTemplateV4Parameters openStackParameters) {
        getRequest().setOpenstack(openStackParameters);
        return this;
    }

    public InstanceTemplateDto withYarnParameters(YarnInstanceTemplateV4Parameters yarnParameters) {
        getRequest().setYarn(yarnParameters);
        return this;
    }

    public InstanceTemplateDto withRootVolume(RootVolumeDto rootVolume) {
        getRequest().setRootVolume(rootVolume.getRequest());
        return this;
    }

    public InstanceTemplateDto withRootVolumeKey(String key) {
        getRequest().setRootVolume(getTestContext().get(key));
        return this;
    }

    public InstanceTemplateDto withAttachedVolume(VolumeDto... volumes) {
        getRequest().setAttachedVolumes(Stream.of(volumes).map(AbstractCloudbreakDto::getRequest).collect(Collectors.toSet()));
        return this;
    }

    public InstanceTemplateDto withAttachedVolumeKeys(String... keys) {
        getRequest().setAttachedVolumes(Stream.of(keys).map(key -> {
            VolumeDto value = getTestContext().get(key);
            return value.getRequest();
        }).collect(Collectors.toSet()));
        return this;
    }

    public InstanceTemplateDto withAzure(AzureInstanceTemplateV4Parameters azure) {
        getRequest().setAzure(azure);
        return this;
    }

    public InstanceTemplateDto withGcp(GcpInstanceTemplateV4Parameters gcp) {
        getRequest().setGcp(gcp);
        return this;
    }

    public InstanceTemplateDto withAws(AwsInstanceTemplateV4Parameters aws) {
        getRequest().setAws(aws);
        return this;
    }

    public InstanceTemplateDto withOpenstack(OpenStackInstanceTemplateV4Parameters openstack) {
        getRequest().setOpenstack(openstack);
        return this;
    }

    public InstanceTemplateDto withYarn(YarnInstanceTemplateV4Parameters yarn) {
        getRequest().setYarn(yarn);
        return this;
    }

    public InstanceTemplateDto withInstanceType(String instanceType) {
        getRequest().setInstanceType(instanceType);
        return this;
    }
}
