package wsh.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import wsh.auth.exception.CustomException;
import wsh.auth.mapper.SysMenuMapper;
import wsh.auth.model.system.SysMenu;
import wsh.auth.model.system.SysRoleMenu;
import wsh.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import wsh.auth.service.SysRoleMenuService;
import wsh.auth.utils.MenuHelpUtil;
import wsh.auth.vo.system.AssginMenuVo;
import wsh.auth.vo.system.MetaVo;
import wsh.auth.vo.system.RouterVo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author wsh
 * @since 2023-08-24
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenu> findNodes() {
        // 1.查询菜单表所有数据
        List<SysMenu> sysMenus = baseMapper.selectList(null);
        // 2.构建树状结构
        /*{
            第一层{
            children:[

                {
                    第二层...
                }
             ]
        }*/
        List<SysMenu> resultList = MenuHelpUtil.buildTree(sysMenus);
        return resultList;
    }


    @Override
    public void removeMenuById(Long id) {
        // 判断是否有子级菜单
        LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysMenu::getParentId,id);
        Integer count = baseMapper.selectCount(lambdaQueryWrapper);
        if (count > 0){
            throw new CustomException(201,"无法删除此菜单");
        }
        baseMapper.deleteById(id);
    }


    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        // 1.查询所有可用(status=1)的菜单信息
        LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysMenu::getStatus,1);
        List<SysMenu> allSysMenuList  = list(lambdaQueryWrapper);

        // 2.根据角色ID查询角色菜单表中角色id对应的菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> sysRoleMenuList  = sysRoleMenuService.list(wrapper);
        List<Long> menuIdList  = sysRoleMenuList.stream().map(item -> item.getMenuId()).collect(Collectors.toList());

        // 3.将得到的菜单ID，获取对应的菜单对象
        for (SysMenu sysMenu : allSysMenuList) {
            // 3.1 拿着菜单ID与所有的菜单集合里面的ID进行比较，相同则进行封装
            if (menuIdList.contains(sysMenu.getId())){
                sysMenu.setSelect(true);
            }else {
                sysMenu.setSelect(false);
            }
        }

        // 4.返回树形显示格式
        List<SysMenu> sysMenuList  = MenuHelpUtil.buildTree(allSysMenuList);
        return sysMenuList ;
    }

    @Override
    @Transactional
    public void doAssign(AssginMenuVo assignMenuVo) {
        // 1.根据角色ID，删除菜单角色表中的分配信息
        Long roleId = assignMenuVo.getRoleId();
        LambdaQueryWrapper<SysRoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysRoleMenu::getRoleId,roleId);
        sysRoleMenuService.remove(lambdaQueryWrapper);
        // 2.将重新分配的数据进行添加
        for (Long menuId : assignMenuVo.getMenuIdList()) {
            if (StringUtils.isEmpty(menuId)){
                continue;
            }
            SysRoleMenu rolePermission = new SysRoleMenu();
            rolePermission.setRoleId(assignMenuVo.getRoleId());
            rolePermission.setMenuId(menuId);
            sysRoleMenuService.save(rolePermission);
        }
    }

    @Override
    public List<RouterVo> findUserMenuList(Long userId) {
//        List<SysMenu> sysMenuList = null;
//        // 1.判断用户是否是管理员
//        if (userId.intValue() == 1){
//            // 1.1如果是管理员，则拥有所有权限，可以查看所有的菜单列表
//            LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            // 可用菜单
//            lambdaQueryWrapper.eq(SysMenu::getStatus,1);
//            lambdaQueryWrapper.orderByAsc(SysMenu::getSortValue);
//            sysMenuList = baseMapper.selectList(lambdaQueryWrapper);
//        }
//        // 1.2如果不是管理员,则查询可以查看的菜单列表
//        // 多表查询：用户角色表、菜单表、角色菜单表
//        sysMenuList = baseMapper.findListByUserId(userId);
//
//        // 3.将查询出的结果构建成前端路由所需要的框架
//        List<SysMenu> sysMenuTreeList = MenuHelpUtil.buildTree(sysMenuList);
//        // 构建成框架所需要的路由结构
//        List<RouterVo> routerVoList = this.buildRouter(sysMenuTreeList);
//        // 4.返回数据
//        return routerVoList;

        //超级管理员admin账号id为：1
        List<SysMenu> sysMenuList = null;
        if (userId.longValue() == 1) {
            sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1).orderByAsc(SysMenu::getSortValue));
        } else {
            sysMenuList = baseMapper.findListByUserId(userId);
        }
        //构建树形数据
        List<SysMenu> sysMenuTreeList = MenuHelpUtil.buildTree(sysMenuList);

        List<RouterVo> routerVoList = this.buildRouter(sysMenuTreeList);
        return routerVoList;
    }

    /**
     * 根据菜单构建路由
     * @param menus
     * @return
     */
    private List<RouterVo> buildRouter(List<SysMenu> menus) {
        // 创建list数据，用于存储最终结构
        List<RouterVo> routers = new ArrayList<>();
        // 遍历
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            // 下一层数据
            List<SysMenu> children = menu.getChildren();
            //如果当前是菜单，需将按钮对应的路由加载出来，如：“角色授权”按钮对应的路由在“系统管理”下面
            if (menu.getType().intValue() == 1){
                // 加载出隐藏路由
                List<SysMenu> hiddenMenuList = children.stream()
                        .filter(item -> !StringUtils.isEmpty(item.getComponent()))// 将不为空的数据提取封装成list集合(即有隐藏路由)
                        .collect(Collectors.toList());

                // hidden值为true，表示隐藏路由
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            }else {
                if (!CollectionUtils.isEmpty(children)) {
                    if(children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    // 递归
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        return routers;
//        List<RouterVo> routers = new LinkedList<RouterVo>();
//        for (SysMenu menu : menus) {
//            RouterVo router = new RouterVo();
//            router.setHidden(false);
//            router.setAlwaysShow(false);
//            router.setPath(getRouterPath(menu));
//            router.setComponent(menu.getComponent());
//            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
//            List<SysMenu> children = menu.getChildren();
//            //如果当前是菜单，需将按钮对应的路由加载出来，如：“角色授权”按钮对应的路由在“系统管理”下面
//            if(menu.getType().intValue() == 1) {
//                List<SysMenu> hiddenMenuList = children.stream().filter(item -> !StringUtils.isEmpty(item.getComponent())).collect(Collectors.toList());
//                for (SysMenu hiddenMenu : hiddenMenuList) {
//                    RouterVo hiddenRouter = new RouterVo();
//                    hiddenRouter.setHidden(true);
//                    hiddenRouter.setAlwaysShow(false);
//                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
//                    hiddenRouter.setComponent(hiddenMenu.getComponent());
//                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
//                    routers.add(hiddenRouter);
//                }
//            } else {
//                if (!CollectionUtils.isEmpty(children)) {
//                    if(children.size() > 0) {
//                        router.setAlwaysShow(true);
//                    }
//                    router.setChildren(buildRouter(children));
//                }
//            }
//            routers.add(router);
//        }
//        return routers;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    @Override
    public List<String> findUserPermsList(Long userId) {
        //超级管理员admin账号id为：1
        List<SysMenu> sysMenuList = null;
        // 1.判断用户是否是管理员
        if (userId.longValue() == 1) {
            // 1.1如果是管理员，则拥有所有权限，可以查看所有的菜单列表
            sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));
        } else {
            // 1.2如果不是管理员,则查询可以查看的菜单列表
            // 多表查询：用户角色表、菜单表、角色菜单表
            sysMenuList = baseMapper.findListByUserId(userId);
        }
        // 3.将查询出的结果,获取成可以操作按钮值的list集合，返回前端
        List<String> permsList = sysMenuList.stream().filter(item -> item.getType() == 2).map(item -> item.getPerms()).collect(Collectors.toList());
        return permsList;
    }


}
