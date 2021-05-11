package example.auth.client.security.model

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

class JwtAuthenticationToken extends AbstractAuthenticationToken {

    final String token
    final UserDetails principal

    JwtAuthenticationToken(String token) {
        super(null)
        this.token = token
        this.principal = null
        this.authenticated = false
    }

    JwtAuthenticationToken(UserDetails principal, String token) {
        super(principal.authorities)
        this.token = token
        this.principal = principal
        this.authenticated = true
    }

    @Override
    Object getCredentials() {
        return null
    }

    @Override
    Object getPrincipal() {
        return principal
    }

    @Override
    boolean isAuthenticated() {
        true
    }
}
