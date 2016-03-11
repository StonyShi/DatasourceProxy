package com.stone.db.proxy.config;

import com.stone.db.proxy.cache.CachezConfig;
import com.stone.db.proxy.cache.CachezManager;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by Stony on 2016/3/11.
 */
public class CachezBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private static final String REF = "ref";

    protected Class getBeanClass(Element element) {
        return CachezConfig.class;
    }


    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        String managerRef = element.getAttribute(REF);
        parserContext.popAndRegisterContainingComponent();

    }
}
