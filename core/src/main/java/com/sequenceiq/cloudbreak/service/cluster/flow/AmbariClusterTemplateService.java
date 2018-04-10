package com.sequenceiq.cloudbreak.service.cluster.flow;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.io.CharStreams;
import com.sequenceiq.ambari.client.AmbariClient;
import com.sequenceiq.cloudbreak.core.CloudbreakException;
import com.sequenceiq.cloudbreak.domain.Cluster;
import com.sequenceiq.cloudbreak.domain.KerberosConfig;
import com.sequenceiq.cloudbreak.service.cluster.AmbariSecurityConfigProvider;
import com.sequenceiq.cloudbreak.service.cluster.AmbariServiceException;
import com.sequenceiq.cloudbreak.service.cluster.flow.kerberos.KerberosDetailService;
import com.sequenceiq.cloudbreak.util.JsonUtil;

import groovyx.net.http.HttpResponseException;

@Service
public class AmbariClusterTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmbariClusterTemplateService.class);

    private static final String KEY_TYPE = "PERSISTED";

    @Inject
    private KerberosDetailService kerberosDetailService;

    @Inject
    private AmbariSecurityConfigProvider ambariSecurityConfigProvider;

    @Inject
    private AmbariRepositoryVersionService ambariRepositoryVersionService;

    void addClusterTemplate(Cluster cluster, Map<String, List<Map<String, String>>> hostGroupMappings, AmbariClient ambariClient) throws CloudbreakException {
        String clusterName = cluster.getName();
        String blueprintName = cluster.getBlueprint().getAmbariName();
        String configStrategy = cluster.getConfigStrategy().name();
        String clusterTemplate;

        if (ambariClient.getClusterName() == null) {
            try {
                String repositoryVersion = ambariRepositoryVersionService.getRepositoryVersion(cluster.getId(), cluster.getStack().getOrchestrator());
                if (cluster.isSecure()) {
                    KerberosConfig kerberosConfig = cluster.getKerberosConfig();
                    String principal = kerberosDetailService.resolvePrincipalForKerberos(kerberosConfig);
                    clusterTemplate = ambariClient.createSecureCluster(clusterName, blueprintName, hostGroupMappings, configStrategy,
                            ambariSecurityConfigProvider.getAmbariPassword(cluster), principal, kerberosConfig.getPassword(), KEY_TYPE, false,
                            repositoryVersion);
                } else {
                    clusterTemplate = ambariClient.createCluster(clusterName, blueprintName, hostGroupMappings, configStrategy,
                            ambariSecurityConfigProvider.getAmbariPassword(cluster), false, repositoryVersion);
                }
                LOGGER.info("Submitted cluster creation template: {}", JsonUtil.minify(clusterTemplate, Collections.singleton("credentials")));
            } catch (HttpResponseException exception) {
                String reason = collectErrorReason(exception);
                String msg = String.format("Ambari client failed to apply cluster creation template! Reason: %s", reason);
                LOGGER.error(msg, exception);
                throw new AmbariServiceException(msg, exception);
            }
        } else {
            LOGGER.info("Ambari cluster already exists: {}", clusterName);
        }
    }

    private String collectErrorReason(HttpResponseException exception) {
        Object data = exception.getResponse().getData();
        String reason;
        try {
            if (data instanceof Readable) {
                reason = CharStreams.toString((Readable) data);
            } else if (data != null) {
                reason = data.toString();
            } else {
                reason = "No response from Ambari Server!";
            }
        } catch (IOException e) {
            reason = "Ambari server response cannot be parsed!";
        }
        return reason;
    }

}
