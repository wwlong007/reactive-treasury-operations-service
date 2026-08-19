package com.acme.treasury.r2dbc;
import com.acme.treasury.application.port.PaymentInstructionRepository;
import com.acme.treasury.domain.PaymentInstruction;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;
@Repository
public class R2dbcPaymentInstructionRepository implements PaymentInstructionRepository {
    private static final String COLUMNS="id,tenant_id,client_reference,debit_account_id,beneficiary_name,beneficiary_account,amount,currency,status,required_approvals,approval_count,created_by,created_at,updated_at,version";
    private final DatabaseClient db; public R2dbcPaymentInstructionRepository(DatabaseClient db){this.db=db;}
    public Mono<PaymentInstruction> insert(PaymentInstruction p){return db.sql("insert into payment_instruction ("+COLUMNS+") values (:id,:tenant,:ref,:account,:name,:beneficiary,:amount,:currency,:status,:required,:count,:actor,:created,:updated,:version) on conflict (tenant_id, client_reference) do nothing")
      .bind("id",p.id()).bind("tenant",p.tenantId().value()).bind("ref",p.clientReference()).bind("account",p.debitAccountId()).bind("name",p.beneficiaryName()).bind("beneficiary",p.beneficiaryAccount()).bind("amount",p.amount().amount()).bind("currency",p.amount().currency().getCurrencyCode()).bind("status",p.status().name()).bind("required",p.requiredApprovals()).bind("count",p.approvalCount()).bind("actor",p.createdBy()).bind("created",p.createdAt()).bind("updated",p.updatedAt()).bind("version",p.version()).fetch().rowsUpdated().thenReturn(p);}
    public Mono<PaymentInstruction> update(PaymentInstruction p){return db.sql("update payment_instruction set status=:status,required_approvals=:required,approval_count=:count,updated_at=:updated,version=:next where id=:id and version=:version")
      .bind("status",p.status().name()).bind("required",p.requiredApprovals()).bind("count",p.approvalCount()).bind("updated",p.updatedAt()).bind("next",p.version()).bind("id",p.id()).bind("version",p.version()-1).fetch().rowsUpdated().flatMap(n->n==1?Mono.just(p):Mono.error(new OptimisticLockingFailureException("payment was concurrently modified")));}
    public Mono<PaymentInstruction> findById(UUID id){return db.sql("select "+COLUMNS+" from payment_instruction where id=:id").bind("id",id).map((r,m)->RowMappings.payment(r)).one();}
    public Mono<PaymentInstruction> findByClientReference(String ref){return db.sql("select "+COLUMNS+" from payment_instruction where client_reference=:ref").bind("ref",ref).map((r,m)->RowMappings.payment(r)).one();}
    public Flux<PaymentInstruction> findAll(int limit,int offset){return db.sql("select "+COLUMNS+" from payment_instruction order by created_at desc,id limit :limit offset :offset").bind("limit",limit).bind("offset",offset).map((r,m)->RowMappings.payment(r)).all();}
}
