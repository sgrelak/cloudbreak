package com.sequenceiq.cloudbreak.service.cluster.flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmbariHostCheckerTestUtils {

    private AmbariHostCheckerTestUtils() {
    }

    public static Map<String, Map<String, String>> getHostComponentStates(List<Map<String, String>> componentStates) {
        Map<String, Map<String, String>> hostComponentsStates = new HashMap<>();
        int index = 1;
        for (Map<String, String> componentState : componentStates) {
            hostComponentsStates.put("host" + index++, componentState);
        }
        return hostComponentsStates;
    }

    public static Map<String, String> getComponentStates(String... states) {
        return getMapWithStates("component", states);
    }

    public static Map<String, String> getHostStatuses(String... states) {
        return getMapWithStates("host", states);
    }

    private static Map<String, String> getMapWithStates(String prefix, String... states) {
        Map<String, String> compStates = new HashMap<>();
        int index = 1;
        for (String state : states) {
            compStates.put(prefix + index++, state);
        }
        return compStates;
    }

}
