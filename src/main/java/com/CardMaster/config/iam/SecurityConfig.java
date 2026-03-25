package com.CardMaster.config.iam;

import com.CardMaster.security.iam.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                
                // Admin Modules
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/fees/**").hasRole("ADMIN")
                .requestMatchers("/api/auditlogs/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                // Customer Specialized Endpoints (Specific before General)
                .requestMatchers("/api/customers/my", "/api/customers").hasRole("CUSTOMER")
                .requestMatchers("/api/applications/my", "/api/applications/customer/**").hasRole("CUSTOMER")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/applications").hasRole("CUSTOMER")
                .requestMatchers("/api/cards/my").hasRole("CUSTOMER")
                .requestMatchers("/api/accounts/my").hasRole("CUSTOMER")
                .requestMatchers("/api/transactions/my").hasRole("CUSTOMER")
                .requestMatchers("/api/billing/statements/my").hasRole("CUSTOMER")
                .requestMatchers("/api/billing/payments/my").hasRole("CUSTOMER")
                .requestMatchers("/api/documents/upload").hasRole("CUSTOMER")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/documents").hasRole("CUSTOMER")

                // Underwriter Modules
                .requestMatchers("/api/scores/**").hasRole("UNDERWRITER")
                .requestMatchers("/api/decisions/**").hasRole("UNDERWRITER")
                .requestMatchers("/api/applications/*/scores", "/api/applications/*/decisions").hasRole("UNDERWRITER")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/applications").hasRole("UNDERWRITER")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/documents/**").hasAnyRole("UNDERWRITER", "OFFICER")

                // Operations Specialized
                .requestMatchers("/api/transactions/recent").hasRole("ADMIN")
                .requestMatchers("/api/transactions/authorize", "/api/transactions/post/**", "/api/transactions/reverse/**").hasAnyRole("OFFICER", "ADMIN")
                .requestMatchers("/api/billing/statements/generate", "/api/billing/statements/close/**").hasRole("OFFICER")
                .requestMatchers("/api/billing/payments/capture").hasAnyRole("CUSTOMER", "OFFICER")
                
                // Operations General Collections
                .requestMatchers("/api/cards", "/api/cards/").hasRole("OFFICER")
                .requestMatchers("/api/accounts", "/api/accounts/").hasRole("OFFICER")
                .requestMatchers("/api/transactions", "/api/transactions/").hasRole("OFFICER")
                .requestMatchers("/api/billing/statements", "/api/billing/statements/").hasRole("OFFICER")
                .requestMatchers("/api/billing/payments", "/api/billing/payments/").hasRole("OFFICER")

                // Shared Read-Only
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/products").permitAll()

                // Default Guard
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
