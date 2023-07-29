package lk.ijse.gdse.webposbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ItemDTO {
    private String itemCode;
    private String description;
    private Double unitPrice;
    private int qtyOnHand;
}
