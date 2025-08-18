package com.mashang.yunbac.web.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mashang.yunbac.web.entity.domian.YunUser;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@Component
public class JWTUtil {
    // 加密的秘钥封装一下
    private static final String SECRET = "secret";
    // id字段
    private static final String ID_FIELD = "userID";
    // token的有效时间 2 天
    private static final Integer TIME_OUT_DAY = 2;

    // 存储失效的 Token（实际项目建议用 Redis）
    private static Set<String> tokenBlacklist = new HashSet<>();

    /**
     * 使 Token 失效（加入黑名单）
     */
    public static Boolean invalidateToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        boolean add = tokenBlacklist.add(token);
        if (add) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建token
     *
     * @param user 登陆的用户
     * @return 返回Token字符串
     */
    public static String createToken(YunUser user) {
        // 获取日历对象实例
        Calendar calendar = Calendar.getInstance();
        // 在当前日期加上 TIME_OUT_DAY 的时间，用于设置过期时间
        calendar.add(Calendar.DATE, TIME_OUT_DAY);
        System.out.println(user.getUserId());
        // 创建jwt
        String token = JWT.create()
                // 可以在token中设置数据,设置一个userId为用户的id
                // 后续可以直接在token中获取id
                .withClaim(ID_FIELD, user.getUserId())
                // 设置token过期时间
                .withExpiresAt(calendar.getTime())
                // Algorithm.HMAC256(SECRET) 使用HMAC256的加密方式
                // secret 指的是秘钥，在这个秘钥的基础上，进行加密，加大破解的难度这个秘钥爱写什么写什么
                .sign(Algorithm.HMAC256(SECRET));
        return "Bearer " + token;
    }

    /**
     * 验证JWT，返回为false的时候表示验证失败
     *
     * @param token token字符串
     * @return 返回boolean 表示是否登录成功
     */
    public static boolean verifyToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            // 验证JWT，验证不通过会报错
            JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取用户id，返回值是0表示没有找到id
     *
     * @param token token 字符串
     * @return 返回对应的用户id，如果为0则表示没有用户
     */
    public static Long getUserId(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            // 获取id，没有id则会报错
            return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token).getClaim(ID_FIELD).asLong();
        } catch (Exception e) {
            // 如果报错就返回null表示没有找到对应的用户
            return 0l;
        }
    }

    /**
     * 获取token
     *
     * @return
     */
    public static String getTokenFromHeader() {
        // 获取当前请求属性
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        // 从请求头中获取token
        HttpServletRequest request = requestAttributes.getRequest();
        String token = (String) request.getAttribute("Authorization");
//        return Integer.valueOf(request.getHeader("token")) ;
        return token;
    }
}
