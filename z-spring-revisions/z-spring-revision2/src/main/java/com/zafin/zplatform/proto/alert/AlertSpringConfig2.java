package com.zafin.zplatform.proto.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecordBuilder;
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
public class AlertSpringConfig2 implements VersionedProtocolConfiguration {
    
    private final Map<Object,ServiceRegistry<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder>> allRegisteredServices = new HashMap<>();
    
    public static StartupArgs STARTUP_ARGS = null;
    
    private static final String ALERT_SERVICE = "/AlertService2";
    public static final String TEST_PAYLOAD = "testPayLoad2";
    
    private ConfigurationProperties config = new ConfigurationProperties(2);
    
    public static ApplicationContext context;
    
    private static VersionedProtocolConfiguration INSTANCE;
    private static VersionedProtocolConfiguration previousConfiguration;
    
    private boolean validPreviousConfigurationRequired = true;;
    
    private boolean validated = false;
    
    public static VersionedProtocolConfiguration instance() throws BuilderServiceException {
    	if (INSTANCE == null) throw new BuilderServiceException("Instance not created.");
    	return INSTANCE;
    }
    
    @Autowired
    @Qualifier("payloadFactory2")
    private PayLoadFactory<Alert> payloadFactory2;
    
    @Autowired
    @Qualifier("alertBuilderPopulator2")
    private BuilderPopulator<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> alertBuilderPopulator2;
    
    @Autowired
    @Qualifier("transferState2")
    private TransferState<com.zafin.models.avro1.Alert.Builder,com.zafin.models.avro2.Alert.Builder> transferState2;
    
    @Autowired
    @Qualifier(ALERT_SERVICE)
    private com.zafin.zplatform.proto.Builder<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> alertService2;
    
    @Autowired 
    @Qualifier("alertClient2")
    private Client<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> alertClient2;
    
