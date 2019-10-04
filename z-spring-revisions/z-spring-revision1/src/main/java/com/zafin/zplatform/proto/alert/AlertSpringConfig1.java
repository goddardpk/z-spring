package com.zafin.zplatform.proto.alert;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import com.zafin.models.avro1.Alert;
import com.zafin.zplatform.proto.Builder;
import com.zafin.zplatform.proto.BuilderBase;
import com.zafin.zplatform.proto.BuilderPopulator;
import com.zafin.zplatform.proto.ConfigurationProperties;
import com.zafin.zplatform.proto.PayLoad;
import com.zafin.zplatform.proto.PayLoadFactory;
import com.zafin.zplatform.proto.ServiceRegistry;
import com.zafin.zplatform.proto.SpringServiceRegistryEntry;
import com.zafin.zplatform.proto.service.AlertService;
import com.zafin.zplatform.proto.service.StartupArgs;

//@Configuration
//@ComponentScan
//@EnableAutoConfiguration
public class AlertSpringConfig1<T, B> {
    private final Map<Object,ServiceRegistry<T,B>> allRegisteredServices = new HashMap<>();
    
    public static StartupArgs STARTUP_ARGS = null;
    private static final String ALERT_SERVICE = "AlertService";

    private ConfigurationProperties config = new ConfigurationProperties(1);
    
    public AlertSpringConfig1() {
        System.out.println("Starting [" + this.getClass().getCanonicalName() + "]...");
    }
    
    @Bean
    ServiceRegistry<T,B> serviceRegistry() throws ClassNotFoundException {
        System.out.println("Calling " + getClass().getSimpleName() + ".serviceRegistry()...");
        @SuppressWarnings("unchecked")
        SpringServiceRegistryEntry<T,B> registry = SpringServiceRegistryEntry.builder()
                .setSpringConfig(AlertSpringConfig1.class.getCanonicalName())
                .setArgs(STARTUP_ARGS)
                .build();
        allRegisteredServices.put(registry.getRegistryKey(), registry);
        return registry;
    }
    
    @Bean
    Builder<T,B> builder() {
       return  new BuilderBase<T,B>(new AlertBuilderPopulator1<T,B>()) {

            @SuppressWarnings("unchecked")
            @Override
            public B createNewNativeBuilder() {
                return builder = (B)Alert.newBuilder();
            }

            @SuppressWarnings("unchecked")
            @Override
            public T build() {
                return (T) ((Alert.Builder) builder).build();
            }
        };
    }

    @Bean
    BuilderPopulator<T, B> builderPopulator() {
        if (config.getProtocolRevision() == 1) {
            return new AlertBuilderPopulator1<>();
        }
        throw new IllegalStateException(
                "Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }

    @Bean(name = "/" + ALERT_SERVICE)
    AlertService<T, B> alertService() {
        System.out.println("Spring Creating: " + HttpInvokerServiceExporter.class.getCanonicalName());
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        AlertService<T, B> alertService1 = new AlertServiceBase<>();
        exporter.setService(alertService1);
        exporter.setServiceInterface(AlertService.class);
        return alertService1;
    }

    @Bean(name = "alertClient")
    AlertSpringClient1<T, B> proxy() {
        if (config.getProtocolRevision() == 1) {
            return new AlertSpringClient1<>();
        }
        throw new IllegalStateException(
                "Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }

    @Bean(name = "testPayLoad")
    PayLoad testPayLoad() {
        if (config.getProtocolRevision() == 1) {
            return new AlertTestPayLoad1();
        }
        throw new IllegalStateException(
                "Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }

    @Bean
    PayLoadFactory payLoadFactory() {
        if (config.getProtocolRevision() == 1) {
            return new AlertPayLoadFactory1();
        }
        throw new IllegalStateException(
                "Configuration protocol revision [" + config.getProtocolRevision() + "] is unsupported.");
    }

    public static void main(String[] args) {
        STARTUP_ARGS = new StartupArgs(args);
        System.out.println("Alert Config main()...");
    }
}
