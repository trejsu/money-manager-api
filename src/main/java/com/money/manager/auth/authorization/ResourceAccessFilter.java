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
        final String pathInfo = httpServletRequest.getServletPath();
//        if (pathInfo.length() < 4) {
//            return true;
//        }
        String requestedResourceOwner = httpServletRequest.getServletPath().split("/")[3];
        return Objects.equals(user.getLogin(), requestedResourceOwner);
    }
}
