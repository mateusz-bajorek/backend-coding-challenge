package com.engagetech.config;

import com.engagetech.security.domain.model.AuthorityConstants;
import com.engagetech.security.service.AuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Security configuration, setting up authentication provider, CORS filters, disabling CSRF, requiring authentication
 * for any request except for OPTIONS and logout. Extra setup for dummy authentication to bypass lack of login site
 * and put hardcoded login credentials in the context.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll() // allow CORS option calls
                .anyRequest().authenticated()
                .and()
                .addFilterAt(dummyAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout().permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    /* ================================================
     * Artificially login default user upon any request
     * ================================================ */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD", "TRACE"));
        configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DummyAuthFilter dummyAuthFilter() throws Exception {
        DummyAuthFilter dummyAuthFilter = new DummyAuthFilter();
        dummyAuthFilter.setAuthenticationManager(authenticationManagerBean());
        dummyAuthFilter.setAuthenticationSuccessHandler(new SuccessHandler());
        return dummyAuthFilter;
    }

    private static class DummyAuthFilter extends UsernamePasswordAuthenticationFilter {
        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    "user", "$2y$10$dJYZJ1hQV9h4nmtU17sIgefgeRxVH.5sowWf88UuHhP4oWntKxEli"
            );
            setDetails(request, authRequest);
            return getAuthenticationManager().authenticate(authRequest);
        }

        @Override
        protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
            return SecurityContextHolder.getContext().getAuthentication() == null ||
                    Objects.equals(
                            SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority(),
                            AuthorityConstants.ANONYMOUS
                    );
        }
    }

    private static class SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
            getRedirectStrategy().sendRedirect(request, response, request.getRequestURI());
        }
    }

}
