package com.acme.treasury.web.security;
import com.acme.treasury.domain.TenantId;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import java.util.UUID;
class ReactorTenantContextTest {
 @Test void readsTenantFromSubscriberContext(){var tenant=new TenantId(UUID.randomUUID());StepVerifier.create(new ReactorTenantContext().currentTenant().contextWrite(c->c.put(ReactorTenantContext.KEY,tenant))).expectNext(tenant).verifyComplete();}
 @Test void rejectsMissingTenant(){StepVerifier.create(new ReactorTenantContext().currentTenant()).expectError(MissingTenantException.class).verify();}
}

