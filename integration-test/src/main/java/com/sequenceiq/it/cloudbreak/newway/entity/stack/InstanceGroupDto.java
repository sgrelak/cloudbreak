package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import static com.sequenceiq.cloudbreak.doc.ModelDescriptions.HostGroupModelDescription.RECOVERY_MODE;
import static com.sequenceiq.it.cloudbreak.newway.cloud.HostGroupType.COMPUTE;
import static com.sequenceiq.it.cloudbreak.newway.cloud.HostGroupType.MASTER;
import static com.sequenceiq.it.cloudbreak.newway.cloud.HostGroupType.WORKER;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.InstanceGroupType;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.RecoveryMode;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.InstanceGroupV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.instancegroup.InstanceGroupV4Response;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.cloud.HostGroupType;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
public class InstanceGroupDto extends AbstractCloudbreakDto<InstanceGroupV4Request, InstanceGroupV4Response, InstanceGroupDto> {

    private static final String AUTO = "auto";

    private static final String MANUAL = "manual";

    protected InstanceGroupDto(InstanceGroupV4Request request, TestContext testContext) {
        super(request, testContext);
    }

    protected InstanceGroupDto(TestContext testContext) {
        super(new InstanceGroupV4Request(), testContext);
    }

    public InstanceGroupDto() {
        super(InstanceGroupDto.class.getSimpleName().toUpperCase());
    }

    public InstanceGroupDto valid() {
        return withHostGroup(MASTER);
    }

    public InstanceGroupDto withHostGroup(HostGroupType hostGroupType) {
        return withRecoveryMode(getRecoveryModeParam(hostGroupType))
                .withNodeCount(hostGroupType.determineInstanceCount(getTestParameter()))
                .withGroup(hostGroupType.getName())
                .withSecurityGroup(getTestContext().init(SecurityGroupDto.class))
                .withType(hostGroupType.getInstanceGroupType())
                .withName(hostGroupType.getName().toLowerCase())
                .withTemplate(getTestContext().given(InstanceTemplateDto.class));
    }

    public static InstanceGroupDto hostGroup(TestContext testContext, HostGroupType hostGroupType) {
        return create(testContext, hostGroupType);
    }

    public static List<InstanceGroupDto> defaultHostGroup(TestContext testContext) {
        return withHostGroup(testContext, MASTER, COMPUTE, WORKER);
    }

    public static List<InstanceGroupDto> withHostGroup(TestContext testContext, HostGroupType... groupTypes) {
        return Stream.of(groupTypes)
                .map(groupType -> create(testContext, groupType))
                .collect(Collectors.toList());
    }

    private static InstanceGroupDto create(TestContext testContext, HostGroupType hostGroupType) {
        InstanceGroupDto entity = testContext.init(InstanceGroupDto.class);
        return entity
                .withRecoveryMode(entity.getRecoveryModeParam(hostGroupType))
                .withNodeCount(hostGroupType.determineInstanceCount(entity.getTestParameter()))
                .withGroup(hostGroupType.getName())
                .withSecurityGroup(testContext.init(SecurityGroupDto.class))
                .withType(hostGroupType.getInstanceGroupType())
                .withName(hostGroupType.getName().toLowerCase())
                .withTemplate(testContext.given(InstanceTemplateDto.class));
    }

    public InstanceGroupDto withNodeCount(int nodeCount) {
        getRequest().setNodeCount(nodeCount);
        return this;
    }

    public InstanceGroupDto withRecipes(String... recipeNames) {
        for (String recipeName : recipeNames) {
            getRequest().getRecipeNames().add(recipeName);
        }
        return this;
    }

    public InstanceGroupDto withGroup(String group) {
        getRequest().setName(group);
        return this;
    }

    public InstanceGroupDto withType(InstanceGroupType instanceGroupType) {
        getRequest().setType(instanceGroupType);
        return this;
    }

    public InstanceGroupDto withSecurityGroup(String key) {
        SecurityGroupDto securityGroupDto = getTestContext().get(key);
        return withSecurityGroup(securityGroupDto);
    }

    public InstanceGroupDto withSecurityGroup(SecurityGroupDto securityGroup) {
        getRequest().setSecurityGroup(securityGroup.getRequest());
        return this;
    }

    public InstanceGroupDto withTemplate(InstanceTemplateDto template) {
        getRequest().setTemplate(template.getRequest());
        return this;
    }

    public InstanceGroupDto withRecoveryMode(RecoveryMode recoveryMode) {
        getRequest().setRecoveryMode(recoveryMode);
        return this;
    }

    public InstanceGroupDto withName(String name) {
        getRequest().setName(name);
        return this;
    }

    private RecoveryMode getRecoveryModeParam(HostGroupType hostGroupType) {
        String argumentName = String.join("", hostGroupType.getName(), RECOVERY_MODE);
        String argumentValue = getTestParameter().getWithDefault(argumentName, MANUAL);
        return argumentValue.equals(AUTO) ? RecoveryMode.AUTO : RecoveryMode.MANUAL;
    }
}