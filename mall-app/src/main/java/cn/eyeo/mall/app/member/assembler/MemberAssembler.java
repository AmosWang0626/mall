package cn.eyeo.mall.app.member.assembler;

import cn.eyeo.mall.client.member.dto.MemberModifyCmd;
import cn.eyeo.mall.client.member.dto.MemberRegisterCmd;
import cn.eyeo.mall.client.member.dto.data.MemberVO;
import cn.eyeo.mall.client.member.dto.data.RoleVO;
import cn.eyeo.mall.gateway.impl.member.database.dataobject.MemberDO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Member Application层 转换器
 * 用于DTO与实体之间的互转
 *
 * @author <a href="mailto:daoyuan0626@gmail.com">amos.wang</a>
 * @date 2021/11/27
 */
public class MemberAssembler {

    public static MemberDO toDO(MemberRegisterCmd cmd) {
        MemberDO memberDO = new MemberDO();
        memberDO.setUsername(cmd.getUsername());
        memberDO.setPassword(cmd.getPassword());
        memberDO.setNickname(cmd.getNickname());
        memberDO.setAvatar(cmd.getAvatar());
        memberDO.setEmail(cmd.getEmail());
        memberDO.setMobile(cmd.getMobile());
        memberDO.setGender(cmd.getGender());
        memberDO.setBirthday(cmd.getBirthday());
        memberDO.setStatus(1);
        memberDO.setGmtCreate(LocalDateTime.now());
        memberDO.setGmtModified(LocalDateTime.now());
        return memberDO;
    }

    public static MemberDO toDO(MemberModifyCmd cmd) {
        MemberDO memberDO = new MemberDO();
        memberDO.setId(cmd.getUserId());
        memberDO.setUsername(cmd.getUsername());
        memberDO.setNickname(cmd.getNickname());
        memberDO.setAvatar(cmd.getAvatar());
        memberDO.setEmail(cmd.getEmail());
        memberDO.setMobile(cmd.getMobile());
        memberDO.setGender(cmd.getGender());
        memberDO.setBirthday(cmd.getBirthday());
        memberDO.setGmtModified(LocalDateTime.now());
        return memberDO;
    }

    public static MemberVO toValueObject(MemberDO memberDO) {
        MemberVO memberVO = new MemberVO();
        memberVO.setUserId(memberDO.getId());
        memberVO.setUsername(memberDO.getUsername());
        memberVO.setNickname(memberDO.getNickname());
        memberVO.setAvatar(memberDO.getAvatar());
        memberVO.setEmail(memberDO.getEmail());
        memberVO.setMobile(memberDO.getMobile());
        memberVO.setGender(memberDO.getGender());
        memberVO.setBirthday(memberDO.getBirthday());

        // 临时拓展信息
        memberVO.setRealName(memberDO.getUsername());
        memberVO.setDesc("manager");
        // FIXME 暂时使用用户ID作为TOKEN
        memberVO.setToken(String.valueOf(memberVO.getUserId()));
        memberVO.setHomePath("/dashboard/analysis");

        RoleVO roleVO = new RoleVO();
        roleVO.setRoleName("Super Admin");
        roleVO.setValue("super");

        List<RoleVO> roles = new ArrayList<>();
        roles.add(roleVO);
        memberVO.setRoles(roles);

        return memberVO;
    }

}
