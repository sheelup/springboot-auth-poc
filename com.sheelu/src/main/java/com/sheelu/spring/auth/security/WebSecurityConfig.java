package com.sheelu.spring.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheelu.spring.auth.models.UserRole;
import com.sheelu.spring.auth.security.basicAuth.BasicAuthLoginProcessingFilter;
import com.sheelu.spring.auth.security.basicAuth.BasicAuthenticationProvider;
import com.sheelu.spring.auth.security.tokenAuth.RequestMatcherImpl;
import com.sheelu.spring.auth.security.tokenAuth.TokenAuthenticationProcessingFilter;
import com.sheelu.spring.auth.security.tokenAuth.TokenAuthenticationProvider;
import com.sheelu.spring.auth.GlobalConstants;
import com.sheelu.spring.auth.security.jwt.TokenExtractor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AuthenticationSuccessHandler successHandler;
    @Autowired
    private AuthenticationFailureHandler failureHandler;
    @Autowired
    private BasicAuthenticationProvider basicAuthenticationProvider;
    @Autowired
    private TokenAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private TokenExtractor tokenExtractor;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    protected BasicAuthLoginProcessingFilter buildAjaxLoginProcessingFilter(String loginEndPoint) throws Exception {
        BasicAuthLoginProcessingFilter filter = new BasicAuthLoginProcessingFilter(loginEndPoint, successHandler, failureHandler, objectMapper);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    protected TokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter() throws Exception {
        List<Pair<String, String>> skipAuthPaths = Arrays.asList(
                Pair.of(GlobalConstants.AUTHENTICATION_URL, HttpMethod.POST.name()),
                Pair.of(GlobalConstants.REFRESH_TOKEN_URL, HttpMethod.POST.name()),
                Pair.of(GlobalConstants.HEALTH_API_V1_API, HttpMethod.GET.name()),
                Pair.of(GlobalConstants.SIGNUP_URL, HttpMethod.POST.name())
        );

        List<RequestMatcher> unAuthenticatedPathMatcher = skipAuthPaths.stream()
                .map(pair -> new AntPathRequestMatcher(pair.getLeft(), pair.getRight()))
                .collect(Collectors.toList());
        unAuthenticatedPathMatcher.add(new AntPathRequestMatcher("/documentation/**"));

        List<RequestMatcher> authenticatedPathMatcher = Arrays
                .asList(GlobalConstants.ROOT_API_PATTERN, GlobalConstants.ADMIN_API_PATTERN)
                .stream().map(p -> new AntPathRequestMatcher(p))
                .collect(Collectors.toList());

        RequestMatcherImpl requestMatcher =
                new RequestMatcherImpl(new OrRequestMatcher(unAuthenticatedPathMatcher), new OrRequestMatcher(authenticatedPathMatcher));
        TokenAuthenticationProcessingFilter filter
            = new TokenAuthenticationProcessingFilter(failureHandler, tokenExtractor, requestMatcher);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(basicAuthenticationProvider);
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

   @Bean
   public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
      StrictHttpFirewall firewall = new StrictHttpFirewall();
      firewall.setAllowUrlEncodedSlash(true);
      return firewall;
   }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .csrf().disable()
            .exceptionHandling()
            .authenticationEntryPoint(this.authenticationEntryPoint)

            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
                .authorizeRequests()
                .antMatchers(GlobalConstants.AUTH_API_PATTERN, GlobalConstants.SYSTEM_PUBLIC_API_PATTERN, "/documentation/**")
                .permitAll()
            .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, GlobalConstants.SIGNUP_URL)
                .permitAll()
            .and()
                .authorizeRequests()
                .antMatchers(GlobalConstants.ADMIN_API_PATTERN).hasAnyAuthority(UserRole.ADMIN.name())
                .antMatchers(GlobalConstants.ROOT_API_PATTERN).hasAnyAuthority(UserRole.STANDARD.name(), UserRole.ADMIN.name())
                .anyRequest().authenticated()
            .and()
                .addFilterBefore(new CustomCorsFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildAjaxLoginProcessingFilter(GlobalConstants.AUTHENTICATION_URL), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

    }

   @Override
   public void configure(WebSecurity web) throws Exception {
      super.configure(web);
      web.httpFirewall(new DefaultHttpFirewall());

   }
}
