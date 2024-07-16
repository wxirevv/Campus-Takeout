package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;
    /**
     * 支付超时订单处理
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    public void processTimeoutOrder() {
        log.info("开始进行支付超时订单处理:{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT,time);

        if(ordersList != null && ordersList.size()>0){
            for (Orders orders : ordersList){
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 派送中状态的订单处理
     */
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行一次
    public void processDeliveryOrder(){
         log.info("开始进行未完成订单状态处理:{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS,time);

        if(ordersList != null && ordersList.size()>0){
            for (Orders orders : ordersList){
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
