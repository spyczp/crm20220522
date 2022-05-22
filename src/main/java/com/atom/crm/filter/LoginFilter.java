package com.atom.crm.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter({"*.do", "*.jsp"})
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
            throws IOException, ServletException {

        System.out.println("进入到登录过滤器");
        /*
        * 为了保证用户登录之后才能访问主页，需要加上过滤器
        * 1./settings/user/login.do 是验证用户的合法性，允许放行。
        * 2./login.jsp 是登录页面，允许放行。
        * 3.session存在且session域中存在user对象，允许放行。
        * 4.其它情况，不允许放行。回到登录页面。
        * */
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HttpSession session = request.getSession(false);

        String servletPath = request.getServletPath();

        if("/settings/user/login.do".equals(servletPath) || "/login.jsp".equals(servletPath) || "/index.jsp".equals(servletPath)
                || (session != null && session.getAttribute("user") != null)){

            filterChain.doFilter(request, response);
        }else {

            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }

    }
}
