package cn.eyeo.mall.app.order.scheduler;

import cn.eyeo.mall.client.order.api.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 自动扫描过期订单任务
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/12/4
 */
@Component
public class OrderAutoCancelScheduler {

    @Autowired
    private IOrderService iOrderService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void execute() {
        try {
            iOrderService.autoCancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
