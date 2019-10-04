package com.zafin.zplatform.proto.alert;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import com.zafin.zplatform.proto.Builder;
import com.zafin.zplatform.proto.BuilderServiceException;
import com.zafin.zplatform.proto.Client;
import com.zafin.zplatform.proto.ClientBase;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.service.AlertService;

/**
 * This is a Spring implementation of calling a 'build' service.
 * 
 * @author Paul Goddard
 *
 * @param <T>
 * @param <B>
 */
//@SpringBootApplication
public class AlertSpringClient1<T,B> extends ClientBase<T,B> {
    
    @Qualifier("testPayLoad")
    @Autowired
    private PayLoad testPayLoad;

    public AlertSpringClient1() {
        System.out.println("Running [" + getClass().getSimpleName() + "]...");
    }
    
    @Autowired
    private AlertService<T,B> alertService;
    
    @Autowired
    private Builder<T,B> builder;
    
    @SpringBootApplication
    //@Import({AlertSpringConfig1.class})
    public static class TestSpring1 {
        public static void main(String[] args) throws ClassNotFoundException, BuilderServiceException {
            String[] testArgs = {"foo"};
            System.out.println(TestSpring1.class.getSimpleName() + ": " + AlertSpringConfig1.class.getSimpleName());
            ApplicationContext ctx = SpringApplication.run(AlertSpringConfig1.class, testArgs);
            AlertSpringClient1.test(ctx);
        }
    }
    
    public static void test(ApplicationContext context) throws BuilderServiceException, ClassNotFoundException {
        Client<?,?> client =  null;
        client = (Client<?,?>)context.getBean("alertClient");
        client.test();
    }

    public T create(PayLoad payload) throws BuilderServiceException {
        return alertService.build(payload, builder);
    }
    

    @Override
    public void test() {
        try {
            T alert = create(testPayLoad);
            System.out.println("Alert created: " + alert);
        } catch (BuilderServiceException e) {
            throw new IllegalStateException("Unable to create Alert using test payload: [" + testPayLoad + "].",e);
        }
    }

    @Override
    public Class<?> getSupportedClient() {
        return AlertSpringClient1.class;
    }

}
