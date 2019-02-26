package com.sequenceiq.cloudbreak.api.model.v2;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sequenceiq.cloudbreak.api.model.JsonEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class StorageLocationBase implements JsonEntity {

    @ApiModelProperty
    @NotNull
    private String propertyFile;

    @ApiModelProperty
    @NotNull
    private String propertyName;

    @ApiModelProperty
    @NotNull
    private String value;

    public String getPropertyFile() {
        return propertyFile;
    }

    public void setPropertyFile(String propertyFile) {
        this.propertyFile = propertyFile;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StorageLocationBase)) {
            return false;
        }
        StorageLocationBase that = (StorageLocationBase) o;
        return Objects.equals(propertyFile, that.propertyFile)
                && Objects.equals(propertyName, that.propertyName)
                && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyFile, propertyName, value);
    }

}
