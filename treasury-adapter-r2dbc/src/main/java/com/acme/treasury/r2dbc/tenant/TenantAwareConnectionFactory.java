package com.acme.treasury.r2dbc.tenant;

import io.r2dbc.spi.*;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Objects;

/** Associates the database session used by repositories with the request tenant. */
public final class TenantAwareConnectionFactory implements ConnectionFactory {
    private final ConnectionFactory delegate;
    public TenantAwareConnectionFactory(ConnectionFactory delegate) { this.delegate = Objects.requireNonNull(delegate); }

    @Override public Publisher<? extends Connection> create() {
        return Mono.from(delegate.create()).flatMap(connection -> TenantThreadLocalBridge.current()
                .map(tenant -> execute(connection, "select set_config('app.tenant_id', '" + tenant + "', false)")
                        .thenReturn((Connection) new ResettingConnection(connection)))
                .orElseGet(() -> Mono.just(new ResettingConnection(connection))));
    }
    @Override public ConnectionFactoryMetadata getMetadata() { return delegate.getMetadata(); }

    private static Mono<Void> execute(Connection connection, String sql) {
        return Flux.from(connection.createStatement(sql).execute()).flatMap(Result::getRowsUpdated).then();
    }

    private static final class ResettingConnection implements Connection {
        private final Connection delegate;
        private ResettingConnection(Connection delegate) { this.delegate = delegate; }
        @Override public Publisher<Void> close() {
            execute(delegate, "reset app.tenant_id").onErrorResume(ignored -> Mono.empty()).subscribe();
            return delegate.close();
        }
        @Override public Batch createBatch() { return delegate.createBatch(); }
        @Override public Publisher<Void> beginTransaction() { return delegate.beginTransaction(); }
        @Override public Publisher<Void> beginTransaction(TransactionDefinition definition) { return delegate.beginTransaction(definition); }
        @Override public Publisher<Void> commitTransaction() { return delegate.commitTransaction(); }
        @Override public Statement createStatement(String sql) { return delegate.createStatement(sql); }
        @Override public boolean isAutoCommit() { return delegate.isAutoCommit(); }
        @Override public ConnectionMetadata getMetadata() { return delegate.getMetadata(); }
        @Override public IsolationLevel getTransactionIsolationLevel() { return delegate.getTransactionIsolationLevel(); }
        @Override public Publisher<Void> releaseSavepoint(String name) { return delegate.releaseSavepoint(name); }
        @Override public Publisher<Void> rollbackTransaction() { return delegate.rollbackTransaction(); }
        @Override public Publisher<Void> rollbackTransactionToSavepoint(String name) { return delegate.rollbackTransactionToSavepoint(name); }
        @Override public Publisher<Void> createSavepoint(String name) { return delegate.createSavepoint(name); }
        @Override public Publisher<Void> setAutoCommit(boolean autoCommit) { return delegate.setAutoCommit(autoCommit); }
        @Override public Publisher<Void> setLockWaitTimeout(java.time.Duration timeout) { return delegate.setLockWaitTimeout(timeout); }
        @Override public Publisher<Void> setStatementTimeout(java.time.Duration timeout) { return delegate.setStatementTimeout(timeout); }
        @Override public Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) { return delegate.setTransactionIsolationLevel(isolationLevel); }
        @Override public Publisher<Boolean> validate(ValidationDepth depth) { return delegate.validate(depth); }
    }
}

