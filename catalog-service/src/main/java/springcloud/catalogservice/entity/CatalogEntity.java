package springcloud.catalogservice.entity;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "catalog")
public class CatalogEntity implements Serializable { //직렬화

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String productId;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private Integer stock;
    @Column(nullable = false)
    private Integer unitPrice;

    @Column(nullable = false, updatable = false, insertable = false) //update,instert 하지 않음
    @ColumnDefault(value = "CURRENT_TIMESTAMP") //h2 db 현재 날짜시간함수 호출
    private Date createAt;
}
