package com.arraywork.summer.helper;

import java.util.Map;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.extern.slf4j.Slf4j;

/**
 * Java Mail Sender
 * (Depends on spring-boot-starter-mail)
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/09
 */
@Component
@Slf4j
public class MailSender {

    @Autowired(required = false)
    private JavaMailSender mailSender;
    @Resource
    private TemplateEngine templateEngine;

    /** Process template with built-in engine */
    public boolean send(Map<String, Object> model, String template) {
        Context context = new Context();
        context.setVariables(model);
        model.put("content", templateEngine.process(template, context));
        return send(model);
    }

    /** Send mime mail with model: { from, to, subject, content } */
    public boolean send(Map<String, Object> model) {
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
            log.error(e.getMessage(), e);
            return false;
        }
    }

}