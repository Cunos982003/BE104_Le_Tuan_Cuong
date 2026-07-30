package re.edu.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import re.edu.orderservice.dto.InventoryResponse;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/inventory/{productId}")
    InventoryResponse getInventory(@PathVariable Long productId);

    @PostMapping("/api/inventory/{productId}/deduct")
    InventoryResponse deductInventory(@PathVariable Long productId, @RequestParam Integer quantity);

    @PostMapping("/api/inventory/{productId}/add")
    InventoryResponse addInventory(@PathVariable Long productId, @RequestParam Integer quantity);
}