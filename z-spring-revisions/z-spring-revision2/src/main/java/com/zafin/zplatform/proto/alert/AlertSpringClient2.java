package com.zafin.zplatform.proto.alert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.zafin.zplatform.proto.Builder;
import com.zafin.zplatform.proto.BuilderServiceException;
import com.zafin.zplatform.proto.Client;
import com.zafin.zplatform.proto.ClientBase;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.service.AlertService;

public class AlertSpringClient2<T, B> extends ClientBase<T, B> {
    
    @Qualifier("testPayLoad")
    @Autowired
    private PayLoad testPayLoad;

    //@Autowired
    private AlertService<T, B> alertService;

    @Autowired
    private Builder<T, B> builder;

    public AlertSpringClient2() {
        System.out.println("Running [" + getClass().getSimpleName() + "]...");
    }
    @Override
    public Class<?> getSupportedClient() {
        return AlertSpringClient2.class;
    }

    @Override
    public T create(PayLoad payload) throws BuilderServiceException {
        return alertService.build(payload, builder);
    }
    @SpringBootApplication
    public static class TestSpring2 {
        public static void main(String[] args) throws Exception {
            String[] testArgs = {"foo"};
            Class<?> wtf = TestSpring2.class;
            System.out.println(TestSpring2.class.getSimpleName() + ": " + wtf.getSimpleName());
            ApplicationContext ctx = SpringApplication.run(wtf, testArgs);
            AlertSpringClient2.test(ctx);
        }
    }
    
    
    public static void test(ApplicationContext context) throws BuilderServiceException, ClassNotFoundException {
        Client<?,?> client =  null;
        client = (Client<?,?>)context.getBean("alertClient");
        client.test();
    }

    public void test() {
        try {
            T alert = create(testPayLoad);
            System.out.println("Alert created: " + alert);
        } catch (BuilderServiceException e) {
            throw new IllegalStateException("Unable to build alert", e);
        }
    }
}
