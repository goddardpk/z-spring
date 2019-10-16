package com.zafin.zplatform.proto.alert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecordBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.stereotype.Component;

import com.zafin.models.avro2.Alert;
import com.zafin.models.avro2.Alert.Builder;
import com.zafin.zplatform.proto.BuilderPopulator;
import com.zafin.zplatform.proto.Client;
import com.zafin.zplatform.proto.ConfigurationProperties;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.RevisionUtil;
import com.zafin.zplatform.proto.ServiceRegistry;
import com.zafin.zplatform.proto.SpringServiceRegistryEntry;
import com.zafin.zplatform.proto.TransferAvroState;
import com.zafin.zplatform.proto.exception.BuilderServiceException;
import com.zafin.zplatform.proto.factory.PayLoadFactory;
import com.zafin.zplatform.proto.service.AlertService;
import com.zafin.zplatform.proto.service.StartupArgs;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class AlertSpringConfig2 {
    
    private final Map<Object,ServiceRegistry<Alert,Alert.Builder>> allRegisteredServices = new HashMap<>();
    
    public static StartupArgs STARTUP_ARGS = null;
    
    private static final String ALERT_SERVICE = "/AlertService2";
    
    private ConfigurationProperties config = new ConfigurationProperties(2);
    
    @Autowired
    private BuilderPopulator<?,?> alertBuilderPopulator;
    
    public AlertSpringConfig2() {
        System.out.println("Starting [" + this.getClass().getCanonicalName() + "]...");
    }
    
    @Component
    public class ServerPortCustomizer 
      implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
      
        @Override
        public void customize(ConfigurableWebServerFactory factory) {
        	System.out.println("Configuring port for " + AlertSpringConfig2.class.getSimpleName() + "...");
        	int port = 8080 + RevisionUtil.getRevisionFromClassName(AlertSpringConfig2.class.getSimpleName());
        	System.out.println("Port for " + AlertSpringConfig2.class.getSimpleName() + " is " + port);
        	factory.setPort(port);
        }
    }
    
    @Bean
    BuilderPopulator<com.zafin.models.avro1.Alert,com.zafin.models.avro1.Alert.Builder> alertBuilderPopulator1() throws BuilderServiceException {
    	//Example of how builder populators get 'stacked'
    	AlertBuilderPopulator1<com.zafin.models.avro1.Alert,com.zafin.models.avro1.Alert.Builder> bp1 = 
    			new AlertBuilderPopulator1<>();
    	return bp1;
    }
    
    @Bean
    ServiceRegistry<Alert,Alert.Builder> serviceRegistry2() throws ClassNotFoundException {
        @SuppressWarnings("unchecked")
        SpringServiceRegistryEntry<Alert,Alert.Builder> registry = SpringServiceRegistryEntry.builder()
                .setSpringConfig(AlertSpringConfig2.class.getCanonicalName())
                .setArgs(STARTUP_ARGS)
                .build();
        allRegisteredServices.put(registry.getRegistryKey(), registry);
        return registry;
    }
    
	@Bean(name = ALERT_SERVICE)
    //@Qualifier("alertService")
    AlertService2 alertService2() throws BuilderServiceException {
        System.out.println("Spring Creating: " + HttpInvokerServiceExporter.class.getCanonicalName());
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        	AlertService2 alertService = new AlertService2() {

				@Override
				public com.zafin.models.avro2.Alert build() {
					return getBuilder().build();
				}

				@Override
				public com.zafin.models.avro2.Alert.Builder getNativeBuilder() {
					return getBuilder();
				}

				@Override
				public com.zafin.models.avro2.Alert build(Object builder) {
					 return ((com.zafin.models.avro2.Alert.Builder)builder).build();
				}
        	
        };
        //TODO: Fugly setup
        AlertBuilderPopulator2<com.zafin.models.avro2.Alert,com.zafin.models.avro2.Alert.Builder> abp2 = new AlertBuilderPopulator2<>();
        
        com.zafin.models.avro2.Alert.Builder newBuilder = com.zafin.models.avro2.Alert.newBuilder();
        abp2.setCurrentBuilder(newBuilder);
        
        AlertBuilderPopulator1<com.zafin.models.avro1.Alert,com.zafin.models.avro1.Alert.Builder> abp1 = new AlertBuilderPopulator1<>();
        
        com.zafin.models.avro1.Alert.Builder oldBuilder = com.zafin.models.avro1.Alert.newBuilder();
        abp1.setCurrentBuilder(oldBuilder);
        
        TransferAvroState<com.zafin.models.avro1.Alert.Builder,com.zafin.models.avro2.Alert.Builder> ts = 
        		new TransferAvroState<com.zafin.models.avro1.Alert.Builder,com.zafin.models.avro2.Alert.Builder>(oldBuilder,newBuilder) {

					@Override
					public Builder populateAvroBuilder(GenericRecordBuilder genericRecordBuilder) throws BuilderServiceException {
						PayLoad payload = alertBuilderPopulator.createPayloadFrom(genericRecordBuilder.build());
						List<?> list=alertBuilderPopulator.seedOldBuilderFirst(payload);
						Builder seededBuilder = (Builder) list.get(list.size()-1); 
						return seededBuilder;
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
        abp2.setTransferState(ts);
        alertService.setBuilderPopulator(abp2);
        
        alertService.getBuilderPopulator().setPreviousPopulator(abp1);
        alertService.setBuilder(Alert.newBuilder());
        
        exporter.setService(alertService);
        exporter.setServiceInterface(AlertService.class);
        return alertService;
    }
    
    @Bean(name="alertClient2")
    Client<Alert,Alert.Builder> client2() {
        System.out.println("Configuration revision is [" + config.getProtocolRevision() + "].");
        if (config.getProtocolRevision() == 2) {
            return new AlertSpringClient2();
        }
        throw new IllegalStateException("Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }
    
    @Bean(name="testPayLoad2")
    PayLoad testPayLoad2() throws BuilderServiceException {
        if (config.getProtocolRevision() == 2) {
            return new AlertTestPayLoad2<Alert>();
        }
        throw new IllegalStateException("Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }
    
	@Bean
    PayLoadFactory<Alert> payLoadFactory2() throws BuilderServiceException {
        if (config.getProtocolRevision() == 2) {
            return new com.zafin.zplatform.proto.alert.AlertPayLoadFactory2<>();
        }
        throw new IllegalStateException("Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }
    
    public static void main(String[] args) {
        STARTUP_ARGS = new StartupArgs(args);
        System.out.println("Alert Config main()...");
    }
}
