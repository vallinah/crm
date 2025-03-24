package site.easy.to.build.crm.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import site.easy.to.build.crm.config.api.ApiAuthenticationEntryPoint;
import site.easy.to.build.crm.config.api.ApiUserDetails;
import site.easy.to.build.crm.config.api.JWTFilter;
import site.easy.to.build.crm.config.oauth2.CustomOAuth2UserService;
import site.easy.to.build.crm.config.oauth2.OAuthLoginSuccessHandler;
import site.easy.to.build.crm.service.user.OAuthUserService;
import site.easy.to.build.crm.util.StringUtils;

import java.util.Optional;

@Configuration
public class SecurityConfig {

        private final OAuthLoginSuccessHandler oAuth2LoginSuccessHandler;

        private final CustomOAuth2UserService oauthUserService;

        private final CrmUserDetails crmUserDetails;

        private final CustomerUserDetails customerUserDetails;

        private final Environment environment;

        private final JWTFilter jwtFilter;

        private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

        private final ApiUserDetails apiUserDetails;

        @Autowired
        public SecurityConfig(OAuthLoginSuccessHandler oAuth2LoginSuccessHandler,
                        CustomOAuth2UserService oauthUserService, CrmUserDetails crmUserDetails,
                        CustomerUserDetails customerUserDetails, Environment environment, JWTFilter jwtFilter,
                        ApiAuthenticationEntryPoint apiAuthenticationEntryPoint,
                        ApiUserDetails apiUserDetails) {
                this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
                this.oauthUserService = oauthUserService;
                this.crmUserDetails = crmUserDetails;
                this.customerUserDetails = customerUserDetails;
                this.environment = environment;
                this.jwtFilter = jwtFilter;
                this.apiAuthenticationEntryPoint = apiAuthenticationEntryPoint;
                this.apiUserDetails = apiUserDetails;
        }

        @Bean
        @Order(3)
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
                httpSessionCsrfTokenRepository.setParameterName("csrf");

                http.csrf((csrf) -> csrf
                                .csrfTokenRepository(httpSessionCsrfTokenRepository));

                http.authorizeHttpRequests((authorize) -> authorize

                                .requestMatchers("/register/**").permitAll()
                                .requestMatchers("/set-employee-password/**").permitAll()
                                .requestMatchers("/change-password/**").permitAll()
                                .requestMatchers("/font-awesome/**").permitAll()
                                .requestMatchers("/fonts/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/save").permitAll()
                                .requestMatchers("/js/**").permitAll()
                                .requestMatchers("/css/**").permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/**/manager/**")).hasRole("MANAGER")
                                .requestMatchers("/employee/**").hasAnyRole("MANAGER", "EMPLOYEE")
                                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                                .anyRequest().authenticated())

                                .formLogin((form) -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/", true)
                                                .failureUrl("/login")
                                                .permitAll())
                                .userDetailsService(crmUserDetails)
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/login")
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(oauthUserService))
                                                .successHandler(oAuth2LoginSuccessHandler))
                                .logout((logout) -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login")
                                                .permitAll())
                                .exceptionHandling(exception -> {
                                        exception.accessDeniedHandler(accessDeniedHandler());
                                });

                return http.build();
        }

        @Bean
        public AccessDeniedHandler accessDeniedHandler() {
                return new CustomAccessDeniedHandler();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain customerSecurityFilterChain(HttpSecurity http) throws Exception {

                HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
                httpSessionCsrfTokenRepository.setParameterName("csrf");

                http.csrf((csrf) -> csrf
                                .csrfTokenRepository(httpSessionCsrfTokenRepository));

                http.securityMatcher("/customer-login/**").authorizeHttpRequests((authorize) -> authorize
                                .requestMatchers("/set-password/**").permitAll()
                                .requestMatchers("/font-awesome/**").permitAll()
                                .requestMatchers("/fonts/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/js/**").permitAll()
                                .requestMatchers("/css/**").permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/**/manager/**")).hasRole("MANAGER")
                                .anyRequest().authenticated())
                                .formLogin((form) -> form
                                                .loginPage("/customer-login")
                                                .loginProcessingUrl("/customer-login")
                                                .failureUrl("/customer-login")
                                                .defaultSuccessUrl("/", true)
                                                .permitAll())
                                .userDetailsService(customerUserDetails)
                                .logout((logout) -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/customer-login")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        @Order(1)
        public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/api/**") // Isole les routes API
                                .csrf(csrf -> csrf.disable()) // Désactive CSRF pour les appels API (JSON)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/**").permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(AbstractHttpConfigurer::disable) // Désactive le formulaire HTML
                                .httpBasic(AbstractHttpConfigurer::disable) // Désactive l’auth basic
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        response.setContentType("application/json");
                                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                        response.getWriter().write("{\"error\": \"Unauthorized\"}");
                                                })
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.setContentType("application/json");
                                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                                        response.getWriter().write("{\"error\": \"Access Denied\"}");
                                                }))
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
                ;

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
                        throws Exception {
                return http.getSharedObject(AuthenticationManagerBuilder.class)
                                .userDetailsService(apiUserDetails)
                                .passwordEncoder(passwordEncoder)
                                .and()
                                .build();
        }
}
