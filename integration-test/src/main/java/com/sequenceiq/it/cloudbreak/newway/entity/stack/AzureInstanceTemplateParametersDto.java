package com.sequenceiq.it.cloudbreak.newway.entity.stack;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.template.AzureInstanceTemplateV4Parameters;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.entity.AbstractCloudbreakDto;

@Prototype
public class AzureInstanceTemplateParametersDto extends AbstractCloudbreakDto<AzureInstanceTemplateV4Parameters, AzureInstanceTemplateV4Parameters,
        AzureInstanceTemplateParametersDto> {

    protected AzureInstanceTemplateParametersDto(TestContext testContext) {
        super(new AzureInstanceTemplateV4Parameters(), testContext);
    }
}
