package cn.eyeo.mall.app.order.scheduler;

import cn.eyeo.mall.client.order.api.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
            LocalDateTime lastTime = LocalDateTime.now().minus(20, ChronoUnit.MINUTES);
            iOrderService.autoCancel(lastTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
