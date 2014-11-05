package statsapp.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import statsapp.data.TableData;
import statsapp.data.records.TableRecord;
import statsapp.loaders.Loader;
import statsapp.tables.DataTable;

/**
 *
 * @author Adrian Olszewski, Dariusz Obuchowski
 */
public class DataManager
{
    private static volatile DataManager instance = null;
    
    private final ObservableList dataList;
    
    private TableData tableData;
    
    private static Loader dataLoader;
    
    private DataTable dataTable;
    
    private DataManager(Loader dataLoader)
    {
        this.dataLoader = dataLoader;
        
        this.dataList = FXCollections.observableArrayList();
    }
    
    public static DataManager getInstance()
    {
        if(instance == null)
        {
            synchronized(DataManager.class)
            {
                if(instance == null)
                {
                    instance = new DataManager(dataLoader);
                }
            }
        }
        return instance;
    }
    
    public static void setDataLoader(Loader loader)
    {
        dataLoader = loader;
    }

    public ObservableList getDataList()
    {
        return this.dataList;
    }
    
    public DataTable createDataTable(TableData tableData)
    {
        this.dataTable = new DataTable();
        
        String[] colNames = tableData.getColumnsNames();
        
        this.dataTable.setColumns(colNames);
        this.dataTable.setItems(dataList);
        
        return this.dataTable;
    }
    
    public DataTable getDataTable()
    {
        return this.dataTable;
    }
    
    public TableData getTableData()
    {
        return this.tableData;
    }
    
    public TableData loadData(String fileName,
			boolean hasColumnsHeader,
			boolean loadClassFlag,
			String classColumnName)
    {
        this.tableData = dataLoader.loadData(
				fileName, hasColumnsHeader,
				loadClassFlag, classColumnName);
        
		this.dataList.removeAll(this.dataList);

        this.dataList.addAll(tableData.getRecords());
        
        return tableData;
    }
    
	public ArrayList<Object> getColumnData(String col_id)
    {
        ArrayList<Object> columnData = new ArrayList<>();
        
        for(Object col : this.dataTable.getColumns())
        {
            TableColumn tableCol = ((TableColumn) col);
            
            if(tableCol.getId().equals(col_id))
            {
                for(int i = 0; i < this.dataList.size(); i++)
                {
                    columnData.add(tableCol.getCellData(i));
                }
            }
        }
        return columnData;
    }
    
    public void setLastColumnData(String col_id, ArrayList<Object> colData)
    {
        for(int i = 0; i < this.dataList.size(); i++)
        {
            TableRecord tableRecord = (TableRecord) this.dataList.get(i);
            
            tableRecord.getRecordData().addField(col_id, colData.get(i));
        }
    }
    
	private void updateColumnData(String col_id, List<Object> colData)
    {
        for(int i = 0; i < this.dataList.size(); i++)
        {
            TableRecord tableRecord = (TableRecord) this.dataList.get(i);
            
            tableRecord.getRecordData().setFieldValue(col_id, colData.get(i));
        }
		for(Object obj : this.dataTable.getColumns())
		{
			TableColumn tableCol = (TableColumn) obj;

			if(tableCol.getId().equals(col_id))
			{
				tableCol.setVisible(false);
				tableCol.setVisible(true);
			}
		}
    }
    
    private ArrayList<Float> getNumbericData(List<Object> values)
    {
        ArrayList<Float> numbericData = new ArrayList<>();
        
        for(Object obj : values)
        {
            numbericData.add((float) obj);
        }
        return numbericData;
    }
    
    public boolean isNumbericColumn(String col_id)
    {
          TableRecord tableRecord = tableData.getRecords ().get(0);

          Object obj = tableRecord.getRecordData().getFields().get(col_id);

          if(obj instanceof Number) return true;

          return false;
    }
    
    public boolean isTextDataColumn(String col_id)
    {
        TableRecord tableRecord = tableData.getRecords ().get(0);

        Object obj = tableRecord.getRecordData().getFields().get(col_id);

        if(obj instanceof String) return true;

            return false;
    }
    
    private ArrayList<Object> sortColumnValues(List<Object> colData)
    {
        ArrayList<Float> colValues = this.getNumbericData(colData);
        
        Collections.sort(colValues);
        
        ArrayList<Object> sortedData = new ArrayList<>();
        
        for(Float val : colValues)
        {
            sortedData.add((Object) val);
        }
        return sortedData;
    }
    
	public float getMinValue(String col_id)
    {
        ArrayList<Object> colData = this.getColumnData(col_id);
        
        if(!this.isNumbericColumn(col_id)) return -1;
        
        float minValue = (float) colData.get(0);
        
        for(Object obj : colData)
        {
            if(((float) obj) < minValue) minValue = (float) obj;
        }
        return minValue;
    }
    
    public float getMaxValue(String col_id)
    {
        ArrayList<Object> colData = this.getColumnData(col_id);
        
        if(!this.isNumbericColumn(col_id)) return -1;
        
        float maxValue = (float) colData.get(0);
        
        for(Object obj : colData)
        {
            if(((float) obj) > maxValue) maxValue = (float) obj;
        }
        return maxValue;
    }
}
