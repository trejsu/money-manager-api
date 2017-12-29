package com.money.manager.auth.authorization;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        if (authenticated(httpServletRequest)) {
            chain.doFilter(servletRequest, servletResponse);
        } else {
            httpServletResponse.sendError(401);
        }
    }

    private boolean authenticated(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        String method = httpServletRequest.getMethod();
        return method.equalsIgnoreCase("OPTIONS") ||
                (session != null && session.getAttribute("user") != null);
    }

    @Override
    public void destroy() {

    }


}
