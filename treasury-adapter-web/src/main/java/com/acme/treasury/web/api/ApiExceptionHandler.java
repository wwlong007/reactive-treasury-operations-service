package com.acme.treasury.web.api;
import com.acme.treasury.application.*;
import com.acme.treasury.domain.DomainConflict;
import com.acme.treasury.web.security.MissingTenantException;
import org.springframework.http.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler({PaymentNotFound.class,AccountNotFound.class}) ResponseEntity<ProblemDetail> notFound(RuntimeException ex,ServerWebExchange exchange){return problem(HttpStatus.NOT_FOUND,"Resource not found",ex,exchange);}
 @ExceptionHandler({DomainConflict.class,DuplicateBusinessReference.class}) ResponseEntity<ProblemDetail> conflict(RuntimeException ex,ServerWebExchange exchange){return problem(HttpStatus.CONFLICT,"Business conflict",ex,exchange);}
 @ExceptionHandler({MissingTenantException.class}) ResponseEntity<ProblemDetail> missingTenant(RuntimeException ex,ServerWebExchange exchange){return problem(HttpStatus.UNAUTHORIZED,"Tenant context required",ex,exchange);}
 @ExceptionHandler(WebExchangeBindException.class) ResponseEntity<ProblemDetail> invalid(WebExchangeBindException ex,ServerWebExchange exchange){return problem(HttpStatus.BAD_REQUEST,"Invalid request",ex,exchange);}
 private ResponseEntity<ProblemDetail> problem(HttpStatus status,String title,Exception ex,ServerWebExchange exchange){var p=ProblemDetail.forStatusAndDetail(status,ex.getMessage());p.setTitle(title);p.setInstance(exchange.getRequest().getURI());return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(p);}
}
