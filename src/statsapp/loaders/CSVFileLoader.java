package statsapp.loaders;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import statsapp.data.RecordData;
import statsapp.data.TableData;

/**
 *
 * @author Adrian Olszewski, Dariusz Obuchowski
 */
public class CSVFileLoader implements Loader
{
	@Override
	public TableData loadData(String filePath,
			boolean hasColumnsHeader,
			boolean loadClassFlag,
			String classColumnName)
	{
TableData tableData = new TableData();
        
        FileReader fileReader = null;
        try
        {
            fileReader = new FileReader(filePath);
        }
        catch(FileNotFoundException ex)
        {
            ex.printStackTrace(System.err);
        }
        if(fileReader == null) return null;
        
        BufferedReader buffReader = new BufferedReader(fileReader);
        try
        {
            String fileLine = buffReader.readLine();
            
            while(fileLine.trim().startsWith("#")
					|| fileLine.trim().length() == 0)
            {
                fileLine = buffReader.readLine();
            }
            
			String[] colNames;

			if(hasColumnsHeader)
			{
				colNames = this.splitValues(fileLine, loadClassFlag);
			}
			else 
			{
				String[] firstLine = this.splitValues(fileLine, loadClassFlag);                    
						
				colNames = new String[firstLine.length];

				for(int i = 0; i < firstLine.length - 1; i++)
				{                
					colNames[i] = "attr" + (i + 1);
				}
				colNames[firstLine.length - 1] = "class";
				
				RecordData recData = new RecordData(
						this.parseData(colNames, firstLine)
				);
				tableData.addRecord(recData);
			}
            tableData.setColumnsNames(colNames);
            
            while((fileLine = buffReader.readLine()) != null)
            {
                if(fileLine.trim().startsWith("#")) continue;
                
                String[] splittedValues = this.splitValues(fileLine, loadClassFlag);
                                              
                RecordData recordData = new RecordData(
                        this.parseData(colNames, splittedValues)
                );
                tableData.addRecord(recordData);
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace(System.err);
        }
        try
        {
            fileReader.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace(System.err);
        }
        return tableData;
    }
    
    private String[] splitValues(String fileLine, boolean loadClassFlag)
    {
        if(loadClassFlag == true)
        {
            return fileLine.split(",");
        }
        
        String [] splitValues = fileLine.split(",");
        String [] cuttedValues = new String [splitValues.length -1];

        for(int i = 0 ; i < splitValues.length -1; i++)
        {
            cuttedValues[i] = splitValues[i];              
        }
        return cuttedValues;                    
    }
    
    private HashMap<String, Object> parseData(
			String[] colNames, String[] values)
    {
        if(colNames.length != values.length) return null;
        
        HashMap<String, Object> parsedData = new HashMap<>();
        
        for(int i = 0; i < values.length; i++)
        {
            if(values[i].matches("\\-\\d+\\.\\d+"))
            {
                parsedData.put(colNames[i], Float.parseFloat(values[i]));
            }
            
			else if(values[i].matches("\\d+\\.\\d+"))
            {
                parsedData.put(colNames[i], Float.parseFloat(values[i]));
            }
			
			else if(values[i].matches("\\-\\.\\d+"))
            {
                parsedData.put(colNames[i], Float.parseFloat(values[i]));
            }
			
			else if(values[i].matches("\\.\\d+"))
            {
                parsedData.put(colNames[i], Float.parseFloat(values[i]));
            }

            else if(values[i].matches ("\\d+"))            
            {                
                parsedData.put(colNames[i], Float.parseFloat(values[i]));            
            }

            else parsedData.put(colNames[i], values[i]);
        }
        return parsedData;
    }
}
