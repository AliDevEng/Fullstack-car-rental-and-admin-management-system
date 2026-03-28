package com.nextcar.carrental.config;

import com.nextcar.carrental.security.JwtAuthenticationFilter;
import com.nextcar.carrental.security.JwtTokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())

            // Stateless — no HTTP session, JWT only
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 401 and 403 return JSON instead of redirecting
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(
                        "{\"status\": 401, \"error\": \"Unauthorized - valid token required\"}"
                    );
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(
                        "{\"status\": 403, \"error\": \"Access denied - insufficient permissions\"}"
                    );
                })
            )

            // Role-based access control
            .authorizeHttpRequests(auth -> auth

                // Public: login and registration
                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/admin/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/customers/register").permitAll()

                // Public: browsing cars and categories (read-only)
                .requestMatchers(HttpMethod.GET, "/cars", "/cars/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/categories").permitAll()

                // Admin only: car management (write)
                .requestMatchers(HttpMethod.POST, "/cars").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/cars/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/cars/**").hasAuthority("ADMIN")

                // Admin only: admin dashboard and customer management
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/customers").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/customers/**").hasAuthority("ADMIN")

                // Rentals: admin-only for full list and status updates
                .requestMatchers(HttpMethod.GET, "/rentals").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/rentals/*/status").hasAuthority("ADMIN")

                // Rentals: any authenticated user can create, view own, cancel
                .requestMatchers(HttpMethod.POST, "/rentals").authenticated()
                .requestMatchers(HttpMethod.GET, "/rentals/my").authenticated()
                .requestMatchers(HttpMethod.GET, "/rentals/*").authenticated()
                .requestMatchers(HttpMethod.PUT, "/rentals/*/cancel").authenticated()

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenUtil),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
