package com.atom.crm.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter("*.do")
public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        //处理中文乱码问题
        response.setCharacterEncoding("UTF-8");

        response.setContentType("application/json;charset=UTF-8");

        filterChain.doFilter(request, response);
    }
}
