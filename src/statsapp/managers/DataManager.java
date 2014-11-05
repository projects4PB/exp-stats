package statsapp.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import statsapp.data.TableData;
import statsapp.data.records.TableRecord;
import statsapp.loaders.ExcelFileLoader;
import statsapp.loaders.Loader;
import statsapp.loaders.TextFileLoader;
import statsapp.models.Division;
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
    
    public TableData loadData(String fileName, boolean loadClassAttr, String columnClassName)
    {
        this.tableData = dataLoader.loadData(fileName,loadClassAttr, columnClassName);
        
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
    
	public float getAverage(String col_id)
    {
        ArrayList<Object> colData = this.getColumnData(col_id);
        
        if(!this.isNumbericColumn(col_id)) return -1;
        
        float amount = 0;
        
        for(Object obj : colData)
        {
            amount += (float) obj;
        }
        return amount / this.dataList.size();
    }
    
    public float getAverage(ArrayList<Float> values)
    {
        float amount = 0;
        
        for(Float value : values)
        {
            amount += value;
        }
        return amount / values.size();
    }

    public float getMedian(String col_id)
    {
        if(!this.isNumbericColumn(col_id)) return -1;
        
        ArrayList<Object> colData = this.getColumnData(col_id);

        ArrayList<Object> sortedData = this.sortColumnValues(colData);
        
        if(sortedData.size() % 2 == 0)
        { 
            int secondValueIndex = sortedData.size() / 2;
            int firstValueIndex = secondValueIndex - 1;
            
            float firstValue = (float) sortedData.get(firstValueIndex);
            
            float secondValue = (float) sortedData.get(secondValueIndex);
            
            return (firstValue + secondValue) / 2;
        }
        else return (float) sortedData.get(sortedData.size() / 2);
    }
    
    public float getMedian(List<Object> values)
    {
        ArrayList<Object> sortedData =
                this.sortColumnValues(values);
        
        if(sortedData.size() % 2 == 0)
        { 
            int secondValueIndex = sortedData.size() / 2;
            int firstValueIndex = secondValueIndex - 1;
            
            float firstValue = (float) sortedData.get(firstValueIndex);
            
            float secondValue = (float) sortedData.get(secondValueIndex);
            
            return (firstValue + secondValue) / 2;
        }
        else return (float) sortedData.get(sortedData.size() / 2);
    }
    
    public float getMedian(ArrayList<Float> values)
    {
		Collections.sort(values);

        if(values.size() % 2 == 0)
        { 
            int secondValueIndex = values.size() / 2;
            int firstValueIndex = secondValueIndex - 1;
            
            float firstValue = (float) values.get(firstValueIndex);
            
            float secondValue = (float) values.get(secondValueIndex);
            
            return (firstValue + secondValue) / 2;
        }
        else return (float) values.get(values.size() / 2);
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
    
    public float getQ1Value(String col_id)
    {
        if(!this.isNumbericColumn(col_id)) return -1;
        
        ArrayList<Object> colData = this.getColumnData(col_id);
        
        ArrayList<Object> sortedData =
                this.sortColumnValues(colData);
        
        List<Object> subListData = new ArrayList<>();
        
        if(this.dataList.size() % 2 == 0)
        {
            subListData = sortedData.subList(0,
                    this.dataList.size() / 2);
            
            return this.getMedian(subListData);
        }
        else
        {
            subListData = colData.subList(
                    0, (this.dataList.size() / 2) + 1
            );
            return this.getMedian(subListData);
        }
    }
    
    public float getQ3Value(String col_id)
    {
        if(!this.isNumbericColumn(col_id)) return -1;
        
        ArrayList<Object> colData = this.getColumnData(col_id);
        
        ArrayList<Object> sortedData =
                this.sortColumnValues(colData);
        
        List<Object> subListData = new ArrayList<>();
        
        if(this.dataList.size() % 2 == 0)
        {
            subListData = sortedData.subList(
                    this.dataList.size() / 2,
                    this.dataList.size());
            
            return this.getMedian(subListData);
        }
        else
        {
            subListData = colData.subList(
                    (this.dataList.size() / 2),
                    this.dataList.size()
            );
            return this.getMedian(subListData);
        }
    }
    
    public float getPercentileValue(String col_id, float cValue)
    {
		if(!this.isNumbericColumn(col_id)) return -1.0f;

        ArrayList<Object> colData = this.getColumnData(col_id);
        ArrayList<Object> sortedValues =
                this.sortColumnValues(colData);

        float tValue = cValue * (sortedValues.size() - 1);

        int iValue = (int) tValue;
        
        return (iValue - tValue + 1)
                * (float) sortedValues.get(iValue)
                + (tValue - iValue)
                * (float) sortedValues.get(iValue + 1);
    }

    public void getStandardization(String col_id)
    {           
        ArrayList<Object> colData  = this.getColumnData(col_id);
        ArrayList<Object> resultData = new ArrayList<>();
        
        float average = getAverage(col_id);
        float standardDeviation = getStandardDeviation(col_id);
        float standardizationValue = 0;
        
        for(Object obj : colData)
        {
            standardizationValue = ((float)obj - average) / standardDeviation;
            resultData.add((Object)standardizationValue);
        }
        
        setLastColumnData("std_" + col_id, resultData );
        dataTable.addColumn("std_" + col_id);
    }
     
    public float getStandardDeviation(String col_id)
    {
        ArrayList<Object> colData  = this.getColumnData(col_id);
        
	float average = getAverage(col_id);
        float counterVariation = 0;
        float valueColumn = 0; 
        
        for(Object obj : colData)
        {
            valueColumn = (float) obj;
            counterVariation += Math.pow(
					2, valueColumn - average
			);
        }
        
        float variation = counterVariation / colData.size();
               
        return  (float) Math.sqrt(variation);
    }
    
    public void mapColumValues(String col_id, float minVal, float maxVal)
    {
        float min = getMinValue(col_id);
        float max = getMaxValue(col_id);
        float mappedValue = 0;
        ArrayList<Object> colData = this.getColumnData(col_id);
        ArrayList<Object> resultData = new ArrayList<>();
        
        for(Object obj : colData)
        {
             mappedValue = this.map((float)obj, min, max, minVal, maxVal);
             resultData.add(mappedValue);
        }
        
        this.updateColumnData(col_id, resultData);
    }
    
    private float map(float x, float in_min, float in_max, float out_min, float out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
    
    public void digitizeValues(String col_id)
    {
        if(!this.isTextDataColumn(col_id)) return;
        
        ArrayList<Object> colData = this.getColumnData(col_id);
        
        HashMap<String, Integer> mappedValues = new HashMap<>();

        ArrayList<Object> resultData = new ArrayList<>();
        
        for(Object obj : colData)
        {
            if(!mappedValues.containsKey(obj.toString()))
            {
                if(mappedValues.isEmpty())
                {
                    mappedValues.put(obj.toString(), 1);
                }
                else
                {
                    int lastValue = (Collections.max(mappedValues.values()));
                    
                    mappedValues.put(obj.toString(), lastValue + 1);
                }
            }
            resultData.add(mappedValues.get(obj.toString()));
        }
        this.updateColumnData(col_id, resultData);
    }
    
    public void divideValues(String col_id, int numOfDivisions)
    {
        ArrayList<Division> divisions = new ArrayList<>();
        
        float maxValue = this.getMaxValue(col_id);
        float minValue = this.getMinValue(col_id);
        
        float divisionSize = (maxValue - minValue) / numOfDivisions;
        
        for(int i = 0; i < numOfDivisions; i++)
        {
            float startValue = minValue;
            
            if(i != 0) startValue = minValue + (i * divisionSize);
            
            float endValue = startValue + divisionSize;
            
            if(i == numOfDivisions - 1) endValue = maxValue + 0.1f;
            
            divisions.add(new Division(i + 1, startValue, endValue));
        }
        
        ArrayList<Object> colData = this.getColumnData(col_id);
        
		ArrayList<Object> resultsData = new ArrayList<>();

        for(Object obj : colData)
        {
            for(Division division : divisions)
            {
                float value = (float) obj;
                
                if(value >= division.getStartValue()
                        && value < division.getEndValue())
                {
					resultsData.add((Object)
							division.getDivisionID()
					);
                }
            }
        }
		this.setLastColumnData("div_" + col_id, resultsData);

		this.dataTable.addColumn("div_" + col_id);
    }
    
    public void groupByValueAmount(String col_id, int numOfGroups)
    {
        ArrayList<Object> colData = this.getColumnData(col_id);
        
        ConcurrentHashMap<Object, Integer> valuesAmounts =
                new ConcurrentHashMap<>();
        
        for(Object obj : colData)
        {
            if(valuesAmounts.containsKey(obj))
            {
                valuesAmounts.put(obj, valuesAmounts.get(obj) + 1);
            }
            else valuesAmounts.put(obj, 1);
        }
        HashMap<Object, Integer> groupsIDs = new HashMap<>();

        int groupID = 0;
        
        Iterator<Object> iterator = valuesAmounts.keySet().iterator();
        
		int prevValueAmount = -1;

        while(groupID < numOfGroups)
        {
			while(iterator.hasNext())
			{
				Object obj = iterator.next();
            
				int maxValueAmount = Collections.max(
					    valuesAmounts.values()
				);
				if(valuesAmounts.get(obj) == maxValueAmount)
				{
					if(maxValueAmount == prevValueAmount)
					{
						groupsIDs.put(obj, groupID);
					
						valuesAmounts.remove(obj);

						continue;
					}
					groupsIDs.put(obj, ++groupID);
                
					valuesAmounts.remove(obj);
				
					prevValueAmount = maxValueAmount;
				}
			}
			if(valuesAmounts.size() == 0) break;

			iterator = valuesAmounts.keySet().iterator();
		}
        ArrayList<Object> resultData = new ArrayList<>();
        
        for(Object obj : colData)
        {
            if(groupsIDs.containsKey(obj))
            {
                resultData.add(groupsIDs.get(obj));
            }
            else resultData.add(numOfGroups + 1);
        }
		this.setLastColumnData("grp_" + col_id, resultData);

		this.dataTable.addColumn("grp_" + col_id);	
    }
}
