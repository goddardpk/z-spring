package com.zafin.zplatform.proto.alert;


import static com.zafin.zplatform.proto.alert.AlertSpringConfig1.ALERT_SERVICE;
import static com.zafin.zplatform.proto.alert.AlertSpringConfig1.TEST_PAYLOAD;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.zafin.models.avro1.Alert;
import com.zafin.models.avro1.Alert.Builder;
import com.zafin.zplatform.proto.BuilderPopulator;
import com.zafin.zplatform.proto.Client;
import com.zafin.zplatform.proto.ClientBase;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.VersionedProtocolConfiguration;
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
public class AlertSpringClient1 extends ClientBase<Alert,Alert.Builder,Alert.Builder> {
    

    public AlertSpringClient1() throws BuilderServiceException {
    	super(Alert.class,Alert.Builder.class,Alert.Builder.class);
//    	PayLoad payload = new AlertTestPayLoad1<Alert>();
//    	if (!payload.loadTestData()) {
//    		throw new BuilderServiceException("Unable to load Test Data");
//    	}
//    	if (!addTestPayLoad(payload)) {
//    		throw new BuilderServiceException("Unable to add test payload data.");
//    	}
        System.out.println("Creating[" + getClass().getSimpleName() + "]...");
    }
    
    @Autowired
    @Qualifier(ALERT_SERVICE)
    private AlertService1 alertService1;
    
    @Autowired
    @Qualifier(TEST_PAYLOAD)
    private PayLoad testPayLoad1;
    
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
		Client<Alert,Alert.Builder,Alert.Builder> client = (Client<Alert,Alert.Builder,Alert.Builder>) context.getBean("alertClient1");
        client.test();
    }
    @Override
    public Alert create(PayLoad payload) throws BuilderServiceException {
    	if (payload == null) throw new BuilderServiceException("Null payload.");
        List<?> builders =  alertService1.seedOldBuilderFirst(payload);
        List<Object> buildResults = new ArrayList<>();
        for(Object builder:builders) {
        	buildResults.add(alertService1.build(builder));
        }
        return (Alert) buildResults.get(buildResults.size()-1);
    }

    @Override
    public Class<?> getSupportedClient() {
        return AlertSpringClient1.class;
    }

	@Override
	public Alert create(Builder builder) {
		return builder.build();
	}

	@Override
	public VersionedProtocolConfiguration getVersionedProtocolConfiguration() throws BuilderServiceException {
		return AlertSpringConfig1.instance();
	}

	@Override
	public List<?> seedOldBuilderFirst(PayLoad payload) throws BuilderServiceException {
		return alertService1.seedOldBuilderFirst(payload);
	}

	/**
	 * Since a client is a loose extension of a 'builder'...
	 * The only state a client service has is test payload(s) so making this stateless call: client.build() 
	 * is really just a sanity test that a client can actually build an instance of its supported type without
	 * throwing a builder service exception.
	 */
	@Override
	public Alert build() throws BuilderServiceException {
		Object lastAlert = null;
		try {
			System.out.println("Performing a test build using a test payloads...");
			for (PayLoad payload:this.getTestPayLoads()) {
				lastAlert = create(payload);
				if (lastAlert == null) {
					throw new BuilderServiceException("Unable to perform test build using payload: " + payload);
				}
			}
		} catch (BuilderServiceException e) {
			throw new IllegalStateException("Unable to build using a test payload",e);
		}
		return (Alert) lastAlert;//Should be able to cast to current Alert type
	}

	/**
	 * Since a client is a loose extension of a 'builder'...
	 * This builder is not be used other than for testing purposes.
	 * This insures the underlying framework is capable of producing its builder instance.
	 */
	@Override
	public Builder getNativeBuilder() {
		return Alert.newBuilder();
	}

	@SuppressWarnings("unchecked")
	@Override
	public BuilderPopulator<Alert, Builder, Builder> getBuilderPopulator() {
		try {
			return (BuilderPopulator<Alert, Builder, Builder>) getVersionedProtocolConfiguration().getAlertBuilderPopulator();
		} catch (BuilderServiceException e) {
			throw new IllegalStateException("Unable to get alert builder populator from configuration",e);
		}
	}
	@Override
	public boolean loadTestPayLoad() throws BuilderServiceException {
		if (testPayLoad1 == null) {
			throw new BuilderServiceException("Spring Environment invalid: testPayload1 is not wired.");
		}
		return testPayLoad1.loadTestData();
	}
	@Override
	public Class<Alert> getClassToBuild() {
		return Alert.class;
	}
	@Override
	public Class<Builder> getCurrentBuilderClass() {
		return Alert.Builder.class;
	}
	@Override
	public Class<Builder> getPreviousBuilderClass() {
		return Alert.Builder.class;
	}

}
