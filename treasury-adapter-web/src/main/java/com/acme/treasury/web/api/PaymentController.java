package com.acme.treasury.web.api;
import com.acme.treasury.application.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.util.UUID;
@RestController @RequestMapping("/api/v1/payments")
public class PaymentController {
 private final PaymentService service; public PaymentController(PaymentService service){this.service=service;}
 @PostMapping public Mono<ResponseEntity<PaymentDtos.Response>> create(@Valid @RequestBody PaymentDtos.CreateRequest request){return service.create(request.toCommand()).map(PaymentDtos.Response::from).map(body->ResponseEntity.created(URI.create("/api/v1/payments/"+body.id())).body(body));}
 @GetMapping("/{id}") public Mono<PaymentDtos.Response> get(@PathVariable UUID id){return service.get(id).switchIfEmpty(Mono.error(new PaymentNotFound(id))).map(PaymentDtos.Response::from);}
 @GetMapping public Flux<PaymentDtos.Response> list(@RequestParam(defaultValue="50") @Min(1) @Max(200) int limit,@RequestParam(defaultValue="0") @Min(0) int offset){return service.list(limit,offset).map(PaymentDtos.Response::from);}
 @PostMapping("/{id}/decisions") public Mono<PaymentDtos.Response> decide(@PathVariable UUID id,@Valid @RequestBody PaymentDtos.DecisionRequest request){return service.decide(new PaymentCommands.Decide(id,request.decision(),request.comment())).map(PaymentDtos.Response::from);}
}
