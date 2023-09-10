package wsh.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import wsh.auth.model.system.SysUserRole;

/**
 * <p>
 * 用户角色 Mapper 接口
 * </p>
 *
 * @author wsh
 * @since 2023-08-24
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

}
