package com.sequenceiq.it.cloudbreak;

import java.util.ArrayList;
import java.util.List;

public class TemplateAdditionHelper {

    public static final int WITH_TYPE_LENGTH = 3;

    public List<TemplateAddition> parseTemplateAdditions(String additionString) {
        List<TemplateAddition> additions = new ArrayList<>();
        String[] additionsArray = additionString.split(";");
        for (String additionsString : additionsArray) {
            String[] additionArray = additionsString.split(",");
            String type = additionArray.length == WITH_TYPE_LENGTH ? additionArray[WITH_TYPE_LENGTH - 1] : "CORE";
            additions.add(new TemplateAddition(additionArray[0], Integer.parseInt(additionArray[1]), type));
        }
        return additions;
    }

    public List<String[]> parseCommaSeparatedRows(String source) {
        List<String[]> result = new ArrayList<>();
        String[] rows = source.split(";");
        for (String row : rows) {
            result.add(row.split(","));
        }
        return result;
    }
}
