package com.stone.db.proxy;

import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * Created by ShiHui on 2016/1/11.
 */
public class RelTests

{
    public static void main(String[] args) throws Exception{
        Class<?> clz = Class.forName("org.springframework.transaction.interceptor.TransactionAspectSupport");

        System.out.println(clz);

        Method currentTransactionInfo = findMethod(clz,"currentTransactionInfo");
        if(currentTransactionInfo != null){
            Object txInfo = currentTransactionInfo.invoke(null,new Object[0]);
            System.out.println(txInfo);
            if(txInfo != null){
                //TransactionInfo
                //TransactionAttribute getTransactionAttribute()
                Method getTransactionAttribute = findMethod(txInfo.getClass(),"getTransactionAttribute");
                if(getTransactionAttribute != null){
                    Object ta = getTransactionAttribute.invoke(txInfo,new Object[0]);
                    if(ta != null && ta.getClass().isAssignableFrom(TransactionAttribute.class)){
                        TransactionAttribute attribute = (TransactionAttribute) ta;
                        if(attribute.isReadOnly()){

                        }
                    }
                }
            }
        }


//        Field[] fields = clz.getDeclaredFields();
//        for(Field f : fields){
//            if(f.getName().equals("transactionInfoHolder")){
//                System.out.println(f);
//            }
//        }
    }

    public static Method findMethod(Class clz,String name){
        Method[] methods = clz.getDeclaredMethods();
        for(Method m : methods){
            if(name.equals(m.getName())){
                if(Method.PUBLIC != m.getModifiers() || (!m.isAccessible())){
                    m.setAccessible(true);
                }
                return m;
            }
        }
        return null;
    }
}
