package com.zafin.zplatform.proto.alert;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import com.zafin.models.avro2.Alert;
import com.zafin.zplatform.proto.Builder;
import com.zafin.zplatform.proto.BuilderBase;
import com.zafin.zplatform.proto.BuilderPopulator;
import com.zafin.zplatform.proto.Client;
import com.zafin.zplatform.proto.ConfigurationProperties;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.PayLoadFactory;
import com.zafin.zplatform.proto.ServiceRegistry;
import com.zafin.zplatform.proto.SpringServiceRegistryEntry;
import com.zafin.zplatform.proto.service.AlertService;
import com.zafin.zplatform.proto.service.StartupArgs;

@Configuration
//@ComponentScan
@EnableAutoConfiguration
public class AlertSpringConfig2<T,B> {
    
    private final Map<Object,ServiceRegistry<T,B>> allRegisteredServices = new HashMap<>();
    
    public static StartupArgs STARTUP_ARGS = null;
    
    private static final String ALERT_SERVICE = "AlertService";
    
    private ConfigurationProperties config = new ConfigurationProperties(2);
    
    public AlertSpringConfig2() {
        System.out.println("Starting [" + this.getClass().getCanonicalName() + "]...");
    }
    @Bean
    ServiceRegistry<T,B> serviceRegistry() throws ClassNotFoundException {
        @SuppressWarnings("unchecked")
        SpringServiceRegistryEntry<T,B> registry = SpringServiceRegistryEntry.builder()
                .setSpringConfig(AlertSpringConfig2.class.getCanonicalName())
                .setArgs(STARTUP_ARGS)
                .build();
        allRegisteredServices.put(registry.getRegistryKey(), registry);
        return registry;
    }
    @Bean
    Builder<T,B> builder() {
       return  new BuilderBase<T,B>(new AlertBuilderPopulator1<T,B>()) {
           //Hook to a specific a specific Alert (version 1)
           @SuppressWarnings("unchecked")
           @Override
           public B createNewNativeBuilder() {
               return builder = (B)Alert.newBuilder();
           }

           @SuppressWarnings("unchecked")
           @Override
           public T build() {
               //Hook to a specific a specific Alert.Builder (version 1)
               return (T) ((Alert.Builder) builder).build();
           }
       };

    }
    
    @Bean
    BuilderPopulator<T,B> builderPopulator() {
        if (config.getProtocolRevision() == 2) {
            return new AlertBuilderPopulator2<T,B>();
        }
        throw new IllegalStateException("Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
       
    }
    
    @Bean(name = "/" + ALERT_SERVICE)
    AlertService<T,B> alertService() {
        System.out.println("Spring Creating: " + HttpInvokerServiceExporter.class.getCanonicalName());
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        AlertService<T,B> alertService1 = new AlertServiceBase<>();
        exporter.setService(alertService1);
        exporter.setServiceInterface(AlertService.class);
        return alertService1;
    }
    
    @Bean(name="alertClient")
    Client<T,B> client() {
        System.out.println("Configuration revision is [" + config.getProtocolRevision() + "].");
        if (config.getProtocolRevision() == 2) {
            return new AlertSpringClient2<>();
        }
        throw new IllegalStateException("Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }
    
    @Bean(name="testPayLoad")
    PayLoad testPayLoad() {
        if (config.getProtocolRevision() == 2) {
            return new AlertTestPayLoad2();
        }
        throw new IllegalStateException("Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }
    
    @Bean
    PayLoadFactory payLoadFactory() {
        if (config.getProtocolRevision() == 2) {
            return new com.zafin.zplatform.proto.alert.AlertPayLoadFactory2();
        }
        throw new IllegalStateException("Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }
    
    public static void main(String[] args) {
        STARTUP_ARGS = new StartupArgs(args);
        System.out.println("Alert Config main()...");
    }
}
