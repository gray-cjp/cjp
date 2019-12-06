package com.cjp.web.servlet;

import com.cjp.domain.User;
import com.cjp.service.UserService;
import com.cjp.utils.CommonsUtils;
import com.cjp.utils.MailUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@WebServlet(name = "UserServlet", urlPatterns = {"/user"})
public class UserServlet extends BaseServlet {
    //激活用户
    public void activeUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String activeCode = request.getParameter("activeCode");
        UserService service = new UserService();
        service.active(activeCode);
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
    //注册用户
    public void registerUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User user = new User();
        Map<String, String[]> map = request.getParameterMap();
        try {
            ConvertUtils.register(new Converter() {
                @Override
                public Object convert(Class clazz, Object value) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date parse = null;
                    try {
                        parse = format.parse(value.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return parse;
                }
            }, Date.class);
            BeanUtils.populate(user, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        user.setUid(CommonsUtils.getUUID());
        user.setTelephone(null);
        user.setState(0);
        user.setCode(CommonsUtils.getUUID());
        UserService service = new UserService();
        boolean isregister = service.register(user);
        if (isregister) {
            String emailMes = "恭喜您注册成功，请点击下面的连接进行激活账户" + "<a href='http://localhost:8080/CJPShop/user?method=activeUser&" +
                    "activeCode='" + user.getCode() + ">" + "http://localhost:8080/CJPShop/active?activeCode=" + "</a>";
            try {
                MailUtils.sendMail(user.getEmail(), emailMes);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/registerSuccess.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/registerFail.jsp");
        }
    }
    //检测用户名是否存在
    public void checkUsername(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        UserService service = new UserService();
        boolean isExit = service.checkUsername(username);
        String json = "{\"isExit\":" + isExit + "}";
        response.getWriter().write(json);
    }
    //用户登录
    public void userLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String autoLogin = request.getParameter("autoLogin");
        UserService service =new UserService();
        User user =service.userLogin(username,password);
        if (user==null){
            request.setAttribute("loginError","用户名或密码错误");
            request.getRequestDispatcher("/login.jsp").forward(request,response);
        }else {
            if (user.getState()==0){
                request.setAttribute("loginError","用户名未激活");
                request.getRequestDispatcher("/login.jsp").forward(request,response);
            } else {
                if (autoLogin!=null) {
                    String autoLogin_user = username + "-" + password;
                    Cookie cookie = new Cookie("autoLogin_user", autoLogin_user);
                    cookie.setMaxAge(30 * 1000);
                    cookie.setPath(request.getContextPath());
                    response.addCookie(cookie);
                }
                session.setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/product?method=index");
            }
        }

    }
    //用户注销
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        Cookie cookie =new Cookie("autoLogin_user","a");
        cookie.setMaxAge(0);
        cookie.setPath(request.getContextPath());
        response.addCookie(cookie);
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        response.sendRedirect(request.getContextPath()+"/product?method=index");
    }
}
