package com.sequenceiq.cloudbreak.service.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.instance.InstanceGroup;
import com.sequenceiq.cloudbreak.domain.stack.instance.InstanceMetaData;
import com.sequenceiq.cloudbreak.repository.InstanceMetaDataRepository;

@Service
public class InstanceGroupMetadataCollector {

    @Inject
    private InstanceMetaDataRepository instanceMetadataRepository;

    public Map<String, List<InstanceMetaData>> collectMetadata(Stack stack) {
        Map<String, List<InstanceMetaData>> result = new HashMap<>();
        for (InstanceGroup instanceGroup : stack.getInstanceGroups()) {
            result.put(
                    instanceGroup.getGroupName(),
                    new ArrayList<>(instanceMetadataRepository.findAliveInstancesInInstanceGroup(instanceGroup.getId())));
        }
        return result;
    }
}
