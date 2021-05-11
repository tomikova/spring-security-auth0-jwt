package example.auth.client.security.service

import org.springframework.security.core.Authentication

interface AuthenticationService<T extends Authentication> {

    T authenticate(T authenticationToken)

}