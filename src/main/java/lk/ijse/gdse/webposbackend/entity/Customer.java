package lk.ijse.gdse.webposbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Customer implements SuperEntity{
    private String customerID;
    private String name;
    private String address;
    private String contact;
}
