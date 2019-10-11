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

import com.zafin.models.avro1.Alert;
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
public class AlertSpringConfig1 {
	
    private final Map<Object,ServiceRegistry<Alert,Alert.Builder>> allRegisteredServices = new HashMap<>();
    
    @Autowired
    @Qualifier("protoFactory1")
    private ProtoFactory<Alert,Alert.Builder> protofactory;
    
    public static StartupArgs STARTUP_ARGS = null;
    private static final String ALERT_SERVICE = "AlertService";

    private ConfigurationProperties config = new ConfigurationProperties(1);
    
    public AlertSpringConfig1() {
        System.out.println("Starting [" + this.getClass().getCanonicalName() + "]...");
    }
    
    @Bean
    ServiceRegistry<Alert,Alert.Builder> serviceRegistry() {
        System.out.println("Calling " + getClass().getSimpleName() + ".serviceRegistry()...");
        @SuppressWarnings("unchecked")
        SpringServiceRegistryEntry<Alert,Alert.Builder> registry = SpringServiceRegistryEntry.builder()
                .setSpringConfig(AlertSpringConfig1.class.getCanonicalName())
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
        RemoteBuilderService<Alert,Alert.Builder> alertService1 = protofactory.createService(Alert.class);
        exporter.setService(alertService1);
        exporter.setServiceInterface(AlertService.class);
        return alertService1;
    }

    @Bean(name = "alertClient")
    AlertSpringClient1 proxy() {
        if (config.getProtocolRevision() == 1) {
            return new AlertSpringClient1();
        }
        throw new IllegalStateException(
                "Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }
    
    @Bean(name="protoFactory1")
    ProtoFactory<Alert,Alert.Builder> protoFactory() {
        return new AlertProtoFactory<>();
    }

    @Bean(name = "testPayLoad1")
    PayLoad testPayLoad() throws BuilderServiceException {
        if (config.getProtocolRevision() == 1) {
            return new AlertTestPayLoad1<Alert>();
        }
        throw new IllegalStateException(
                "Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }

    @Bean
    PayLoadFactory<Alert> payLoadFactory() throws BuilderServiceException {
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
}
