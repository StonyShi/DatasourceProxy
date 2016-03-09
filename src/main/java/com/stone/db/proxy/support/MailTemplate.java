package com.stone.db.proxy.support;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

/**
 * Created by Stony on 2016/2/1.
 */
public interface MailTemplate {

    public void send(final Map model, final String name, final SimpleMailMessage mailMessage);
    public void send(final Map model, final String name);

}
