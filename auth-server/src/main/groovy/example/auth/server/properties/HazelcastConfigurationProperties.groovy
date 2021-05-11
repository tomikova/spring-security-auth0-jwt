package example.auth.server.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "hazelcast-client")
class HazelcastConfigurationProperties {
    String[] members = {}
    Integer connectionTimeout = 5000
}
