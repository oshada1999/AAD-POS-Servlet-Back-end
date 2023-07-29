package lk.ijse.gdse.webposbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class OrderDetailsDTO {
    private String orderID;
    private String itemCode;
    private Double unitPrice;
    private int qty;
    private Double total;
}
