package com.lidachui.websocket.web.env;



import com.lidachui.websocket.web.env.support.DatabasePropertySourceLoader;
import com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.*;

/**
 * @author lihuijie
 * @date 2022/11/18 9:43
 * @since 1.0
 */
public class DatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PREFIX = "ENC(";
    private static final String SUFFIX = ")";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        if (environment.getPropertySources().contains("databasePropertySources")) {
            return;
        }
        boolean commandLineArgs = environment.getPropertySources().contains("commandLineArgs");

        if (commandLineArgs) {
            environment.getPropertySources()
                    .addBefore("commandLineArgs", loadConfigurationFromDatabase(environment));
        } else {
            if (environment.getProperty("spring.datasource.url") != null) {
                environment.getPropertySources()
                        .addFirst(loadConfigurationFromDatabase(environment));
                // 设置激活的Profile
                String activeProfile = environment.getProperty("spring.profiles.active", "dev");
                environment.addActiveProfile(activeProfile);
            }
        }
    }

    private PropertySource loadConfigurationFromDatabase(ConfigurableEnvironment environment) {
        DefaultLazyEncryptor encryptor = new DefaultLazyEncryptor(environment);
        String jdbcUrl = environment.getProperty("spring.datasource.url");
        String user = environment.getProperty("spring.datasource.username");
        if (user.startsWith("ENC(")) {
            user = user.substring(PREFIX.length(), (user.length() - SUFFIX.length()));
            user = encryptor.decrypt(user);
        }
        String password = environment.getProperty("spring.datasource.password");
        if (password.startsWith("ENC(")) {
            password = password.substring(PREFIX.length(), (password.length() - SUFFIX.length()));
            password = encryptor.decrypt(password);
        }

        String driverClassName = environment.getProperty(
                "spring.datasource.driver-class-name");
        // 在这之前加载到jdbcUrl, user, password
        Map<String, ?> configs = new DatabasePropertySourceLoader(jdbcUrl, user, password,
                driverClassName).load();
        return new MapPropertySource("databasePropertySources",
                (Map<String, Object>) configs);
    }
}
