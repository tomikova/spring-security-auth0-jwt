package example.auth.client.security.exception

import org.springframework.security.core.AuthenticationException

class ResourceAuthenticationException extends AuthenticationException {

    ResourceAuthenticationException(String msg) {
        super(msg)
    }
}
