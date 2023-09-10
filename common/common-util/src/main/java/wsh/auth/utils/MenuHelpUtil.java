package wsh.auth.utils;


import wsh.auth.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelpUtil {

    public static List<SysMenu> buildTree(List<SysMenu> sysMenus) {
        // 创建集合,用于最终数据
        List<SysMenu> trees = new ArrayList<>();
        // 遍历从表中得到的数据
        for (SysMenu sysMenu : sysMenus) {
            // 递归入口  parentId = 0
            if (sysMenu.getParentId().longValue() == 0){
                trees.add(getChildren(sysMenu,sysMenus));
            }
        }
        return trees;
    }

    private static SysMenu getChildren(SysMenu sysMenu, List<SysMenu> sysMenus) {
        sysMenu.setChildren(new ArrayList<>());
        // 遍历所有的菜单数据，判断id和parentId的关系
        for (SysMenu item : sysMenus) {
            if (sysMenu.getId().longValue() == item.getParentId().longValue()){
                if (item.getParentId().longValue() == 0){
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(getChildren(item,sysMenus));
            }
        }
        return sysMenu;
    }
}
