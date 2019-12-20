package documentTreeModel.implementation;

import java.util.UUID;

import documentTreeModel.interfaces.IPeerBoxResponseDocumentBoxMigrated;

public class PeerBoxResponseDocumentBoxMigrated extends PeerBoxResponse implements IPeerBoxResponseDocumentBoxMigrated
{

	public PeerBoxResponseDocumentBoxMigrated(UUID requestId, ResponseType respType, Object privateData, long migrationTarget ) 
	{
		super(requestId, respType, privateData);
		
		_migrationTarget = migrationTarget;
	}

	long _migrationTarget; 	
	
	@Override
	public long MigrationTarget() 
	{
		return _migrationTarget;
	}

}
