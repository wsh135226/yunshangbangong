package wsh.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import nonapi.io.github.classgraph.utils.LogNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import wsh.auth.model.system.SysRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import wsh.auth.result.Result;
import wsh.auth.service.SysRoleService;
import wsh.auth.vo.system.AssginRoleVo;
import wsh.auth.vo.system.SysRoleQueryVo;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/admin/system/sysRole")
@Slf4j
@Api(tags = "角色管理")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;


    /**
     * 查询所有角色 和 当前用户所属角色
     * @param userId
     * @return
     */
    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String, Object> roleMap = sysRoleService.findRoleByAdminId(userId);
        return Result.ok(roleMap);
    }

    /**
     * 为用户分配角色
     * @param assginRoleVo
     * @return
     */
    @ApiOperation(value = "根据用户分配角色")
    @PostMapping("doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }





    @ApiOperation("查询角色所有信息")
    @GetMapping("/findAll")
    public Result<List<SysRole>> getList() {
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }

    /**
     * page:当前页
     * pageSize:每页显示记录数
     * sysRoleQueryVo:条件对象
     *
     * @return
     */
    @GetMapping("/{page}/{limit}")
    @ApiOperation("角色分页查询")
    public Result page(@PathVariable Long page,
                       @PathVariable Long limit,
                       SysRoleQueryVo sysRoleQueryVo) {
        // 创建page对象
        Page<SysRole> pageInfo = new Page<>(page, limit);
        // 创建查询对象
        LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询条件
        String roleName = sysRoleQueryVo.getRoleName();
        // 不是删除状态
        lambdaQueryWrapper.eq(SysRole::getIsDeleted, 0);
        // 搜索：模糊查询
        lambdaQueryWrapper.like(!StringUtils.isEmpty(roleName), SysRole::getRoleName, roleName);
        // 分页查询
        sysRoleService.page(pageInfo, lambdaQueryWrapper);
        // 返回结果
        return Result.ok(pageInfo);
    }

    @PostMapping("/save")
    @ApiOperation("添加角色")
    public Result save(@RequestBody SysRole sysRole) {
        long random = getRandom();
        sysRole.setId(random);
        boolean save = sysRoleService.save(sysRole);
        if (save == false) {
            return Result.fail();
        }
        return Result.ok("添加角色成功");
    }

    @GetMapping("get/{id}")
    @ApiOperation("根据id获取角色数据")
    public Result getRoleById(@PathVariable Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        if (sysRole != null) {
            return Result.ok(sysRole);
        }
        return Result.fail("角色不存在");
    }

    @PutMapping("/update")
    @ApiOperation("修改角色")
    public Result update(@RequestBody SysRole sysRole) {
        boolean update = sysRoleService.updateById(sysRole);
        if (update == false) {
            return Result.fail();
        }
        return Result.ok("修改角色成功");
    }

    @DeleteMapping("/remove/{id}")
    @ApiOperation("根据id删除角色")
    public Result removeById(@PathVariable Long id) {
        boolean remove = sysRoleService.removeById(id);
        if (remove == false) {
            return Result.fail("删除角色失败");
        }
        return Result.ok("删除角色成功");
    }

    @DeleteMapping("/batchRemove")
    @ApiOperation("批量删除")
    public Result batchRemove(@RequestBody List<Long> idList){
        boolean b = sysRoleService.removeByIds(idList);
        if (b == false) {
            return Result.fail("删除角色失败");
        }
        return Result.ok("删除角色成功");
    }

    // 生成随机数
    public static long getRandom() {
        // 随机生成六位数随机数
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            Random random = new Random();
            int ran = random.nextInt(10);
            sb.append(ran);
        }
        String s = sb.toString();
        int i = Integer.parseInt(s);
        long l = i;
        return l;
    }

}
