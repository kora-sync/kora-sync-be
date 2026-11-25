package com.minierp.kora_sync.security;

import java.lang.annotation.*;

/**
 * Injects the company id of the signed-in user (from the access token) into a controller parameter.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentCompany {
}
