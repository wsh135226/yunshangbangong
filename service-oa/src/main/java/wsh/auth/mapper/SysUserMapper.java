package wsh.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import wsh.auth.model.system.SysUser;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author wsh
 * @since 2023-08-21
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
