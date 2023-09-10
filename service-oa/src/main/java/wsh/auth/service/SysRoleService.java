package wsh.auth.service;

import wsh.auth.model.system.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import wsh.auth.vo.system.AssginRoleVo;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {
    Map<String, Object> findRoleByAdminId(Long userId);

    void doAssign(AssginRoleVo assginRoleVo);
}
