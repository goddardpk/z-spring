package com.zafin.zplatform.proto.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.stereotype.Component;

import com.zafin.models.avro2.Alert;
import com.zafin.models.avro2.Alert.Builder;
import com.zafin.zplatform.proto.BuilderPopulator;
import com.zafin.zplatform.proto.Client;
import com.zafin.zplatform.proto.ConfigValidator;
import com.zafin.zplatform.proto.ConfigurationProperties;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.RevisionUtil;
import com.zafin.zplatform.proto.ServiceRegistry;
import com.zafin.zplatform.proto.SpringServiceRegistryEntry;
import com.zafin.zplatform.proto.TransferAvroState;
import com.zafin.zplatform.proto.TransferState;
import com.zafin.zplatform.proto.VersionedProtocolConfiguration;
import com.zafin.zplatform.proto.exception.BuilderServiceException;
import com.zafin.zplatform.proto.factory.PayLoadFactory;
import com.zafin.zplatform.proto.service.AlertService;
import com.zafin.zplatform.proto.service.StartupArgs;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class AlertSpringConfig2<T,B,O> implements VersionedProtocolConfiguration<T,B,O> {
    
    private final Map<Object,ServiceRegistry<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder>> allRegisteredServices = new HashMap<>();
    
    public static StartupArgs STARTUP_ARGS = null;
    
    private static final String ALERT_SERVICE = "/AlertService2";
    public static final String TEST_PAYLOAD = "testPayLoad2";
    
    private ConfigurationProperties config = new ConfigurationProperties(2);
    
    public static ApplicationContext context;
    
    private static VersionedProtocolConfiguration<com.zafin.models.avro2.Alert, com.zafin.models.avro2.Alert.Builder, com.zafin.models.avro1.Alert.Builder> INSTANCE;
    private static VersionedProtocolConfiguration <?,com.zafin.models.avro1.Alert.Builder,?> previousConfiguration;
    
    private boolean validPreviousConfigurationRequired = true;;
    
    private boolean validated = false;
    
    public static VersionedProtocolConfiguration<com.zafin.models.avro2.Alert, com.zafin.models.avro2.Alert.Builder, com.zafin.models.avro1.Alert.Builder> instance() throws BuilderServiceException {
    	if (INSTANCE == null) throw new BuilderServiceException("Instance not created.");
    	return INSTANCE;
    }
    
    @Autowired
    @Qualifier("payloadFactory2")
    private PayLoadFactory<Alert> payloadFactory2;
    
    @Autowired
    @Qualifier("alertBuilderPopulator2")
    private BuilderPopulator<T,B,O> alertBuilderPopulator2;
    
    @Autowired
    @Qualifier("transferState2")
    private TransferState<com.zafin.models.avro2.Alert,com.zafin.models.avro2.Alert.Builder,com.zafin.models.avro1.Alert.Builder> transferState2;
    
    @Autowired
    @Qualifier(ALERT_SERVICE)
    private com.zafin.zplatform.proto.Builder<T,B,O> alertService2;
    
    @Autowired 
    @Qualifier("alertClient1")
    private Client<?,?,?> alertClient1;
    
    @Autowired 
    @Qualifier("alertClient2")
    private Client<T,B,O> alertClient2;
    
    @SuppressWarnings("unchecked")
	public AlertSpringConfig2() {
        System.out.println("Starting [" + this.getClass().getCanonicalName() + "]...");
        INSTANCE = (VersionedProtocolConfiguration<Alert, Builder, com.zafin.models.avro1.Alert.Builder>) this;
    }
    
    public static ApplicationContext getContext(int revision) throws BuilderServiceException {
    	int myRevision = RevisionUtil.getRevisionFromClassName(AlertSpringConfig2.class.getSimpleName());
    	if (revision > myRevision) {
    		throw new IllegalArgumentException("Argument revision [" + revision + "] exceeds my revision [" + myRevision + "].");
    	}
    	if (revision == 1) {
    		return com.zafin.zplatform.proto.alert.AlertSpringConfig1.context;
    	} else if (revision == 2) {
    		return context;
    	}
    	throw new IllegalArgumentException("Unsupported revision: [" + revision + "].");
    }
    
    @SuppressWarnings("unchecked")
	BuilderPopulator<?,?,?> getBuilderPopulator(int revision) throws BuilderServiceException {
    	String beanName = "alertBuilderPopulator" + revision;
		BuilderPopulator<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> populator = (BuilderPopulator<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder>) getContext(revision).getBean(beanName);
    	if (populator == null) {
    		throw new IllegalStateException("No bean defined as: [" + beanName + "].");
    	}
    	if (revision >= 1) {
    		BuilderPopulator<?,com.zafin.models.avro1.Alert.Builder,?> previousPopulator = 
    				(BuilderPopulator<?, com.zafin.models.avro1.Alert.Builder, ?>) getBuilderPopulator(--revision);
    		populator.setPreviousPopulator(previousPopulator);
    	}
    	return populator;
    }
    
    @Bean(name="payloadFactory2")
    PayLoadFactory<?> payloadFactory2() throws BuilderServiceException {
    	return new AvroAlertPayLoadFactory2<com.zafin.models.avro2.Alert>();
    }
    
    
    @Bean(name="alertBuilderPopulator2")
    @DependsOn({"transferState2"})
    AlertBuilderPopulator2<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> alertBuilderPopulator2() throws BuilderServiceException {
    	AlertBuilderPopulator2<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> abp2 =  new AlertBuilderPopulator2<>();
    	abp2.setTransferState(transferState2());
    	return abp2;
    }
    
    @Bean(name="transferState2") 
    @DependsOn({"payloadFactory2"})
    TransferState<com.zafin.models.avro2.Alert,com.zafin.models.avro2.Alert.Builder,com.zafin.models.avro1.Alert.Builder> transferState2() throws BuilderServiceException {
    	
    	Class<com.zafin.models.avro2.Alert> currentClassToBuild = com.zafin.models.avro2.Alert.class;
		Class<com.zafin.models.avro2.Alert.Builder> currentBuilderClass = com.zafin.models.avro2.Alert.Builder.class;
		Class<com.zafin.models.avro1.Alert.Builder> previousBuilderClass = com.zafin.models.avro1.Alert.Builder.class; 
		TransferAvroState<com.zafin.models.avro2.Alert,com.zafin.models.avro2.Alert.Builder,com.zafin.models.avro1.Alert.Builder>  ts = 
				new TransferAvroState<com.zafin.models.avro2.Alert,com.zafin.models.avro2.Alert.Builder,com.zafin.models.avro1.Alert.Builder>(currentClassToBuild,currentBuilderClass,previousBuilderClass,getRevision()) {

			@Override
			public Schema getPreviousSchema() {
				return com.zafin.models.avro1.Alert.getClassSchema();
			}

			@Override
			public Schema getCurrentSchema() {
				return com.zafin.models.avro2.Alert.getClassSchema();
			}

    	};
    	//ts.setPreviousTransferState(alertBuilderPopulator2.getPreviousPopulator().getTransferState());
    	return ts;
    }
    
    @Component
    public class ServerPortCustomizer 
      implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
      
        @Override
        public void customize(ConfigurableWebServerFactory factory) {
        	System.out.println("Configuring port for " + AlertSpringConfig2.class.getSimpleName() + "...");
        	int port;
			try {
				port = 8080 + RevisionUtil.getRevisionFromClassName(AlertSpringConfig2.class.getSimpleName());
			} catch (BuilderServiceException e) {
				throw new IllegalStateException("Unable to cusomize",e);
			}
        	System.out.println("Port for " + AlertSpringConfig2.class.getSimpleName() + " is " + port);
        	factory.setPort(port);
        }
    }
    
    @Component
    public class ApplicationContextProvider implements ApplicationContextAware {

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            context = applicationContext;
        } 

        public ApplicationContext getContext() {
            return context;
        }
    }
    
    @Bean
    ServiceRegistry<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> serviceRegistry2() throws ClassNotFoundException, BuilderServiceException {
        @SuppressWarnings("unchecked")
        SpringServiceRegistryEntry<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> registry = SpringServiceRegistryEntry.builder()
                .setSpringConfig(AlertSpringConfig2.class.getCanonicalName())
                .setArgs(STARTUP_ARGS)
                .build();
        allRegisteredServices.put(registry.getRegistryKey(), registry);
        return registry;
    }
    
	@Bean(name = ALERT_SERVICE)
	@DependsOn({"transferState2","alertBuilderPopulator2","payloadFactory2"})
    //@Qualifier("alertService")
    AlertService2 alertService2() throws BuilderServiceException {
        System.out.println("Spring Creating: " + HttpInvokerServiceExporter.class.getCanonicalName());
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        //Implement methods that require specific (versioned) schema types like Alert and Builder
    	AlertService2 alertService = new AlertService2() {

			@Override
			public Alert build() throws BuilderServiceException {
				return  getBuilder().build();
			}

			@Override
			public Builder getNativeBuilder() {
				return  getBuilder();
			}

			@Override
			public Alert build(Object builder) {
				if (!getNativeBuilder().getClass().isAssignableFrom(builder.getClass())) {
					throw new IllegalArgumentException("Builder argument is not assignable to [" + getNativeBuilder().getClass().getCanonicalName() + "].");
				}
				return ((Alert.Builder) builder).build();
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
    		
    	};
        alertService.setBuilderPopulator(alertBuilderPopulator2());
        alertService.setTransferState(transferState2());
        exporter.setService(alertService);
        exporter.setServiceInterface(AlertService.class);
        List<String> issues = new ConfigValidator().issues(alertService);
        if (!issues.isEmpty()) {
        	new BuilderServiceException("Unable to create service due to issues: " + issues);
        }
        return alertService;
    }
    
    @Bean(name="alertClient2")
    Client<com.zafin.models.avro2.Alert, com.zafin.models.avro2.Alert.Builder, com.zafin.models.avro1.Alert.Builder> alertClient2() throws BuilderServiceException {
        System.out.println("Configuration revision is [" + config.getProtocolRevision() + "].");
        AlertSpringClient2<com.zafin.models.avro2.Alert, com.zafin.models.avro2.Alert.Builder, com.zafin.models.avro1.Alert.Builder> client = null;
        if (config.getProtocolRevision() == 2) {
        	client =  new AlertSpringClient2<com.zafin.models.avro2.Alert, com.zafin.models.avro2.Alert.Builder, com.zafin.models.avro1.Alert.Builder>();
        	client.setPreviousBuilder(alertClient1);
        	for (PayLoad prev: alertClient1.getTestPayLoads()) {
        		PayLoad payload2 = testPayLoad2();
        		payload2.setPreviousPayLoad(prev);
        		client.addTestPayLoad(payload2);
        	}
        } else {
        	throw new BuilderServiceException("Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
        }
        return client;
    }
    
    @Bean(TEST_PAYLOAD)
    PayLoad testPayLoad2() throws BuilderServiceException {
    	return new AlertTestPayLoad2<com.zafin.models.avro2.Alert>();
    }
    
	@Bean
    PayLoadFactory<Alert> payLoadFactory2() throws BuilderServiceException {
        if (config.getProtocolRevision() == 2) {
            return new AvroAlertPayLoadFactory2<Alert>();
        }
        throw new IllegalStateException("Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }
    
    public static void main(String[] args) {
        STARTUP_ARGS = new StartupArgs(args);
        System.out.println("Alert Config main()...");
    }

	@Override
	public TransferState<com.zafin.models.avro2.Alert,com.zafin.models.avro2.Alert.Builder,com.zafin.models.avro1.Alert.Builder> getTransferState() throws BuilderServiceException {
		return transferState2;
	}

	@Override
	public BuilderPopulator<T,B,O> getAlertBuilderPopulator() throws BuilderServiceException {
		return alertBuilderPopulator2;
	}

	@Override
	public com.zafin.zplatform.proto.Builder<T,B,O> getBuilder() throws BuilderServiceException {
		return alertService2;
	}
	
	/**
	 * TODO
	 * General validation of a configuration will follow this pattern, so it
	 * makes sense to refactor this logic out of each configuration version.
	 * 
	 * There are basically 2 types of validation:
	 * 1: Where a configuration has a previous configuration
	 * 2. Where a configuration has no previous configuration (root configuration...probably rev 1)
	 * 
	 * It is also not ideal to be having a testConfiguration() call tackling some missed wiring cases,
	 * like if a populator is not wired with its previous populator or a client not wired with its previous client.
	 * This responsibility of backward chaining should be moved out to some some higher protocol requirement.
	 * Basically: A revised configuration shall always be able to reference a valid previous configuration.
	 */
	@SuppressWarnings("unchecked")
	@EventListener(ApplicationReadyEvent.class)
	@Override
	public void testConfiguration() throws BuilderServiceException {
		System.out.println("Testing Configuration " + this.getClass().getSimpleName() + "...");
		
		//TODO: Should not assume that previous configurations are co-located. 
		previousConfiguration = AlertSpringConfig1.instance(); //TODO Fix: wiring at test time is not ideal
		
		List<String> issues = new ArrayList<>();
		if (previousConfiguration == null) {
			issues.add("Spring Env not valid: PreviousConfiguration not set.");
		}
		
		if (transferState2 == null) {
			issues.add("Spring Env not valid: TranfserState2 not initialized.");
		}
		if (alertService2 == null) {
			issues.add("Spring Env not valid: AlertService2 not initialized.");
		}
		if (alertBuilderPopulator2 == null) {
			issues.add("Spring Env not valid: AlertBuilderPopulator2 not initialized.");
		}
		if (alertClient2 == null) {
			issues.add("Spring Env not valid: AlertClient2 not initialized.");
		}
		if (validPreviousConfigurationRequired && getPreviousConfiguration() == null) {
			issues.add("Spring Env not valid: Previous valid configuration required");
		}
		if (alertClient2.getPreviousBuilder() == null) {
			alertClient2.setPreviousBuilder(previousConfiguration.getBuilder());
			if (alertClient2.getPreviousBuilder() == null) {
				issues.add("Spring Env not valid: AlertClient2 intialized but missing previous client");
			}
		}
		if (alertBuilderPopulator2.getPreviousPopulator() == null) {
			System.out.println("Attempting to wire previous alert builder populator...");
			BuilderPopulator<?,?,?> previousPopulator = 
					 previousConfiguration.getAlertBuilderPopulator();
			alertBuilderPopulator2.setPreviousPopulator((BuilderPopulator<?, O, ?>) previousPopulator);
			if (alertBuilderPopulator2.getPreviousPopulator() == null) {
				issues.add("Spring Env not valid: AlertBuilderPopulator2 is initialized but missing previous populator.");
			}
		}
		validated = issues.isEmpty();
		if (!validated) {
			throw new BuilderServiceException(issues.size() + " issues: " + issues);
		} else {
			System.out.println("Configuration " + this.getClass().getSimpleName() + " valid");
		}
	}

	@Override
	public Client<T, B, O> getClient() throws BuilderServiceException {
		return this.alertClient2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public VersionedProtocolConfiguration<?,O,?> getPreviousConfiguration() {
		return (VersionedProtocolConfiguration<?, O, ?>) previousConfiguration;
	}

	@Override
	public boolean validated() {
		return validated;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPreviousConfiguration(VersionedProtocolConfiguration<?,O,?> versionedProtocolConfiguration)
			throws BuilderServiceException {
		//Protect setter from nulls by ignoring calls if previous config already set.
		if (previousConfiguration != null && versionedProtocolConfiguration != null) {
			throw new BuilderServiceException("Configuration already set: Not accepting " + versionedProtocolConfiguration.getClass().getSimpleName());
		}
		if (versionedProtocolConfiguration == null && previousConfiguration == null) {
			throw new BuilderServiceException("Previous Configuration arg is null: Configuration [" + AlertSpringConfig2.class.getSimpleName() + " requires previous configuration");
		}
		previousConfiguration = (VersionedProtocolConfiguration<?, com.zafin.models.avro1.Alert.Builder, ?>) versionedProtocolConfiguration;
		
	}

	@Override
	public int getRevision() {
		return 2;
	}
}
