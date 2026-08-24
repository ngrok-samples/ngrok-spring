package com.ngroksamples.ngrokspring;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class NgrokAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(NgrokAutoConfiguration.class));

    @Test
    void activatesByDefault() {
        contextRunner.run(
                context -> {
                    assertThat(context).hasSingleBean(NgrokForwarderLifecycle.class);
                    assertThat(context).hasSingleBean(NgrokProperties.class);
                });
    }

    @Test
    void disabledPropertySkipsAutoConfiguration() {
        contextRunner
                .withPropertyValues("ngrok.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(NgrokForwarderLifecycle.class));
    }

    @Test
    void bindsDomainAndTrafficPolicyFromProperties() {
        contextRunner
                .withPropertyValues(
                        "ngrok.domain=example.ngrok.app", "ngrok.traffic-policy=on_http_request: []")
                .run(
                        context -> {
                            var properties = context.getBean(NgrokProperties.class);
                            assertThat(properties.getDomain()).isEqualTo("example.ngrok.app");
                            assertThat(properties.getTrafficPolicy()).isEqualTo("on_http_request: []");
                        });
    }

    @Test
    void domainAndTrafficPolicyDefaultToNull() {
        contextRunner.run(
                context -> {
                    var properties = context.getBean(NgrokProperties.class);
                    assertThat(properties.getDomain()).isNull();
                    assertThat(properties.getTrafficPolicy()).isNull();
                });
    }
}
