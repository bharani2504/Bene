package com.example.bene.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.lock.LockRepository;
import org.springframework.integration.support.locks.LockRegistry;

import javax.sql.DataSource;

@Configuration
public class LockConfig {
    @Bean
    public LockRepository lockRepository(DataSource dataSource) {
        DefaultLockRepository repository = new DefaultLockRepository(dataSource);
        repository.setRegion("BENE_NICK_NAME_LOCK");
        repository.setTimeToLive(60000);
        return repository;
    }

    @Bean
    public LockRegistry lockRegistry(LockRepository lockRepository) {
        return new JdbcLockRegistry(lockRepository);

    }
}