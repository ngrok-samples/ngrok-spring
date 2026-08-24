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
that port — closed again on shutdown. 

## Setup

1. Sign up at [ngrok.com](https://ngrok.com) and grab an authtoken from
   [the dashboard](https://dashboard.ngrok.com/authtokens).
2. Set it as an env var:
   ```
   NGROK_AUTHTOKEN=your_token_here
   ```
3. Add the dependency above, **and** the `os-maven-plugin` build extension (needed to resolve
   ngrok-java's platform-specific native artifact:
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


## Development

```
mvn test
mvn -pl examples/basic -am spring-boot:run
```

## License

MIT
