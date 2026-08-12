package com.acme.treasury.web.api;
import com.acme.treasury.application.AuditQueryService;
import com.acme.treasury.domain.ComplianceAudit;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import java.time.Instant;
import java.util.*;
@RestController @RequestMapping("/api/v1/audits")
public class AuditController {
 private final AuditQueryService service; public AuditController(AuditQueryService service){this.service=service;}
 @GetMapping public Flux<Response> list(@RequestParam(defaultValue="100") @Min(1) @Max(500) int limit,@RequestParam(defaultValue="0") @Min(0) int offset){return service.list(limit,offset).map(Response::from);}
 public record Response(UUID id,String eventType,String aggregateType,UUID aggregateId,String actor,Map<String,String> attributes,Instant occurredAt){static Response from(ComplianceAudit a){return new Response(a.id(),a.eventType(),a.aggregateType(),a.aggregateId(),a.actor(),a.attributes(),a.occurredAt());}}
}

