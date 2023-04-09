package cn.eyeo.mall.start.test;

import cn.eyeo.mall.client.order.api.IOrderService;
import cn.eyeo.mall.client.order.dto.data.CreateOrderCmd;
import cn.eyeo.mall.client.order.dto.data.OrderInfoVO;
import cn.eyeo.mall.start.Application;
import com.alibaba.cola.dto.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单测试
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/4/3
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderServiceTest {

    @Autowired
    private IOrderService iOrderService;

    @Test
    public void createOrder() {
        CreateOrderCmd cmd = new CreateOrderCmd();
        cmd.setMemberId(1000L);
        cmd.setRecipientName("amos.wang");
        cmd.setRecipientPhone("18966668888");
        cmd.setRecipientAddress("上海市-浦东-三林-上南路3339弄");

        List<CreateOrderCmd.OrderItemParam> items = new ArrayList<>();
        items.add(getOrderItem(1L, 2));
        items.add(getOrderItem(2L, 3));
        cmd.setOrderItemParamList(items);

        SingleResponse<OrderInfoVO> response = iOrderService.create(cmd);
        Assert.assertTrue(response.isSuccess());
    }

    private static CreateOrderCmd.OrderItemParam getOrderItem(Long skuId, Integer quantity) {
        CreateOrderCmd.OrderItemParam orderItem = new CreateOrderCmd.OrderItemParam();
        orderItem.setGoodsSkuId(skuId);
        orderItem.setQuantity(quantity);
        return orderItem;
    }

}
