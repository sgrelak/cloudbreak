package com.sequenceiq.cloudbreak.cmtemplate.generator.template;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cmtemplate.generator.template.domain.GeneratedClusterTemplate;

@Service
public class GeneratedClusterTemplateService {

    public GeneratedClusterTemplate prepareClusterTemplate(Set<String> services, String stackType, String version) {
        return new GeneratedClusterTemplate();
    }
}
