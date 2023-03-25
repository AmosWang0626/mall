package cn.eyeo.mall.user.command.query;

import org.springframework.stereotype.Component;

/**
 * UserListByNameQueryExe
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/1/10
 */
@Component
public class UserLoginQueryExe {

//    @Resource
//    private UserGateway userGateway;
//
//    public void execute(UserLoginQuery query) {
//        UserEntity userEntity = userGateway.findPasswordInfo(query.getUsername());
//        if (Objects.isNull(userEntity)) {
//            throw new MallBizException(ErrorCode.B_USER_PASSWORD_ERROR);
//        }
//
//        // 校验密码是否正确
//        if (!userEntity.getPassword().isCorrect(query.getPassword())) {
//            throw new MallBizException(ErrorCode.B_USER_PASSWORD_ERROR);
//        }
//    }

}
