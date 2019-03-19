package com.sequenceiq.it.cloudbreak.newway.assertion;

import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

public class CommonAssert {

    private CommonAssert() {
    }

    public static <T extends AbstractCloudbreakDto> T responseExists(TestContext tc, T entity, CloudbreakClient cc) {
        if (entity == null) {
            throw new IllegalArgumentException("Given entity is null!");
        }
        if (entity.getResponse() == null) {
            throw new IllegalArgumentException("Response object for " + entity.getClass().getName() + " is null!");
        }
        return entity;
    }

    public static <T extends AbstractCloudbreakDto> T responsesExists(TestContext tc, T entity, CloudbreakClient cc) {
        if (entity == null) {
            throw new IllegalArgumentException("Given entity is null!");
        }
        if (entity.getResponses() == null) {
            throw new IllegalArgumentException("Response object for " + entity.getClass().getName() + " is null!");
        }
        return entity;
    }

}
