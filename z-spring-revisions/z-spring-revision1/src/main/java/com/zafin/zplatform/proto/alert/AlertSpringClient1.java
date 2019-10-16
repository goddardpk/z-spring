package com.zafin.zplatform.proto.alert;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.zafin.models.avro1.Alert;
import com.zafin.models.avro1.Alert.Builder;
import com.zafin.zplatform.proto.Client;
import com.zafin.zplatform.proto.ClientBase;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.exception.BuilderServiceException;

/**
 * This is a Spring implementation of calling a 'build' service.
 * 
 * @author Paul Goddard
 *
 * @param <T> Type that is to be built
 * @param <B> Builder that will produce type <T>
 */
//@SpringBootApplication
public class AlertSpringClient1 extends ClientBase<Alert,Alert.Builder> {
    
    @Qualifier("testPayLoad1")
    @Autowired
    private PayLoad testPayLoad;

    public AlertSpringClient1() {
    	super(Alert.class,Alert.Builder.class);
        System.out.println("Running [" + getClass().getSimpleName() + "]...");
    }
    
    @Autowired
    @Qualifier(AlertSpringConfig1.ALERT_SERVICE)
    private AlertService1 alertService1;
    
    @SpringBootApplication
    public static class TestSpring1 {
        
        public static void main(String[] args) throws ClassNotFoundException, BuilderServiceException {
            String[] testArgs = {"spring.main.allow-bean-definition-overriding=true"};
            System.out.println(TestSpring1.class.getSimpleName() + ": " + AlertSpringConfig1.class.getSimpleName());
            ApplicationContext ctx = SpringApplication.run(AlertSpringConfig1.class, testArgs);
            AlertSpringClient1.test(ctx);
        }
    }
    
    public static void test(ApplicationContext context) throws BuilderServiceException, ClassNotFoundException {
        @SuppressWarnings("unchecked")
		Client<Alert,Alert.Builder> client = (Client<Alert,Alert.Builder>) context.getBean("alertClient1");
        client.test();
    }

    public Alert create(PayLoad payload) throws BuilderServiceException {
        List<?> builders =  alertService1.seedOldBuilderFirst(payload);
        Object o = null;
        for(Object builder:builders) {
        	o = alertService1.build(builder);
        }
        return (Alert)o;
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

	@Override
	public Alert create(Builder builder) {
		return builder.build();
	}

}
