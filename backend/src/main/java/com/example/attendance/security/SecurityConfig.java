package com.example.attendance.security;

import com.example.attendance.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsService userDetailsService() {
                return new UserDetailsServiceImpl();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
                provider.setUserDetailsService(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http,
                        DaoAuthenticationProvider authenticationProvider) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Enable H2 Console
                                                                                                    // frames
                                .authenticationProvider(authenticationProvider)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/login.html", "/register_faculty", "/assets/**",
                                                                "/css/**", "/js/**", "/h2-console/**")
                                                .permitAll()
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/admin-dashboard.html").hasRole("ADMIN")
                                                .requestMatchers("/api/faculty/**").hasRole("FACULTY")
                                                .requestMatchers("/faculty-dashboard.html").hasRole("FACULTY")
                                                .requestMatchers("/post-login").authenticated()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login.html")
                                                .loginProcessingUrl("/perform_login")
                                                .defaultSuccessUrl("/post-login", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login.html?logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .clearAuthentication(true)
                                                .permitAll())
                                .sessionManagement(session -> session
                                                .maximumSessions(1)
                                                .maxSessionsPreventsLogin(false));

                return http.build();
        }
}
