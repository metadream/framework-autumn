package com.arraywork.springforce;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.arraywork.springforce.external.BCryptEncoder;

/**
 * SpringBoot Base Application
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/05
 */
@SpringBootApplication(scanBasePackages = "com.arraywork.springforce")
@ServletComponentScan("com.arraywork.springforce.security") // @WebListener support
public class BaseApplication {

    // Get the home directory of application
    // also where the JAR package located
    @Bean
    public String appHome() {
        return new ApplicationHome().getDir().toString();
    }

    // BCrypt strong hashing encoder
    @Bean
    public BCryptEncoder bCryptEncoder() {
        return new BCryptEncoder();
    }

    // Web socket endpoint
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}