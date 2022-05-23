package springcloud_MSA.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import springcloud_MSA.userservice.vo.ResponseOrder;

import java.util.List;

@FeignClient(name = "order-service") //feignclient선언, 연결할 microservice 이름 지정
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder> getOrders(@PathVariable String userId);
}
