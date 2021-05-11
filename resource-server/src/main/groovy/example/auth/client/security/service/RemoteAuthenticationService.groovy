package example.auth.client.security.service

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec

@Service
class RemoteAuthenticationService {

    private RestTemplate authServerRestClient

    RemoteAuthenticationService(@Qualifier('authServerRestClient') RestTemplate authServerRestClient) {
        this.authServerRestClient = authServerRestClient
    }

    @Cacheable(cacheNames = ["inMemoryPublicKeyMap"], key = "#kid", cacheManager = "inMemoryHazelcastCacheManager")
    RSAPublicKey fetchPublicKey(String kid) {
        String fetchCommand = JsonOutput.toJson([kid: kid])
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> request = new HttpEntity<String>(fetchCommand, headers)
        ResponseEntity<String> response = authServerRestClient.postForEntity("", request, String.class)
        def jsonBody = new JsonSlurper().parseText(response.getBody())

        String pubKeyB64 = jsonBody.result.publicKey
        KeyFactory kf = KeyFactory.getInstance("RSA")
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(pubKeyB64))
        RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509)
        publicKey
    }

}
