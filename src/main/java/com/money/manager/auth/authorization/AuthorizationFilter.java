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

public abstract class AuthorizationFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        HttpSession session = httpServletRequest.getSession(false);

        String method = httpServletRequest.getMethod();

        if (method.equalsIgnoreCase("OPTIONS") || authorized(session, httpServletRequest)) {
            chain.doFilter(servletRequest, servletResponse);
        } else {
            httpServletResponse.sendError(403, "You do not have access to this resource");
        }
    }

    protected abstract boolean authorized(HttpSession session, HttpServletRequest httpServletRequest);

    @Override
    public void destroy() {

    }
}
