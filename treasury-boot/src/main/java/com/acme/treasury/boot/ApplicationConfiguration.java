package com.acme.treasury.boot;
import com.acme.treasury.application.port.RiskAssessmentPort;
import org.springframework.context.annotation.*;
import reactor.core.publisher.Mono;
import java.time.Clock;
@Configuration
public class ApplicationConfiguration {
 @Bean Clock clock(){return Clock.systemUTC();}
 @Bean RiskAssessmentPort riskAssessmentPort(){return payment -> Mono.defer(() -> Mono.just(RiskAssessmentPort.RiskDecision.accept(payment.amount().amount().compareTo(new java.math.BigDecimal("100000.00"))>=0?2:1)));}
}

