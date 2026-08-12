package com.acme.treasury.boot;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class FlywayConfiguration {
 @Bean Flyway flyway(@Value("${spring.flyway.url}") String url,@Value("${spring.flyway.user}") String user,@Value("${spring.flyway.password}") String password){return Flyway.configure().dataSource(url,user,password).locations("classpath:db/migration").load();}
 @Bean ApplicationRunner migrate(Flyway flyway){return args -> flyway.migrate();}
}

