package wsh.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import wsh.auth.model.system.SysUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wsh
 * @since 2023-08-21
 */
public interface SysUserService extends IService<SysUser> {

    void updateStatus(Long id, Integer status);

    SysUser getByUsername(String username);
}
