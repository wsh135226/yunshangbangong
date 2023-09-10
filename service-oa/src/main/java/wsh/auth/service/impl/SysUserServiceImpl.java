package wsh.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import wsh.auth.exception.CustomException;
import wsh.auth.mapper.SysUserMapper;
import wsh.auth.model.system.SysUser;
import wsh.auth.result.ResultCodeEnum;
import wsh.auth.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wsh
 * @since 2023-08-21
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Transactional
    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser sysUser = this.getById(id);

        if(status.intValue() == 1) {
            sysUser.setStatus(status);
        } else if (status.intValue() == 0){
            sysUser.setStatus(0);
        }else {
            throw new CustomException(ResultCodeEnum.FAIL.getCode(), "非法状态");
        }
        this.updateById(sysUser);
    }

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = getOne(lambdaQueryWrapper);
        return sysUser;
    }
}
