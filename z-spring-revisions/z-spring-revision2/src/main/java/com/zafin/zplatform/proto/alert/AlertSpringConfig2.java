package com.zafin.zplatform.proto.alert;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import com.zafin.models.avro2.Alert;
import com.zafin.zplatform.proto.Client;
import com.zafin.zplatform.proto.ConfigurationProperties;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.ServiceRegistry;
import com.zafin.zplatform.proto.SpringServiceRegistryEntry;
import com.zafin.zplatform.proto.exception.BuilderServiceException;
import com.zafin.zplatform.proto.factory.PayLoadFactory;
import com.zafin.zplatform.proto.factory.ProtoFactory;
import com.zafin.zplatform.proto.service.AlertService;
import com.zafin.zplatform.proto.service.RemoteBuilderService;
import com.zafin.zplatform.proto.service.StartupArgs;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class AlertSpringConfig2 {
    
    private final Map<Object,ServiceRegistry<Alert,Alert.Builder>> allRegisteredServices = new HashMap<>();
    
    public static StartupArgs STARTUP_ARGS = null;
    
    private static final String ALERT_SERVICE = "AlertService";
    
    private ConfigurationProperties config = new ConfigurationProperties(2);
    
    @Autowired
    @Qualifier("protoFactory2")
    private ProtoFactory<Alert,Alert.Builder> protofactory;
    
    public AlertSpringConfig2() {
        System.out.println("Starting [" + this.getClass().getCanonicalName() + "]...");
    }
    
    @Bean
    ServiceRegistry<Alert,Alert.Builder> serviceRegistry() throws ClassNotFoundException {
        @SuppressWarnings("unchecked")
        SpringServiceRegistryEntry<Alert,Alert.Builder> registry = SpringServiceRegistryEntry.builder()
                .setSpringConfig(AlertSpringConfig2.class.getCanonicalName())
                .setArgs(STARTUP_ARGS)
                .build();
        allRegisteredServices.put(registry.getRegistryKey(), registry);
        return registry;
    }
    
    @Bean(name = "/" + ALERT_SERVICE)
    @Qualifier("alertService")
    RemoteBuilderService<Alert,Alert.Builder> alertService() throws BuilderServiceException {
        System.out.println("Spring Creating: " + HttpInvokerServiceExporter.class.getCanonicalName());
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        RemoteBuilderService<Alert,Alert.Builder> alertService2 =  protofactory.createService(Alert.class);
        exporter.setService(alertService2);
        exporter.setServiceInterface(AlertService.class);
        return alertService2;
    }
    
    @Bean(name="protoFactory2")
    ProtoFactory<Alert,Alert.Builder> protoFactory() {
        return new AlertProtoFactory<>();
    }
    
    @Bean(name="alertClient")
    Client<Alert,Alert.Builder> client() {
        System.out.println("Configuration revision is [" + config.getProtocolRevision() + "].");
        if (config.getProtocolRevision() == 2) {
            return new AlertSpringClient2();
        }
        throw new IllegalStateException("Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }
    
    @Bean(name="testPayLoad2")
    PayLoad testPayLoad() throws BuilderServiceException {
        if (config.getProtocolRevision() == 2) {
            return new AlertTestPayLoad2<Alert>();
        }
        throw new IllegalStateException("Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }
    
	@Bean
    PayLoadFactory<Alert> payLoadFactory() throws BuilderServiceException {
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
