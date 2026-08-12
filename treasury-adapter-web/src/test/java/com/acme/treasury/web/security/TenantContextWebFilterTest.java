package com.acme.treasury.web.security;
import com.acme.treasury.domain.TenantId;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Instant;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
class TenantContextWebFilterTest {
 @Test void publishesJwtTenantIntoSubscriberContext(){var tenant=new TenantId(UUID.randomUUID());var jwt=new Jwt("token",Instant.now(),Instant.now().plusSeconds(60),Map.of("alg","none"),Map.of("sub","maker","tenant_id",tenant.toString()));var exchange=MockServerWebExchange.from(org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/").build());var chain=(org.springframework.web.server.WebFilterChain)e->Mono.deferContextual(c->{assertThat(c.<TenantId>get(ReactorTenantContext.KEY)).isEqualTo(tenant);return Mono.empty();});var secured=exchange.mutate().principal(Mono.just(new JwtAuthenticationToken(jwt))).build();StepVerifier.create(new TenantContextWebFilter().filter(secured,chain)).verifyComplete();}
}
