package com.sequenceiq.cloudbreak.service.cluster.flow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.sequenceiq.ambari.client.AmbariClient;
import com.sequenceiq.cloudbreak.domain.stack.Stack;

@RunWith(MockitoJUnitRunner.class)
public class AmbariComponenstJoinStatusCheckerTaskTest {

    private final AmbariClient ambariClient = Mockito.mock(AmbariClient.class);

    private final AmbariComponenstJoinStatusCheckerTask underTest = new AmbariComponenstJoinStatusCheckerTask();

    @Test
    public void checkStatusWithNoUnknownComponentState() {
        AmbariHostsCheckerContext ambariHostsCheckerContext = new AmbariHostsCheckerContext(new Stack(), ambariClient, Collections.emptySet(), 0);
        Map<String, Map<String, String>> hostComponentStates = getHostComponentStates(Arrays.asList(
                getComponentStates("UNHEALTHY", "HEALTHY"),
                getComponentStates("HEALTHY", "HEALTHY"),
                getComponentStates("UNHEALTHY", "UNHEALTHY")
        ));
        when(ambariClient.getHostComponentsStates()).thenReturn(hostComponentStates);
        boolean result = underTest.checkStatus(ambariHostsCheckerContext);
        assertTrue(result);
    }

    @Test
    public void checkStatusWithOneUnknownComponentState() {
        AmbariHostsCheckerContext ambariHostsCheckerContext = new AmbariHostsCheckerContext(new Stack(), ambariClient, Collections.emptySet(), 0);
        Map<String, Map<String, String>> hostComponentStates = getHostComponentStates(Arrays.asList(
                getComponentStates("UNHEALTHY", "HEALTHY"),
                getComponentStates("HEALTHY", "HEALTHY", "UNKNOWN"),
                getComponentStates("UNHEALTHY", "HEALTHY")
        ));
        when(ambariClient.getHostComponentsStates()).thenReturn(hostComponentStates);
        boolean result = underTest.checkStatus(ambariHostsCheckerContext);
        assertFalse(result);
    }

    @Test
    public void checkStatusWithMultipleUnknownComponentStatesInDifferentHosts() {
        AmbariHostsCheckerContext ambariHostsCheckerContext = new AmbariHostsCheckerContext(new Stack(), ambariClient, Collections.emptySet(), 0);
        Map<String, Map<String, String>> hostComponentStates = getHostComponentStates(Arrays.asList(
                getComponentStates("UNKNOWN", "UNKNOWN"),
                getComponentStates("HEALTHY", "HEALTHY", "UNKNOWN"),
                getComponentStates("UNHEALTHY", "HEALTHY")
        ));
        when(ambariClient.getHostComponentsStates()).thenReturn(hostComponentStates);
        boolean result = underTest.checkStatus(ambariHostsCheckerContext);
        assertFalse(result);
    }

    private Map<String, Map<String, String>> getHostComponentStates(List<Map<String, String>> componentStates) {
        Map<String, Map<String, String>> hostComponentsStates = new HashMap<>();
        int index = 1;
        for (Map<String, String> componentState : componentStates) {
            hostComponentsStates.put("host" + index++, componentState);
        }
        return hostComponentsStates;
    }

    private Map<String, String> getComponentStates(String... states) {
        Map<String, String> compStates = new HashMap<>();
        int index = 1;
        for (String state : states) {
            compStates.put("component" + index++, state);
        }
        return compStates;
    }
}