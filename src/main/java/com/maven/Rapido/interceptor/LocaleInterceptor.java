package com.maven.Rapido.interceptor;

import com.maven.Rapido.exception.APIException;
import com.maven.Rapido.payload.request.language.LocalHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

@Component
public class LocaleInterceptor implements HandlerInterceptor {
    @Resource(name = "localHolder")
    LocalHolder localHolder;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver == null) {
            throw new APIException("No LocaleResolver found: not in a DispatcherServlet request?");
        }

        if (localeResolver instanceof AcceptHeaderLocaleResolver headerLocaleResolver) {
            localHolder.setCurrentLocale(headerLocaleResolver.resolveLocale(request));
        } else {
            throw new APIException("Resolver should be of AcceptHeaderLocaleResolver type");
        }

        return true;
    }
}
