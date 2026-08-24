# ngrok-spring

Attach a public ngrok URL to a Spring Boot app's own startup and shutdown — just add the
dependency:

```xml
<dependency>
    <groupId>com.ngroksamples</groupId>
    <artifactId>ngrok-spring</artifactId>
    <version>0.1.0</version>
</dependency>
```

## What it is

A Spring Boot autoconfiguration module. It activates automatically once it's on the classpath,
waits for Spring's embedded server to bind its port, then opens an ngrok forwarder pointed at
that port — closed again on shutdown. No code to write in your app at all.

## Setup

1. Sign up at [ngrok.com](https://ngrok.com) and grab an authtoken from
   [the dashboard](https://dashboard.ngrok.com/authtokens).
2. Set it as an env var:
   ```
   NGROK_AUTHTOKEN=your_token_here
   ```
3. Add the dependency above, **and** the `os-maven-plugin` build extension (needed to resolve
   ngrok-java's platform-specific native artifact — confirmed live that omitting this fails
   the build with an unresolved `${os.detected.classifier}` dependency):
   ```xml
   <build>
       <extensions>
           <extension>
               <groupId>kr.motd.maven</groupId>
               <artifactId>os-maven-plugin</artifactId>
               <version>1.7.0</version>
           </extension>
       </extensions>
   </build>
   ```
4. Run your app — see
   [`examples/basic`](examples/basic/src/main/java/com/ngroksamples/ngrokspring/example/BasicApplication.java).

You'll get a public URL bound to your ngrok account's default dev domain if you don't configure
one.

## Config

All configuration is via Spring properties under the `ngrok` prefix:

```properties
ngrok.enabled=true
ngrok.domain=your-reserved-domain.ngrok.app
ngrok.traffic-policy=on_http_request:\n  - actions:\n      - type: basic-auth\n        config:\n          credentials:\n            - "user:password123"
```

| Property | Type | Description |
|---|---|---|
| `ngrok.enabled` | `boolean` | Set to `false` to disable the forwarder entirely — e.g. in production. Defaults to `true`. |
| `ngrok.domain` | `String` | Reserved domain for this endpoint. Unset falls back to the account's default dev domain. |
| `ngrok.traffic-policy` | `String` | A raw [ngrok Traffic Policy](https://ngrok.com/docs/traffic-policy/) document (YAML or JSON) — the mechanism for auth, IP restrictions, header manipulation, webhook verification, and more. |

There's no `pooling` or `binding` property, unlike the ngrok-axum, ngrok-fastapi, and
ngrok-gin siblings — checked ngrok-java 1.1.1's actual published sources exhaustively and
neither exists in the SDK. (The official `java-sdk-example` comments a `.poolingEnabled(true)`
call as if it works; it doesn't, on the current release — stale documentation, not a real
option.) There's also no multiple-endpoint / collision-guard support: a Spring Boot app has
exactly one embedded server and one port, so the same-session collision those guards exist
for elsewhere structurally can't happen here — this library only ever opens one forwarder.

## Why forward-based, not a custom connector

An earlier prototype ([`ngrok/ngrok-spring`](https://github.com/ngrok/ngrok-spring), archived
since March 2024) took a lower-level approach: a custom Jetty `Connector` wrapping
ngrok-java's raw `Listener`. Checked its actual source — it doesn't compile (references a
`NgrokConnector` class that was never committed) and its own comments call it Jetty-only,
with generic web-container support left as future work that never happened.

This library sidesteps that entirely: ngrok-java's `session.httpEndpoint().forward(url)` points
ngrok at a port your app is already listening on, rather than requiring a listener your server
binds to directly. Hooking Spring's `WebServerInitializedEvent` — fired after the embedded
server has already bound its port — means this works with Tomcat (Spring Boot's default),
Jetty, or Undertow, with no server-specific integration code at all. Confirmed live:
`examples/basic` served a real request through its public ngrok URL.

## Development

```
mvn test
mvn -pl examples/basic -am spring-boot:run
```

## License

MIT
