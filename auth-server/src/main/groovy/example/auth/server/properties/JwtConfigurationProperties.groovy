package example.auth.server.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtConfigurationProperties {
    String issuer
    Long ttlMills = 300000
    Long keyRotationFrequencyMills = 900000
    Long initialKeyRotationDelayMills = 20000
    String minLockLeaseTime = "10s"
    String maxLockLeaseTime = "15s"
    Boolean loadKeysFromVaultOnStart = false
}
