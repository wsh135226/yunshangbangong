package wsh.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import wsh.auth.exception.CustomException;
import wsh.auth.model.system.SysUser;
import wsh.auth.result.Result;
import wsh.auth.service.SysMenuService;
import wsh.auth.service.SysUserService;
import wsh.auth.utils.JwtHelpUtil;
import wsh.auth.vo.system.LoginVo;
import wsh.auth.vo.system.RouterVo;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/system/index")
@Api(tags = "后台登录功能")
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @PostMapping("/login")
    @ApiOperation("登录功能(login)")
    public Result login(@RequestBody LoginVo loginVo){
        // 响应结果{"code":200,"data":{"token":"admin-token"}}
//        Map<String,Object> map = new HashMap<>();
//        map.put("token","admin-token");

        // 1.获取用户输入的用户名和密码
        String username = loginVo.getUsername();
        String password = loginVo.getPassword();
        // 2.根据用户名获取数据库中的用户信息
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = sysUserService.getOne(lambdaQueryWrapper);
        // 3.判断用户是否存在
        if (sysUser == null){
            throw new CustomException(201,"用户不存在");
        }
        // 4.判断密码是否正确
        // 加密后的密码
        String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        //判断
        if (!pwd.equals(sysUser.getPassword())){
            throw new CustomException(201,"密码不正确");
        }
        // 5.判断用户是否被禁用
        if (sysUser.getStatus().intValue() != 1){
            throw new CustomException(201,"用户被禁用");
        }
        // 6.生成jwt令牌
        String token = JwtHelpUtil.createToken(sysUser.getId(), sysUser.getUsername());
        // 7.返回用户信息，登录成功
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }


    @GetMapping("/info")
    @ApiOperation("登录功能(info)")
    public Result info(HttpServletRequest request){
        // 1.从请求头中获取用户信息（获取请求头中的token信息）
        String token = request.getHeader("token");

        // 2.从token中获取用户ID或者用户名称
        Long userId =  JwtHelpUtil.getUserId(token);

        // 3.根据用户ID查询用户数据
        SysUser user = sysUserService.getById(userId);
        // 4.根据用户ID查询到用户可以操作的菜单权限
        // 查询数据库动态构建路由结构，进行显示
        List<RouterVo> routerList = sysMenuService.findUserMenuList(userId);

        // 5.根据用户ID查询到用户可以操作的按钮权限
        List<String> permsList = sysMenuService.findUserPermsList(userId);
        // 6.返回相关数据
        Map<String,Object> map = new HashMap<>();
        map.put("roles",user.getName());
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("name","admin");
        map.put("buttons", permsList);
        map.put("routers", routerList);
        return Result.ok(map);
    }

    @PostMapping("/logout")
    @ApiOperation("退出功能")
    public Result logout(){
        return Result.ok();
    }


}
