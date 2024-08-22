package kr.co.ultari.board.config.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${ultari.web.filter.ignore:/api/**,/css/**,/img/**,/jquery/**,/js/**}")
    private String[] ignorePatterns;

    @Value("${ultari.web.filter.secure:}")
    private String[] securePatterns;

    @Autowired
    HttpBasicAuthenticationFilter authenticationFilter;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable().csrf().disable().cors(c -> {
                    CorsConfigurationSource source = request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("*"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        return config;
                    };
                    c.configurationSource(source);
                }).authorizeRequests()

                .antMatchers(ignorePatterns).permitAll().and().formLogin().disable().logout().disable().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        if (ObjectUtils.isEmpty(securePatterns))
            http.authorizeRequests().anyRequest().authenticated();
        else
            http.authorizeRequests().antMatchers(securePatterns).authenticated();

    }

    /*
     * @Bean public PasswordEncoder passwordEncoder() { return
     * PasswordEncoderFactories.createDelegatingPasswordEncoder(); }
     */
}