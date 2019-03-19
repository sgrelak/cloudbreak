package com.sequenceiq.it.cloudbreak.newway;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sequenceiq.cloudbreak.api.endpoint.v4.clustertemplate.requests.ClusterTemplateV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.clustertemplate.responses.ClusterTemplateV4Response;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClusterTemplateDto extends AbstractCloudbreakDto<ClusterTemplateV4Request, ClusterTemplateV4Response, ClusterTemplateDto> {
    public static final String CLUSTER_TEMPLATE = "CLUSTER_TEMPLATE";

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterTemplateDto.class);

    ClusterTemplateDto(String newId) {
        super(newId);
        setRequest(new ClusterTemplateV4Request());
    }

    ClusterTemplateDto() {
        this(CLUSTER_TEMPLATE);
    }

    public ClusterTemplateDto(TestContext testContext) {
        super(new ClusterTemplateV4Request(), testContext);
    }

    public ClusterTemplateDto withName(String name) {
        getRequest().setName(name);
        setName(name);
        return this;
    }

    public ClusterTemplateDto withDescription(String description) {
        getRequest().setDescription(description);
        return this;
    }

    private static class MapTypeReference extends TypeReference<Map<String, Object>> {
    }
}
