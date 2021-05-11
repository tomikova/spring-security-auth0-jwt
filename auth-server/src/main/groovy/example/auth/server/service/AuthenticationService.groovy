package example.auth.server.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import example.auth.server.enums.JwtTokenRealm
import example.auth.server.exception.AuthenticationException
import example.auth.server.model.AuthenticationResponse
import example.auth.server.model.PublicKeyResponse
import example.auth.server.model.RefreshTokenResponse
import example.auth.server.properties.JwtConfigurationProperties
import example.auth.server.provider.KeyPairProvider
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec

@Service
class AuthenticationService {

    private KeyPairProvider keyPairProvider
    private VaultService vaultService
    private JwtConfigurationProperties jwtConfigurationProperties
    private JsonSlurper jsonSlurper

    @Autowired
    AuthenticationService(KeyPairProvider keyPairProvider, VaultService vaultService,
                          JwtConfigurationProperties jwtConfigurationProperties) {
        this.keyPairProvider = keyPairProvider
        this.vaultService = vaultService
        this.jwtConfigurationProperties = jwtConfigurationProperties
        this.jsonSlurper = new JsonSlurper()
    }

    AuthenticationResponse authenticate(String username, String password) throws JWTCreationException {
        // custom authentication logic goes here
        String token = issueJwtToken(username, JwtTokenRealm.ACCESS_TOKEN)
        String refreshToken = issueRefreshToken(username, JwtTokenRealm.REFRESH_TOKEN)
        [token: token, refreshToken: refreshToken] as AuthenticationResponse
    }

    PublicKeyResponse fetchPublicKey(String kid) {
        String publicKey = vaultService.getSecret(kid)
        [publicKey: publicKey] as PublicKeyResponse
    }

    DecodedJWT verifyToken(String token, JwtTokenRealm realm) throws JWTVerificationException {
        // client would do this
        String[] jwtParts = token.split("\\.")
        String jwtHeader = new String(Base64.getDecoder().decode(jwtParts[0]))
        def jwtHeaderJson = jsonSlurper.parseText(jwtHeader)
        String kid = jwtHeaderJson?.kid
        String pubKeyB64 = fetchPublicKey(kid).publicKey
        KeyFactory kf = KeyFactory.getInstance("RSA")
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(pubKeyB64))
        RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509)
        Algorithm algorithm = Algorithm.RSA256(publicKey, null)
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(jwtConfigurationProperties.getIssuer())
                .withClaim('realm', realm.value)
                .build()
        verifier.verify(token)
    }

    RefreshTokenResponse refreshToken(String refreshToken) throws JWTVerificationException, JWTCreationException, AuthenticationException {
        DecodedJWT decodedJwt = verifyToken(refreshToken, JwtTokenRealm.REFRESH_TOKEN)
        String subject = decodedJwt.getSubject()
        String currentRefreshToken = vaultService.getSecret("${JwtTokenRealm.REFRESH_TOKEN.value}_$subject")
        if (!currentRefreshToken || currentRefreshToken != refreshToken) {
            // token not stored in Vault treat as revoked
            throw new AuthenticationException("Refresh token is revoked")
        }
        [token: issueJwtToken(subject, JwtTokenRealm.ACCESS_TOKEN)] as RefreshTokenResponse
    }

    private String issueJwtToken(String subject, JwtTokenRealm realm, Long duration = jwtConfigurationProperties.getTtlMills()) {
        Algorithm algorithm = Algorithm.RSA256(keyPairProvider.pubKey, keyPairProvider.priKey)
        String token = JWT.create()
                .withIssuer(jwtConfigurationProperties.getIssuer())
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + duration))
                .withKeyId(keyPairProvider.kid)
                .withClaim('realm', realm.value)
                .sign(algorithm)
        token
    }

    private String issueRefreshToken(String subject, JwtTokenRealm realm, Long duration = 1000 * 60 * 60 * 3) {
        String refreshToken = vaultService.getSecret("${realm.value}_$subject")
        if (!refreshToken) {
            // refresh token can be JWT tokens and can have expiration time and be verified
            refreshToken = issueJwtToken(subject, realm, duration)
            // we will use Vault to store it securely
            vaultService.setSecret("${realm.value}_$subject", refreshToken)
        }
        refreshToken
    }

}
