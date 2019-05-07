package com.ssm.demo.common;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CrossOriginesourceSharingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        //允许所有外部资源访问，生产环境指定具体的站点提高安全
        response.addHeader("Access-Control-Allow-Origin", "*");
        //允许访问的方法类型，多个用逗号分隔
        response.addHeader("Access-Control-Allow-Methods", "POST,OPTIONS,PUT,GET,DELETE");
        //允许自定义的请求头，多个用逗号分隔
        response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,token");
        //OPTIONS预请求缓存的有效时间 单位秒
        response.addHeader("Access-Control-Max-Age", "60");

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
