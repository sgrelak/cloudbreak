package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.ambari.ambarirepository.AmbariRepositoryV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.cluster.ambari.ambarirepository.AmbariRepositoryV4Response;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
    public class AmbariRepositoryDto extends AbstractCloudbreakDto<AmbariRepositoryV4Request, AmbariRepositoryV4Response, AmbariRepositoryDto> {

    protected AmbariRepositoryDto(TestContext testContext) {
        super(new AmbariRepositoryV4Request(), testContext);
    }

    @Override
    public AmbariRepositoryDto valid() {
        return withVersion("2.7.2.2")
                .withGpgKeyUrl("http://public-repo-1.hortonworks.com/ambari/centos7/RPM-GPG-KEY/RPM-GPG-KEY-Jenkins")
                .withBaseUrl("http://public-repo-1.hortonworks.com/ambari/centos7/2.x/updates/2.7.2.2");
    }

    public AmbariRepositoryDto withVersion(String version) {
        getRequest().setVersion(version);
        return this;
    }

    public AmbariRepositoryDto withBaseUrl(String baseUrl) {
        getRequest().setBaseUrl(baseUrl);
        return this;
    }

    public AmbariRepositoryDto withGpgKeyUrl(String gpgKeyUrl) {
        getRequest().setGpgKeyUrl(gpgKeyUrl);
        return this;
    }
}
