package re.edu.inventoryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.edu.inventoryservice.dto.InventoryResponse;
import re.edu.inventoryservice.model.Product;
import re.edu.inventoryservice.service.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(inventoryService.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryById(productId));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return ResponseEntity.ok(inventoryService.createProduct(product));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @Valid @RequestBody Product productDetails) {
        return ResponseEntity.ok(inventoryService.updateProduct(productId, productDetails));
    }

    @PostMapping("/{productId}/deduct")
    public ResponseEntity<InventoryResponse> deductInventory(@PathVariable Long productId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.deductInventory(productId, quantity));
    }

    @PostMapping("/{productId}/add")
    public ResponseEntity<InventoryResponse> addInventory(@PathVariable Long productId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.addInventory(productId, quantity));
    }
}
