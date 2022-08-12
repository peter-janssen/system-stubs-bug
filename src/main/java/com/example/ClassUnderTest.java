package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;
import java.util.stream.StreamSupport;

@Component
public class ClassUnderTest {
    private Environment environment;

    @Autowired
    public ClassUnderTest(Environment environment) {
        this.environment = environment;
    }

    public Properties getEnvironmentProperties() {
        final Properties props = new Properties();
        final MutablePropertySources propSrcs = ((AbstractEnvironment) environment).getPropertySources();

        StreamSupport.stream(propSrcs.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .forEach(propName -> props.setProperty(propName, environment.getProperty(propName)));
        return props;
    }
}
