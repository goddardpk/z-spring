package com.zafin.zplatform.proto;

import static com.zafin.zplatform.proto.RevisionUtil.getRevisionFromClassName;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.zafin.zplatform.proto.exception.BuilderServiceException;
import com.zafin.zplatform.proto.service.RemoteBuilderService;
import com.zafin.zplatform.proto.service.RevisableBuilderService;

public class RevisableSpringBuilderServiceBase<T,B> implements RevisableBuilderService<T,B> {
    
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
    public BuilderPopulator<T, B> getBuilderPopulator() {
        return builderPopulator;
    }

	@Override
	public int getRevision() {
		return RevisionUtil.getRevisionFromClassName(getClass().getSimpleName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public RemoteBuilderService<T, B> setup(RemoteBuilderService<?, ?> remoteBuilderService) {
		return (RemoteBuilderService<T, B>) remoteBuilderService.setup(remoteBuilderService);
	}

	@Override
	public void setPreviousPopulator(BuilderPopulator<?, ?> previous) {
		builderPopulator.setPreviousPopulator(previous);
		
	}

	@Override
	public BuilderPopulator<?, ?> getPreviousPopulator() {
		return previousCompatibleBuilderService;
	}

	@Override
	public B getCurrentBuilder() {
		return builderPopulator.getCurrentBuilder();
	}

	@Override
	public void setCurrentBuilder(B builder) {
		builderPopulator.setCurrentBuilder(builder);
	}

	@Override
	public TransferState<?, ?> getTransferState() {
		return builderPopulator.getTransferState();
	}

	@Override
	public PayLoad loadTestPayLoad(PayLoad payload) {
		return builderPopulator.loadTestPayLoad(payload);
	}

	@Override
	public T build(Object builder) throws BuilderServiceException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public boolean transferStateForward(PayLoad payload) throws BuilderServiceException {
		return builderPopulator.transferStateForward(payload);
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
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public B seed(PayLoad payload)
			throws BuilderServiceException {
		return (B) builderPopulator.seed(payload);
	}
	
	@Override
	public void setTransferState(TransferState<?,?> transferState) {
		builderPopulator.setTransferState(transferState);
	}
    
}