    public AlertSpringConfig2() {
        System.out.println("Starting [" + this.getClass().getCanonicalName() + "]...");
        INSTANCE = this;
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
    
    BuilderPopulator<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> getBuilderPopulator(int revision) throws BuilderServiceException {
    	String beanName = "alertBuilderPopulator" + revision;
    	BuilderPopulator<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> populator = (BuilderPopulator<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder>) getContext(revision).getBean(beanName);
    	if (populator == null) {
    		throw new IllegalStateException("No bean defined as: [" + beanName + "].");
    	}
    	--revision;
    	if (revision >= 1) {
    		populator.setPreviousPopulator(getBuilderPopulator(revision));
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
    	abp2.setCurrentBuilder(Alert.newBuilder());
    	return abp2;
    }
    
    @Bean(name="transferState2") 
    @DependsOn({"payloadFactory2"})
    TransferState<com.zafin.models.avro1.Alert.Builder,com.zafin.models.avro2.Alert.Builder> transferState2() throws BuilderServiceException {
    	Class<com.zafin.models.avro1.Alert.Builder> previousObject = com.zafin.models.avro1.Alert.Builder.class;
		Class<com.zafin.models.avro2.Alert.Builder> newObject = com.zafin.models.avro2.Alert.Builder.class;
    	return new TransferAvroState<com.zafin.models.avro1.Alert.Builder,com.zafin.models.avro2.Alert.Builder>(previousObject,newObject) {

			@Override
			public Builder populateAvroBuilder(GenericRecordBuilder genericRecordBuilder) throws BuilderServiceException {
				PayLoad payLoad = payloadFactory2.create(genericRecordBuilder);
				return (com.zafin.models.avro2.Alert.Builder) alertBuilderPopulator2.seed(payLoad);
			}

			@Override
			public Schema getPreviousSchema() {
				return com.zafin.models.avro1.Alert.getClassSchema();
			}

			@Override
			public Schema getCurrentSchema() {
				return com.zafin.models.avro2.Alert.getClassSchema();
			}

			@Override
			public Builder convert(com.zafin.models.avro1.Alert.Builder arg0) {
				throw new IllegalStateException("No implemented yet");
			}
    	};
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
    Client<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> alertClient2() throws BuilderServiceException {
        System.out.println("Configuration revision is [" + config.getProtocolRevision() + "].");
        AlertSpringClient2 client = null;
        if (config.getProtocolRevision() == 2) {
        	client =  new AlertSpringClient2();
        	client.addTestPayLoad(testPayLoad2());
        } else {
        	throw new BuilderServiceException("Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
        }
        return client;
    }
    
    @Bean(TEST_PAYLOAD)
    PayLoad testPayLoad2() throws BuilderServiceException {
    	return new AlertTestPayLoad1<com.zafin.models.avro1.Alert>();
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
	public TransferState<com.zafin.models.avro1.Alert.Builder,com.zafin.models.avro2.Alert.Builder> getTransferState() throws BuilderServiceException {
		return transferState2;
	}

	@Override
	public BuilderPopulator<Alert,Alert.Builder, com.zafin.models.avro1.Alert.Builder> getAlertBuilderPopulator() throws BuilderServiceException {
		return alertBuilderPopulator2;
	}

	@Override
	public com.zafin.zplatform.proto.Builder<com.zafin.models.avro2.Alert,com.zafin.models.avro2.Alert.Builder,com.zafin.models.avro1.Alert.Builder> getBuilder() throws BuilderServiceException {
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
	@EventListener(ApplicationReadyEvent.class)
	@Override
	public void testConfiguration() throws BuilderServiceException {
		System.out.println("Testing Configuration " + this.getClass().getSimpleName() + "...");
		
		//TODO: Should not assume that previous configurations are co-located. 
		previousConfiguration = AlertSpringConfig1.instance(); //TODO Fix: wiring at test time is not ideal
		
		List<String> issues = new ArrayList<>();
		if (previousConfiguration == null) {
			issues.add("PreviousConfiguration not set.");
		}
		
		if (transferState2 == null) {
			issues.add("TranfserState2 not initialized.");
		}
		if (alertService2 == null) {
			issues.add("AlertService2 not initialized.");
		}
		if (alertBuilderPopulator2 == null) {
			issues.add("AlertBuilderPopulator2 not initialized.");
		}
		if (alertClient2 == null) {
			issues.add("AlertClient2 not initialized.");
		}
		if (validPreviousConfigurationRequired && getPreviousConfiguration() == null) {
			issues.add("Previous valid configuration required");
		}
		if (alertClient2.getPreviousBuilder() == null) {
			alertClient2.setPreviousBuilder(previousConfiguration.getBuilder());
			if (alertClient2.getPreviousBuilder() == null) {
				issues.add("AlertClient2 intialized but missing previous client");
			}
		}
		if (alertBuilderPopulator2.getPreviousPopulator() == null) {
			System.out.println("Attempting to wire previous alert builder populator...");
			alertBuilderPopulator2.setPreviousPopulator(previousConfiguration.getAlertBuilderPopulator());
			if (alertBuilderPopulator2.getPreviousPopulator() == null) {
				issues.add("AlertBuilderPopulator2 is initialized but missing previous populator.");
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
	public Client<Alert,Alert.Builder,com.zafin.models.avro1.Alert.Builder> getClient() throws BuilderServiceException {
		
		return this.alertClient2;
	}

	@Override
	public VersionedProtocolConfiguration getPreviousConfiguration() {
		return previousConfiguration;
	}

	@Override
	public boolean validated() {
		return validated;
	}

	@Override
	public void setPreviousConfiguration(VersionedProtocolConfiguration versionedProtocolConfiguration)
			throws BuilderServiceException {
		//Protect setter from nulls by ignoring calls if previous config already set.
		if (previousConfiguration != null && versionedProtocolConfiguration != null) {
			throw new BuilderServiceException("Configuration already set: Not accepting " + versionedProtocolConfiguration.getClass().getSimpleName());
		}
		if (versionedProtocolConfiguration == null && previousConfiguration == null) {
			throw new BuilderServiceException("Previous Configuration arg is null: Configuration [" + AlertSpringConfig2.class.getSimpleName() + " requires previous configuration");
		}
		previousConfiguration = versionedProtocolConfiguration;
		
	}
}
