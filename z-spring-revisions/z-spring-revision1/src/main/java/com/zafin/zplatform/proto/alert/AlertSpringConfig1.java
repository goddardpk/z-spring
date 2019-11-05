package com.zafin.zplatform.proto.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.stereotype.Component;

import com.zafin.models.avro1.Alert;
import com.zafin.models.avro1.Alert.Builder;
import com.zafin.zplatform.proto.BuilderPopulator;
import com.zafin.zplatform.proto.Client;
import com.zafin.zplatform.proto.ConfigValidator;
import com.zafin.zplatform.proto.ConfigurationProperties;
import com.zafin.zplatform.proto.Field;
import com.zafin.zplatform.proto.FieldBase;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.RevisionUtil;
import com.zafin.zplatform.proto.Schema;
import com.zafin.zplatform.proto.ServiceRegistry;
import com.zafin.zplatform.proto.SpringServiceRegistryEntry;
import com.zafin.zplatform.proto.TransferAvroState;
import com.zafin.zplatform.proto.TransferState;
import com.zafin.zplatform.proto.VersionedProtocolConfiguration;
import com.zafin.zplatform.proto.exception.BuilderServiceException;
import com.zafin.zplatform.proto.factory.PayLoadFactory;
import com.zafin.zplatform.proto.factory.SchemaFactory;
import com.zafin.zplatform.proto.service.AlertService;
import com.zafin.zplatform.proto.service.StartupArgs;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class AlertSpringConfig1<T,B,O> implements VersionedProtocolConfiguration<T,B,O> {
	
    private final Map<Object,ServiceRegistry<Alert,Alert.Builder,Alert.Builder>> allRegisteredServices = new HashMap<>();
    
    public static StartupArgs STARTUP_ARGS = null;
    public static final String ALERT_SERVICE = "/AlertService1";
    public static final String TEST_PAYLOAD = "testPayLoad1";
    
    private ConfigurationProperties config = new ConfigurationProperties(1);
    
    public static ApplicationContext context;
    
    private static VersionedProtocolConfiguration<com.zafin.models.avro1.Alert, com.zafin.models.avro1.Alert.Builder, com.zafin.models.avro1.Alert.Builder> INSTANCE;
    
    private boolean validated = false;
    
    @Autowired
    @Qualifier(ALERT_SERVICE)
    private com.zafin.zplatform.proto.Builder<T,B,O> alertService1;
    
    @Autowired
    @Qualifier("alertClient1")
    private Client<T,B,O> alertClient1;
    
    public static VersionedProtocolConfiguration<com.zafin.models.avro1.Alert, com.zafin.models.avro1.Alert.Builder, com.zafin.models.avro1.Alert.Builder> instance() {
    	if (INSTANCE == null) throw new IllegalStateException("Instance not created.");
    	return INSTANCE;
    }
    
    @SuppressWarnings("unchecked")
	public AlertSpringConfig1() {
        System.out.println("Starting [" + this.getClass().getCanonicalName() + "]...");
        INSTANCE = (VersionedProtocolConfiguration<com.zafin.models.avro1.Alert, com.zafin.models.avro1.Alert.Builder, com.zafin.models.avro1.Alert.Builder>) this;
    }
    
    public ApplicationContext getContext(int revision) throws BuilderServiceException {
    	int myRevision = RevisionUtil.getRevisionFromClassName(AlertSpringConfig1.class.getSimpleName());
    	if (revision > myRevision) {
    		throw new IllegalArgumentException("Argument revision [" + revision + "] exceeds my revision [" + myRevision + "].");
    	}
    	if (revision == 1) {
    		return AlertSpringConfig1.context;
    	} else if (revision == 2) {
    		return context;
    	}
    	throw new IllegalArgumentException("Unsupported revision: [" + revision + "].");
    }
    
    BuilderPopulator<Alert,Alert.Builder,Alert.Builder> getBuilderPopulator(int revision) throws BuilderServiceException {
    	if (revision < 1) return null;
    	String beanName = "alertBuilderPopulator" + revision;
    	@SuppressWarnings("unchecked")
		BuilderPopulator<Alert,Alert.Builder,Alert.Builder> populator = (BuilderPopulator<Alert, Builder, Builder>) getContext(revision).getBean(beanName);
    	if (populator == null) {
    		throw new IllegalStateException("No bean defined as: [" + beanName + "].");
    	}
    	populator.setPreviousPopulator(getBuilderPopulator(--revision));
    	return populator;
    }
    
    @Autowired
    @Qualifier("alertBuilderPopulator1")
    private BuilderPopulator<T,B,O> alertBuilderPopulator1;
    
    @Autowired
    @Qualifier("transferState1")
    private TransferState<Alert,Alert.Builder,Alert.Builder> transferState1;
    
    @Autowired
    @Qualifier("payloadFactory1")
    private PayLoadFactory<Alert> payloadFactory1;
    
    @Component
    public class ServerPortCustomizer 
      implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
      
        @Override
        public void customize(ConfigurableWebServerFactory factory)  {
        	System.out.println("Configuring port for " + AlertSpringConfig1.class.getSimpleName() + "...");
        	int port;
			try {
				port = 8080 + RevisionUtil.getRevisionFromClassName(AlertSpringConfig1.class.getSimpleName());
			} catch (BuilderServiceException e) {
				 throw new IllegalStateException("Unable to customize",e);
			}
        	System.out.println("Port for " + AlertSpringConfig1.class.getSimpleName() + " is " + port);
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
    
    @Bean(name="payloadFactory1")
    PayLoadFactory<?> payloadFactory1() throws BuilderServiceException {
    	return new AvroAlertPayLoadFactory1<Alert>();
    }
    
	@Bean(name="transferState1")
	@DependsOn({"payloadFactory1"})
	TransferState<Alert,Alert.Builder,Alert.Builder> transferState1() throws BuilderServiceException {
		Class<com.zafin.models.avro1.Alert> currentClassToBuild = com.zafin.models.avro1.Alert.class;
		Class<com.zafin.models.avro1.Alert.Builder> currentBuilderClass = com.zafin.models.avro1.Alert.Builder.class;
		Class<com.zafin.models.avro1.Alert.Builder> previousBuilderClass = com.zafin.models.avro1.Alert.Builder.class; 
		return new TransferAvroState<Alert,Alert.Builder, Alert.Builder>(
				currentClassToBuild, currentBuilderClass,previousBuilderClass ,getRevision()) {

			@Override
			public org.apache.avro.Schema getPreviousSchema() {
				return com.zafin.models.avro1.Alert.getClassSchema();
			}

			@Override
			public org.apache.avro.Schema getCurrentSchema() {
				return com.zafin.models.avro1.Alert.getClassSchema();
			}

		};
	}
    
    @Bean("alertBuilderPopulator1")
    //@DependsOn({"transferState1"})
    BuilderPopulator<Alert,Alert.Builder,Alert.Builder> alertBuilderPopulator1() throws BuilderServiceException {
    	AlertBuilderPopulator1<Alert,Alert.Builder,Alert.Builder> abp1 = new AlertBuilderPopulator1<>();
    	abp1.setTransferState(transferState1());
    	return abp1;
    }
    
    /**
     * TODO: The type of web container needs to be moved to a core web config and not specified in this 
     * version 1 spring config
     * @return
     */
    @Bean 
    ServletWebServerFactory servletWebServerFactory(){
        return new JettyServletWebServerFactory();
    }
    
    @Bean
    SchemaFactory<Alert> schemaFactory() {
    	return new SchemaFactory<Alert>() {
			@Override
			public Schema createSchema(Alert alert, int revision) {
				return new Schema() {
					
					@Override
					public List<Field> getFields() {
						return alert.getSchema().getFields().stream()
								.map(x->new FieldBase(x.name())).collect(Collectors.toList());
					}

					@Override
					public int getRevision() {
						return revision;
					}

					@Override
					public boolean supports(String fieldName) {
						return getFields().contains(new FieldBase(fieldName));
					}

					@SuppressWarnings("unchecked")
					@Override
					public Class<Alert> getRegisteredClass() {
						return (Class<Alert>) alert.getClass();
					}
				};
			}
    		
    	};
    }
    @Bean
    ServiceRegistry<Alert,Alert.Builder,Alert.Builder> serviceRegistry1() throws BuilderServiceException {
        System.out.println("Calling " + getClass().getSimpleName() + ".serviceRegistry()...");
        @SuppressWarnings("unchecked")
        SpringServiceRegistryEntry<Alert,Alert.Builder,Alert.Builder> registry = SpringServiceRegistryEntry.builder()
                .setSpringConfig(AlertSpringConfig1.class.getCanonicalName())
                .setArgs(STARTUP_ARGS)
                .build();
        allRegisteredServices.put(registry.getRegistryKey(), registry);
        return registry;
    }
    
	@Bean(name = ALERT_SERVICE)
	@DependsOn({"transferState1","alertBuilderPopulator1"})
    AlertService1 alertService1() throws BuilderServiceException {
        System.out.println("Spring Creating: " + HttpInvokerServiceExporter.class.getCanonicalName());
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        AlertService1 alertService = new AlertService1() {

			@Override
			public Alert build() {
				return getBuilder().build();
			}

			@Override
			public Builder getNativeBuilder() {
				return getBuilder();
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
			public Class<Builder> getPreviousBuilderClass() {
				return Alert.Builder.class;
			}
			
        };
        alertService.setBuilderPopulator(alertBuilderPopulator1());
        alertService.setTransferState(transferState1());
        alertService.setBuilder(Alert.newBuilder());
        exporter.setService(alertService);
        exporter.setServiceInterface(AlertService.class);
        List<String> issues = new ConfigValidator().issues(alertService);
        if (!issues.isEmpty()) {
        	new BuilderServiceException("Unable to create service due to issues: " + issues);
        } 
        return alertService;
    }

    @Bean(name = "alertClient1")
    Client<Alert,Alert.Builder,Alert.Builder> alertClient1() throws BuilderServiceException {
    	AlertSpringClient1<Alert,Alert.Builder,Alert.Builder> client = new AlertSpringClient1<>();
    	client.addTestPayLoad(testPayLoad1());
    	return client;
    }
    
    @Bean(TEST_PAYLOAD)
    PayLoad testPayLoad1() throws BuilderServiceException {
    	return new AlertTestPayLoad1<Alert>();
    }

    @Bean
    PayLoadFactory<Alert> payLoadFactory1() throws BuilderServiceException {
        if (config.getProtocolRevision() == 1) {
            return payloadFactory1;
        }
        throw new IllegalStateException(
                "Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }

    public static void main(String[] args) {
        STARTUP_ARGS = new StartupArgs(args);
        System.out.println("Alert Config main()...");
    }

	@Override
	public TransferState<Alert,Alert.Builder,Alert.Builder> getTransferState() throws BuilderServiceException {
		return transferState1;
	}

	@Override
	public BuilderPopulator<T,B,O> getAlertBuilderPopulator() throws BuilderServiceException {
		return alertBuilderPopulator1;
	}

	@Override
	public com.zafin.zplatform.proto.Builder<T,B,O> getBuilder() throws BuilderServiceException {
		return alertService1;
	}
	
	@EventListener(ApplicationReadyEvent.class)
	@Override
	public void testConfiguration() throws BuilderServiceException {
		System.out.println("Testing Configuration " + this.getClass().getSimpleName() + "...");
		List<String> issues = new ArrayList<>();
		if (transferState1 == null) {
			issues.add("Spring Env not valid: TransferState1 not initialized.");
		}
		if (alertService1 == null) {
			issues.add("Spring Env not valid: AlertService1 not initialized.");
		}
		if (alertBuilderPopulator1 == null) {
			issues.add("Spring Env not valid: AlertBuilderPopulator1 not initialized.");
		}
		if (alertClient1 == null) {
			issues.add("Spring Env not valid: AlertClient1 not initialized.");
		}
		if (getPreviousConfiguration() != null) {
			issues.add("Spring Env not valid: No previous configuration expected. Found: [" + getPreviousConfiguration().getClass().getCanonicalName() + "].");
		}
		validated = issues.isEmpty();
		if (!validated) {
			throw new BuilderServiceException(issues.size() + " issues: " + issues);
		} else {
			System.out.println("Configuration " + getClass().getSimpleName() + " valid!");
		}
		
	}

	@Override
	public Client<T,B,O> getClient() throws BuilderServiceException {
		return alertClient1;
	}

	@Override
	public VersionedProtocolConfiguration<?,O,?> getPreviousConfiguration() {
		return null;
	}

	@Override
	public boolean validated() {
		return validated;
	}

	@Override
	public void setPreviousConfiguration(VersionedProtocolConfiguration<?,O,?> versionedProtocolConfiguration)
			throws BuilderServiceException {
		if (versionedProtocolConfiguration != null) {
			throw new BuilderServiceException("This is revision 1, should not be be trying to set previous revision using [" + versionedProtocolConfiguration.getClass().getCanonicalName());
		}
	}

	@Override
	public int getRevision() {
		return 1;
	}
}
