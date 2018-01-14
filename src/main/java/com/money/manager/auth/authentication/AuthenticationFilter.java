package com.money.manager.auth.authentication;

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

import static java.util.Optional.ofNullable;

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

        if (isOptions(httpServletRequest) || authenticated(httpServletRequest)) {
            chain.doFilter(servletRequest, servletResponse);
        } else {
            httpServletResponse.sendError(401, "Not authenticated user. Log in and try again.");
        }
    }

    private boolean isOptions(HttpServletRequest httpServletRequest) {
        String method = httpServletRequest.getMethod();
        return method.equalsIgnoreCase("OPTIONS");
    }

    private boolean authenticated(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        return ofNullable(session)
                .map(s -> ofNullable(s.getAttribute("user")).isPresent())
                .orElse(false);
    }

    @Override
    public void destroy() {

    }


}
