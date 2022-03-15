package com.dyw.util.Jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Devil
 * @create 2022-03-14 19:02
 */
@Component
@ConfigurationProperties(prefix = "jwt.util")
public class JwtUtil {
    /**
     * 默认3600秒过期时间
     */
    private static long tokenExpiredTime = 60*60*1000;
    /**
     * 默认jwt id
     */
    private static String JWT_ID = "tokenId";
    /**
     * 默认的KEY_SECRET_SALT
     */
    private static String KEY_SECRET_SALT = "aPbOBbnH4gnZBzIKEY7mxWNu49kYljNPMeta9Fjrwwqzw0bFlO0kPXZTCGaVcw0jaq";

    /**
     * 由key明文生成的SecretKey
     *
     * @return SecretKey
     */
    public static SecretKey generalKey() {
        String secretSalt = JwtUtil.KEY_SECRET_SALT;
        byte[] decode = Decoders.BASE64.decode(secretSalt);
        return Keys.hmacShaKeyFor(decode);
    }

    /**
     * 创建jwt
     *
     * @param map  负载参数
     * @param time 过期时间
     * @return jws(String)
     */
    public static String createJwt(Map<String, Object> map, Long time) {
        //签发时间时间
        Date now = new Date(System.currentTimeMillis());
        //指定的签名的加密算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //获取由key明文生成的SecretKey
        SecretKey secretKey = generalKey();
        JwtBuilder builder = Jwts.builder()
                //设置JWT
                .setId(JWT_ID)
                //设置负载
                .setClaims(map)
                //签发时间
                .setIssuedAt(now)
                //使用指定算法进行签名加密
                .signWith(secretKey, signatureAlgorithm);
        //生成jwt的时间
        long nowMillis = System.currentTimeMillis();
        if (time >= 0) {
            Date date = new Date(nowMillis + time);
            builder.setExpiration(date);
        }
        return builder.compact();
    }

    public static Claims verifyJwt(String token) {
        //签名密钥, 同一密钥明文生成的密钥相同
        SecretKey secretKey = generalKey();
        JwtParser build = Jwts.parserBuilder().setSigningKey(secretKey).build();
        return build.parseClaimsJws(token).getBody();
    }

    /**
     * @param id 业务数据库中表中的id
     * @return String 返回jws
     */
    public static String generateToken(String id) {
        HashMap<String, Object> map = new HashMap<>(5);
        map.put("id", id);
        return createJwt(map, tokenExpiredTime);
    }

    /**
     * @param id 业务数据库中表中的id
     * @return String 返回jws
     */
    public static String generateToken(Integer id) {
        HashMap<String, Object> map = new HashMap<>(5);
        map.put("id", id);
        return createJwt(map, tokenExpiredTime);
    }

    public static long getTokenExpiredTime() {
        return tokenExpiredTime;
    }

    public static void setTokenExpiredTime(long tokenExpiredTime) {
        JwtUtil.tokenExpiredTime = tokenExpiredTime;
    }

    public static String getJwtId() {
        return JWT_ID;
    }

    public static void setJwtId(String jwtId) {
        JWT_ID = jwtId;
    }

    public static String getKeySecretSalt() {
        return KEY_SECRET_SALT;
    }

    public static void setKeySecretSalt(String keySecretSalt) {
        KEY_SECRET_SALT = keySecretSalt;
    }

//    /**
//     * 如果不愿使用默认的jwt相关参数 可以使用init方法修改
//     *
//     * @param tokenExpiredTime 指定的过期时间
//     * @param jwtId            指定的jwtId
//     * @param secretSalt       指定的加密盐
//     */
//    public static void init(long tokenExpiredTime, String jwtId, String secretSalt) {
//        JwtUtil.tokenExpiredTime = tokenExpiredTime;
//        JwtUtil.JWT_ID = jwtId;
//        JwtUtil.KEY_SECRET_SALT = secretSalt;
//    }
}
