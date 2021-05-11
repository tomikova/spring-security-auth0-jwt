package example.auth.client.security

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class UniversalAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 'Not authorized to access resource')
    }

}
