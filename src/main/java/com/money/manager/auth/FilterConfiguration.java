package com.money.manager.auth;


import com.money.manager.auth.authorization.AdminAccessFilter;
import com.money.manager.auth.authentication.AuthenticationFilter;
import com.money.manager.auth.authorization.ResourceAccessFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean adminAccessFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(adminAccessFilter());
        registration.addUrlPatterns("/admin");
        registration.addUrlPatterns("/resources/users");
        registration.setName("adminAccessFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean authenticationFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(authenticationFilter());
        registration.addUrlPatterns("/resources/*");
        registration.addUrlPatterns("/admin");
        registration.setName("authenticationFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean resourceAccessFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(resourceAccessFilter());
        registration.addUrlPatterns("/resources/users/*");
        registration.setName("resourceAccessFilter");
        registration.setOrder(1);
        return registration;
    }

    private Filter adminAccessFilter() {
        return new AdminAccessFilter();
    }

    private Filter authenticationFilter() {
        return new AuthenticationFilter();
    }

    private Filter resourceAccessFilter() {
        return new ResourceAccessFilter();
    }

}
