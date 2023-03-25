package cn.eyeo.mall.user.command.query;

import cn.eyeo.mall.user.assembler.UserAssembler;
import com.alibaba.cola.dto.MultiResponse;
import cn.eyeo.mall.dto.data.UserVO;
import cn.eyeo.mall.dto.query.UserListByParamQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserListByNameQueryExe
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/10
 */
@Component
public class UserListByParamQueryExe {

//    @Resource
//    private UserGateway userGateway;
//
//    public MultiResponse<UserVO> execute(UserListByParamQuery query) {
//        List<UserEntity> userEntities = userGateway.findByParam(query);
//        List<UserVO> userVOList = userEntities.stream()
//                .map(UserAssembler::toValueObject)
//                .collect(Collectors.toList());
//
//        return MultiResponse.of(userVOList);
//    }

}
