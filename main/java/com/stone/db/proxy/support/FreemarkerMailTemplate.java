package com.stone.db.proxy.support;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Stony on 2016/2/1.
 */
public class FreemarkerMailTemplate implements MailTemplate {

    private JavaMailSender mailSender;

    private FreeMarkerConfigurer freeMarkerConfigurer;

    private String from;

    @Override
    public void send(final Map model, final String name, final SimpleMailMessage mailMessage){
        try {
            mailMessage.setText(FreeMarkerTemplateUtils.processTemplateIntoString(getTemplate(name),model));
            this.mailSender.send(mailMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(final Map model, final String name){
        TemplateLoader loader = freeMarkerConfigurer.getConfiguration().getTemplateLoader();
        Configuration configuration = freeMarkerConfigurer.getConfiguration();

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo((String) model.get("to"));
                message.setFrom(getFrom());
                String text = FreeMarkerTemplateUtils.processTemplateIntoString(getTemplate(name),model);
                System.out.println("send text : " + text);
                message.setText(text, true);
            }
        };
        this.mailSender.send(preparator);
    }



    public Template getTemplate(String name) throws IOException {
        return freeMarkerConfigurer.getConfiguration().getTemplate(name);
    }
    public String getFrom(){
        return this.from;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.freeMarkerConfigurer = freeMarkerConfigurer;
    }

    public void setFrom(String from) {
        this.from = from;
    }


}
