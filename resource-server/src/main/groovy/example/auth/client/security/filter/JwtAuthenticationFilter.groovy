package example.auth.client.security.filter

import example.auth.client.security.exception.ResourceAuthenticationException
import example.auth.client.security.model.JwtAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter extends BaseAuthenticationFilter {

    JwtAuthenticationFilter(AuthenticationManager authenticationManager, String[] anonymousUrls) {
        super(anonymousUrls)
        this.authenticationManager = authenticationManager
    }

    @Override
    Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        Authentication authentication = resolveAuthenticationFromRequest(request)
        authenticationManager.authenticate(authentication)
    }

    @Override
    protected void successfulAuthentication(
            final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain, final Authentication authResult)
            throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult)
        chain.doFilter(request, response)
    }

    private Authentication resolveAuthenticationFromRequest(HttpServletRequest request) {
        // get token from request header or cookie
        String token = null
        String authorizationHeader = request.getHeader('Authorization')
        String headerSchema = 'Bearer'
        if (authorizationHeader && authorizationHeader.find(headerSchema)) {
            token = authorizationHeader.substring(headerSchema.length()).trim()
        }
        if (!token) {
            throw new ResourceAuthenticationException("Token not provided")
        }
        new JwtAuthenticationToken(token)
    }
}
