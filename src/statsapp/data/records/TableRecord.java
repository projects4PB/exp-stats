package statsapp.data.records;

import statsapp.data.RecordData;

/**
 *
 * @author Adrian Olszewski
 */
public class TableRecord
{
    private final RecordData recordData;
    
    public TableRecord(RecordData recordData)
    {
        this.recordData = recordData;
    }
    
    public RecordData getRecordData()
    {
        return this.recordData;
    }
}
