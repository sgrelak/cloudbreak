package com.sequenceiq.it.cloudbreak.newway.client;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.newway.action.Action;
import com.sequenceiq.it.cloudbreak.newway.action.udaptestack.UpdateStackTestAction;
import com.sequenceiq.it.cloudbreak.newway.entity.udaptestack.UpdateStackTestDto;

@Service
public class UpdateStackTestClient {

    public Action<UpdateStackTestDto> put() {
        return new UpdateStackTestAction();
    }

}