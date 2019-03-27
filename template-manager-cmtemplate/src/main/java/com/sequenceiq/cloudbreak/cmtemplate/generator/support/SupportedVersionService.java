package com.sequenceiq.cloudbreak.cmtemplate.generator.support;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cmtemplate.generator.support.domain.SupportedVersions;

@Service
public class SupportedVersionService {

    public SupportedVersions collectSupportedVersions(Set<String> services) {
        return new SupportedVersions();
    }

}
