package cn.eyeo.mall.start.test;

import cn.eyeo.mall.client.product.api.ICategoryOpsService;
import cn.eyeo.mall.client.product.api.IProductOpsService;
import cn.eyeo.mall.client.product.dto.CategoryCreateCmd;
import cn.eyeo.mall.client.product.dto.CategoryModifyCmd;
import cn.eyeo.mall.client.product.dto.ProductCreateCmd;
import cn.eyeo.mall.client.product.dto.ProductModifyCmd;
import cn.eyeo.mall.client.product.dto.data.CategoryVO;
import cn.eyeo.mall.client.product.dto.data.ProductVO;
import cn.eyeo.mall.client.product.dto.query.ProductPageQuery;
import cn.eyeo.mall.start.Application;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * ProductServiceTest
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2023/3/26
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductServiceTest {

    @Autowired
    private ICategoryOpsService iCategoryOpsService;
    @Autowired
    private IProductOpsService iProductOpsService;

    private static final String categoryName = "Category_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    private static final String productName = "Product_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    private static Long categoryId = null;
    private static Long productId = null;

    @Before
    public void setUp() {
        log.info("test categoryName is [" + categoryName + "]");
        log.info("test productName is [" + productName + "]");
    }

    @Test
    public void category_1_create() {
        //1.prepare
        CategoryCreateCmd createCmd = new CategoryCreateCmd();
        createCmd.setName(categoryName);
        createCmd.setParentId(null);
        createCmd.setDescription("This is category description!");

        //2.execute
        SingleResponse<CategoryVO> response = iCategoryOpsService.create(createCmd);

        //3.assert
        Assert.assertTrue(Objects.nonNull(response.getData().getId()));

        categoryId = response.getData().getId();
    }

    @Test
    public void category_2_modify() {
        //1.prepare
        String toBeUpdateName = categoryName + "_update";

        CategoryModifyCmd modifyCmd = new CategoryModifyCmd();
        modifyCmd.setId(categoryId);
        modifyCmd.setName(toBeUpdateName);
        modifyCmd.setParentId(null);
        modifyCmd.setDescription("This is category description, for update!");

        //2.execute
        SingleResponse<CategoryVO> response = iCategoryOpsService.modify(modifyCmd);

        //3.assert
        CategoryVO categoryVO = response.getData();
        Assert.assertTrue(Objects.nonNull(categoryVO.getId()));
        Assert.assertEquals(categoryVO.getName(), toBeUpdateName);
    }

    @Test
    public void product_1_create() {
        //1.prepare
        ProductCreateCmd createCmd = new ProductCreateCmd();
        createCmd.setName(productName);
        createCmd.setImage("https://eyeo.cn/images/home/creativity.png");
        createCmd.setPrice(new BigDecimal("100"));
        createCmd.setStock(20L);
        createCmd.setStatus(1);
        createCmd.setCategoryId(categoryId);
        createCmd.setDescription("This is product description!");

        //2.execute
        SingleResponse<ProductVO> response = iProductOpsService.create(createCmd);

        //3.assert
        Assert.assertTrue(Objects.nonNull(response.getData().getId()));

        productId = response.getData().getId();
    }

    @Test
    public void product_2_modify() {
        //1.prepare
        String toBeUpdateName = productName + "_update";

        ProductModifyCmd createCmd = new ProductModifyCmd();
        createCmd.setId(productId);
        createCmd.setName(toBeUpdateName);
        createCmd.setImage("https://eyeo.cn/images/home/creativity.png");
        createCmd.setPrice(new BigDecimal("200"));
        createCmd.setStock(30L);
        createCmd.setCategoryId(categoryId);
        createCmd.setDescription("This is product description, for update!");

        //2.execute
        SingleResponse<ProductVO> response = iProductOpsService.modify(createCmd);

        //3.assert
        ProductVO productVO = response.getData();
        Assert.assertTrue(Objects.nonNull(productVO.getId()));
        Assert.assertEquals(productVO.getName(), toBeUpdateName);
    }

    @Test
    public void product_3_getById() {
        //1.prepare

        //2.execute
        SingleResponse<ProductVO> response = iProductOpsService.getProductById(productId);

        //3.assert
        ProductVO productVO = response.getData();
        Assert.assertTrue(Objects.nonNull(productVO.getId()));
    }

    @Test
    public void product_4_pageQuery() {
        //1.prepare
        ProductPageQuery pageQuery = new ProductPageQuery();
        pageQuery.setCategoryId(categoryId);
        pageQuery.setPageIndex(1);
        pageQuery.setPageSize(10);
        pageQuery.setOrderBy("gmt_create");
        pageQuery.setOrderDirection("desc");

        //2.execute
        PageResponse<ProductVO> response = iProductOpsService.pageQuery(pageQuery);

        //3.assert
        List<ProductVO> data = response.getData();
        int totalCount = response.getTotalCount();
        int totalPages = response.getTotalPages();
        Assert.assertFalse(CollectionUtils.isEmpty(data));
        Assert.assertTrue(totalCount > 0);
        Assert.assertTrue(totalPages > 0);
    }

}
