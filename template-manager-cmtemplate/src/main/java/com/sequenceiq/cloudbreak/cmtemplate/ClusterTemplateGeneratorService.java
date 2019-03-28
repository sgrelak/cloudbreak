package com.sequenceiq.cloudbreak.cmtemplate;

import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cmtemplate.generator.dependencies.ServiceDependencyMatrixService;
import com.sequenceiq.cloudbreak.cmtemplate.generator.dependencies.domain.ServiceDependencyMatrix;
import com.sequenceiq.cloudbreak.cmtemplate.generator.support.SupportedVersionService;
import com.sequenceiq.cloudbreak.cmtemplate.generator.support.domain.SupportedVersions;
import com.sequenceiq.cloudbreak.cmtemplate.generator.template.GeneratedClusterTemplateService;
import com.sequenceiq.cloudbreak.cmtemplate.generator.template.domain.GeneratedClusterTemplate;

@Service
public class ClusterTemplateGeneratorService {

    @Inject
    private SupportedVersionService supportedVersionService;

    @Inject
    private ServiceDependencyMatrixService serviceDependencyMatrixService;

    @Inject
    private GeneratedClusterTemplateService generatedClusterTemplateService;

    public GeneratedClusterTemplate generateTemplateByServices(Set<String> services, String stackType, String version) {
        String generatedId = UUID.randomUUID().toString();
        return generatedClusterTemplateService.prepareClusterTemplate(services, stackType, version, generatedId);
    }

    public ServiceDependencyMatrix getServicesAndDependencies(Set<String> services, String stackType, String version) {
        return serviceDependencyMatrixService.collectServiceDependencyMatrix(services, stackType, version);
    }

    public SupportedVersions getVersionsAndSupportedServiceList() {
        return supportedVersionService.collectSupportedVersions();
    }

}
