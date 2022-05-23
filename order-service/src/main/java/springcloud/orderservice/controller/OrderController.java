package springcloud.orderservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springcloud.orderservice.dto.OrderDto;
import springcloud.orderservice.entity.OrderEntity;
import springcloud.orderservice.messsagequeue.KafkaProducer;
import springcloud.orderservice.messsagequeue.OrderProducer;
import springcloud.orderservice.service.OrderService;
import springcloud.orderservice.vo.RequestOrder;
import springcloud.orderservice.vo.ResponseOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-service/")
@Slf4j
public class OrderController {

    private Environment env;
    private OrderService orderService;
    private KafkaProducer kafkaProducer;
    private OrderProducer orderProducer;

    @Autowired
    public OrderController(Environment env, OrderService orderService,
                           KafkaProducer kafkaProducer,OrderProducer orderProducer) {
        this.env = env;
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
        this.orderProducer = orderProducer;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseOrder createOrder(@PathVariable("userId") String userId,
                                     @RequestBody RequestOrder orderDetails) {
        log.info("Before add orders data");
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

        //jpa
        OrderDto createOrder = orderService.createOrder(orderDto);
        ResponseOrder resultValue = mapper.map(createOrder, ResponseOrder.class);

        //kafka : service에서 해주던 수량처리를 컨트롤러에서 해주어야한다.
//        orderDto.setOrderId(UUID.randomUUID().toString());
//        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice()); //수량*단가
//
//        //send this order to the kafka
        kafkaProducer.send("example-catalog-topic", orderDto);
//        orderProducer.send("orders", orderDto);

//        ResponseOrder resultValue = mapper.map(orderDto, ResponseOrder.class);
        log.info("After added orders data");

        return resultValue;
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception {
        log.info("Before retrieve orders data");
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> resultList= new ArrayList<>();
        orderList.forEach(v -> {
            resultList.add(new ModelMapper().map(v, ResponseOrder.class));
        });

/*
        //zipkin 장애 발생 테스트
        try {
            Thread.sleep(2000);
            throw new Exception("장애 발생 테스트");
        } catch (InterruptedException e) {
            log.warn(e.getMessage());
        }
*/

        log.info("Add retrieve orders data");

        return ResponseEntity.status(HttpStatus.OK).body(resultList);
    }
}
