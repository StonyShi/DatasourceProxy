package com.stone.db.proxy.support;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

/**
 * Created by Stony on 2016/2/1.
 */
public class VelocityMailTemplate implements MailTemplate {

    private JavaMailSender mailSender;

    @Override
    public void send(Map model, String name, SimpleMailMessage mailMessage) {

    }

    @Override
    public void send(Map model, String name) {

    }
}
