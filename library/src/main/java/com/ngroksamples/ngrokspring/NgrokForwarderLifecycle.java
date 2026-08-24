package com.ngroksamples.ngrokspring;

import com.ngrok.Forwarder;
import com.ngrok.Session;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Opens an ngrok forwarder pointed at the app's own embedded server once it's
 * bound, and closes it on shutdown.
 *
 * <p>ngrok-java is forward-based, not listener-based: {@code
 * session.httpEndpoint().forward(url)} points ngrok at a port your app is
 * already listening on, rather than handing you a listener to bind to
 * directly. That's why this hooks {@link WebServerInitializedEvent} —
 * Spring's own signal that the embedded server (Tomcat by default) has
 * already bound a real port — instead of trying to swap out the server's
 * connector, which is what left the older, unfinished ngrok/ngrok-spring
 * prototype stuck supporting Jetty only.
 */
public class NgrokForwarderLifecycle
        implements ApplicationListener<WebServerInitializedEvent>, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(NgrokForwarderLifecycle.class);

    private final NgrokProperties properties;

    private Session session;
    private Forwarder.Endpoint forwarder;

    public NgrokForwarderLifecycle(NgrokProperties properties) {
        this.properties = properties;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        try {
            session = Session.withAuthtokenFromEnv().connect();

            var builder = session.httpEndpoint();
            if (properties.getDomain() != null) {
                builder.domain(properties.getDomain());
            }
            if (properties.getTrafficPolicy() != null) {
                builder.trafficPolicy(properties.getTrafficPolicy());
            }

            forwarder = builder.forward(new URL("http://localhost:" + port));
            log.info("Ingress established at: {}", forwarder.getUrl());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Unreachable: localhost URL is always well-formed", e);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to open ngrok forwarder", e);
        }
    }

    @Override
    public void destroy() {
        try {
            if (forwarder != null) {
                forwarder.close();
            }
            if (session != null) {
                session.close();
            }
        } catch (IOException e) {
            log.warn("Error closing ngrok session", e);
        }
    }
}
