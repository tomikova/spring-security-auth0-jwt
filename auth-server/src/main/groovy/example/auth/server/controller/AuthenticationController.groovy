package example.auth.server.controller

import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import example.auth.server.enums.JwtTokenRealm
import example.auth.server.exception.AuthenticationException
import example.auth.server.model.AuthenticationResponse
import example.auth.server.model.UsernamePasswordLoginCommand
import example.auth.server.model.FetchPublicKeyCommand
import example.auth.server.model.PublicKeyResponse
import example.auth.server.model.RefreshTokenCommand
import example.auth.server.model.RefreshTokenResponse
import example.auth.server.model.ServiceResult
import example.auth.server.model.VerifyTokenCommand
import example.auth.server.service.AuthenticationService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@Slf4j
@RestController
@RequestMapping('auth')
class AuthenticationController {

    private AuthenticationService authenticationService

    @Autowired
    AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService
    }

    @PostMapping('login')
    ServiceResult authenticate(@Validated @RequestBody UsernamePasswordLoginCommand loginCommand) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(loginCommand.username, loginCommand.password)
            [success: true, result: response] as ServiceResult
        } catch (JWTCreationException jce) {
            log.error jce.message
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
    }

    @PostMapping('public-key')
    ServiceResult fetchPublicKey(@RequestBody FetchPublicKeyCommand fetchCommand) {
        PublicKeyResponse response = authenticationService.fetchPublicKey(fetchCommand.kid)
        [success: true, result: response] as ServiceResult
    }

    @PostMapping('verify-token')
    ServiceResult verifyToken(@RequestBody VerifyTokenCommand vtc) {
        try {
            DecodedJWT decodedJWT = authenticationService.verifyToken(vtc.token, JwtTokenRealm.ACCESS_TOKEN)
            [success: true, result: decodedJWT.getToken()] as ServiceResult
        } catch (JWTVerificationException jve) {
            log.error jve.message
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
    }

    @PostMapping('refresh-token')
    ServiceResult refreshToken(@RequestBody RefreshTokenCommand rtc) {
        try {
            RefreshTokenResponse response = authenticationService.refreshToken(rtc.refreshToken)
            [success: true, result: response] as ServiceResult
        } catch (JWTVerificationException | JWTCreationException | AuthenticationException ex) {
            log.error ex.message
            throw new ResponseStatusException(HttpStatus.FORBIDDEN)
        }
    }

}
