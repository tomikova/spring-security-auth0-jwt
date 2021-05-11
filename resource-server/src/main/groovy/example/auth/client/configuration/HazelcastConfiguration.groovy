package example.auth.client.configuration

import com.hazelcast.config.Config
import com.hazelcast.config.EvictionConfig
import com.hazelcast.config.JoinConfig
import com.hazelcast.config.MapConfig
import com.hazelcast.config.MaxSizePolicy
import com.hazelcast.config.MulticastConfig
import com.hazelcast.config.NetworkConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.spring.cache.HazelcastCacheManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableCaching
@Configuration
class HazelcastConfiguration {

    private final String INSTANCE_NAME = "IN-MEMORY-KEY-CACHE"

    @Bean("inMemoryHazelcastConfig")
    Config inMemoryHazelcastConfig() {
        MapConfig mapConfig = new MapConfig()
                .setTimeToLiveSeconds(60 * 60 * 24)
                .setEvictionConfig(
                        (new EvictionConfig())
                        .setMaxSizePolicy(MaxSizePolicy.FREE_HEAP_PERCENTAGE)
                        .setSize(85))

        NetworkConfig networkConfig = new NetworkConfig()
        JoinConfig joinConfig = new JoinConfig()
        MulticastConfig multicastConfig = new MulticastConfig()
        multicastConfig.setEnabled(false)
        joinConfig.setMulticastConfig(multicastConfig)
        networkConfig.setJoin(joinConfig)

        Config config = new Config()
        config.setNetworkConfig(networkConfig)
                .setInstanceName(INSTANCE_NAME)
                .getMapConfigs().put("inMemoryPublicKeyMap", mapConfig)
        config
    }

    @Bean("inMemoryHazelcastInstance")
    HazelcastInstance inMemoryHazelcastInstance(@Qualifier("inMemoryHazelcastConfig") Config config) {
        HazelcastInstance hazelcastInstance = Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME)
        if (hazelcastInstance) {
            hazelcastInstance
        } else {
            Hazelcast.newHazelcastInstance(config)
        }
    }

    @Bean("inMemoryHazelcastCacheManager")
    CacheManager inMemoryHazelcastCacheManager(@Qualifier("inMemoryHazelcastInstance") HazelcastInstance hazelcastInstance) {
        new HazelcastCacheManager(hazelcastInstance)
    }

}
