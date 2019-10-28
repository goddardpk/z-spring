package com.zafin.zplatform.proto.alert;

import static com.zafin.zplatform.proto.alert.AlertSpringConfig2.TEST_PAYLOAD;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.zafin.models.avro2.Alert;
import com.zafin.models.avro2.Alert.Builder;
import com.zafin.zplatform.proto.BuilderPopulator;
import com.zafin.zplatform.proto.ClientBase;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.VersionedProtocolConfiguration;
import com.zafin.zplatform.proto.exception.BuilderServiceException;

public class AlertSpringClient2 extends ClientBase<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> {
    
    @Qualifier(TEST_PAYLOAD)
    @Autowired
    private PayLoad testPayLoad2;

    @Autowired
    //@Qualifier("alertService2")
    private AlertService2 alertService2;

    public AlertSpringClient2() throws BuilderServiceException {
    	super(Alert.class,Alert.Builder.class,com.zafin.models.avro1.Alert.Builder.class);
        System.out.println("Running [" + getClass().getSimpleName() + "]...");
    }
    
    @Override
    public Class<?> getSupportedClient() {
        return AlertSpringClient2.class;
    }

    @Override
    public Alert create(PayLoad payload) throws BuilderServiceException {
        List<?> builders = alertService2.seedOldBuilderFirst(payload);
        List<Object> buildResults = new ArrayList<>();
        for (Object builder:builders) {
        	buildResults.add(build(builder));
        }
       
        return (Alert) buildResults.get(buildResults.size()-1);
    }
    
    /**
     * Regression test revision 2 client and revision 1 client
     * @author Paul.Goddard
     *
     */
	
	@SpringBootApplication
	public static class RegressionTestSpring2 {
		public static void main(String[] args) throws Exception {

			AlertSpringConfig1.context = SpringApplication.run(AlertSpringConfig1.class, args);

			AlertSpringConfig2.context = SpringApplication.run(AlertSpringConfig2.class, args);

			AlertSpringClient2.regressionTest(AlertSpringConfig2.context);
		}
	}
	 
    
    /**
     * Wire together current client to its previous client revision
     * @param context
     * @throws BuilderServiceException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
	public static void regressionTest(ApplicationContext context) throws BuilderServiceException, ClassNotFoundException {
		com.zafin.zplatform.proto.Builder<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> client2 = 
			(com.zafin.zplatform.proto.Builder<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder>) context.getBean("alertClient2");
        
        com.zafin.zplatform.proto.Builder<com.zafin.models.avro1.Alert,com.zafin.models.avro1.Alert.Builder,com.zafin.models.avro1.Alert.Builder> client1 =  null;
        String alertClient = "alertClient1";
        Object clientObject = context.getBean(alertClient);
        if (clientObject == null) {
        	throw new BuilderServiceException("Spring Environment Invalid: No Bean defined as [" + alertClient + "].");
        }
        client1 = (com.zafin.zplatform.proto.Builder<com.zafin.models.avro1.Alert,com.zafin.models.avro1.Alert.Builder,com.zafin.models.avro1.Alert.Builder>) clientObject;
        
        client2.setPreviousBuilder((com.zafin.zplatform.proto.Builder<?,?,?>) client1);
        
        client2.test();
    }

	@Override
	public Alert create(Builder builder) {
		return builder.build();
	}

	@Override
	public VersionedProtocolConfiguration getVersionedProtocolConfiguration() throws BuilderServiceException {
		return AlertSpringConfig2.instance();
	}
	
	/**
	 * Since a client is a loose extension of a 'builder'...
	 * The only state a client service has is test payload(s) so maing this stateless call: client.build() 
	 * is really just a sanity test that a client can actually build an instance of its supported type without
	 * throwing a builder service exception.
	 */
	@Override
	public Alert build() {
		try {
			System.out.println("Performing a test build using a test payload...");
			return create(testPayLoad2);
		} catch (BuilderServiceException e) {
			throw new IllegalStateException("Unable to build using a test payload",e);
		}
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
	public BuilderPopulator<Alert, Builder,com.zafin.models.avro1.Alert.Builder> getBuilderPopulator() {
		try {
			return (BuilderPopulator<Alert, Builder,com.zafin.models.avro1.Alert.Builder>) getVersionedProtocolConfiguration().getAlertBuilderPopulator();
		} catch (BuilderServiceException e) {
			throw new IllegalStateException("Unable to get alert builder populator from configuration",e);
		}
	}

	@Override
	public List<?> seedOldBuilderFirst(PayLoad payload) throws BuilderServiceException {
		return alertService2.seedOldBuilderFirst(payload);
	}

	@Override
	public boolean loadTestPayLoad() throws BuilderServiceException {
		if (testPayLoad2 == null) {
			throw new BuilderServiceException("Spring Environment invalid: testPayload2 is not wired.");
		}
		return testPayLoad2.loadTestData();
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
	public Class<com.zafin.models.avro1.Alert.Builder> getPreviousBuilderClass() {
		return com.zafin.models.avro1.Alert.Builder.class;
	}

}
