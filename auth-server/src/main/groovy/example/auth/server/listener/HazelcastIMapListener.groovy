package example.auth.server.listener

import com.hazelcast.core.EntryEvent
import com.hazelcast.map.listener.EntryAddedListener
import com.hazelcast.map.listener.EntryUpdatedListener
import example.auth.server.provider.KeyPairProvider

class HazelcastIMapListener<K, V> implements EntryAddedListener<K, V>, EntryUpdatedListener<K, V> {

    private KeyPairProvider keyPairProvider

    HazelcastIMapListener(KeyPairProvider keyPairProvider) {
        this.keyPairProvider = keyPairProvider
    }

    @Override
    void entryAdded(EntryEvent<K, V> event) {
        keyPairProvider.reloadKeys()
    }

    @Override
    void entryUpdated(EntryEvent<K, V> event) {
        keyPairProvider.reloadKeys()
    }
}
