package com.acme.treasury.r2dbc;
import com.acme.treasury.application.port.ApprovalRepository;
import com.acme.treasury.domain.*;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;
@Repository
public class R2dbcApprovalRepository implements ApprovalRepository {
 private final DatabaseClient db; public R2dbcApprovalRepository(DatabaseClient db){this.db=db;}
 public Mono<ApprovalRecord> insert(ApprovalRecord a){return db.sql("insert into approval_record(id,tenant_id,payment_id,approver,decision,comment,decided_at) values(:id,:tenant,:payment,:actor,:decision,:comment,:at)").bind("id",a.id()).bind("tenant",a.tenantId().value()).bind("payment",a.paymentId()).bind("actor",a.approver()).bind("decision",a.decision().name()).bind("comment",a.comment()==null?"":a.comment()).bind("at",a.decidedAt()).fetch().rowsUpdated().thenReturn(a);}
 public Mono<Boolean> exists(UUID paymentId,String actor){return db.sql("select exists(select 1 from approval_record where payment_id=:payment and approver=:actor) present").bind("payment",paymentId).bind("actor",actor).map((r,m)->r.get("present",Boolean.class)).one().defaultIfEmpty(false);}
 public Flux<ApprovalRecord> findByPaymentId(UUID paymentId){return db.sql("select id,tenant_id,payment_id,approver,decision,comment,decided_at from approval_record where payment_id=:payment order by decided_at").bind("payment",paymentId).map((r,m)->new ApprovalRecord(r.get("id",UUID.class),new TenantId(r.get("tenant_id",UUID.class)),r.get("payment_id",UUID.class),r.get("approver",String.class),ApprovalRecord.Decision.valueOf(r.get("decision",String.class)),r.get("comment",String.class),r.get("decided_at",java.time.Instant.class))).all();}
}

