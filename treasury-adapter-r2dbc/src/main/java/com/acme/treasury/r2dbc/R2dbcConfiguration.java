package com.acme.treasury.r2dbc;

import com.acme.treasury.r2dbc.tenant.TenantAwareConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import java.time.Duration;

@Configuration
public class R2dbcConfiguration {
    @Bean(destroyMethod = "dispose")
    ConnectionPool connectionPool(@Value("${spring.r2dbc.url}") String url,
                                  @Value("${spring.r2dbc.username}") String username,
                                  @Value("${spring.r2dbc.password}") String password,
                                  @Value("${treasury.database.pool.max-size:10}") int maxSize) {
        var options = ConnectionFactoryOptions.parse(url).mutate().option(USER, username).option(PASSWORD, password).build();
        var base = ConnectionFactories.get(options);
        return new ConnectionPool(ConnectionPoolConfiguration.builder(base).initialSize(0).maxSize(maxSize)
                .maxIdleTime(Duration.ofMinutes(15)).validationQuery("SELECT 1").name("treasury-pool").build());
    }
    @Bean @Primary ConnectionFactory tenantConnectionFactory(ConnectionPool pool) { return new TenantAwareConnectionFactory(pool); }
    @Bean DatabaseClient databaseClient(ConnectionFactory connectionFactory) { return DatabaseClient.create(connectionFactory); }
    @Bean ReactiveTransactionManager transactionManager(ConnectionPool pool) { return new R2dbcTransactionManager(pool); }
}
