package com.money.manager.auth.authorization;

import com.money.manager.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AdminAccessFilter extends AuthorizationFilter {
    @Override
    protected boolean authorized(HttpSession session, HttpServletRequest httpServletRequest) {
        return ((User) session.getAttribute("user")).isAdmin();
    }
}
