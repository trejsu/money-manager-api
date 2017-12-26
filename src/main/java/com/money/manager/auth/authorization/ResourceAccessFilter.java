package com.money.manager.auth.authorization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// todo: after login user should return to desired page
public class ResourceAccessFilter extends AuthorizationFilter {
    @Override
    protected boolean authorized(HttpSession session, HttpServletRequest httpServletRequest) {
        System.out.println("resource access filter");
//        final User user = (User) session.getAttribute("user");
//        if (user.isAdmin()) {
//            return true;
//        }
//        final String pathInfo = httpServletRequest.getPathInfo();
//        if (pathInfo.length() < 4) {
//            return true;
//        }
//        String requestedResourceOwner = httpServletRequest.getPathInfo().split("/")[3];
//        return Objects.equals(user.getLogin(), requestedResourceOwner);
        return true;
    }
}
