package example.auth.server.service

import com.hazelcast.map.IMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class HazelcastService {

    private IMap keyPairLastChangedMap

    @Autowired
    HazelcastService(@Qualifier('keyPairLastChangedMap') IMap keyPairLastChangedMap) {
        this.keyPairLastChangedMap = keyPairLastChangedMap
    }

    void mapPut(String key, String value) {
        keyPairLastChangedMap.put(key, value)
    }

}
