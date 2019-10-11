package com.zafin.zplatform.proto;

import static com.zafin.zplatform.proto.RevisionUtil.getRevisionFromClassName;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.zafin.zplatform.proto.exception.BuilderServiceException;
import com.zafin.zplatform.proto.service.RevisableBuilderService;

public class RevisableSpringBuilderServiceBase<T,B> implements RevisableBuilderService<T,B> {
    /**
     * There is a tight coupling in RPC (client know the interface it supports).
     * Ergo this service must comply with this client's service interface.
     */
    
    @Autowired
    private BuilderPopulator<T,B> builderPopulator;

    private RevisableBuilderService<T,B> previousCompatibleBuilderService;
    
    
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
    public B seedOldBuilderFirst(PayLoad payload) throws BuilderServiceException {
        return builderPopulator.seedOldBuilderFirst(payload);
    }

    @Override
    public BuilderPopulator<T, B> getBuilderPopulator() {
        return builderPopulator;
    }

	@Override
	public T build(B builder) throws BuilderServiceException {
		return null;
	}

	@Override
	public int getRevision() {
		return RevisionUtil.getRevisionFromClassName(getClass().getSimpleName());
	}
    
}
