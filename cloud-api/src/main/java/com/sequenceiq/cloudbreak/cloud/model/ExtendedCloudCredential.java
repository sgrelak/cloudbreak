package com.sequenceiq.cloudbreak.cloud.model;

import java.util.Map;
import java.util.Map.Entry;

import com.sequenceiq.cloudbreak.common.model.user.IdentityUser;

public class ExtendedCloudCredential extends CloudCredential {

    private final String description;

    private final String cloudPlatform;

    private final IdentityUser identityUser;

    private final String userId;

    private final Long workspaceId;

    public ExtendedCloudCredential(Long id, String cloudPlatform, String name, String description,
            IdentityUser identityUser, String userId, Long workspaceId) {
        super(id, name);
        this.cloudPlatform = cloudPlatform;
        this.description = description;
        this.identityUser = identityUser;
        this.userId = userId;
        this.workspaceId = workspaceId;
    }

    public ExtendedCloudCredential(CloudCredential cloudCredential, String cloudPlatform, String description,
            IdentityUser identityUser, String userId, Long workspaceId) {
        super(cloudCredential.getId(), cloudCredential.getName());
        Map<String, Object> parameters = cloudCredential.getParameters();
        for (Entry<String, Object> parameter : parameters.entrySet()) {
            putParameter(parameter.getKey(), parameter.getValue());
        }
        this.cloudPlatform = cloudPlatform;
        this.description = description;
        this.identityUser = identityUser;
        this.userId = userId;
        this.workspaceId = workspaceId;
    }

    public String getDescription() {
        return description;
    }

    public String getCloudPlatform() {
        return cloudPlatform;
    }

    public IdentityUser getIdentityUser() {
        return identityUser;
    }

    public String getUserId() {
        return userId;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }
}
