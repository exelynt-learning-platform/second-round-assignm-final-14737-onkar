package com.ecommerce.project.config;

import com.ecommerce.project.security.JwtFilter;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ✅ Enable CORS
            .cors(cors -> {})

            // ✅ Disable CSRF (for REST APIs)
            .csrf(csrf -> csrf.disable())

            // ✅ Authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()

                .requestMatchers(HttpMethod.POST, "/payment/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            // ✅ Stateless session (JWT)
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        // ✅ Add JWT filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ FIXED CORS CONFIGURATION
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {

        org.springframework.web.cors.CorsConfiguration config =
                new org.springframework.web.cors.CorsConfiguration();

        config.setAllowCredentials(true);

        // ✅ FIX: specify trusted origins (NO "*")
        config.setAllowedOrigins(java.util.Arrays.asList(
                "http://localhost:3000",   // React frontend
                "http://localhost:8080"    // optional
        ));

        // ✅ Allowed headers
        config.setAllowedHeaders(java.util.Arrays.asList(
                "Authorization",
                "Content-Type"
        ));

        // ✅ Allowed methods
        config.setAllowedMethods(java.util.Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // ✅ Expose headers (important for JWT)
        config.setExposedHeaders(java.util.Arrays.asList(
                "Authorization"
        ));

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // ✅ Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ✅ Password Encoder
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}