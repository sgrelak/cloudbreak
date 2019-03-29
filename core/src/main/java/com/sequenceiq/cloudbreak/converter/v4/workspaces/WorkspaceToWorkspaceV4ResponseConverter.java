package com.sequenceiq.cloudbreak.converter.v4.workspaces;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.workspace.responses.UserV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.workspace.responses.WorkspaceV4Response;
import com.sequenceiq.cloudbreak.api.util.ConverterUtil;
import com.sequenceiq.cloudbreak.converter.AbstractConversionServiceAwareConverter;
import com.sequenceiq.cloudbreak.domain.workspace.Workspace;

@Component
public class WorkspaceToWorkspaceV4ResponseConverter extends AbstractConversionServiceAwareConverter<Workspace, WorkspaceV4Response> {

    @Inject
    private ConverterUtil converterUtill;

    @Override
    public WorkspaceV4Response convert(Workspace workspace) {
        WorkspaceV4Response json = new WorkspaceV4Response();
        json.setDescription(workspace.getDescription());
        json.setName(workspace.getName());
        json.setId(workspace.getId());
        json.setStatus(workspace.getStatus());
        json.setUsers(converterUtill.convertAllAsSet(workspace.getUsers(), UserV4Response.class));
        return json;
    }
}
