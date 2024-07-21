package com.example.blogapp.config;

// Import necessary classes and packages
import com.example.blogapp.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Define the class as a Configuration in the Spring framework
@Configuration
// Enable Spring Security's web security support
@EnableWebSecurity
// Enable Spring Security's method security support
@EnableMethodSecurity
public class SecurityConfig {

    // Autowire the CustomUserDetailsService
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // Define a Bean for the PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use NoOpPasswordEncoder which does no encoding
        return NoOpPasswordEncoder.getInstance();
    }

    // Define a Bean for the SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configure the authorization requests
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                // Permit all requests to "/", "/register", "/css/**", and "/js/**"
                                .requestMatchers("/", "/register", "/css/**", "/js/**").permitAll()
                                // Require authentication for all other requests
                                .anyRequest().authenticated()
                )
                // Configure the form login
                .formLogin(formLogin ->
                        formLogin
                                // Set the login page URL
                                .loginPage("/login")
                                // Set the default success URL
                                .defaultSuccessUrl("/", true)
                                // Permit all requests to the login page
                                .permitAll()
                )
                // Configure the logout
                .logout(logout ->
                        logout
                                // Set the logout URL
                                .logoutUrl("/logout")
                                // Set the logout success URL
                                .logoutSuccessUrl("/login")
                                // Permit all requests to the logout URL
                                .permitAll()
                );

        // Build and return the SecurityFilterChain
        return http.build();
    }
}