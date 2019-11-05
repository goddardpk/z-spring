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
import com.zafin.zplatform.proto.BuilderPopulator;
import com.zafin.zplatform.proto.ClientBase;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.VersionedProtocolConfiguration;
import com.zafin.zplatform.proto.exception.BuilderServiceException;

public class AlertSpringClient2<T,B,O>	extends ClientBase<T,B,O> {
    
    @Qualifier(TEST_PAYLOAD)
    @Autowired
    private PayLoad testPayLoad2;

    @Autowired
    //@Qualifier("alertService2")
    private AlertService2 alertService2;

    @SuppressWarnings("unchecked")
	public AlertSpringClient2() throws BuilderServiceException {
    	super((Class<B>) Alert.Builder.class);
        System.out.println("Running [" + getClass().getSimpleName() + "]...");
    }
    
    @Override
    public Class<?> getSupportedClient() {
        return AlertSpringClient2.class;
    }
    /**
     * 'Supporting' a payload depends on the revision number of payload.
     * No client should be asked to support a payload from the future- only from the present & the past.
     * 
     * @param payload
     * @return
     * @throws BuilderServiceException
     */
    private boolean isPayLoadSupported(PayLoad payload) throws BuilderServiceException {
    	boolean supported = payload.getRevision() <= alertService2.getRevision();
    	if (!supported) {
    		System.out.println("Warning: Payload with revision [" + payload.getRevision() + " is not supported with service at revision [" + alertService2.getRevision() + "].");
    	}
    	return supported;
    }
    
    
    @SuppressWarnings("unchecked")
	@Override
    public T create(PayLoad payload) throws BuilderServiceException {
    	if (!isPayLoadSupported(payload)) {
    		return null;
    	}
        List<?> builders = alertService2.seedOldBuilderFirst(payload);
        //boolean payLoadSupported = payload.getRevision() <= alertService2.getRevision();
        List<Object> buildResults = new ArrayList<>();
        for (Object builder:builders) {
        	buildResults.add(build(builder));
        }
       
        return (T) buildResults.get(buildResults.size()-1);
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

	@SuppressWarnings("unchecked")
	@Override
	public T create(B builder) {
		Alert.Builder nativeBuilder = (Alert.Builder) builder;
		return (T) nativeBuilder.build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public VersionedProtocolConfiguration<T,B,O> getVersionedProtocolConfiguration() throws BuilderServiceException {
		return (VersionedProtocolConfiguration<T, B, O>) AlertSpringConfig2.instance();
	}
	
	/**
	 * Client is a loose extension of a 'builder'...
	 * The only state a client service has is test payload(s) so making this stateless call: client.build() 
	 * is really just sanity test that a client can actually build an instance of its supported type without
	 * throwing a builder service exception.
	 */
	@Override
	public T build() {
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
	@SuppressWarnings("unchecked")
	@Override
	public B getNativeBuilder() {
		return (B) Alert.newBuilder();
	}

	@SuppressWarnings("unchecked")
	@Override
	public BuilderPopulator<T,B,O> getBuilderPopulator() {
		try {
			return (BuilderPopulator<T,B,O>) getVersionedProtocolConfiguration().getAlertBuilderPopulator();
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

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getClassToBuild() {
		return (Class<T>) Alert.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<B> getCurrentBuilderClass() {
		return (Class<B>)Alert.Builder.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<O> getPreviousBuilderClass() {
		return (Class<O>) com.zafin.models.avro1.Alert.Builder.class;
	}

	@Override
	public int getRevision() throws BuilderServiceException {
		if (alertService2 == null) return -1;
		return alertService2.getRevision();
	}

}
