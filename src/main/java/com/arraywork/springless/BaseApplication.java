package com.arraywork.springless;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * SringBoot Base Application
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/05
 */
@SpringBootApplication(scanBasePackages = "com.arraywork.springless")
@ServletComponentScan("com.arraywork.springless.security") // @WebListener support
public class BaseApplication {}