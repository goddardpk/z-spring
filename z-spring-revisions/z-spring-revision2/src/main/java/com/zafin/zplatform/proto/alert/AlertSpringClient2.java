package com.zafin.zplatform.proto.alert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.zafin.models.avro2.Alert;
import com.zafin.zplatform.proto.Client;
import com.zafin.zplatform.proto.ClientBase;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.exception.BuilderServiceException;
import com.zafin.zplatform.proto.service.AlertService;

public class AlertSpringClient2 extends ClientBase<Alert, Alert.Builder> {
    
    @Qualifier("testPayLoad2")
    @Autowired
    private PayLoad testPayLoad;

    @Autowired
    @Qualifier("alertService")
    private AlertService<Alert,Alert.Builder> alertService;

    public AlertSpringClient2() {
        System.out.println("Running [" + getClass().getSimpleName() + "]...");
    }
    @Override
    public Class<?> getSupportedClient() {
        return AlertSpringClient2.class;
    }

    @Override
    public Alert create(PayLoad payload) throws BuilderServiceException {
        Alert.Builder builder = alertService.seedOldBuilderFirst(payload);
        return builder.build();
    }
    
    @SpringBootApplication
    public static class TestSpring2 {
        
        @Bean 
        ServletWebServerFactory servletWebServerFactory(){
            return new JettyServletWebServerFactory();
        }
        
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
            Alert alert = create(testPayLoad);
            System.out.println("Alert created: " + alert);
        } catch (BuilderServiceException e) {
            throw new IllegalStateException("Unable to build alert", e);
        }
    }
}
