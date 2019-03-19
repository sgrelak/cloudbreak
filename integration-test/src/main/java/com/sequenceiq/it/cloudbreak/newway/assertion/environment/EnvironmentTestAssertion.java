package com.sequenceiq.it.cloudbreak.newway.assertion.environment;

import com.sequenceiq.it.cloudbreak.exception.TestFailException;
import com.sequenceiq.it.cloudbreak.newway.assertion.AssertionV2;
import com.sequenceiq.it.cloudbreak.newway.entity.environment.EnvironmentTestDto;

public class EnvironmentTestAssertion {

    private EnvironmentTestAssertion() {
    }

    public static AssertionV2<EnvironmentTestDto> isCredentialAttached(String expectedCredentialName) {
        return (testContext, entity, cloudbreakClient) -> {
            String credentialName = entity.getResponse().getCredentialName();
            if (!credentialName.equals(expectedCredentialName)) {
                throw new TestFailException("Credential is not attached to environment");
            }
            return entity;
        };
    }
}
