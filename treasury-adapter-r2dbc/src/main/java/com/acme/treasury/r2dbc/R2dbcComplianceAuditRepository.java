package com.acme.treasury.r2dbc;
import com.acme.treasury.application.port.ComplianceAuditRepository;
import com.acme.treasury.domain.*;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.*;
@Repository
public class R2dbcComplianceAuditRepository implements ComplianceAuditRepository {
 private final DatabaseClient db; public R2dbcComplianceAuditRepository(DatabaseClient db){this.db=db;}
 public Mono<ComplianceAudit> insert(ComplianceAudit a){return db.sql("insert into compliance_audit(id,tenant_id,event_type,aggregate_type,aggregate_id,actor,attributes,occurred_at) values(:id,:tenant,:event,:type,:aggregate,:actor,cast(:attributes as jsonb),:at)").bind("id",a.id()).bind("tenant",a.tenantId().value()).bind("event",a.eventType()).bind("type",a.aggregateType()).bind("aggregate",a.aggregateId()).bind("actor",a.actor()).bind("attributes",json(a.attributes())).bind("at",a.occurredAt()).fetch().rowsUpdated().thenReturn(a);}
 public Flux<ComplianceAudit> findByAggregate(UUID id,int limit){return query("where aggregate_id=:id order by occurred_at desc limit :limit").bind("id",id).bind("limit",limit).map((r,m)->map(r)).all();}
 public Flux<ComplianceAudit> findAll(int limit,int offset){return query("order by occurred_at desc,id limit :limit offset :offset").bind("limit",limit).bind("offset",offset).map((r,m)->map(r)).all();}
 private DatabaseClient.GenericExecuteSpec query(String suffix){return db.sql("select id,tenant_id,event_type,aggregate_type,aggregate_id,actor,attributes::text attributes,occurred_at from compliance_audit "+suffix);}
 private static ComplianceAudit map(io.r2dbc.spi.Row r){return new ComplianceAudit(r.get("id",UUID.class),new TenantId(r.get("tenant_id",UUID.class)),r.get("event_type",String.class),r.get("aggregate_type",String.class),r.get("aggregate_id",UUID.class),r.get("actor",String.class),Map.of("json",r.get("attributes",String.class)),r.get("occurred_at",java.time.Instant.class));}
 private static String json(Map<String,String> map){return map.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e->"\""+escape(e.getKey())+"\":\""+escape(e.getValue())+"\"").collect(java.util.stream.Collectors.joining(",","{","}"));}
 private static String escape(String v){return v.replace("\\","\\\\").replace("\"","\\\"");}
}

