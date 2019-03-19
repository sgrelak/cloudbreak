package com.sequenceiq.it.cloudbreak.newway.assertion.util;

import com.sequenceiq.it.cloudbreak.newway.assertion.AssertionV2;
import com.sequenceiq.it.cloudbreak.newway.entity.util.CloudStorageMatrixTestDto;

public class CloudStorageMatrixTestAssertion {

    private CloudStorageMatrixTestAssertion() {
    }

    public static AssertionV2<CloudStorageMatrixTestDto> matrixIsNotEmpty() {
        return (testContext, entity, cloudbreakClient) -> {
            if (entity.getResponses().isEmpty()) {
                throw new IllegalArgumentException("Stackmatrix should not be empty");
            }
            return entity;
        };
    }

}
