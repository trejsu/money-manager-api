package com.money.manager.auth.authorization;

import com.money.manager.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

// todo: after login user should return to desired page
public class ResourceAccessFilter extends AuthorizationFilter {
    @Override
    protected boolean authorized(HttpSession session, HttpServletRequest httpServletRequest) {
        final User user = (User) session.getAttribute("user");
        if (user.isAdmin()) {
            return true;
        }
        final String servletPath = httpServletRequest.getServletPath();
        if (isUnprotectedResource(servletPath)) {
            return true;
        }
        String requestedResourceOwner = servletPath.split("/")[3];
        return Objects.equals(user.getLogin(), requestedResourceOwner);
    }

    private boolean isUnprotectedResource(String servletPath) {
        return servletPath.equals("/resources/users");
    }
}
