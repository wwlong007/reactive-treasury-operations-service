package com.acme.treasury.web.api;
import com.acme.treasury.application.AccountQueryService;
import com.acme.treasury.domain.CashAccount;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.UUID;
@RestController @RequestMapping("/api/v1/accounts")
public class AccountController {
 private final AccountQueryService service; public AccountController(AccountQueryService service){this.service=service;}
 @GetMapping("/{id}") public Mono<Response> get(@PathVariable UUID id){return service.get(id).map(Response::from);}
 @GetMapping public Flux<Response> list(@RequestParam(defaultValue="50") @Min(1) @Max(200) int limit,@RequestParam(defaultValue="0") @Min(0) int offset){return service.list(limit,offset).map(Response::from);}
 public record Response(UUID id,String accountReference,String displayName,String currency,BigDecimal availableBalance,BigDecimal reservedBalance,String status,long version){static Response from(CashAccount a){return new Response(a.id(),a.accountReference(),a.displayName(),a.availableBalance().currency().getCurrencyCode(),a.availableBalance().amount(),a.reservedBalance().amount(),a.status().name(),a.version());}}
}

