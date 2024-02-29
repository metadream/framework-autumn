package com.arraywork.springshot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * SringBoot Base Application
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/05
 */
@SpringBootApplication(scanBasePackages = "com.arraywork.springshot")
@ServletComponentScan("com.arraywork.springshot.security") // @WebListener support
public class BaseApplication {}