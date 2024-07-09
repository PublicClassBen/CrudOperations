package com.dataPractice.CrudOperations.Configs;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration class to create needed beans for the user components
 * 
 * @author Benjamin Triggiani
 */
@Configuration
@EnableTransactionManagement
public class RepositoryConfig {

    @Bean
	JdbcTemplate jdbcTemplate(DataSource dataSource){
		return new JdbcTemplate(dataSource);
	}

	@Bean
	PlatformTransactionManager transactionManager(DataSource dataSource){
		return new DataSourceTransactionManager(dataSource);
	}
}
