package example.auth.client.app.repository

import example.auth.client.app.model.InventoryItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class InventoryRepository {

    private final String GET_ALL_SQL = "SELECT * FROM INVENTORY"
    private JdbcTemplate jdbcTemplate

    @Autowired
    InventoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate
    }

    List<InventoryItem> getAll() {
        List<InventoryItem> inventoryItems = jdbcTemplate.query(GET_ALL_SQL, new BeanPropertyRowMapper(InventoryItem.class))
        inventoryItems
    }

}
