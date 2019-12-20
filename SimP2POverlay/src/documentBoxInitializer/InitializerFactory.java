package documentBoxInitializer;

import documentBoxInitializer.BinaryTree.DocumentBoxBinaryTreeInitializer;
import documentBoxInitializer.BinaryTree.DocumentBoxBinaryTreeSettings;
import documentBoxInitializer.baPreferentialAttachment.DocumentBoxBaPreferentialAttachmentInitializer;
import documentBoxInitializer.baPreferentialAttachment.DocumentBoxBaPreferentialAttachmentSettings;
import documentBoxInitializer.interfaces.IDocumentBoxNetworkInitializer;

public class InitializerFactory {
	
	public static IDocumentBoxNetworkInitializer CreateBySettingsType(DocumentBoxInitializerSettingsBase settings) throws Exception
	{
		if(settings instanceof DocumentBoxBinaryTreeSettings)
		{
			return new DocumentBoxBinaryTreeInitializer((DocumentBoxBinaryTreeSettings)settings);
		}
		
		if(settings instanceof DocumentBoxBaPreferentialAttachmentSettings)
		{
			return new DocumentBoxBaPreferentialAttachmentInitializer((DocumentBoxBaPreferentialAttachmentSettings)settings);
		}
		
		return null;
	}
}
