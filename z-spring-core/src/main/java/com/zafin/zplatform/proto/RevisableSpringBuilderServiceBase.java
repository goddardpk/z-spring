package com.zafin.zplatform.proto;

import static com.zafin.zplatform.proto.RevisionUtil.getRevisionFromClassName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.zafin.zplatform.proto.exception.BuilderServiceException;
import com.zafin.zplatform.proto.service.RemoteBuilderService;
import com.zafin.zplatform.proto.service.RevisableBuilderService;

public class RevisableSpringBuilderServiceBase<T,B,O> implements RevisableBuilderService<T,B,O> {
    
    @Autowired
    private BuilderPopulator<T,B,O> builderPopulator;

    private RevisableBuilderService<?,O,?> previousCompatibleBuilderService;
    
    @Override
    public boolean isSubRevision(int revision) throws BuilderServiceException {
        return revision >= getStartingCompatibleRevision() && 
                revision <= getEndingCompatibleRevision();
    }

    @Override
    public int getStartingCompatibleRevision() throws BuilderServiceException {
        return getRevisionFromClassName(getClass().getSimpleName());
    }

    @Override
    public int getEndingCompatibleRevision() throws BuilderServiceException {
        return getRevisionFromClassName(getClass().getSimpleName());
    }

    @Override
    public RevisableBuilderService<T,B,O> routeTo(Map<String, Object> props) {
        return null;
    }
    
    public RevisableBuilderService<?,O,?> getPreviousCompatibleService() {
        return previousCompatibleBuilderService;
    }

    public void setPreviousCompatibleService(RevisableBuilderService<?,O,?> previousCompatibleBuilderService) {
        this.previousCompatibleBuilderService = previousCompatibleBuilderService;
    }

    @Override
    public BuilderPopulator<T,B,O> getBuilderPopulator() {
        return builderPopulator;
    }

	@Override
	public int getRevision() throws BuilderServiceException {
		return RevisionUtil.getRevisionFromClassName(getClass().getSimpleName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public RemoteBuilderService<T,B,O> setup(RemoteBuilderService<?,?,?> remoteBuilderService) {
		return (RemoteBuilderService<T,B,O>) remoteBuilderService.setup(remoteBuilderService);
	}

	@Override
	public void setPreviousPopulator(BuilderPopulator<?,O,?> previous) throws BuilderServiceException {
		builderPopulator.setPreviousPopulator(previous);
		
	}

	@Override
	public BuilderPopulator<?,O,?> getPreviousPopulator() {
		return previousCompatibleBuilderService;
	}

	@Override
	public B getCurrentBuilder() {
		return builderPopulator.getCurrentBuilder();
	}

	@Override
	public void setCurrentBuilder(B builder) throws BuilderServiceException {
		builderPopulator.setCurrentBuilder(builder);
	}

	@Override
	public TransferState<T,B,O> getTransferState() {
		return builderPopulator.getTransferState();
	}

	@Override
	public PayLoad loadTestPayLoad(PayLoad payload) throws BuilderServiceException {
		return builderPopulator.loadTestPayLoad(payload);
	}

	@Override
	public T build(Object builder) throws BuilderServiceException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public boolean canConvert(Object object, Class<?> toType) {
		return builderPopulator.canConvert(object, toType);
	}

	@Override
	public Object convert(Object object, Class<?> toType) throws BuilderServiceException {
		return builderPopulator.convert(object, toType);
	}

	@Override
	public TypeConverter getTypeConverter() {
		return builderPopulator.getTypeConverter();
	}

	@Override
	public List<Class<?>> getSupportedTypes() throws BuilderServiceException {
		return builderPopulator.getSupportedTypes();
	}

	@Override
	public String getSupportedPackageName() throws BuilderServiceException {
		return builderPopulator.getSupportedPackageName();
	}

	@Override
	public List<?> seedOldBuilderFirst(PayLoad payload) throws BuilderServiceException {
		List<Object> seededBuilders = new ArrayList<>();
		if (previousCompatibleBuilderService != null) {
			seededBuilders.addAll(previousCompatibleBuilderService.seedOldBuilderFirst(payload));
		}
		return seededBuilders;
	}

	@Override
	public B seed(Object payload)
			throws BuilderServiceException {
		return (B) builderPopulator.seed(payload);
	}
	
	@Override
	public void setTransferState(TransferState<T,B,O> transferState) throws BuilderServiceException {
		builderPopulator.setTransferState(transferState);
	}

	@Override
	public boolean isInitialRevision() throws BuilderServiceException {
		return builderPopulator.isInitialRevision();
	}

	@Override
	public BuilderPopulator<?, ?, B> getNextPopulator() {
		return builderPopulator.getNextPopulator();
	}

	@Override
	public void setNextPopulator(BuilderPopulator<?, ?, B> nextPopulator) throws BuilderServiceException {
		builderPopulator.setNextPopulator(nextPopulator);
	}

	@Override
	public Object getValue(Object objectKey, Object map) throws BuilderServiceException {
		return builderPopulator.getValue(objectKey, map);
	}

	@Override
	public B set(Object objectKey, Object value, B newBuilder) throws BuilderServiceException {
		return builderPopulator.set(objectKey, value, newBuilder);
	}


	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}
}
