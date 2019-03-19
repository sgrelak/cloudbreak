package com.sequenceiq.it.cloudbreak.newway.entity.kubernetes;

import static com.sequenceiq.it.cloudbreak.newway.util.ResponseUtil.getErrorMessage;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;

import com.sequenceiq.cloudbreak.api.endpoint.v4.kubernetes.KubernetesV4Endpoint;
import com.sequenceiq.cloudbreak.api.endpoint.v4.kubernetes.requests.KubernetesV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.kubernetes.responses.KubernetesV4Response;
import com.sequenceiq.it.IntegrationTestContext;
import com.sequenceiq.it.cloudbreak.exception.ProxyMethodInvocationException;
import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.RandomNameCreator;
import com.sequenceiq.it.cloudbreak.newway.context.Purgable;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
public class KubernetesTestDto extends AbstractCloudbreakDto<KubernetesV4Request, KubernetesV4Response, KubernetesTestDto>
        implements Purgable<KubernetesV4Response> {

    public static final String KERBEROS = "KERBEROS";

    KubernetesTestDto(String newId) {
        super(newId);
        setRequest(new KubernetesV4Request());
    }

    KubernetesTestDto() {
        this(KERBEROS);
    }

    public KubernetesTestDto(TestContext testContext) {
        super(new KubernetesV4Request(), testContext);
    }

    @Override
    public void cleanUp(TestContext context, CloudbreakClient cloudbreakClient) {
        LOGGER.info("Cleaning up resource with name: {}", getName());
        try {
            cloudbreakClient.getCloudbreakClient().kubernetesV4Endpoint().delete(cloudbreakClient.getWorkspaceId(), getName());
        } catch (WebApplicationException ignore) {
            LOGGER.info("Something happend.");
        }
    }

    public KubernetesTestDto valid() {
        return withName(getNameCreator().getRandomNameForResource())
                .withContent("content")
                .withDesription("great kubernetes config");
    }

    public KubernetesTestDto withRequest(KubernetesV4Request request) {
        setRequest(request);
        return this;
    }

    public KubernetesTestDto withName(String name) {
        getRequest().setName(name);
        setName(name);
        return this;
    }

    public KubernetesTestDto withContent(String content) {
        getRequest().setContent(content);
        return this;
    }

    public KubernetesTestDto withDesription(String desription) {
        getRequest().setDescription(desription);
        return this;
    }

    @Override
    public List<KubernetesV4Response> getAll(CloudbreakClient client) {
        KubernetesV4Endpoint kubernetesV4Endpoint = client.getCloudbreakClient().kubernetesV4Endpoint();
        return kubernetesV4Endpoint.list(client.getWorkspaceId(), null, Boolean.FALSE).getResponses().stream()
                .filter(s -> s.getName() != null)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deletable(KubernetesV4Response entity) {
        return entity.getName().startsWith(RandomNameCreator.PREFIX);
    }

    @Override
    public void delete(TestContext testContext, KubernetesV4Response entity, CloudbreakClient client) {
        try {
            client.getCloudbreakClient().kubernetesV4Endpoint().delete(client.getWorkspaceId(), entity.getName());
        } catch (ProxyMethodInvocationException e) {
            LOGGER.warn("Something went wrong on {} purge. {}", entity.getName(), getErrorMessage(e), e);
        }
    }

    @Override
    public int order() {
        return 500;
    }

    static Function<IntegrationTestContext, KubernetesTestDto> getNew() {
        return testContext -> new KubernetesTestDto();
    }

    public static KubernetesTestDto request() {
        return new KubernetesTestDto();
    }

}