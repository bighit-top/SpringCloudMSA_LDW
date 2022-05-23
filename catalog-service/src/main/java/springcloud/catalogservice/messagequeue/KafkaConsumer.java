package springcloud.catalogservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import springcloud.catalogservice.entity.CatalogEntity;
import springcloud.catalogservice.entity.CatalogRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer { //리스너로부터 데이터를 가져와서 데이터베이스에 업데이트

    private CatalogRepository repository;

    @Autowired
    public KafkaConsumer(CatalogRepository catalogRepository) {
        this.repository = catalogRepository;
    }

    //리스너 연결
    @KafkaListener(topics = "example-catalog-topic")
    public void updateQty(String kafkaMessage) {
        log.info("Kafka message: " + kafkaMessage);

        //역직렬화해서 사용 stringJson -> Json
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //데이터베이스에서 값 검색 후 업데이트 처리
        CatalogEntity entity = repository.findByProductId((String) map.get("productId"));
        if (entity != null) {
            entity.setStock(entity.getStock() - (Integer)map.get("qty")); //재고 감소 : 디비수량 - 메시지수량
            repository.save(entity);
        }

    }
}
