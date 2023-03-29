package cn.eyeo.mall.adapter.web;

import cn.eyeo.mall.client.order.api.IOrderService;
import cn.eyeo.mall.client.order.dto.data.CreateOrderCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户界面（对外提供的API接口）
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/12/1
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    @PostMapping("create")
    public String create(CreateOrderCmd cmd) {
        boolean createResult;

        try {
            createResult = iOrderService.create(cmd);
        } catch (Exception e) {
            createResult = false;
            e.printStackTrace();
        }

        return createResult ? "success" : "fail";
    }

}
