package com.sequenceiq.it.cloudbreak.newway.assertion.database;

import com.sequenceiq.it.cloudbreak.newway.assertion.AssertionV2;
import com.sequenceiq.it.cloudbreak.newway.entity.database.DatabaseTestDto;
import com.sequenceiq.it.cloudbreak.newway.entity.database.DatabaseTestTestDto;

public class DatabaseTestAssertion {

    private DatabaseTestAssertion() {

    }

    public static AssertionV2<DatabaseTestDto> isExist(String databaseName, Integer expectedCount) {
        return (testContext, entity, cloudbreakClient) -> {
            boolean countCorrect = entity.getResponses()
                    .stream()
                    .filter(databaseV4Response -> databaseV4Response.getName().contentEquals(databaseName))
                    .count() == expectedCount;
            if (!countCorrect) {
                throw new IllegalArgumentException("Database count for " + databaseName + " is not as expected!");
            }
            return entity;
        };
    }

    public static AssertionV2<DatabaseTestTestDto> isAccessDenied() {
        return (testContext, entity, cloudbreakClient) -> {
            if (!entity.getResponse().getResult().contains("access is denied")) {
                throw new IllegalArgumentException("Database test connection result is not as expected.");
            }
            return entity;
        };
    }

}
