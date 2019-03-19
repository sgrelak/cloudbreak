package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.repository.RepositoryV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.ambari.stackrepository.StackRepositoryV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.cluster.ambari.stackrepository.StackRepositoryV4Response;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;
import com.sequenceiq.it.cloudbreak.newway.entity.CloudbreakEntity;
import com.sequenceiq.it.cloudbreak.newway.entity.mpack.ManagementPackDetailsDto;

@Prototype
public class StackRepositoryDto extends AbstractCloudbreakDto<StackRepositoryV4Request, StackRepositoryV4Response, StackRepositoryDto> {

    protected StackRepositoryDto(TestContext testContext) {
        super(new StackRepositoryV4Request(), testContext);
    }

    @Override
    public CloudbreakEntity valid() {
        return withVersion("2.7")
                .withStack("HDP")
                .withRepoId("HDP")
                .withRepositoryVersion("2.7.5.0-292")
                .withVersionDefinitionFileUrl("http://public-repo-1.hortonworks.com/HDP/centos7/2.x/updates/2.7.5.0/HDP-2.7.5.0-292.xml")
                .withVerify(true);
    }

    public StackRepositoryDto withVersion(String version) {
        getRequest().setVersion(version);
        return this;
    }

    public StackRepositoryDto withOs(String os) {
        getRequest().setOs(os);
        return this;
    }

    public StackRepositoryDto withOsType(String osType) {
        getRequest().setOsType(osType);
        return this;
    }

    public StackRepositoryDto withRepoId(String stackRepoId) {
        getRequest().setRepoId(stackRepoId);
        return this;
    }

    public StackRepositoryDto withStackBaseURL(String stackBaseURL) {
        if (getRequest().getRepository() == null) {
            getRequest().setRepository(new RepositoryV4Request());
        }
        getRequest().getRepository().setBaseUrl(stackBaseURL);
        return this;
    }

    public StackRepositoryDto withUtilsRepoId(String utilsRepoId) {
        getRequest().setUtilsRepoId(utilsRepoId);
        return this;
    }

    public StackRepositoryDto withUtilsBaseURL(String utilsBaseURL) {
        getRequest().setUtilsBaseURL(utilsBaseURL);
        return this;
    }

    public StackRepositoryDto withEnableGplRepo(boolean enableGplRepo) {
        getRequest().setEnableGplRepo(enableGplRepo);
        return this;
    }

    public StackRepositoryDto withVerify(boolean verify) {
        getRequest().setVerify(verify);
        return this;
    }

    public StackRepositoryDto withRepositoryVersion(String repositoryVersion) {
        if (getRequest().getRepository() == null) {
            getRequest().setRepository(new RepositoryV4Request());
        }
        getRequest().getRepository().setVersion(repositoryVersion);
        return this;
    }

    public StackRepositoryDto withVersionDefinitionFileUrl(String versionDefinitionFileUrl) {
        getRequest().setVersionDefinitionFileUrl(versionDefinitionFileUrl);
        return this;
    }

    public StackRepositoryDto withMpackUrl(String mpackUrl) {
        getRequest().setMpackUrl(mpackUrl);
        return this;
    }

    public StackRepositoryDto withMpacks(String... mpacks) {
        getRequest().setMpacks(Stream.of(mpacks).map(key -> {
            ManagementPackDetailsDto entity = getTestContext().get(key);
            return entity.getRequest();
        }).collect(Collectors.toList()));
        return this;
    }

    public StackRepositoryDto withGpgKeyUrl(String gpgKeyUrl) {
        if (getRequest().getRepository() == null) {
            getRequest().setRepository(new RepositoryV4Request());
        }
        getRequest().getRepository().setGpgKeyUrl(gpgKeyUrl);
        return this;
    }

    public StackRepositoryDto withStack(String stack) {
        getRequest().setStack(stack);
        return this;
    }
}
