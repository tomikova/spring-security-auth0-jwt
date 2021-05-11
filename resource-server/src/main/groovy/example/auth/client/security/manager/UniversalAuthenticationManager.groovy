package example.auth.client.security.manager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.stereotype.Component

@Component
class UniversalAuthenticationManager extends ProviderManager implements AuthenticationManager {

    @Autowired
    UniversalAuthenticationManager(AuthenticationProvider[] authenticationProviders) {
        super(authenticationProviders.toList())
    }
}
