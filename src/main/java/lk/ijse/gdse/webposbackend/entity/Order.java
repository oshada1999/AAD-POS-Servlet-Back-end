package lk.ijse.gdse.webposbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Date;


@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Order implements SuperEntity{
    private String orderID;
    private String customerID;
    private Date orderDate;
}
