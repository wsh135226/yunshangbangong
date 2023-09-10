package wsh.auth.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import wsh.auth.model.system.SysUser;
import wsh.auth.result.Result;
import wsh.auth.service.SysUserService;
import wsh.auth.vo.system.SysUserQueryVo;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wsh
 * @since 2023-08-21
 */
@RestController
@RequestMapping("/admin/system/sysUser")
@Api(tags = "用户管理")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;


    @ApiOperation(value = "更新用户状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        sysUserService.updateStatus(id, status);
        return Result.ok();
    }

    @GetMapping("{page}/{limit}")
    @ApiOperation("分页查询所有")
    public Result page(@PathVariable Long page,
                       @PathVariable Long limit,
                       SysUserQueryVo sysUserQueryVo) {

        // 创建分页对象
        Page<SysUser> pageInfo = new Page<>(page,limit);

        // 创建条件构造器
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 封装条件
        // 1.根据关键字（用户名）搜索
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(sysUserQueryVo.getKeyword()),
                SysUser::getUsername, sysUserQueryVo.getKeyword());

        // 2.大于等于创建时间
        lambdaQueryWrapper.ge(StringUtils.isNotEmpty(sysUserQueryVo.getCreateTimeBegin()),
                SysUser::getCreateTime, sysUserQueryVo.getCreateTimeBegin());

        // 3.小于等于结束时间
        lambdaQueryWrapper.le(StringUtils.isNotEmpty(sysUserQueryVo.getCreateTimeEnd()),
                SysUser::getCreateTime, sysUserQueryVo.getCreateTimeEnd());

        sysUserService.page(pageInfo, lambdaQueryWrapper);

        // 返回数据
        return Result.ok(pageInfo);
    }

    @ApiOperation(value = "获取用户")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation(value = "保存用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser user) {
        // 将密码进行加密
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(password);
        sysUserService.save(user);
        return Result.ok();
    }

    @ApiOperation(value = "更新用户")
    @PutMapping("update")
    public Result updateById(@RequestBody SysUser user) {
        sysUserService.updateById(user);
        return Result.ok();
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.ok();
    }
}

