package wsh.auth.exception;

import lombok.Data;

/**
 * 自定义异常类
 */
@Data
public class CustomException extends RuntimeException{

    private Integer code; // 错误编码
    private String msg; // 错误信息

    public CustomException(Integer code,String msg){
        super(msg);
        this.code = code;
    }
}
