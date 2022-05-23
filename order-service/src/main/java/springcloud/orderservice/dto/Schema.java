package springcloud.orderservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder //생성자 대신 사용 가능
public class Schema {
    private String type;
    private List<Field> fields;
    private boolean optional;
    private String name;
}
