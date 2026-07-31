package re.edu.inventoryservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
