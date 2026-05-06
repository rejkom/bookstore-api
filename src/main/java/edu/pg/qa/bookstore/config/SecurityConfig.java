package edu.pg.qa.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }

// Zakomentuj powyższą metodę oraz odkomentuj poniższą aby włączyć autentykację
//    @Bean
//    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        // publiczne GET-y
//                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
//                        // tokenowe endpointy obsługujemy ręcznie
//                        .requestMatchers("/api/auth/**", "/api/secure/**").permitAll()
//                        // modyfikacje książek tylko dla ADMIN
//                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PATCH, "/api/books/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
//                        // reszta – domyślnie wymaga uwierzytelnienia
//                        .anyRequest()
//                        .authenticated()
//                )
//                .httpBasic(Customizer.withDefaults());
//        return http.build();
//    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password("admin123")
                        .roles("ADMIN")
                        .build()
        );
    }

    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        // Na potrzeby warsztatów – brak kodowania hasła.
        return NoOpPasswordEncoder.getInstance();
    }
}