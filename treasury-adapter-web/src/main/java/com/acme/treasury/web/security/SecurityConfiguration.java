package com.acme.treasury.web.security;
import com.acme.treasury.application.port.ActorContext;
import com.acme.treasury.application.port.TenantContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
@Configuration @EnableReactiveMethodSecurity
public class SecurityConfiguration {
 @Bean TenantContext tenantContext(){return new ReactorTenantContext();}
 @Bean ActorContext actorContext(){return new ReactorActorContext();}
 @Bean SecurityWebFilterChain security(ServerHttpSecurity http){return http.csrf(ServerHttpSecurity.CsrfSpec::disable).authorizeExchange(e->e.pathMatchers("/actuator/health/**","/v3/api-docs/**","/swagger-ui/**").permitAll().anyExchange().authenticated()).oauth2ResourceServer(o->o.jwt(jwt->{})).build();}
}

