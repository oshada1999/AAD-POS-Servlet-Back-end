package lk.ijse.gdse.webposbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class OrderDetails implements SuperEntity{
    private String orderID;
    private String itemCode;
    private double unitPrice;
    private int qty;
    private double total;
}
