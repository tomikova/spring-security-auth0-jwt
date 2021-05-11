package example.auth.client.security.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt.auth")
class SecurityClientProperties {

    String authServerUrl
    String issuer
    String[] anonymousUrls = ["/h2-console/**", "/login/**", "/error/**", "/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs", "/webjars/**"]

}
