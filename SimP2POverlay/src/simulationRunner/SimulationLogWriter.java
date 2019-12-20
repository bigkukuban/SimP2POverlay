package simulationRunner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import documentBoxInitializer.interfaces.IDocumentBoxNetworkInitializer;
import documentTreeModel.implementation.GlobalSimulationParameters;
import networkInitializer.interfaces.INetworkInitializer;

public class SimulationLogWriter 
{

	private String _fileNamePrefix = "MigrationSimulation.csv";
	private String _path = System.getProperty("user.dir")+"//";
	
	
	private File PrepareFile()
	{
		System.out.println(""+_path);
		
		String fullPath = _path+_fileNamePrefix;		
		File f = new File(fullPath);
		
		// Open file, write simulation parameters into it
		int z =0;
		File backUpfileRenamed = new File(_path+"BackUp"+z+".csv");
		while(backUpfileRenamed.exists())
		{
			backUpfileRenamed = new File(_path+"BackUp"+z+".csv");			
			z++;
		}	
		
		f.renameTo(backUpfileRenamed);
		
		return f;
	}
	
	
	private String GetGlobalParametersDescription()
	{
		String result  = "";
		
		if(GlobalSimulationParameters.bUseLRDCache) {
			result = result + "Cache type : LRDCache ";
		}
		
		if(GlobalSimulationParameters.bUseLRUCache) {
			result = result + " Cache type : LRUCache";
		}
		result = result + " AuthenticationRepeatRatio : "+GlobalSimulationParameters.AuthenticationRepeatRatio +" ";
		result = result + " ForwardPointerCacheLength : "+GlobalSimulationParameters.ForwardPointerCacheLength +" ";		
		result = result + " GarbageCollectionRatio : "+GlobalSimulationParameters.GarbageCollectionRatio +" ";		
		result = result + " MaximalNumberOfAllowedDocumentBoxesOnPeerBox : "+GlobalSimulationParameters.MaximalInitialNumberOfAllowedDocumentBoxesOnPeerBox +" ";
		result = result + " MeanInitialAuthenticationDuration : "+GlobalSimulationParameters.MeanInitialAuthenticationDuration +" ";
		result = result + " NumberOfItemsInSimulation : "+GlobalSimulationParameters.NumberOfItemsInSimulation +" ";
		result = result + " NumerOfUsedThreads : "+GlobalSimulationParameters.NumerOfUsedThreads +" ";
		result = result + " TimeoutForDocumentBoxMessageBeginAuthState : "+GlobalSimulationParameters.TimeoutForDocumentBoxMessageBeginAuthState +" ";
		result = result + " TimeoutForDocumentBoxMessageResponse : "+GlobalSimulationParameters.TimeoutForDocumentBoxMessageResponse +" ";
		result = result + " DisturbeAuthentificationAfterMigration : "+GlobalSimulationParameters.DisturbeAuthentificationAfterMigration+"  (Probability : 0.5)";
		result = result + " TimeoutForDocumentBoxResentDuringMigration : "+GlobalSimulationParameters.TimeoutForDocumentBoxResentDuringMigration +" \n";
		
		
		
		return result;
	}
	
	public void BeginNewSimulation(INetworkInitializer nwInit, IDocumentBoxNetworkInitializer dbInit)
	{
		File f = PrepareFile();
		_bSimulationParametersWritten = false;
		try {
			Writer  output = new BufferedWriter(new FileWriter(f.getPath(), true));
			output.write(nwInit.GetReadableDescription()+" \n");
			output.write(dbInit.GetReadableDescription()+" \n");						
			output.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	boolean _bSimulationParametersWritten = false;
	public void WriteSimulationParameters()
	{		
		
		if(_bSimulationParametersWritten ) return;
		_bSimulationParametersWritten = true;
		String fullPath = _path+_fileNamePrefix;	
		File f = new File(fullPath);
		try {
			Writer  output = new BufferedWriter(new FileWriter(f.getPath(), true));		
			output.write(GetGlobalParametersDescription()+" \n");	
			output.write("SimulationStep;NumberOfMigrations;TopologyErrorDetected\n");	
			output.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void EndUpSimulation()
	{
		
	}
	
	public void HandleNewSimulationStep(int simulationStep, int numberOfMigrations,long averageChannelLength, boolean isTopologyErrorDetected)
	{
		
		try {
			
			String fullPath = _path+_fileNamePrefix;	
			File f = new File(fullPath);
			
			Writer  output = new BufferedWriter(new FileWriter(f.getPath(), true));
			output.write(simulationStep+","+numberOfMigrations+","+isTopologyErrorDetected+","+averageChannelLength+"\n");					
			output.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
