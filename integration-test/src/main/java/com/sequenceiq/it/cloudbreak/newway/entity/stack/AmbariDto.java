package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import javax.ws.rs.core.Response;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.ConfigStrategy;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.ambari.AmbariV4Request;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
public class AmbariDto extends AbstractCloudbreakDto<AmbariV4Request, Response, AmbariDto> {

    public AmbariDto(TestContext testContex) {
        super(new AmbariV4Request(), testContex);
    }

    public AmbariDto() {
        super(AmbariDto.class.getSimpleName().toUpperCase());
    }

    public AmbariDto valid() {
        return withValidateRepositories(true);
    }

    public AmbariDto withValidateRepositories(Boolean validateRepositories) {
        getRequest().setValidateRepositories(validateRepositories);
        return this;
    }

    public AmbariDto withStackRepository(String key) {
        StackRepositoryDto ambariStack = getTestContext().get(key);
        return withStackRepository(ambariStack);
    }

    public AmbariDto withStackRepository(StackRepositoryDto ambariStackDetails) {
        getRequest().setStackRepository(ambariStackDetails.getRequest());
        return this;
    }

    public AmbariDto withAmbariRepoDetails() {
        AmbariRepositoryDto ambariRepo = getTestContext().get(AmbariRepositoryDto.class);
        return withAmbariRepoDetails(ambariRepo);
    }

    public AmbariDto withAmbariRepoDetails(String key) {
        AmbariRepositoryDto ambariRepo = getTestContext().get(key);
        return withAmbariRepoDetails(ambariRepo);
    }

    public AmbariDto withAmbariRepoDetails(AmbariRepositoryDto ambariRepoDetailsJson) {
        getRequest().setRepository(ambariRepoDetailsJson.getRequest());
        return this;
    }

    public AmbariDto withConfigStrategy(ConfigStrategy configStrategy) {
        getRequest().setConfigStrategy(configStrategy);
        return this;
    }

    public AmbariDto withAmbariSecurityMasterKey(String ambariSecurityMasterKey) {
        getRequest().setSecurityMasterKey(ambariSecurityMasterKey);
        return this;
    }
}
