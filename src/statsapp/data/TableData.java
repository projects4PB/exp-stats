package statsapp.data;

import java.util.ArrayList;
import statsapp.data.records.TableRecord;

/**
 *
 * @author Adrian Olszewski
 */
public class TableData
{
    String[] colNames;
    
    ArrayList<TableRecord> records = new ArrayList<>();

    public void addRecord(RecordData recordData)
    {
        this.records.add(new TableRecord(recordData));
    }
    
    public void setColumnsNames(String[] colNames)
    {
        this.colNames = colNames;
    }
    
    public String[] getColumnsNames()
    {
        return this.colNames;
    }
    
    public void setData(ArrayList<TableRecord> records)
    {
        this.records = records;
    }

    public ArrayList<TableRecord> getRecords()
    {
        return this.records;
    }
}
