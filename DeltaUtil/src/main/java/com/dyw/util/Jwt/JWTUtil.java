package com.dyw.util.Jwt;

import com.dyw.util.Jwt.work.JwtWork;
import io.jsonwebtoken.Claims;

/**
 * @author Devil
 * @create 2022-04-05 23:42
 */
@SuppressWarnings("all")
public class JWTUtil {
    private JwtWork jwtWork;

    public JWTUtil(String keySecretSalt) {
        this.jwtWork = new JwtWork(keySecretSalt);
    }

    public JWTUtil(String keySecretSalt, long tokenExpiredTime, String jwtId) {
        this.jwtWork = new JwtWork(tokenExpiredTime, jwtId, keySecretSalt);
    }

    public JWTUtil(long tokenExpiredTime) {
        this.jwtWork = new JwtWork(tokenExpiredTime);
    }
    public JWTUtil(){
        this.jwtWork = new JwtWork();
    }

    public JWTUtil(String jwtId, String keySecretSalt) {
        this.jwtWork = new JwtWork(jwtId, keySecretSalt);
    }

    /**
     * 字符串类型id 生成token
     * @param id 业务id(String)
     * @return token(JWS)
     */
    public String generateToken(String id) {
        return jwtWork.generateToken(id);
    }

    /**
     * 整型id 生成token
     * @param id 业务id(Integer)
     * @return token(JWS)
     */
    public String generateToken(Integer id) {
        return jwtWork.generateToken(id);
    }

    /**
     * 核实token 并且取出载荷
     * @param token token(JWS)
     * @return Claims
     */
    public Claims verifyJwt(String token){
        return jwtWork.verifyJwt(token);
    }
}