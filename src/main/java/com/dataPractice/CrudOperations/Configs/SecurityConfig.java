package com.dataPractice.CrudOperations.Configs;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
        .authorizeHttpRequests(request -> request
        .requestMatchers("/user/**").hasRole("USER")
        .anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsManager users(PasswordEncoder passwordEncoder, DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource);
    }


    // @Bean
    // CommandLineRunner commandLineRunner(UserDetailsManager userDetailsManager){
    //     return args -> {
    //         System.out.print("attempting to create User");
    //         PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    //         UserDetails user = User.withUsername("atriggiani").password(passwordEncoder.encode("notAPassword!")).roles("USER").build();
    //         userDetailsManager.createUser(user);
    //     };
    // }
}
