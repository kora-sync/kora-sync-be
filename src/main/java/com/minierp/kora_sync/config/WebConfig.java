package com.minierp.kora_sync.config;

import com.minierp.kora_sync.security.CurrentCompany;
import com.minierp.kora_sync.security.CurrentCompanyArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    static {
        // The company comes from the token, so it must not appear as a parameter in Swagger.
        SpringDocUtils.getConfig().addAnnotationsToIgnore(CurrentCompany.class);
    }

    private final CurrentCompanyArgumentResolver currentCompanyArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentCompanyArgumentResolver);
    }
}