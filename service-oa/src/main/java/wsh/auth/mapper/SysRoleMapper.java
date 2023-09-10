package wsh.auth.mapper;

import wsh.auth.model.system.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
