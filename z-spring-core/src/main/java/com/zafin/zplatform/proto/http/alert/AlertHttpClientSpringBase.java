package com.zafin.zplatform.proto.http.alert;

import com.zafin.zplatform.proto.RevisionUtil;
import com.zafin.zplatform.proto.exception.BuilderServiceException;
import com.zafin.zplatform.proto.http.HttpClientBase;
import com.zafin.zplatform.proto.service.AlertService;

public abstract class AlertHttpClientSpringBase<T,B,O>
    extends HttpClientBase<T,B,O>
    implements AlertHttpClient<T,B,O> {
    
    private static int BASE_PORT = 8080;
    
    private int getAdjustedPort() throws BuilderServiceException {
        return BASE_PORT + getRevision();
    }
    
    private int getRevision() throws BuilderServiceException {
        return RevisionUtil.getRevisionFromClassName(this.getClass().getSimpleName());
    }
    
    public final String LOCAL_URL;
    
    private final String DEFAULT_LOCAL_URL;
    
    public static final Class<?>  DEFAULT_INTERFACE = AlertService.class;
    
    public String getAlertUrl() {
        return DEFAULT_LOCAL_URL;
    }
    
    public AlertHttpClientSpringBase(Class<?> interfaceClazz, String url,Class<T> classOfObject, Class<B> classOfBuilder,Class<O> classOfPreviousBuilder) throws BuilderServiceException {
    	super(classOfObject,classOfBuilder,classOfPreviousBuilder);
    	LOCAL_URL = "http://localhost:" + getAdjustedPort() + "/";
    	DEFAULT_LOCAL_URL = LOCAL_URL + AlertService.class.getSimpleName();
        if (interfaceClazz == null) {
            System.out.println(this.getClass().getSimpleName() + ": Using default interface: [" + DEFAULT_INTERFACE.getSimpleName() + "].");
            interfaceClazz = DEFAULT_INTERFACE;
        }
        setServiceUrl(getAlertUrl());
        setServiceInterface(interfaceClazz);
    }
}