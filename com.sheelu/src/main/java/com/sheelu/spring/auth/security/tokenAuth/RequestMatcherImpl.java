package com.sheelu.spring.auth.security.tokenAuth;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class RequestMatcherImpl implements RequestMatcher {
    private RequestMatcher unAuthenticatedPathMatcher;
    private RequestMatcher authenticatedPathMatcher;
    
    public RequestMatcherImpl(RequestMatcher unAuthenticatedPathMatcher, RequestMatcher authenticatedPathMatcher) {
        this.unAuthenticatedPathMatcher = unAuthenticatedPathMatcher;
        this.authenticatedPathMatcher = authenticatedPathMatcher;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (unAuthenticatedPathMatcher.matches(request)) {
            return false;
        }
        return authenticatedPathMatcher.matches(request) ? true : false;
    }
}
