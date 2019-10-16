package com.zafin.zplatform.proto.alert;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.zafin.models.avro2.Alert;
import com.zafin.models.avro2.Alert.Builder;
import com.zafin.zplatform.proto.Client;
import com.zafin.zplatform.proto.ClientBase;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.RevisionUtil;
import com.zafin.zplatform.proto.exception.BuilderServiceException;

public class AlertSpringClient2 extends ClientBase<Alert, Alert.Builder> {
    
    @Qualifier("testPayLoad2")
    @Autowired
    private PayLoad testPayLoad2;

    @Autowired
    //@Qualifier("alertService")
    private AlertService2 alertService2;

    public AlertSpringClient2() {
    	super(Alert.class,Alert.Builder.class);
        System.out.println("Running [" + getClass().getSimpleName() + "]...");
    }
    
    @Override
    public Class<?> getSupportedClient() {
        return AlertSpringClient2.class;
    }

    @Override
    public Alert create(PayLoad payload) throws BuilderServiceException {
    	Alert myAlert = null;
        List<?> builders = alertService2.seedOldBuilderFirst(payload);
        List<Object> objects = new ArrayList<>();
        for (Object builder:builders) {
        	objects.add(build(builder));
        }
        for (Object realizedObject: objects) {
        	System.out.println("Type created: [" + realizedObject.getClass().getCanonicalName() + "] toString: " + realizedObject);
        	if (realizedObject.getClass().isAssignableFrom(Alert.class)) {
        		myAlert = (Alert) realizedObject;
        	} else if (Alert.class.isAssignableFrom(realizedObject.getClass())) {
        		myAlert = (Alert) realizedObject;
        	}
        }
       
        return myAlert;
    }
    
    /**
     * Regression test revision 2 client and revision 1 client
     * @author Paul.Goddard
     *
     */
    @SpringBootApplication
    public static class RegressionTestSpring2 {
        
        public static void main(String[] args) throws Exception {
            
            ApplicationContext ctx1 = SpringApplication.run(AlertSpringConfig1.class, args);
            
            ApplicationContext ctx2 = SpringApplication.run(AlertSpringConfig2.class, args);
            
            AlertSpringClient2.regresionTest(ctx2);
        }
    }
    
    /**
     * Wire together current client to its previous client revision
     * @param context
     * @throws BuilderServiceException
     * @throws ClassNotFoundException
     */
    public static void regresionTest(ApplicationContext context) throws BuilderServiceException, ClassNotFoundException {
        @SuppressWarnings("unchecked")
		Client<Alert,Alert.Builder> client2 = 
			(Client<Alert,Alert.Builder>) context.getBean("alertClient2");
        
        @SuppressWarnings("unchecked")
		Client<com.zafin.models.avro1.Alert,com.zafin.models.avro1.Alert.Builder> client1 = 
        		(Client<com.zafin.models.avro1.Alert,com.zafin.models.avro1.Alert.Builder>) context.getBean("alertClient1");
        
        client2.setPreviousClient(client1);
        
        client2.test();
    }

    public void test() {
        try {
            Alert alert = create(testPayLoad2);
            System.out.println("Alert created: " + alert);
        } catch (BuilderServiceException e) {
            throw new IllegalStateException("Unable to build alert", e);
        }
    }

	@Override
	public Alert create(Builder builder) {
		return builder.build();
	}
}
