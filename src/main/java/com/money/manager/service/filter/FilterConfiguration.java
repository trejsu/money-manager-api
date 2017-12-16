package com.money.manager.service.filter;


import com.money.manager.service.filter.security.AdminAccessFilter;
import com.money.manager.service.filter.security.AuthenticationFilter;
import com.money.manager.service.filter.security.ResourceAccessFilter;
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
