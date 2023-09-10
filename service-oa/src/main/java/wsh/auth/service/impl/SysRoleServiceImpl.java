package wsh.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import wsh.auth.model.system.SysRole;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wsh.auth.mapper.SysRoleMapper;
import wsh.auth.model.system.SysUserRole;
import wsh.auth.service.SysRoleService;
import wsh.auth.service.SysUserRoleService;
import wsh.auth.vo.system.AssginRoleVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleService userRoleService;

    @Override
    public Map<String, Object> findRoleByAdminId(Long userId) {
        // 1.查询所有角色，并返回list集合
        List<SysRole> allRolesList = baseMapper.selectList(null);

        // 2.根据用户ID，查询角色用户关系表，查询到用户对应的角色信息
        LambdaQueryWrapper<SysUserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUserRole::getUserId,userId);
        // 得到角色用户关系表信息
        List<SysUserRole> existUserRoleList   = userRoleService.list(lambdaQueryWrapper);

        // 3.获取角色用户关系表中的所有角色ID
        List<Long> existRoleIdList  = existUserRoleList .stream().map(roles -> roles.getRoleId()).collect(Collectors.toList());

        // 4.通过角色ID获取对应的角色信息
        List<SysRole> assginRoleList = new ArrayList<>();

        for (SysRole sysRole : allRolesList) {
            if (existRoleIdList .contains(sysRole.getId())){
                assginRoleList.add(sysRole);
            }
        }

        // 4.合并数据并返回
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("allRolesList", allRolesList);
        roleMap.put("assginRoleList", assginRoleList);

        return roleMap;

    }

    @Override
    @Transactional // 添加事务，防止数据不统一
    public void doAssign(AssginRoleVo assginRoleVo) {
        // 根据用户ID把用户之前分配的角色数据删除，再进行添加
        LambdaQueryWrapper<SysUserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUserRole::getUserId,assginRoleVo.getUserId());
        userRoleService.remove(lambdaQueryWrapper);

        // 重新添加分配
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        for (Long roleId : roleIdList) {
            if (StringUtils.isEmpty(roleId)){
                continue;
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assginRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            Assert.isTrue(userRoleService.save(sysUserRole),"添加失败");
        }
    }
}
