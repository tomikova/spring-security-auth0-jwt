package example.auth.client.app.controller

import example.auth.client.app.model.InventoryItem
import example.auth.client.app.service.InventoryService
import example.auth.client.model.ServiceResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping('inventory')
class InventoryController {

    private InventoryService inventoryService

    @Autowired
    InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService
    }

    @Secured("APP_USER")
    @GetMapping
    ServiceResult getInventory() {
        List<InventoryItem> inventoryItems = inventoryService.getAll()
        [success: true, result: inventoryItems] as ServiceResult
    }

}
