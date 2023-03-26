package cn.eyeo.mall.app.product;

import cn.eyeo.mall.app.product.command.CategoryCreateCmdExe;
import cn.eyeo.mall.app.product.command.CategoryDeleteCmdExe;
import cn.eyeo.mall.app.product.command.CategoryModifyCmdExe;
import cn.eyeo.mall.client.product.api.ICategoryOpsService;
import cn.eyeo.mall.client.product.dto.CategoryCreateCmd;
import cn.eyeo.mall.client.product.dto.CategoryModifyCmd;
import cn.eyeo.mall.client.product.dto.data.CategoryVO;
import com.alibaba.cola.dto.SingleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分类管理
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
@Service
public class ICategoryOpsServiceImpl implements ICategoryOpsService {

    @Autowired
    private CategoryCreateCmdExe categoryCreateCmdExe;
    @Autowired
    private CategoryModifyCmdExe categoryModifyCmdExe;
    @Autowired
    private CategoryDeleteCmdExe categoryDeleteCmdExe;

    @Override
    public SingleResponse<CategoryVO> create(CategoryCreateCmd cmd) {
        CategoryVO categoryVO = categoryCreateCmdExe.execute(cmd);
        return SingleResponse.of(categoryVO);
    }

    @Override
    public SingleResponse<CategoryVO> modify(CategoryModifyCmd cmd) {
        CategoryVO categoryVO = categoryModifyCmdExe.execute(cmd);
        return SingleResponse.of(categoryVO);
    }

    @Override
    public SingleResponse<Boolean> delete(Long id) {
        Boolean result = categoryDeleteCmdExe.execute(id);
        return SingleResponse.of(result);
    }
}
