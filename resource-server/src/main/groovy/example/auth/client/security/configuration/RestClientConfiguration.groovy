package example.auth.client.security.configuration

import example.auth.client.security.properties.SecurityClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.DefaultUriBuilderFactory

@Configuration
class RestClientConfiguration {

    @Bean('authServerRestClient')
    RestTemplate authServerRestClient(SecurityClientProperties securityClientProperties) {
        RestTemplate restTemplate = new RestTemplate()
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(securityClientProperties.authServerUrl))
        restTemplate
    }

}
