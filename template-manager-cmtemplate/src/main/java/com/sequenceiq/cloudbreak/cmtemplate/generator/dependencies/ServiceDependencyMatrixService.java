package com.sequenceiq.cloudbreak.cmtemplate.generator.dependencies;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cmtemplate.generator.dependencies.domain.ServiceDependencyMatrix;

@Service
public class ServiceDependencyMatrixService {

    public ServiceDependencyMatrix collectServiceDependencyMatrix(Set<String> services) {
        return new ServiceDependencyMatrix();
    }
}
