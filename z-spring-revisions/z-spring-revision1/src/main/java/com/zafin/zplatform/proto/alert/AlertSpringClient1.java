package com.zafin.zplatform.proto.alert;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.zafin.models.avro1.Alert;
import com.zafin.zplatform.proto.Client;
import com.zafin.zplatform.proto.ClientBase;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.exception.BuilderServiceException;
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
public class AlertSpringClient1 extends ClientBase<Alert,Alert.Builder> {
    
    @Qualifier("testPayLoad1")
    @Autowired
    private PayLoad testPayLoad;

    public AlertSpringClient1() {
        System.out.println("Running [" + getClass().getSimpleName() + "]...");
    }
    
    @Autowired
    @Qualifier("alsertService")
    private AlertService<Alert,Alert.Builder> alertService;
    
    @SpringBootApplication
    public static class TestSpring1 {
        
        @Bean 
        ServletWebServerFactory servletWebServerFactory(){
            return new JettyServletWebServerFactory();
        }
        
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

    public Alert create(PayLoad payload) throws BuilderServiceException {
        Alert.Builder builder =  alertService.seedOldBuilderFirst(payload);
        return builder.build();
    }
    

    @Override
    public void test() {
        try {
            Alert alert = create(testPayLoad);
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
