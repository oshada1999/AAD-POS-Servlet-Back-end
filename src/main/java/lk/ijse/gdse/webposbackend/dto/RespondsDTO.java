package lk.ijse.gdse.webposbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class RespondsDTO {
    private int status;
    private String massage;
    private Object data;
}
