package com.cjp.web.filter;

import com.cjp.domain.User;
import com.cjp.service.UserService;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AtuoLogin implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Cookie[] cookies = request.getCookies();
        if (cookies!=null){
            for (Cookie cookie : cookies){
                if ("autoLogin_user".equals(cookie.getName())){
                    String value = cookie.getValue();
                    String[] split = value.split("-");
                    String username = split[0];
                    String password = split[1];
                    UserService service =new UserService();
                    User user =service.userLogin(username,password);
                    request.getSession().setAttribute("user",user);
                }
            }
        }

        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
