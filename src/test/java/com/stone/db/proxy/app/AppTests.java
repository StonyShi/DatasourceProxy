package com.stone.db.proxy.app;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.Log4jConfigurer;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileNotFoundException;
;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations ={"classpath:spring/spring-ctx-app.xml", "classpath:spring/spring-servlet.xml"})
public class AppTests {
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;

    @Before
    public  void setup() {
        try {
            Log4jConfigurer.initLogging("classpath:config/log4j.xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void simple() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
    private String json ="{\"entId\":1234,\"userId\":1235,\"key\":\"new\"}";
    @Test
    public void json() throws Exception {
        mockMvc.perform(
                post("/user/json","json").characterEncoding("UFT-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.getBytes())
        ).andExpect(content().string("Read from JSON: JavaBean {foo=[bar], fruit=[apple]}"));

    }

    @Test
    public void register() throws Exception {
        MockHttpServletRequestBuilder createMessage = post("/user/register")
                .param("name", "Spring Rocks")
                .param("text", "In case you didn't know, Spring Rocks!")
                .param("birthday","2016-01-11");
        mockMvc.perform(createMessage)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/messages/123"));

        mockMvc.perform(get("/messages/form"))
                .andExpect(xpath("//input[@name='summary']").exists())
                .andExpect(xpath("//textarea[@name='text']").exists());


    }
}