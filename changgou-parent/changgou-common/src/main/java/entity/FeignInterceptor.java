package entity;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author chaoyue
 * @data2022-02-10 19:47
 */
//实现了RequestInterceptor接口，表示该方法在每次feign调用前运行，生成Token,仅限于当前微服务调用其他feign时生效
public class FeignInterceptor implements RequestInterceptor {
    /***
     * Feign调用之前，进行拦截
     * @param template
     */
    @Override
    public void apply(RequestTemplate template){
        /***

         * 这里因为是用户已经登录了才会涉及到feign调用，所以不能获取admintoken，而是获取用户自己的token
         * 再将令牌封装到头文件中
         */
        try {
            //使用RequestContextHolder工具获取request相关变量
            //这是用户当前请求的时候对应线程里面的数据，如果开启了熔断就新建了一个线程进行feign调用，里面的数据就都没了
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                //取出request
                HttpServletRequest request = attributes.getRequest();
                //获取所有头文件信息的key
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        //头文件的key
                        String name = headerNames.nextElement();
                        //头文件的value
                        String values = request.getHeader(name);
                        //将令牌数据添加到头文件中
                        template.header(name, values);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
