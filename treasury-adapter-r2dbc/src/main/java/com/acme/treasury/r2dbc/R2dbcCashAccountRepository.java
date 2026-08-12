package com.acme.treasury.r2dbc;
import com.acme.treasury.application.port.CashAccountRepository;
import com.acme.treasury.domain.CashAccount;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;
@Repository
public class R2dbcCashAccountRepository implements CashAccountRepository {
    private static final String COLUMNS="id,tenant_id,account_reference,display_name,currency,available_balance,reserved_balance,status,version,created_at,updated_at";
    private final DatabaseClient db; public R2dbcCashAccountRepository(DatabaseClient db){this.db=db;}
    public Mono<CashAccount> findById(UUID id){return db.sql("select "+COLUMNS+" from cash_account where id=:id").bind("id",id).map((r,m)->RowMappings.cashAccount(r)).one();}
    public Mono<CashAccount> findByReference(String ref){return db.sql("select "+COLUMNS+" from cash_account where account_reference=:ref").bind("ref",ref).map((r,m)->RowMappings.cashAccount(r)).one();}
    public Flux<CashAccount> findAll(int limit,int offset){return db.sql("select "+COLUMNS+" from cash_account order by account_reference limit :limit offset :offset").bind("limit",limit).bind("offset",offset).map((r,m)->RowMappings.cashAccount(r)).all();}
    public Mono<CashAccount> save(CashAccount a){return db.sql("update cash_account set available_balance=:available,reserved_balance=:reserved,status=:status,version=:next,updated_at=:updated where id=:id and version=:version")
            .bind("available",a.availableBalance().amount()).bind("reserved",a.reservedBalance().amount()).bind("status",a.status().name()).bind("next",a.version()).bind("updated",a.updatedAt()).bind("id",a.id()).bind("version",a.version()-1)
            .fetch().rowsUpdated().flatMap(n->n==1?Mono.just(a):Mono.error(new OptimisticLockingFailureException("cash account was concurrently modified")));}
}

