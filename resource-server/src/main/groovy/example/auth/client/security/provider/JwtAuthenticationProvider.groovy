package example.auth.client.security.provider

import example.auth.client.security.model.JwtAuthenticationToken
import example.auth.client.security.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider implements AuthenticationProvider {

    AuthenticationService authenticationService

    @Autowired
    JwtAuthenticationProvider(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService
    }

    @Override
    Authentication authenticate(Authentication authentication) throws AuthenticationException {
        authenticationService.authenticate(authentication)
    }

    @Override
    boolean supports(Class<?> authentication) {
        JwtAuthenticationToken.isAssignableFrom(authentication)
    }
}
