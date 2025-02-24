package com.arraywork.summer.security;

import java.util.Map;
import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Security Controller
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/02/29
 */
public class SecurityController {

    @Resource
    private SecuritySession session;
    @Autowired(required = false)
    private SecurityService service;

    /** Login page */
    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    /** Login action */
    @PostMapping("/login")
    @ResponseBody
    public Principal login(@RequestBody Map<String, String> map) {
        Principal principal = service.login(map.get("username"), map.get("password"));
        return session.setPrincipal(principal);
    }

    /** Logout action */
    @GetMapping("/logout")
    public String logout() {
        session.destroy();
        return "redirect:/";
    }

}