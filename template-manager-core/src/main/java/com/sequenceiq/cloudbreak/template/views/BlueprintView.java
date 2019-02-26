package com.sequenceiq.cloudbreak.template.views;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.sequenceiq.cloudbreak.domain.stack.cluster.Cluster;
import com.sequenceiq.cloudbreak.template.BlueprintProcessingException;
import com.sequenceiq.cloudbreak.template.processor.BlueprintTextProcessor;
import com.sequenceiq.cloudbreak.template.model.BlueprintStackInfo;

public class BlueprintView {

    private String blueprintText;

    private String version;

    private String type;

    private Set<String> components;

    public BlueprintView(String blueprintText, String version, String type) {
        this.blueprintText = blueprintText;
        this.type = type;
        this.version = version;
        components = prepareComponents(blueprintText);
    }

    public BlueprintView(Cluster cluster, BlueprintStackInfo blueprintStackInfo) {
        blueprintText = cluster.getBlueprint().getBlueprintText();
        type = blueprintStackInfo.getType();
        version = blueprintStackInfo.getVersion();
        components = prepareComponents(cluster.getBlueprint().getBlueprintText());
    }

    private Set<String> prepareComponents(String blueprintText) {
        Set<String> result = new HashSet<>();
        try {
            BlueprintTextProcessor blueprintTextProcessor = new BlueprintTextProcessor(blueprintText);
            Map<String, Set<String>> componentsByHostGroup = blueprintTextProcessor.getComponentsByHostGroup();
            componentsByHostGroup.values().forEach(result::addAll);
        } catch (BlueprintProcessingException exception) {
            result = new HashSet<>();
        }
        return result;
    }

    public String getVersion() {
        return version;
    }

    public void setBlueprintText(String blueprintText) {
        this.blueprintText = blueprintText;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean isHdf() {
        return "HDF".equalsIgnoreCase(type);
    }

    public String getBlueprintText() {
        return blueprintText;
    }

    public Set<String> getComponents() {
        return components;
    }

    public void setComponents(Set<String> components) {
        this.components = components;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlueprintView)) {
            return false;
        }
        BlueprintView that = (BlueprintView) o;
        return Objects.equals(blueprintText, that.blueprintText)
                && Objects.equals(version, that.version)
                && Objects.equals(type, that.type)
                && Objects.equals(components, that.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blueprintText, version, type, components);
    }

}
