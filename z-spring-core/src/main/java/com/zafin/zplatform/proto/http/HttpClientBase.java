package com.zafin.zplatform.proto.http;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import com.zafin.zplatform.proto.ClientBase;
/**
 * Protect system from framework classes (Spring HTTP)
 * 
 * @author Paul Goddard
 *
 * @param <T>
 * @param <B>
 */
public abstract class HttpClientBase<T,B,O> extends ClientBase<T,B,O> {
    
    public HttpClientBase(Class<T> typeOfObject, Class<B> typeOfCurrentBuilder, Class<O> typeOfPreviousBuilder) {
		super(typeOfObject, typeOfCurrentBuilder,typeOfPreviousBuilder);
		// TODO Auto-generated constructor stub
	}

	protected String serviceUrl;
   
    //Hide invocation mechanism from clients
    
    private HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
    
    private Object service;
    
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        invoker.setServiceUrl(serviceUrl);
    }
    
    public HttpInvokerProxyFactoryBean invoker() {
        return invoker;
    }
    
    @Override
    public void setServiceInterface(Class<?> serviceInterface) {
        super.setServiceInterface(serviceInterface);
        invoker.setServiceInterface(serviceInterface);
    }
    
    @Override
    public void startService(Class<?> clazz) {
        if (service != null) {
            throw new IllegalStateException(clazz.getCanonicalName() + " has already started.");
        }
        if (service == null) {
            ConfigurableApplicationContext context =  SpringApplication.run(clazz);
            service = context.getBean(clazz);
        } else {
            throw new IllegalArgumentException("Service [" + service.getClass().getSimpleName() + "] already started.");
        }
    }
    
    public String getServiceUrl() {
        return serviceUrl;
    }
  
}
