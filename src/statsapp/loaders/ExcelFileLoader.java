package statsapp.loaders;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import statsapp.data.RecordData;
import statsapp.data.TableData;

/**
 *
 * @author Adrian Olszewski
 */
public class ExcelFileLoader implements Loader
{
    @Override
    public TableData loadData(String fileName, boolean loadClassAttr, String columnClassName)
    {
        Workbook workbook = null;
      
        try {
            workbook = Workbook.getWorkbook(
					new File(fileName)
			);
        }
		catch (IOException ex)
		{
			ex.printStackTrace(System.err);
        }
		catch (BiffException ex)
		{
			ex.printStackTrace(System.err);
        }
        Sheet sheet = workbook.getSheet(0);
		
		String[] colNames = new String[sheet.getColumns()];
        
		TableData tableData = new TableData();
		
		for (int i = 0; i < sheet.getRows(); i++)
		{
			RecordData recordData = new RecordData();

			for (int j = 0; j < sheet.getColumns(); j++)
			{
				Cell cell = sheet.getCell(j, i);
				
				CellType type = cell.getType();

				// jesli jest to pierwszy wiersz to
				// zapisz wartosci jako nazwy kolumn
				if(i == 0)
				{
					colNames[j] = cell.getContents();

					continue;
				}

				if(type == CellType.LABEL)
				{
					String value = cell.getContents();

					recordData.addField(colNames[j], (Object) value); 
				}
				
				if(type == CellType.NUMBER)
				{
					Number value = null;

					try {
						value = NumberFormat.getInstance()
							.parse(cell.getContents());
					}
					catch(ParseException ex)
					{
						ex.printStackTrace(System.err);
					}
					recordData.addField(colNames[j], (Object) value);
				}
			}
			if(i != 0) tableData.addRecord(recordData);
		}
        workbook.close();

		tableData.setColumnsNames(colNames);
        
		return tableData;
    }
}
