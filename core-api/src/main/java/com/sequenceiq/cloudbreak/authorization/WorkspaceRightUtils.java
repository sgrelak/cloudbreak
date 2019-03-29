package com.sequenceiq.cloudbreak.authorization;

public class WorkspaceRightUtils {

    private WorkspaceRightUtils() {

    }

    public static String getRight(WorkspaceResource resource, WorkspacePermissions.Action action) {
        return resource.name().toLowerCase() + "/" + action.name().toLowerCase();
    }
}
