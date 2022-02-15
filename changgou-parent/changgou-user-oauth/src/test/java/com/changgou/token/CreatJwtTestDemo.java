package com.changgou.token;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;

/**
 * @author chaoyue
 * @data2022-02-09 21:59
 */
public class CreatJwtTestDemo {

    @Test
    public void testCreateToken(){
        //加载证书
        ClassPathResource classPathResource = new ClassPathResource("changgou.jks"); //读取classPath里面的文件
        //读取证书数据
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource, "changgou".toCharArray());
        //获取私钥
        RSAPrivateKey changgou = (RSAPrivateKey) keyStoreKeyFactory.getKeyPair("changgou", "changgou".toCharArray()).getPrivate();//获取证书中的一对密钥
//创建令牌，需要私钥加密[使用RSA算法]
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("nikename", "tomcat");

        Jwt encode = JwtHelper.encode(JSON.toJSONString(payload), new RsaSigner(changgou));

        System.out.println(encode.getEncoded());


    }
}
