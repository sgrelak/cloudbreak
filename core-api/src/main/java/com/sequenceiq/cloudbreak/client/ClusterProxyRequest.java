package com.sequenceiq.cloudbreak.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClusterProxyRequest {

    @JsonProperty("external_id")
    private String externalId;

    @JsonProperty("cluster_name")
    private String clusterName;

    @JsonProperty("cluster_manager_type")
    private String clusterManagerType;

    @JsonProperty("is_behind_gateway")
    private Boolean isBehindGateway;

    @JsonProperty("tenant_id")
    private String tenantId;

}
