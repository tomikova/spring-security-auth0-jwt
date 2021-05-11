package example.auth.client.app.service

import example.auth.client.app.model.InventoryItem
import example.auth.client.app.repository.InventoryRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class InventoryService {

    private InventoryRepository inventoryRepository

    @Autowired
    InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository
    }

    List<InventoryItem> getAll() {
        // extra business logic
        log.info 'Getting all inventory items'
        List<InventoryItem> inventoryItems = inventoryRepository.getAll()
        inventoryItems
    }

}
