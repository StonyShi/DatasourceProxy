package com.stone.db.proxy.test;

import com.stone.db.proxy.test.annotations.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>DatasourceProxy
 * <p>com.stone.db.proxy.test
 *
 * @author stony
 * @version 下午5:55
 * @since 2019/1/3
 */
public class ScanDaoClass implements BeanFactoryPostProcessor{
    protected final Logger logger = LoggerFactory.getLogger(ScanDaoClass.class);

    String packages = "";

    Class<? extends AnimalFactoryBean> factoryBeanClass = AnimalFactoryBean.class;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.info("post: {}", beanFactory); //DefaultListableBeanFactory

        String[] basePackages = StringUtils.tokenizeToStringArray(this.packages,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);

        if(beanFactory instanceof BeanDefinitionRegistry && basePackages != null) {
            processScanClass(basePackages, (BeanDefinitionRegistry) beanFactory);
        }
    }
    void processScanClass(String[] basePackages, BeanDefinitionRegistry registry) {
        //ClassPathBeanDefinitionScanner
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false){
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return true;
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(DB.class));
        final BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
        final ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {
                logger.info("candidate:\n {}", candidate);

                ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(candidate);
                candidate.setScope(scopeMetadata.getScopeName());
                String beanName = beanNameGenerator.generateBeanName(candidate, registry);
                if(candidate instanceof GenericBeanDefinition) {
                    MutablePropertyValues pvs = candidate.getPropertyValues();
                    pvs.addPropertyValue("daoClass", candidate.getBeanClassName());
                    ((GenericBeanDefinition) candidate).setBeanClass(factoryBeanClass);
                    ((GenericBeanDefinition) candidate).setPropertyValues(pvs);

                }
                candidate.setLazyInit(false);

                if (candidate instanceof AnnotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
                }
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                logger.info("definitionHolder:\n {}", definitionHolder);
                BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
            }
        }
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

}