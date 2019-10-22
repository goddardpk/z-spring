package com.zafin.zplatform.proto.alert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.avro.generic.GenericRecordBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.stereotype.Component;

import com.zafin.models.avro1.Alert;
import com.zafin.models.avro1.Alert.Builder;
import com.zafin.zplatform.proto.BuilderPopulator;
import com.zafin.zplatform.proto.Client;
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
import com.zafin.zplatform.proto.exception.BuilderServiceException;
import com.zafin.zplatform.proto.factory.PayLoadFactory;
import com.zafin.zplatform.proto.factory.SchemaFactory;
import com.zafin.zplatform.proto.service.AlertService;
import com.zafin.zplatform.proto.service.StartupArgs;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class AlertSpringConfig1 {
	
    private final Map<Object,ServiceRegistry<Alert,Alert.Builder>> allRegisteredServices = new HashMap<>();
    
    public static StartupArgs STARTUP_ARGS = null;
    public static final String ALERT_SERVICE = "/AlertService1";

    private ConfigurationProperties config = new ConfigurationProperties(1);
    
    public static ApplicationContext context;
    
    public AlertSpringConfig1() {
        System.out.println("Starting [" + this.getClass().getCanonicalName() + "]...");
    }
    
    public ApplicationContext getContext(int revision) {
    	int myRevision = RevisionUtil.getRevisionFromClassName(this.getClass().getSimpleName());
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
    
    BuilderPopulator<?,?> getBuilderPopulator(int revision) {
    	if (revision < 1) return null;
    	String beanName = "alertBuilderPopulator" + revision;
    	BuilderPopulator<?,?> populator = (BuilderPopulator<?, ?>) getContext(revision).getBean(beanName);
    	if (populator == null) {
    		throw new IllegalStateException("No bean defined as: [" + beanName + "].");
    	}
    	populator.setPreviousPopulator(getBuilderPopulator(--revision));
    	return populator;
    }
    
    @Autowired
    @Qualifier("alertBuilderPopulator1")
    private BuilderPopulator<?,?> alertBuilderPopulator1;
    
    @Autowired
    @Qualifier("transferState1")
    private TransferState<?,?> transferState1;
    
    @Component
    public class ServerPortCustomizer 
      implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
      
        @Override
        public void customize(ConfigurableWebServerFactory factory) {
        	System.out.println("Configuring port for " + AlertSpringConfig1.class.getSimpleName() + "...");
        	int port = 8080 + RevisionUtil.getRevisionFromClassName(AlertSpringConfig1.class.getSimpleName());
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
    
	@Bean(name = "transferState1")
	TransferState<com.zafin.models.avro1.Alert.Builder, com.zafin.models.avro1.Alert.Builder> transferState() throws BuilderServiceException {
		Class<com.zafin.models.avro1.Alert.Builder> newObject = com.zafin.models.avro1.Alert.Builder.class;
		Class<com.zafin.models.avro1.Alert.Builder> previousObject = com.zafin.models.avro1.Alert.Builder.class; 
		return new TransferAvroState<com.zafin.models.avro1.Alert.Builder, com.zafin.models.avro1.Alert.Builder>(
				previousObject, newObject) {

			@Override
			public com.zafin.models.avro1.Alert.Builder populateAvroBuilder(GenericRecordBuilder genericRecordBuilder,
					PayLoad payload) throws BuilderServiceException {
				BuilderPopulator<?, ?> builderPop = getBuilderPopulator(1);
				Object seededBuilder = builderPop.seed(payload, builderPop.getCurrentBuilder(),
						builderPop.getPreviousPopulator());
				return (com.zafin.models.avro1.Alert.Builder) seededBuilder;
			}

			@Override
			public org.apache.avro.Schema getPreviousSchema() {
				return com.zafin.models.avro1.Alert.getClassSchema();
			}

			@Override
			public org.apache.avro.Schema getCurrentSchema() {
				return com.zafin.models.avro1.Alert.getClassSchema();
			}

			@Override
			public com.zafin.models.avro1.Alert.Builder convert(Builder arg0) {
				throw new IllegalStateException("No implemented yet");
			}
		};
	}
    
    @Bean(name="alertBuilderPopulator1")
    BuilderPopulator<Alert,Alert.Builder> alertBuilderPopulator1() throws BuilderServiceException {
    	return new AlertBuilderPopulator1<>();
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
    ServiceRegistry<Alert,Alert.Builder> serviceRegistry1() {
        System.out.println("Calling " + getClass().getSimpleName() + ".serviceRegistry()...");
        @SuppressWarnings("unchecked")
        SpringServiceRegistryEntry<Alert,Alert.Builder> registry = SpringServiceRegistryEntry.builder()
                .setSpringConfig(AlertSpringConfig1.class.getCanonicalName())
                .setArgs(STARTUP_ARGS)
                .build();
        allRegisteredServices.put(registry.getRegistryKey(), registry);
        return registry;
    }
    
	@Bean(name = ALERT_SERVICE)
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
				return ((com.zafin.models.avro1.Alert.Builder) builder).build();
			}
			
        };
        AlertBuilderPopulator1<Alert,Alert.Builder> abp1 = new AlertBuilderPopulator1<Alert,Alert.Builder>();
        alertService.setBuilderPopulator(new AlertBuilderPopulator1<Alert,Alert.Builder>());
        alertService.setBuilder(Alert.newBuilder());
        abp1.setTransferState(transferState1);
        exporter.setService(alertService);
        exporter.setServiceInterface(AlertService.class);
        return alertService;
    }

    @Bean(name = "alertClient1")
    Client<Alert,Alert.Builder> alertClient1() {
        if (config.getProtocolRevision() == 1) {
            return new AlertSpringClient1();
        }
        throw new IllegalStateException(
                "Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }
    
    @Bean(name = "testPayLoad1")
    PayLoad testPayLoad1() throws BuilderServiceException {
        if (config.getProtocolRevision() == 1) {
            return new AlertTestPayLoad1<Alert>();
        }
        throw new IllegalStateException(
                "Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }

    @Bean
    PayLoadFactory<Alert> payLoadFactory1() throws BuilderServiceException {
        if (config.getProtocolRevision() == 1) {
            return new AlertPayLoadFactory1<>();
        }
        throw new IllegalStateException(
                "Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }

    public static void main(String[] args) {
        STARTUP_ARGS = new StartupArgs(args);
        System.out.println("Alert Config main()...");
    }
    /*
    @SpringBootApplication
    public static class TestSpringService1 {
        
        @Bean 
        ServletWebServerFactory servletWebServerFactory(){
            return new JettyServletWebServerFactory();
        }
        
        public static void main(String[] args) throws ClassNotFoundException, BuilderServiceException {
            SpringApplication.run(AlertSpringConfig1.class, args);
        }
    }
    */
}
