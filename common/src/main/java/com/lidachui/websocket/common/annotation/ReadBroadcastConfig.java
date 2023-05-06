package com.lidachui.websocket.common.annotation;


import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ReadBroadcastConfig
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/23 23:29
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadBroadcastConfig {
    @NotNull
    String policy();
}
