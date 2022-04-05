package com.dyw.util.Jwt.work;

import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Devil
 * @create 2022-03-14 19:02
 */

//@SuppressWarnings("all")
public class JwtWork {
    public long getTokenExpiredTime() {
        return tokenExpiredTime;
    }

    public void setTokenExpiredTime(long tokenExpiredTime) {
        this.tokenExpiredTime = tokenExpiredTime;
    }

    public String getJwtId() {
        return jwtId;
    }

    public void setJwtId(String jwtId) {
        this.jwtId = jwtId;
    }

    public String getKeySecretSalt() {
        return keySecretSalt;
    }

    public void setKeySecretSalt(String keySecretSalt) {
        this.keySecretSalt = keySecretSalt;
    }

    public JwtWork(long tokenExpiredTime) {
        this.tokenExpiredTime = tokenExpiredTime;
    }

    public JwtWork() {
    }
    public JwtWork(String keySecretSalt){
        this.keySecretSalt = keySecretSalt;
    }
    public JwtWork(long tokenExpiredTime, String jwtId, String keySecretSalt) {
        this.tokenExpiredTime = tokenExpiredTime;
        this.jwtId = jwtId;
        this.keySecretSalt = keySecretSalt;
    }
    public JwtWork(String jwtId,String keySecretSalt) {
        this.jwtId = jwtId;
        this.keySecretSalt = keySecretSalt;
    }
    /**
     * 默认3600秒过期时间
     */
    private long tokenExpiredTime = 60*60*1000;
    /**
     * 默认jwt id
     */
    private String jwtId = "tokenId";
    /**
     * 默认的KEY_SECRET_SALT
     */
    private String keySecretSalt = "aPbOBbnH4gnZBzIKEY7mxWNu49kYljNPMeta9Fjrwwqzw0bFlO0kPXZTCGaVcw0jzq";

    /**
     * 由key明文生成的SecretKey
     *
     * @return SecretKey
     */
    public SecretKey generalKey() {
        byte[] decode = BaseEncoding.base64().decode(keySecretSalt);
        return new SecretKeySpec(decode, 0, decode.length, "HmacSHA256");

    }

    /**
     * 创建jwt
     *
     * @param map  负载参数
     * @param time 过期时间
     * @return jws(String)
     */
    public String createJwt(Map<String, Object> map, Long time) {
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
                .signWith(signatureAlgorithm,secretKey);
        //生成jwt的时间
        long nowMillis = System.currentTimeMillis();
        if (time >= 0) {
            Date date = new Date(nowMillis + time);
            builder.setExpiration(date);
        }
        return builder.compact();
    }

    public Claims verifyJwt(String token) {
        //签名密钥, 同一密钥明文生成的密钥相同
        SecretKey secretKey = generalKey();
        Claims claims;
        claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims;
    }

    /**
     * @param id 业务数据库中表中的id
     * @return String 返回jws
     */
    public String generateToken(String id) {
        HashMap<String, Object> map = new HashMap<>(5);
        map.put("id", id);
        return createJwt(map, tokenExpiredTime);
    }

    /**
     * @param id 业务数据库中表中的id
     * @return String 返回jws
     */
    public String generateToken(Integer id) {
        HashMap<String, Object> map = new HashMap<>(5);
        map.put("id", id);
        return createJwt(map, tokenExpiredTime);
    }
}
