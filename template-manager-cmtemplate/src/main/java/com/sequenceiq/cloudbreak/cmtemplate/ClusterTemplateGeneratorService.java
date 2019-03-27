package com.sequenceiq.cloudbreak.cmtemplate;

import java.util.Set;

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

    public GeneratedClusterTemplate generateTemplateByServices(Set<String> services){
        return generatedClusterTemplateService.prepareClusterTemplate(services);
    }

    public ServiceDependencyMatrix getServicesAndDependencies(Set<String> services){
        return serviceDependencyMatrixService.collectServiceDependencyMatrix(services);
    }

    public SupportedVersions getVersionsAndSupportedServiceList(Set<String> services){
        return supportedVersionService.collectSupportedVersions(services);
    }

}
