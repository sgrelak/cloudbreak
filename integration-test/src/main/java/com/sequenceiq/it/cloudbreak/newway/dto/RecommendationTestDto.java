package com.sequenceiq.it.cloudbreak.newway.dto;

import com.sequenceiq.cloudbreak.api.endpoint.v4.clusterdefinition.responses.RecommendationV4Response;

public class RecommendationTestDto extends AbstractCloudbreakTestDto<Object, RecommendationV4Response, RecommendationTestDto> {

    static final String RECOMMENDATION = "RECOMMENDATION";

    private String credentialName;

    private String region;

    private String availabilityZone;

    private String clusterDefinitionName;

    private RecommendationTestDto(String newId) {
        super(newId);
    }

    RecommendationTestDto() {
        this(RECOMMENDATION);
    }

    public RecommendationTestDto withClusterDefinitionName(String name) {
        this.clusterDefinitionName = name;
        return this;
    }

    public RecommendationTestDto withCredentialName(String name) {
        this.credentialName = name;
        return this;
    }

    public RecommendationTestDto withRegion(String regionName) {
        this.region = regionName;
        return this;
    }

    public RecommendationTestDto withAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
        return this;
    }

    public String getCredentialName() {
        return credentialName;
    }

    public String getRegion() {
        return region;
    }

    public String getAvailabilityZone() {
        return availabilityZone;
    }

    public String getClusterDefinitionName() {
        return clusterDefinitionName;
    }
}
