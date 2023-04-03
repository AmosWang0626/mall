package cn.eyeo.mall.client.order.dto.data;

import com.alibaba.cola.dto.Command;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.util.List;

/**
 * 创建订单参数
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/12/2
 */
@Getter
@Setter
public class CreateOrderCmd extends Command {

    @Serial
    private static final long serialVersionUID = 8565694029085275647L;

    /**
     * 用户ID
     */
    private Long memberId;
    /**
     * 收货人姓名
     */
    private String recipientName;

    /**
     * 收货人联系电话
     */
    private String recipientPhone;

    /**
     * 收货地址
     */
    private String recipientAddress;

    /**
     * 订单条目
     */
    @NotEmpty(message = "订单条目不能为空")
    private List<OrderItemParam> orderItemParamList;

    /**
     * 订单条目
     */
    @Getter
    @Setter
    public static class OrderItemParam {

        /**
         * 商品SKU ID
         */
        @NotNull(message = "商品SKU编号不能为空")
        private Long goodsSkuId;
        /**
         * 购买数量
         */
        @NotNull(message = "购买数量不能为空")
        private Integer quantity;

    }
}
