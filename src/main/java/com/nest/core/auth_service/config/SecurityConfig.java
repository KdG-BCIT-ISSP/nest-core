package com.nest.core.auth_service.config;

import com.nest.core.auth_service.security.JWTFilter;
import com.nest.core.auth_service.security.JWTUtil;
import com.nest.core.auth_service.security.LoginFilter;
import com.nest.core.auth_service.service.CustomUserDetailService;
import com.nest.core.member_management_service.model.MemberRole;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration configuration;
    private final CustomUserDetailService customUserDetailService;
    private final JWTUtil jwtUtil;

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(
                                // Swagger Setting
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                // User-Management-Service
                                "/api/v1/member/join",
                                "/api/v1/member/login"
                                ).permitAll()
                        .requestMatchers(
                                "/api/v1/auth/getNewAccessToken",
                                // User-Management-Service
                                "/api/v1/member/me"
                        ).hasAnyRole(MemberRole.USER.name(),MemberRole.MODERATOR.name(),MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                "/api/v1/member/all"
                        ).hasAnyRole(MemberRole.SUPER_ADMIN.name())
                        // Post-Management-Service
                        .requestMatchers(
                                HttpMethod.POST, "/api/v1/posts"
                        ).hasAnyRole(MemberRole.USER.name(),MemberRole.MODERATOR.name(),MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.PUT, "/api/v1/posts"
                        ).hasAnyRole(MemberRole.USER.name(),MemberRole.MODERATOR.name(),MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.DELETE, "/api/v1/posts/**"
                        ).hasAnyRole(MemberRole.USER.name(),MemberRole.MODERATOR.name(),MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.GET, "/api/v1/posts"
                        ).permitAll()
                        // Article-Management-Service
                        .requestMatchers(
                                HttpMethod.DELETE, "/api/v1/article/bookmark/**"
                        ).hasAnyRole(MemberRole.USER.name(),MemberRole.MODERATOR.name(),MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.DELETE,"/api/v1/article/**"
                        ).hasAnyRole(MemberRole.ADMIN.name(), MemberRole.MODERATOR.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.POST,"/api/v1/article"
                        ).hasAnyRole(MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.PUT,"/api/v1/article"
                        ).hasAnyRole(MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.GET,"/api/v1/article"
                        ).permitAll()
                        // Comment-Management-Service
                        .requestMatchers(
                                HttpMethod.DELETE,"/api/v1/comment/**"
                        ).hasAnyRole(MemberRole.USER.name(),MemberRole.ADMIN.name(), MemberRole.MODERATOR.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.POST,"/api/v1/comment"
                        ).hasAnyRole(MemberRole.USER.name(),MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.PUT,"/api/v1/comment"
                        ).hasAnyRole(MemberRole.USER.name(),MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.GET,"/api/v1/comment"
                        ).hasAnyRole(MemberRole.USER.name(),MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        // Report-Management-Service
                        .requestMatchers(
                                HttpMethod.GET, "/api/v1/report/post/**"
                        ).hasAnyRole(MemberRole.ADMIN.name(), MemberRole.MODERATOR.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.GET, "/api/v1/report/article/**"
                        ).hasAnyRole(MemberRole.ADMIN.name(), MemberRole.MODERATOR.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.DELETE, "/api/v1/report/**"
                        ).hasAnyRole(MemberRole.ADMIN.name(), MemberRole.MODERATOR.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.POST, "/api/v1/report/post/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/report/article/**"
                        ).permitAll()
                        // Content-Management-Service (Interaction with content)
                        .requestMatchers(
                                HttpMethod.GET,"/api/v1/content/**"
                        ).hasAnyRole(MemberRole.USER.name(),MemberRole.MODERATOR.name(),MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        // Notification-Service
                        .requestMatchers(
                                HttpMethod.GET, "/api/v1/notification/subscribe/**"
                        ).hasAnyRole(MemberRole.USER.name(),MemberRole.MODERATOR.name(),MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        .requestMatchers(
                                HttpMethod.POST, "/api/v1/notification/send/**"
                        ).hasAnyRole(MemberRole.ADMIN.name(), MemberRole.SUPER_ADMIN.name())
                        // Search-Service
                        .requestMatchers(HttpMethod.GET, "/api/v1/search/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                ).exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                        })
                );
        http
                .httpBasic(AbstractHttpConfigurer::disable);


        http
                .addFilterBefore(new JWTFilter(jwtUtil, customUserDetailService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager(configuration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .logout((auth) -> auth
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((req, res, authentication) ->
                                res.setStatus(HttpServletResponse.SC_ACCEPTED))
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                );

        http
                .csrf((auth) -> auth.disable());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){

        return new BCryptPasswordEncoder();
    }
}
