package com.zafin.zplatform.proto.alert;


import static com.zafin.zplatform.proto.alert.AlertSpringConfig1.TEST_PAYLOAD;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.zafin.models.avro1.Alert;
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
public class AlertSpringClient1<T,B,O> extends ClientBase<T,B,O> {
    
	@SuppressWarnings("unchecked")
    public AlertSpringClient1() throws BuilderServiceException {
    	super((Class<B>)Alert.Builder.class);
        System.out.println("Creating[" + getClass().getSimpleName() + "]...");
    }
    
    @Autowired
    @Qualifier(AlertSpringConfig1.ALERT_SERVICE)
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
    
    @SuppressWarnings("unchecked")
    @Override
    public T create(PayLoad payload) throws BuilderServiceException {
    	if (payload == null) throw new BuilderServiceException("Null payload.");
        List<?> builders =  alertService1.seedOldBuilderFirst(payload);
        List<Object> buildResults = new ArrayList<>();
        for(Object builder:builders) {
        	buildResults.add(alertService1.build(builder));
        }
        return (T) buildResults.get(buildResults.size()-1);
    }

    @Override
    public Class<?> getSupportedClient() {
        return AlertSpringClient1.class;
    }

    @SuppressWarnings("unchecked")
	@Override
	public T create(B builder) {
		Alert.Builder nativeBuilder = (Alert.Builder) builder;
		return (T) nativeBuilder.build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public VersionedProtocolConfiguration<T,B,O> getVersionedProtocolConfiguration() throws BuilderServiceException {
		return (VersionedProtocolConfiguration<T,B,O>)AlertSpringConfig1.instance();
	}

	@Override
	public List<?> seedOldBuilderFirst(PayLoad payload) throws BuilderServiceException {
		return alertService1.seedOldBuilderFirst(payload);
	}

	/**
	 * Client is an extension of a 'builder'...
	 * A client service can have test payload(s), so making this stateless call: client.build() 
	 * is an easy calling mechanism to test on a service mesh environment to validate that a client can actually request a build of a supported type without
	 * throwing a builder service exception.
	 */
	@Override
	public T build() throws BuilderServiceException {
		T lastAlert = null;
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
		return lastAlert;//Should be able to cast to current Alert type
	}

	/**
	 * Since a client is a loose extension of a 'builder'...
	 * This builder is not be used other than for testing purposes.
	 * This insures the underlying framework is capable of producing its builder instance.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public B getNativeBuilder() {
		return (B)Alert.newBuilder();
	}

	@Override
	public BuilderPopulator<T, B, O> getBuilderPopulator() {
		try {
			return (BuilderPopulator<T, B, O>) getVersionedProtocolConfiguration().getAlertBuilderPopulator();
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
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getClassToBuild() {
		return (Class<T>)Alert.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<B> getCurrentBuilderClass() {
		return (Class<B>)Alert.Builder.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<O> getPreviousBuilderClass() {
		return (Class<O>)Alert.Builder.class;
	}
	@Override
	public int getRevision() throws BuilderServiceException {
		if (alertService1 == null) return -1;
		return alertService1.getRevision();
	}

}
