package wsh.auth.utils;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 工具类：用于生成jwt令牌
 */
public class JwtHelpUtil {

    // 令牌有效期
    private static long tokenExpiration = 365 * 24 * 60 * 60 * 1000;
    // 签名加密秘钥
    private static String tokenSignKey = "123456";

    // 根据用户ID和用户名称生成令牌
    public static String createToken(Long userId, String username) {
        String token = Jwts.builder()
                // 分类
                .setSubject("AUTH-USER")
                // 设置token有效时长
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                // 主体部分
                .claim("userId", userId)
                .claim("username", username)
                // 签名部分
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    // 从生成的token中获取用户ID
    public static Long getUserId(String token) {
        try {
            if (StringUtils.isEmpty(token)) return null;

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            Integer userId = (Integer) claims.get("userId");
            return userId.longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 从生成的token中获取用户名称
    public static String getUsername(String token) {
        try {
            if (StringUtils.isEmpty(token)) return "";

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return (String) claims.get("username");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static void main(String[] args) {
//        String token = JwtHelpUtil.createToken(1L, "admin");
//        System.out.println(token);
//        System.out.println(JwtHelpUtil.getUserId(token));
//        System.out.println(JwtHelpUtil.getUsername(token));
//    }
}
