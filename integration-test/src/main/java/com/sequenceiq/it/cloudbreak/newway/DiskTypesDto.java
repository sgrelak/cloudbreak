package com.sequenceiq.it.cloudbreak.newway;

import java.util.Collection;

import com.sequenceiq.cloudbreak.api.endpoint.v4.connector.responses.PlatformDisksV4Response;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

public class DiskTypesDto extends AbstractCloudbreakDto<Integer, PlatformDisksV4Response, DiskTypesDto> {
    public static final String DISKTYPES = "DISKTYPES";

    private String type;

    private Collection<String> responses;

    private final Integer request;

    private DiskTypesDto(String newId) {
        super(newId);
        request = 1;
    }

    DiskTypesDto() {
        this(DISKTYPES);
    }

    public Collection<String> getByFilterResponses() {
        return responses;
    }

    public void setByFilterResponses(Collection<String> responses) {
        this.responses = responses;
    }

    public String getType() {
        return type;
    }

    public DiskTypesDto withType(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }
}
