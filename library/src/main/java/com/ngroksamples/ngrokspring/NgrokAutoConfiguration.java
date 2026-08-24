package com.ngroksamples.ngrokspring;

import com.ngrok.Session;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Activates automatically when ngrok-java is on the classpath. Set {@code
 * ngrok.enabled=false} to opt out, e.g. in production.
 */
@AutoConfiguration
@ConditionalOnClass(Session.class)
@ConditionalOnProperty(prefix = "ngrok", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(NgrokProperties.class)
public class NgrokAutoConfiguration {

    @Bean
    public NgrokForwarderLifecycle ngrokForwarderLifecycle(NgrokProperties properties) {
        return new NgrokForwarderLifecycle(properties);
    }
}
