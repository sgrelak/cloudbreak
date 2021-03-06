package com.sequenceiq.cloudbreak.controller.v4;

import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.EnvironmentNames;
import com.sequenceiq.cloudbreak.api.endpoint.v4.kerberos.KerberosConfigV4Endpoint;
import com.sequenceiq.cloudbreak.api.endpoint.v4.kerberos.requests.KerberosV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.kerberos.responses.KerberosV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.kerberos.responses.KerberosViewV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.kerberos.responses.KerberosViewV4Responses;
import com.sequenceiq.cloudbreak.api.util.ConverterUtil;
import com.sequenceiq.cloudbreak.domain.KerberosConfig;
import com.sequenceiq.cloudbreak.service.kerberos.KerberosService;
import com.sequenceiq.cloudbreak.util.WorkspaceEntityType;

@Controller
@Transactional(TxType.NEVER)
@WorkspaceEntityType(KerberosConfig.class)
public class KerberosConfigV4Controller extends NotificationController implements KerberosConfigV4Endpoint {

    @Inject
    private KerberosService kerberosService;

    @Inject
    private ConverterUtil converterUtil;

    @Override
    public KerberosViewV4Responses list(Long workspaceId, String environment, Boolean attachGlobal) {
        Set<KerberosConfig> allInWorkspaceAndEnvironment = kerberosService.findAllInWorkspaceAndEnvironment(workspaceId,
                environment, attachGlobal);
        return new KerberosViewV4Responses(converterUtil.convertAllAsSet(allInWorkspaceAndEnvironment, KerberosViewV4Response.class));
    }

    @Override
    public KerberosV4Response get(Long workspaceId, String name) {
        KerberosConfig kerberosConfig = kerberosService.getByNameForWorkspaceId(name, workspaceId);
        return converterUtil.convert(kerberosConfig, KerberosV4Response.class);
    }

    @Override
    public KerberosV4Response create(Long workspaceId, @Valid KerberosV4Request request) {
        KerberosConfig newKerberosConfig = converterUtil.convert(request, KerberosConfig.class);
        KerberosConfig createdKerberosConfig = kerberosService.createInEnvironment(newKerberosConfig, request.getEnvironments(), workspaceId);
        return converterUtil.convert(createdKerberosConfig, KerberosV4Response.class);
    }

    @Override
    public KerberosV4Response delete(Long workspaceId, String name) {
        KerberosConfig deleted = kerberosService.deleteByNameFromWorkspace(name, workspaceId);
        return converterUtil.convert(deleted, KerberosV4Response.class);
    }

    @Override
    public KerberosV4Response attach(Long workspaceId, String name, EnvironmentNames environmentNames) {
        KerberosConfig attached = kerberosService.attachToEnvironments(name, environmentNames.getEnvironmentNames(), workspaceId);
        return converterUtil.convert(attached, KerberosV4Response.class);
    }

    @Override
    public KerberosV4Response detach(Long workspaceId, String name, EnvironmentNames environmentNames) {
        KerberosConfig detached = kerberosService.detachFromEnvironments(name, environmentNames.getEnvironmentNames(), workspaceId);
        return converterUtil.convert(detached, KerberosV4Response.class);
    }

    @Override
    public KerberosV4Request getRequest(Long workspaceId, String name) {
        KerberosConfig kerberosConfig = kerberosService.getByNameForWorkspaceId(name, workspaceId);
        return converterUtil.convert(kerberosConfig, KerberosV4Request.class);
    }
}