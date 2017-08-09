package com.kumuluz.pact;

import au.com.dius.pact.consumer.PactProviderRuleMk2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean({PactProviderRuleMk2.class})
@EnableConfigurationProperties(PactProperties.class)
public class PactAutoConfiguration {

    private static Log log = LogFactory.getLog(PactAutoConfiguration.class);

    @Autowired
    private PactProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public PactProviderRuleMk2 pactProviderRuleMk2() {
        if (this.properties.getProvider().getName() == null
                || this.properties.getProvider().getHost() == null
                || this.properties.getProvider().getPort() == 0)
        {
            String msg = "PactProviderRuleMk2 properties not configured properly." +
                    " Please check test.pact.provider.* properties settings in configuration file.";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2(
                this.properties.getProvider().getName(),
                this.properties.getProvider().getHost(),
                this.properties.getProvider().getPort(),
                this);

        return mockProvider;
    }
}
