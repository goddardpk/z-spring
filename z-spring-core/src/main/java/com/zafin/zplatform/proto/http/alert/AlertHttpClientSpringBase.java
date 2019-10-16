package com.zafin.zplatform.proto.http.alert;

import com.zafin.zplatform.proto.RevisionUtil;
import com.zafin.zplatform.proto.http.HttpClientBase;
import com.zafin.zplatform.proto.service.AlertService;

public abstract class AlertHttpClientSpringBase<T,B>
    extends HttpClientBase<T,B>
    implements AlertHttpClient<T,B> {
    
    private static int BASE_PORT = 8080;
    
    private int getAdjustedPort() {
        return BASE_PORT + getRevision();
    }
    
    private int getRevision() {
        return RevisionUtil.getRevisionFromClassName(this.getClass().getSimpleName());
    }
    
    public final String LOCAL_URL = "http://localhost:" + getAdjustedPort() + "/";
    
    private String DEFAULT_LOCAL_URL = LOCAL_URL + AlertService.class.getSimpleName();
    
    public static final Class<?>  DEFAULT_INTERFACE = AlertService.class;
    
    public String getAlertUrl() {
        return DEFAULT_LOCAL_URL;
    }
    
    public AlertHttpClientSpringBase(Class<?> interfaceClazz, String url,Class<T> classOfObject, Class<B> classOfBuilder) {
    	super(classOfObject,classOfBuilder);
        if (interfaceClazz == null) {
            System.out.println(this.getClass().getSimpleName() + ": Using default interface: [" + DEFAULT_INTERFACE.getSimpleName() + "].");
            interfaceClazz = DEFAULT_INTERFACE;
        }
        setServiceUrl(getAlertUrl());
        setServiceInterface(interfaceClazz);
    }
}