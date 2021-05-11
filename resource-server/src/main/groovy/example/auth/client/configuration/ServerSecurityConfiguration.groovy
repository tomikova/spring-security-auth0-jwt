package example.auth.client.configuration

import example.auth.client.security.UniversalAuthenticationEntryPoint
import example.auth.client.security.filter.JwtAuthenticationFilter
import example.auth.client.security.properties.SecurityClientProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true
)
@Configuration
class ServerSecurityConfiguration extends WebSecurityConfigurerAdapter {

    UniversalAuthenticationEntryPoint universalAuthenticationEntryPoint
    AuthenticationManager authenticationManager
    SecurityClientProperties securityClientProperties

    @Autowired
    ServerSecurityConfiguration(UniversalAuthenticationEntryPoint universalAuthenticationEntryPoint, AuthenticationManager authenticationManager,
                                SecurityClientProperties securityClientProperties) {
        super(true)
        this.universalAuthenticationEntryPoint = universalAuthenticationEntryPoint
        this.authenticationManager = authenticationManager
        this.securityClientProperties = securityClientProperties
    }

    @Override
    void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers(securityClientProperties.anonymousUrls)
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and().csrf().disable()
                .anonymous().disable()
                .exceptionHandling().authenticationEntryPoint(universalAuthenticationEntryPoint)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(securityClientProperties.anonymousUrls).permitAll()
                .and().addFilterBefore(new JwtAuthenticationFilter(authenticationManager, securityClientProperties.anonymousUrls), BasicAuthenticationFilter)
    }

    @Bean
    CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource()
        CorsConfiguration config = new CorsConfiguration()
        config.setAllowCredentials(true)
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return new CorsFilter(source)
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // Remove the ROLE_ prefix
        return new GrantedAuthorityDefaults("")
    }

}
