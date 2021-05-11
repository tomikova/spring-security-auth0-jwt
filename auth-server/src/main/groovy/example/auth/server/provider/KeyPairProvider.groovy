package example.auth.server.provider

import example.auth.server.model.JwtKeyData
import example.auth.server.properties.JwtConfigurationProperties
import example.auth.server.service.HazelcastService
import example.auth.server.service.VaultService
import groovy.util.logging.Slf4j
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

@Slf4j
@Service
class KeyPairProvider {

    Long initialKeyRotationDelayMills
    private RSAPublicKey pubKey
    private RSAPrivateKey priKey
    private String kid
    private VaultService vaultService
    private HazelcastService hazelcastService
    private JwtConfigurationProperties jwtConfigurationProperties

    @Autowired
    KeyPairProvider(VaultService vaultService, @Lazy HazelcastService hazelcastService, JwtConfigurationProperties jwtConfigurationProperties) {
        this.vaultService = vaultService
        this.hazelcastService = hazelcastService
        this.jwtConfigurationProperties = jwtConfigurationProperties
        if (jwtConfigurationProperties.initialKeyRotationDelayMills > 0 && !jwtConfigurationProperties.loadKeysFromVaultOnStart) {
            this.initialKeyRotationDelayMills = 0
        } else {
            this.initialKeyRotationDelayMills = jwtConfigurationProperties.initialKeyRotationDelayMills
        }
    }

    @PostConstruct
    private void loadKeysFromVault() {
        // fetch initial keys from Vault
        if (jwtConfigurationProperties.loadKeysFromVaultOnStart) {
            reloadKeys()
        }
    }

    void reloadKeys() {
        JwtKeyData rotatedKeys = (JwtKeyData) vaultService.getSecret('priKey')
        if (rotatedKeys) {
            log.info "***** Rotating keys *****"
            KeyFactory kf = KeyFactory.getInstance("RSA")
            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(rotatedKeys.getPriKey()))
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(rotatedKeys.getPubKey()))
            this.priKey = (RSAPrivateKey) kf.generatePrivate(keySpecPKCS8)
            this.pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509)
            this.kid = rotatedKeys.getKid()
        }
    }

    @Scheduled(fixedRateString = "#{jwtConfigurationProperties.keyRotationFrequencyMills}",
            initialDelayString = "#{keyPairProvider.initialKeyRotationDelayMills}")
    @SchedulerLock(name = "KeyPairProvider_generateKeyPair",
            lockAtLeastFor = "#{jwtConfigurationProperties.minLockLeaseTime}",
            lockAtMostFor = "#{jwtConfigurationProperties.maxLockLeaseTime}")
    protected void generateKeyPair() {
        log.info "###############################################"
        log.info "########### GENERATING NEW KEY-PAIR ###########"
        log.info "###############################################"
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)
        KeyPair kp = kpg.generateKeyPair()
        RSAPublicKey pubKey = (RSAPublicKey) kp.getPublic()
        RSAPrivateKey priKey = (RSAPrivateKey) kp.getPrivate()
        String kid = UUID.randomUUID().toString()
        Base64.Encoder encoder = Base64.getEncoder()
        String priKeyB64 = encoder.encodeToString(priKey.getEncoded())
        String pubKeyB64 = encoder.encodeToString(pubKey.getEncoded())
        vaultService.setSecret('priKey', [priKey: priKeyB64, pubKey: pubKeyB64, kid: kid] as JwtKeyData)
        vaultService.setSecret(kid, pubKeyB64)
        hazelcastService.mapPut('keyChangeTime', (new Date(System.currentTimeMillis())).toString())
    }

    RSAPublicKey getPubKey() {
        return pubKey
    }

    RSAPrivateKey getPriKey() {
        return priKey
    }

    String getKid() {
        return kid
    }

}
