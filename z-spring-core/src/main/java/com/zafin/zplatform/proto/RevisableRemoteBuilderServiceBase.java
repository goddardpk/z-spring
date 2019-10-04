package com.zafin.zplatform.proto;

import static com.zafin.zplatform.proto.RevisionUtil.getRevisionFromClassName;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

public class RevisableRemoteBuilderServiceBase<T,B> implements RevisableBuilderService<T,B> {
    /**
     * There is a tight coupling in RPC (client know the interface it supports).
     * Ergo this service must comply with this client's service interface.
     */
//    @Autowired
//    private Client<T,B> client;
    
    @Autowired
    private BuilderPopulator<T,B> builderPopulator;

    private RevisableBuilderService<T,B> previousCompatibleBuilderService;
    
//    public Client<T,B> getClient() {
//        return client;
//    }
//    
//    public Class<?> getServiceInterface() {
//        return client.getServiceInterface();
//    }
    
    @Override
    public boolean isSubRevision(int revision) {
        return revision >= getStartingCompatibleRevision() && 
                revision <= getEndingCompatibleRevision();
    }

    @Override
    public int getStartingCompatibleRevision() {
        return getRevisionFromClassName(getClass().getSimpleName());
    }

    @Override
    public int getEndingCompatibleRevision() {
        return getRevisionFromClassName(getClass().getSimpleName());
    }

    @Override
    public RevisableBuilderService<T,B> routeTo(Map<String, Object> props) {
        return null;
    }
    
    public RevisableBuilderService<T, B> getPreviousCompatibleService() {
        return previousCompatibleBuilderService;
    }

    public void setPreviousCompatibleService(RevisableBuilderService<T, B> previousCompatibleBuilderService) {
        this.previousCompatibleBuilderService = previousCompatibleBuilderService;
    }

    @Override
    public Builder<T, B> seedBuilder(PayLoad payload, Builder<T, B> builder) throws BuilderServiceException {
        return builderPopulator.seedBuilder(payload, builder);
    }

    @Override
    public T build(PayLoad payLoad, Builder<T, B> builder) throws BuilderServiceException {
        return builder.feedMe(payLoad).build();
    }

    @Override
    public BuilderPopulator<T, B> getBuilderPopulator() {
        return builderPopulator;
    }
    
}
