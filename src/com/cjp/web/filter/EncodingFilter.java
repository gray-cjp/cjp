package com.cjp.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest enhanceRequest = (HttpServletRequest) Proxy.newProxyInstance(response.getClass().getClassLoader(), request.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String met = method.getName();
                        if (met.equals("getParameter")) {
                            String Parameter = (String) method.invoke(request, args);
                            if (Parameter!=null){
                                Parameter = new String(Parameter.getBytes("iso8859-1"), "UTF-8");
                            }
                            return Parameter;
                        }
                        return method.invoke(request, args);
                    }
                });
        /* EnhanceRequest enhanceRequest = new EnhanceRequest(request);*/
        filterChain.doFilter(enhanceRequest,response);
    }

    @Override
    public void destroy() {

    }
}
