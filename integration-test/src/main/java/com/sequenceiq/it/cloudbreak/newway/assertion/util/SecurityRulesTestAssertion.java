package com.sequenceiq.it.cloudbreak.newway.assertion.util;

import com.sequenceiq.it.cloudbreak.newway.assertion.AssertionV2;
import com.sequenceiq.it.cloudbreak.newway.entity.util.SecurityRulesTestDto;

public class SecurityRulesTestAssertion {

    private SecurityRulesTestAssertion() {
    }

    public static AssertionV2<SecurityRulesTestDto> coreIsNotEmpty() {
        return (testContext, entity, cloudbreakClient) -> {
            if (entity.getResponse().getCore() == null) {
                throw new IllegalArgumentException("Security rules should not be null");
            }
            if (entity.getResponse().getCore().isEmpty()) {
                throw new IllegalArgumentException("Security rules should not be empty");
            }
            return entity;
        };
    }

    public static AssertionV2<SecurityRulesTestDto> gatewayIsNotEmpty() {
        return (testContext, entity, cloudbreakClient) -> {
            if (entity.getResponse().getGateway() == null) {
                throw new IllegalArgumentException("Security rules should not be null");
            }
            if (entity.getResponse().getGateway().isEmpty()) {
                throw new IllegalArgumentException("Security rules should not be empty");
            }
            return entity;
        };
    }

}
