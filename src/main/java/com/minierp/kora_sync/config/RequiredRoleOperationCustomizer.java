package com.minierp.kora_sync.config;

import com.minierp.kora_sync.security.RequiresAdmin;
import com.minierp.kora_sync.security.RequiresManager;
import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;

@Component
public class RequiredRoleOperationCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        String role = requiredRole(handlerMethod);
        if (role != null) {
            String description = operation.getDescription() == null ? "" : operation.getDescription();
            operation.setDescription(description + "\n\n**Required role:** " + role);
        }
        return operation;
    }

    private String requiredRole(HandlerMethod handlerMethod) {
        if (isAnnotated(handlerMethod, RequiresAdmin.class)) {
            return "ADMIN";
        }
        if (isAnnotated(handlerMethod, RequiresManager.class)) {
            return "MANAGER or ADMIN";
        }
        return null;
    }

    private boolean isAnnotated(HandlerMethod handlerMethod, Class<? extends Annotation> annotation) {
        return AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), annotation)
                || AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), annotation);
    }
}