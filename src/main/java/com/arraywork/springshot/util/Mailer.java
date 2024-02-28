package com.arraywork.springshot.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Java Mailer
 * 
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/02/09
 */
@Component
public class Mailer {

    private static final Logger logger = LoggerFactory.getLogger(Mailer.class);

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;

    // Process template with built-in engine
    public boolean sendMail(String template, Map<String, Object> model) {
        Context context = new Context();
        context.setVariables(model);
        model.put("content", templateEngine.process(template, context));
        return sendMail(model);
    }

    // Send mime mail with model: { from, to, subject, content }
    public boolean sendMail(Map<String, Object> model) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom((String) model.get("from"));
            helper.setTo((String) model.get("to"));
            helper.setSubject((String) model.get("subject"));
            helper.setText((String) model.get("content"), true);
            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

}