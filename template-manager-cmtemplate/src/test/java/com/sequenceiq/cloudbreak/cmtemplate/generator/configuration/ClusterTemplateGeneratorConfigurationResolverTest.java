package com.sequenceiq.cloudbreak.cmtemplate.generator.configuration;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.sequenceiq.cloudbreak.cmtemplate.generator.configuration.domain.ServiceConfig;
import com.sequenceiq.cloudbreak.cmtemplate.generator.configuration.domain.versionmatrix.ServiceList;
import com.sequenceiq.cloudbreak.cmtemplate.generator.configuration.domain.StackVersion;

@RunWith(MockitoJUnitRunner.class)
public class ClusterTemplateGeneratorConfigurationResolverTest {

    @InjectMocks
    private final ClusterTemplateGeneratorConfigurationResolver underTest = new ClusterTemplateGeneratorConfigurationResolver();

    @Before
    public void setup() {
        ReflectionTestUtils.setField(underTest, "cdhConfigurationsPath", "cloudera-manager-template/cdh");
        ReflectionTestUtils.setField(underTest, "serviceDefinitionConfigurationPath", "cloudera-manager-template/service-definition.json");
        underTest.prepareConfigs();
    }

    @Test
    public void testThatAllFileIsReadableShouldVerifyThatFileCountMatch() {
        Map<StackVersion, ServiceList> stackVersionServiceListMap = underTest.cdhConfigurations();
        Set<ServiceConfig> serviceConfigs = underTest.serviceInformations();

        Assert.assertEquals(1L, stackVersionServiceListMap.size());
        Assert.assertEquals(27L, serviceConfigs.size());
    }
}