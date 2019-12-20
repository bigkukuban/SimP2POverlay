package documentTreeModel.interfaces;

 
//message between two document-boxes

public interface IDocumentBoxMessageResponse extends IDocumentBoxMessage
{
	enum ResponseTypeDocumentBox { OK, NOK,  }
	
	ResponseTypeDocumentBox GetResponseType();
}
