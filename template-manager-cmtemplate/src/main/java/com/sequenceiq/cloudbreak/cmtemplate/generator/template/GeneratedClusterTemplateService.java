package com.sequenceiq.cloudbreak.cmtemplate.generator.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cloudera.api.swagger.model.ApiClusterTemplateHostTemplate;
import com.cloudera.api.swagger.model.ApiClusterTemplateRoleConfigGroup;
import com.cloudera.api.swagger.model.ApiClusterTemplateService;
import com.sequenceiq.cloudbreak.cmtemplate.CmTemplateProcessor;
import com.sequenceiq.cloudbreak.cmtemplate.CmTemplateProcessorFactory;
import com.sequenceiq.cloudbreak.cmtemplate.generator.configuration.ClusterTemplateGeneratorConfigurationResolver;
import com.sequenceiq.cloudbreak.cmtemplate.generator.configuration.domain.dependencies.ServiceConfig;
import com.sequenceiq.cloudbreak.cmtemplate.generator.template.domain.GeneratedClusterTemplate;
import com.sequenceiq.cloudbreak.util.JsonUtil;

@Service
public class GeneratedClusterTemplateService {

    @Inject
    private ClusterTemplateGeneratorConfigurationResolver resolver;

    @Inject
    private CmTemplateProcessorFactory cmTemplateProcessorFactory;

    public GeneratedClusterTemplate prepareClusterTemplate(Set<String> services, String stackType, String version) {

        CmTemplateProcessor processor = initiateTemplate();

        Set<ServiceConfig> serviceConfigs = collectServiceConfigs(services);
        Map<String, Set<String>> hostServiceMap = new HashMap<>();

        prepareCdhVersion(stackType, version, processor);
        processor.setDisplayName(prepareDisplayName());
        processor.setServices(prepareApiClusterTemplateServices(serviceConfigs, hostServiceMap));
        processor.setHostTemplates(prepareApiClusterTemplateHostTemplates(hostServiceMap));

        return new GeneratedClusterTemplate(prepareTemplate(processor));
    }

    private String prepareTemplate(CmTemplateProcessor processor) {
        return JsonUtil.writeValueAsStringSilent(processor.getTemplate(), true);
    }

    private CmTemplateProcessor initiateTemplate() {
        return cmTemplateProcessorFactory.get("{}");
    }

    private String prepareDisplayName() {
        return "cloudbreak-generated-" + UUID.randomUUID().toString();
    }

    private void prepareCdhVersion(String stackType, String version, CmTemplateProcessor processor) {
        if ("CDH".equals(stackType)) {
            processor.setCdhVersion(version);
        }
    }

    private Set<ServiceConfig> collectServiceConfigs(Set<String> services) {
        Set<ServiceConfig> serviceConfigs = new HashSet<>();
        for (String service : services) {
            for (ServiceConfig serviceInformation : resolver.serviceInformations()) {
                if (serviceInformation.getName().equals(service.toUpperCase())) {
                    serviceConfigs.add(serviceInformation);
                }
            }
        }
        return serviceConfigs;
    }

    private List<ApiClusterTemplateService> prepareApiClusterTemplateServices(Set<ServiceConfig> serviceConfigs, Map<String, Set<String>> hostServiceMap) {
        List<ApiClusterTemplateService> clusterTemplateServices = new ArrayList<>();
        for (ServiceConfig serviceConfig : serviceConfigs) {
            String serviceName = serviceConfig.getName();
            String lowerCaseServiceName = serviceName.toLowerCase();

            ApiClusterTemplateService apiClusterTemplateService = new ApiClusterTemplateService();
            apiClusterTemplateService.setRefName(lowerCaseServiceName);
            apiClusterTemplateService.setServiceType(serviceName);
            apiClusterTemplateService.setRoleConfigGroups(new ArrayList<>());

            List<ApiClusterTemplateRoleConfigGroup> roleConfigGroups = new ArrayList<>();
            serviceConfig.getComponents().forEach(component -> {
                component.getGroups().forEach(group -> {
                    String componentName = component.getName();
                    String hostServiceNameEnd = component.getGroups().size() == 1 ? "BASE" : group.toUpperCase();
                    String hostServiceName = String.format("%s-%s-%s", lowerCaseServiceName, component.getName().toUpperCase(), hostServiceNameEnd);
                    ApiClusterTemplateRoleConfigGroup apiClusterTemplateRoleConfigGroup = new ApiClusterTemplateRoleConfigGroup();
                    apiClusterTemplateRoleConfigGroup.setRoleType(componentName.toUpperCase());
                    apiClusterTemplateRoleConfigGroup.setRefName(hostServiceName);
                    apiClusterTemplateRoleConfigGroup.setBase(component.getGroups().size() == 1 ? true : false);
                    if (hostServiceMap.keySet().contains(group)) {
                        hostServiceMap.get(group).add(hostServiceName);
                    } else {
                        hostServiceMap.put(group, Set.of(hostServiceName));
                    }
                    roleConfigGroups.add(apiClusterTemplateRoleConfigGroup);
                });
                apiClusterTemplateService.getRoleConfigGroups().addAll(roleConfigGroups);
            });
            clusterTemplateServices.add(apiClusterTemplateService);
        }
        return clusterTemplateServices;
    }

    private List<ApiClusterTemplateHostTemplate> prepareApiClusterTemplateHostTemplates(Map<String, Set<String>> hostServiceMap) {
        List<ApiClusterTemplateHostTemplate> hostTemplates = new ArrayList<>();
        for (String key : hostServiceMap.keySet()) {
            ApiClusterTemplateHostTemplate apiClusterTemplateHostTemplate = new ApiClusterTemplateHostTemplate();
            apiClusterTemplateHostTemplate.setRefName(key);
            apiClusterTemplateHostTemplate.setRoleConfigGroupsRefNames(new ArrayList<>());
            for (String name : hostServiceMap.get(key)) {
                apiClusterTemplateHostTemplate.getRoleConfigGroupsRefNames().add(name);
            }
            hostTemplates.add(apiClusterTemplateHostTemplate);
        }
        return hostTemplates;
    }
}
