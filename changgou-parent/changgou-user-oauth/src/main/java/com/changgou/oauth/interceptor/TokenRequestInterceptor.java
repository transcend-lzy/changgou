package com.changgou.oauth.interceptor;

import com.changgou.oauth.util.AdminToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * @author chaoyue
 * @data2022-02-10 19:47
 */
//实现了RequestInterceptor接口，表示该方法在每次feign调用前运行，生成adminToken,仅限于当前微服务调用其他feign时生效
    @Configuration
public class TokenRequestInterceptor implements RequestInterceptor {
    /***
     * Feign调用之前，进行拦截
     * @param template
     */
    @Override
    public void apply(RequestTemplate template){
        /***
         * 从数据库加载查询用户信息
         * 1、没有令牌，Feign调用之前需要生成令牌
         * 2、feign调用之前，令牌需要携带过去
         * 3、feign调用之前，令牌需要放到头文件中
         * 4、请求->feign调用->拦截器->feign调用之前执行拦截
         */
        String adminToken = AdminToken.adminToken();
        template.header("Authorization", "bearer " + adminToken); //因为要调用user的数据库的，所以需要管理员权限
    }

}
