/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package statsapp.savers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import statsapp.data.RecordData;
import statsapp.data.TableData;
import statsapp.data.records.TableRecord;
import statsapp.managers.DataManager;
import statsapp.tables.DataTable;

/**
 *
 * @author admin
 */
public class DataSaver {
    
    DataManager dManager = DataManager.getInstance();
      
    public void saveToFile(String filePath){

        try
		{
            File file = new File(filePath);

			if (!file.exists())
			{
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(
					file.getAbsoluteFile()
			);
			BufferedWriter bw = new BufferedWriter(fw);
			
			int curr_index = 0;
			
			TableData tableData = dManager.getTableData();
			
			String[] colNames = tableData.getColumnsNames();

			StringBuilder sb = new StringBuilder();

			String delimiter = "";
			
			for (String colName : colNames)
			{
				sb.append(delimiter).append(colName);

				delimiter = ",";
			}
			bw.write(sb.toString());
			bw.write("\n");

			for(int i = 0; i < dManager.getDataList().size(); i++)
			{ 
				TableRecord tableRecord = (TableRecord) 
					dManager.getDataList().get(i);
				
				RecordData recordData = tableRecord.getRecordData();
				
				sb = new StringBuilder();

				delimiter = "";
				
				for (String colName : colNames)
				{
					Object obj = recordData.getFields().get(colName);
					
					sb.append(delimiter).append(obj.toString());

					delimiter = ",";
				}
				bw.write(sb.toString());
				bw.write("\n");
			}
			bw.close();
        
        }
		catch (IOException ex) 
		{
            ex.printStackTrace(System.err);
        }        
    }  
}
