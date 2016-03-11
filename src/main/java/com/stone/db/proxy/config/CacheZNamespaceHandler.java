package com.stone.db.proxy.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by Stony on 2016/3/11.
 */
public class CacheZNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("config", new CachezBeanDefinitionParser());
    }
}
