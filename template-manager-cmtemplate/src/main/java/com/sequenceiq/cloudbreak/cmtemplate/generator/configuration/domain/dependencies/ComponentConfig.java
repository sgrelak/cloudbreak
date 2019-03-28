package com.sequenceiq.cloudbreak.cmtemplate.generator.configuration.domain.dependencies;

import java.util.HashSet;
import java.util.Set;

public class ComponentConfig {

    private String name;

    private Set<String> groups = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }
}
