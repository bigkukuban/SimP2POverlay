package persistence.interfaces;

import launcher.ApplicationModelSettings;

public interface IPersister {
	
	boolean DoPersistNetwork(ApplicationModelSettings input,String targetPath);
	ApplicationModelSettings DoRestoreNetwork(String filePath);
			
}
