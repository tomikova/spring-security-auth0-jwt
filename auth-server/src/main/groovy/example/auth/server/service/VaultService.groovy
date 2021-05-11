package example.auth.server.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.vault.core.VaultKeyValueOperations
import org.springframework.vault.core.VaultTemplate
import org.springframework.vault.support.VaultResponse
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend

@Service
class VaultService {

    private String appName
    private VaultTemplate vaultTemplate
    private VaultKeyValueOperations kvOps

    @Autowired
    VaultService(@Value('${spring.application.name}') appName, VaultTemplate vaultTemplate) {
        this.appName = appName
        this.vaultTemplate = vaultTemplate
        this.kvOps = vaultTemplate.opsForKeyValue("secret", KeyValueBackend.KV_2)
    }

    Object getSecret(String key) {
        // communication with Vault must be secure
        VaultResponse response = kvOps.get(appName)
        response?.getData()?.get(key)
    }

    void setSecret(String key, Object value) {
        // communication with Vault must be secure
        VaultResponse response = kvOps.get(appName)
        Map data = response ? response.getData() : [:]
        data.put(key, value)
        kvOps.put(appName, data)
    }


}
