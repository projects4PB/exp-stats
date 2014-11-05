package statsapp.loaders;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import statsapp.data.RecordData;
import statsapp.data.TableData;

/**
 *
 * @author Adrian Olszewski
 */
public class TextFileLoader implements Loader
{
    private int classColumnIndex = -1;
    
    @Override
    public TableData loadData(String filePath, boolean loadClassAttr, String columnName)
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
            
			String[] firstLine = this.splitValues(fileLine, loadClassAttr);                    
            		
            String[] colNames = new String[firstLine.length];
            //String[] colNames = this.splitValues(fileLine, loadClassAttr);

            for(int i = 0; i < firstLine.length; i++)
            {                
                colNames[i] = "attr" + (i + 1);
            }
			
            RecordData recData = new RecordData(
                            this.parseData(colNames, firstLine)
            );
            tableData.addRecord(recData);
            
            tableData.setColumnsNames(colNames);
            
            while((fileLine = buffReader.readLine()) != null)
            {
                if(fileLine.trim().startsWith("#")) continue;
                
                String[] splittedValues = this.splitValues(fileLine, loadClassAttr);
                                              
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
    
    private String[] splitValues(String fileLine, boolean dropLastColumn)
    {
        if(dropLastColumn == false)
        {
            return fileLine.split("\\s+");
        }
        
        String [] SplitValues = fileLine.split("\\s+");
        String [] SplitValues_pom = new String [SplitValues.length -1];

        for(int i = 0 ; i < SplitValues.length -1; i++)
        {
            SplitValues_pom[i] = SplitValues[i];              
        }

        return SplitValues_pom;                    
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
            
            else if(values[i].matches ("\\d+\\,\\d+"))            
            {
				String floatFormat = values[i].replace(",", ".");
                parsedData.put(colNames[i], Float.parseFloat(floatFormat));            
            }
            else if(values[i].matches ("\\,\\d+"))            
            {                
				String floatFormat = values[i].replace(",", ".");
                parsedData.put(colNames[i], Float.parseFloat(floatFormat));            
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
