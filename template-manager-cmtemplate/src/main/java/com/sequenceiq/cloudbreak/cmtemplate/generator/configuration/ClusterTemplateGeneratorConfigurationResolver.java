package com.sequenceiq.cloudbreak.cmtemplate.generator.configuration;

import static com.sequenceiq.cloudbreak.util.FileReaderUtils.readFileFromClasspathQuietly;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sequenceiq.cloudbreak.cloud.model.catalog.CloudbreakImageCatalogV2;
import com.sequenceiq.cloudbreak.cmtemplate.generator.configuration.domain.ServiceConfig;
import com.sequenceiq.cloudbreak.cmtemplate.generator.configuration.domain.versionmatrix.ServiceList;
import com.sequenceiq.cloudbreak.cmtemplate.generator.configuration.domain.StackVersion;
import com.sequenceiq.cloudbreak.domain.json.Json;

import net.sf.json.JSON;

@Service
public class ClusterTemplateGeneratorConfigurationResolver implements ResourceLoaderAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterTemplateGeneratorConfigurationResolver.class);

    @Value("${cb.clusterdefinition.cm.version.files:cloudera-manager-template/cdh}")
    private String cdhConfigurationsPath;

    @Value("${cb.clusterdefinition.cm.services.file:cloudera-manager-template/service-definitions.json}")
    private String serviceDefinitionConfigurationPath;

    private ResourceLoader resourceLoader;

    private Map<StackVersion, ServiceList> cdhConfigurationsMap = new HashMap<>();

    private Set<ServiceConfig> serviceInformations = new HashSet<>();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @PostConstruct
    public void prepareConfigs() {
        cdhConfigurationsMap = readAllFilesFromParameterDir();
        serviceInformations = readServiceDefinitions();
    }


    public Map<StackVersion, ServiceList> cdhConfigurations() {
        return cdhConfigurationsMap;
    }

    public Set<ServiceConfig> serviceInformations() {
        return serviceInformations;
    }

    private Set<ServiceConfig> readServiceDefinitions() throws IOException {
        String content = readFileFromClasspathQuietly(serviceDefinitionConfigurationPath);
        ServiceList serviceList = MAPPER.readValue(content, ServiceList.class);
        return serviceList
    }

    private Map<StackVersion, ServiceList> readAllFilesFromParameterDir() {
        Map<StackVersion, ServiceList> collectedFiles = new HashMap<>();
        try {
            List<Resource> files = getFiles(cdhConfigurationsPath);
            for (Resource serviceEntry : files) {
                String[] serviceAndPath = serviceEntry.getURL().getPath().split(cdhConfigurationsPath);
                String cdhVersion = serviceAndPath[1].split("/")[1].replace(".json", "");
                String insideFolder = String.format("%s%s", cdhConfigurationsPath, serviceAndPath[1]);
                LOGGER.debug("The the entry url is: {} file url is : {} for version: {}", serviceEntry, insideFolder, cdhVersion);
                StackVersion stackVersion = new StackVersion();
                ServiceList serviceList = new ServiceList();
                collectedFiles.put(stackVersion, serviceList);
            }
        } catch (IOException ex) {
            String message = String.format("Could not read files from the definiated folder which was: %s", dir);
            LOGGER.error(message, ex);
        }
        return collectedFiles;
    }

    private List<Resource> getFiles(String configDir) throws IOException {
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        List<Resource> jsonFiles = Arrays.stream(patternResolver
                .getResources("classpath:" + configDir + "/*.json"))
                .collect(toList());
        List<Resource> resources = new ArrayList<>();
        resources.addAll(jsonFiles);
        return resources;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
