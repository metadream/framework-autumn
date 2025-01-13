package com.arraywork.vernal;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * SpringBoot Base Application
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/05
 */
@SpringBootApplication(scanBasePackages = "com.arraywork.vernal")
public class BaseApplication {

    private static final ApplicationHome APP_HOME = new ApplicationHome();

    /**
     * Get the home directory path of current application,
     * also where the JAR package located
     */
    public static String getHomePath() {
        return APP_HOME.getDir().getPath();
    }

    /** Enable web socket endpoint */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}