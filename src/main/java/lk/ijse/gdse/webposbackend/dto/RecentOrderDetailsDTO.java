package lk.ijse.gdse.webposbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class RecentOrderDetailsDTO {
    private String orderID;
    private String customerID;
    private String customerName;
    private String date;
    private double total;
    private List<ItemDTO> itemList;
}
