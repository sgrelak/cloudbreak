package com.sequenceiq.cloudbreak.api.endpoint.v4.workspace.requests;

import java.util.Set;

import com.sequenceiq.cloudbreak.api.endpoint.v4.JsonEntity;

import io.swagger.annotations.ApiModel;

@ApiModel
public class UserWorkspacePermissionsV4Request implements JsonEntity {

    private Set<String> permissions;

    private String userName;

    private String userId;

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
