package com.sequenceiq.it.cloudbreak.newway.entity.environment;

import java.util.Set;

import com.sequenceiq.cloudbreak.api.endpoint.v4.environment.responses.DetailedEnvironmentV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.environment.responses.SimpleEnvironmentV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.environment.EnvironmentSettingsV4Request;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;
import com.sequenceiq.it.cloudbreak.newway.entity.credential.CredentialTestDto;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;

@Prototype
public class EnvironmentSettingsDto extends AbstractCloudbreakDto<EnvironmentSettingsV4Request, DetailedEnvironmentV4Response,
        EnvironmentSettingsDto> {

    public static final String ENVIRONMENT = "ENVIRONMENT";

    private Set<SimpleEnvironmentV4Response> response;

    private SimpleEnvironmentV4Response simpleResponse;

    public EnvironmentSettingsDto(TestContext testContext) {
        super(new EnvironmentSettingsV4Request(), testContext);
    }

    public EnvironmentSettingsDto() {
        super(ENVIRONMENT);
    }

    public EnvironmentSettingsDto(EnvironmentSettingsV4Request environmentV4Request, TestContext testContext) {
        super(environmentV4Request, testContext);
    }

    @Override
    public String getName() {
        return getRequest().getName();
    }

    @Override
    public EnvironmentSettingsDto valid() {
        CredentialTestDto credentialTestDto = getTestContext().get(CredentialTestDto.class);
        if (credentialTestDto == null) {
            throw new IllegalArgumentException("Credential is mandatory for EnvironmentSettings");
        }
        return withName(getNameCreator().getRandomNameForResource())
                .withCredentialName(credentialTestDto.getName());
    }

    public EnvironmentSettingsDto withName(String name) {
        getRequest().setName(name);
        setName(name);
        return this;
    }

    public EnvironmentSettingsDto withCredentialName(String name) {
        getRequest().setCredentialName(name);
        return this;
    }
}