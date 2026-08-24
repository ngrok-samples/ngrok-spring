package com.ngroksamples.ngrokspring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the ngrok forwarder opened by {@link NgrokAutoConfiguration}.
 *
 * <p>Deliberately minimal, same reasoning as the ngrok-axum, ngrok-fastapi, and
 * ngrok-gin siblings: the granular per-module options ngrok's SDKs used to offer
 * (basic auth, oauth, header manipulation, CIDR restrictions, etc.) mirror
 * ngrok's old "Edge Modules" system, superseded by Traffic Policy — use
 * trafficPolicy for all of that instead.
 *
 * <p>Unlike those three siblings, there is no {@code pooling} or {@code binding}
 * field: checked ngrok-java 1.1.1's actual published sources exhaustively and
 * neither exists in the SDK. ngrok-java's own example comments a
 * {@code .poolingEnabled(true)} call that isn't a real method on the current
 * release — stale documentation, not a real option.
 */
@ConfigurationProperties("ngrok")
public class NgrokProperties {

    /**
     * Set to false to disable the ngrok forwarder entirely, e.g. in production.
     */
    private boolean enabled = true;

    /**
     * Reserved domain for this endpoint. Unset falls back to the account's
     * default dev domain.
     */
    private String domain;

    /**
     * Raw ngrok Traffic Policy document (YAML or JSON), passed straight
     * through. See https://ngrok.com/docs/traffic-policy/.
     */
    private String trafficPolicy;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTrafficPolicy() {
        return trafficPolicy;
    }

    public void setTrafficPolicy(String trafficPolicy) {
        this.trafficPolicy = trafficPolicy;
    }
}
