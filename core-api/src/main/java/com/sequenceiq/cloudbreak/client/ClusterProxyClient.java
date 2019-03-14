package com.sequenceiq.cloudbreak.client;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;

public class ClusterProxyClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterProxyClient.class);

    private final String clusterProxyUrl;

    private final ConfigKey configKey;

    public ClusterProxyClient(String clusterProxyUrl, ConfigKey configKey) {
        this.clusterProxyUrl = clusterProxyUrl;
        this.configKey = configKey;
    }

    public IntrospectResponse getKeyForTrustedProxy(String dpsJwtToken) {
        WebTarget clusterProxyWebTarget = getClusterProxyWebTarget();
        WebTarget clustersWebTarget = clusterProxyWebTarget.path("/cluster-proxy/clusters");
        try {
            return clustersWebTarget.request()
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.json(new IntrospectRequest(dpsJwtToken)), IntrospectResponse.class);
        } catch (ForbiddenException e) {
            throw new InvalidTokenException("Token is not valid", e);
        }
    }

    private WebTarget getClusterProxyWebTarget() {
        if (StringUtils.isNotEmpty(clusterProxyUrl)) {
            return RestClientUtil.get(configKey).target(clusterProxyUrl);
        } else {
            LOGGER.warn("ClusterProxy isn't configured");
            throw new InvalidTokenException("ClusterProxy isn't configured");
        }
    }
}
