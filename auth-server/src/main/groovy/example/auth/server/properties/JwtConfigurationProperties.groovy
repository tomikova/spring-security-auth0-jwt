package example.auth.server.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtConfigurationProperties {
    String issuer
    Long ttlMills
    Long keyRotationFrequencyMills
    Long initialKeyRotationDelayMills = -1L
    String minLockLeaseTime = "PT5M"
}
