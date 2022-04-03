package com.dyw.util.Jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Devil
 * @create 2022-03-14 19:02
 */

@Component
@Data
public class JwtUtil {
    public static long getTokenExpiredTime() {
        return tokenExpiredTime;
    }

    public static void setTokenExpiredTime(long tokenExpiredTime) {
        JwtUtil.tokenExpiredTime = tokenExpiredTime;
    }

    public static String getJwtId() {
        return jwtId;
    }

    public static void setJwtId(String jwtId) {
        JwtUtil.jwtId = jwtId;
    }

    public static String getKeySecretSalt() {
        return keySecretSalt;
    }

    public static void setKeySecretSalt(String keySecretSalt) {
        JwtUtil.keySecretSalt = keySecretSalt;
    }

    /**
     * 默认3600秒过期时间
     */
    private static long tokenExpiredTime = 60*60*1000;
    /**
     * 默认jwt id
     */
    private static String jwtId = "tokenId";
    /**
     * 默认的KEY_SECRET_SALT
     */
    private static String keySecretSalt = "aPbOBbnH4gnZBzIKEY7mxWNu49kYljNPMeta9Fjrwwqzw0bFlO0kPXZTCGaVcw0jzq";

    /**
     * 由key明文生成的SecretKey
     *
     * @return SecretKey
     */
    public static SecretKey generalKey() {
        String secretSalt = keySecretSalt;
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
                .setId(jwtId)
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
}
