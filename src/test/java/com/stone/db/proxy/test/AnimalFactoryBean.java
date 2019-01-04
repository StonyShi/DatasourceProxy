package com.stone.db.proxy.test;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * <p>DatasourceProxy
 * <p>com.stone.db.proxy.test
 *
 * @author stony
 * @version 上午10:52
 * @since 2018/12/20
 */
public class AnimalFactoryBean<T>  implements FactoryBean<T> {

    private Class<T> daoClass;


    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(daoClass.getClassLoader(), new Class[]{daoClass}, new AnimalHandler());
    }

    @Override
    public Class<?> getObjectType() {
        return daoClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * Sets the mapper interface of the MyBatis mapper
     *
     * @param daoClass class of the interface
     */
    public void setDaoClass(Class<T> daoClass) {
        this.daoClass = daoClass;
    }

    /**
     * Return the mapper interface of the MyBatis mapper
     *
     * @return class of the interface
     */
    public Class<T> getDaoClass() {
        return daoClass;
    }


}
