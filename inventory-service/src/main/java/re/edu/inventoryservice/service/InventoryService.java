package re.edu.inventoryservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.edu.inventoryservice.dto.InventoryResponse;
import re.edu.inventoryservice.model.Product;
import re.edu.inventoryservice.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public InventoryResponse getInventoryById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        product.setProductName(productDetails.getProductName());
        product.setQuantity(productDetails.getQuantity());
        product.setPrice(productDetails.getPrice());
        return productRepository.save(product);
    }

    @Transactional
    public InventoryResponse deductInventory(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        if (product.getQuantity() >= quantity) {
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
            return mapToResponse(product);
        }
        throw new IllegalStateException("Insufficient stock. Available: " + product.getQuantity() + ", Requested: " + quantity);
    }

    @Transactional
    public InventoryResponse addInventory(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        product.setQuantity(product.getQuantity() + quantity);
        productRepository.save(product);
        return mapToResponse(product);
    }

    private InventoryResponse mapToResponse(Product product) {
        return InventoryResponse.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .build();
    }
}