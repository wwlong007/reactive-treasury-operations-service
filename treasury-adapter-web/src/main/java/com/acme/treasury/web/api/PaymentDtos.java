package com.acme.treasury.web.api;
import com.acme.treasury.application.PaymentCommands;
import com.acme.treasury.domain.PaymentInstruction;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public final class PaymentDtos {
 private PaymentDtos(){}
 public record CreateRequest(@NotBlank @Size(max=80) String clientReference,@NotBlank @Size(max=80) String debitAccountReference,@NotBlank @Size(max=140) String beneficiaryName,@NotBlank @Size(max=80) String beneficiaryAccount,@NotNull @DecimalMin("0.01") @Digits(integer=17,fraction=2) BigDecimal amount,@NotBlank @Pattern(regexp="[A-Z]{3}") String currency){public PaymentCommands.Create toCommand(){return new PaymentCommands.Create(clientReference,debitAccountReference,beneficiaryName,beneficiaryAccount,amount,currency);}}
 public record DecisionRequest(@NotNull PaymentCommands.Decision decision,@Size(max=500) String comment){}
 public record Response(UUID id,String clientReference,UUID debitAccountId,String beneficiaryName,String beneficiaryAccount,BigDecimal amount,String currency,String status,int requiredApprovals,int approvalCount,String createdBy,Instant createdAt,Instant updatedAt,long version){static Response from(PaymentInstruction p){return new Response(p.id(),p.clientReference(),p.debitAccountId(),p.beneficiaryName(),p.beneficiaryAccount(),p.amount().amount(),p.amount().currency().getCurrencyCode(),p.status().name(),p.requiredApprovals(),p.approvalCount(),p.createdBy(),p.createdAt(),p.updatedAt(),p.version());}}
}

