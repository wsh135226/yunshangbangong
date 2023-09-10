package wsh.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import wsh.auth.model.system.SysMenu;
import wsh.auth.vo.system.AssginMenuVo;
import wsh.auth.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author wsh
 * @since 2023-08-24
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    void removeMenuById(Long id);

    List<SysMenu> findSysMenuByRoleId(Long roleId);

    void doAssign(AssginMenuVo assignMenuVo);

    List<RouterVo> findUserMenuList(Long userId);

    List<String> findUserPermsList(Long userId);
}
