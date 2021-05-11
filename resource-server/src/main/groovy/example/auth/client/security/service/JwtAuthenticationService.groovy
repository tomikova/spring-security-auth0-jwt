package example.auth.client.security.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import example.auth.client.security.enums.JwtTokenRealm
import example.auth.client.security.exception.ResourceAuthenticationException
import example.auth.client.security.model.ClientUserDetails
import example.auth.client.security.model.JwtAuthenticationToken
import example.auth.client.security.properties.SecurityClientProperties
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service

import java.security.interfaces.RSAPublicKey

@Slf4j
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Service
class JwtAuthenticationService implements AuthenticationService<JwtAuthenticationToken> {

    private RemoteAuthenticationService remoteAuthenticationService
    private SecurityClientProperties securityClientProperties
    private JsonSlurper jsonSlurper

    @Autowired
    JwtAuthenticationService(RemoteAuthenticationService remoteAuthenticationService, SecurityClientProperties securityClientProperties) {
        this.remoteAuthenticationService = remoteAuthenticationService
        this.securityClientProperties = securityClientProperties
        this.jsonSlurper = new JsonSlurper()
    }

    @Override
    JwtAuthenticationToken authenticate(JwtAuthenticationToken authenticationToken) {
        String token = authenticationToken.token
        if (!token) {
            throw new ResourceAuthenticationException("Token not provided")
        }
        JwtAuthenticationToken jwtAuthenticationToken = null
        String[] jwtParts = token.split("\\.")
        String jwtHeader = new String(Base64.getDecoder().decode(jwtParts[0]))
        def jwtHeaderJson = jsonSlurper.parseText(jwtHeader)
        String kid = jwtHeaderJson?.kid
        RSAPublicKey publicKey = remoteAuthenticationService.fetchPublicKey(kid)
        DecodedJWT decodedJWT = verifyToken(token, publicKey)
        // for test purposes give every user APP_USER role
        Collection<SimpleGrantedAuthority> authorities = [new SimpleGrantedAuthority("APP_USER")]
        ClientUserDetails clientUserDetails = new ClientUserDetails(decodedJWT.getSubject(), authorities)
        jwtAuthenticationToken = new JwtAuthenticationToken(clientUserDetails, decodedJWT.getToken())
        jwtAuthenticationToken
    }

    private DecodedJWT verifyToken(String token, RSAPublicKey publicKey) {
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, null)
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(securityClientProperties.getIssuer())
                    .withClaim('realm', JwtTokenRealm.ACCESS_TOKEN.value)
                    .build()
            verifier.verify(token)
        } catch (JWTVerificationException jve) {
            log.error jve.message
            throw new ResourceAuthenticationException(jve.message)
        }
    }

}
