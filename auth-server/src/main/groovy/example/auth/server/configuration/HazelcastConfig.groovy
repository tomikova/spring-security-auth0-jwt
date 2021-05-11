package example.auth.server.configuration

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.client.config.ClientNetworkConfig
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.map.IMap
import example.auth.server.listener.HazelcastIMapListener
import example.auth.server.properties.HazelcastConfigurationProperties
import example.auth.server.provider.KeyPairProvider
import net.javacrumbs.shedlock.provider.hazelcast4.HazelcastLockProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HazelcastConfig {

    @Bean
    ClientConfig clientConfig(HazelcastConfigurationProperties hazelcastConfig) {
        ClientConfig clientConfig = new ClientConfig()
        ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig()
        networkConfig.addAddress(hazelcastConfig.getMembers())
                .setSmartRouting(true)
                .setRedoOperation(true)
                .setConnectionTimeout(hazelcastConfig.getConnectionTimeout())
        clientConfig
    }

    @Bean
    HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
        HazelcastClient.newHazelcastClient(clientConfig)
    }

    @Bean('keyPairLastChangedMap')
    IMap keyPairLastChangedMap(HazelcastInstance hazelcastInstance, KeyPairProvider keyPairProvider) {
        IMap keyPairLastChangedMap = hazelcastInstance.getMap('keyPairLastChangedMap')
        HazelcastIMapListener<String, String> mapListener = new HazelcastIMapListener<String, String>(keyPairProvider)
        keyPairLastChangedMap.addEntryListener(mapListener, true)
        keyPairLastChangedMap
    }

    @Bean
    HazelcastLockProvider lockProvider(HazelcastInstance hazelcastInstance) {
        new HazelcastLockProvider(hazelcastInstance)
    }

}
