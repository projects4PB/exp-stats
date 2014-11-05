package statsapp.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import statsapp.data.RecordData;
import statsapp.data.TableData;
import statsapp.data.records.TableRecord;
import statsapp.models.AreaObject;

/*
 * Klasa zarządzająca obiekami w przestrzeni obiektów
 */
public class AreaManager
{
    private static volatile AreaManager instance = null;

    private DataManager dManager = DataManager.getInstance();

	private ArrayList<AreaObject> areaObjects;

	private AreaManager()
	{
		this.areaObjects = new ArrayList<>();
	}

    public static AreaManager getInstance()
    {
        if(instance == null)
        {
            synchronized(AreaManager.class)
            {
                if(instance == null)
                {
                    instance = new AreaManager();
                }
            }
        }
        return instance;
    }

	public void addAreaObject(AreaObject areaObj)
	{
		this.areaObjects.add(areaObj);
	}
	
	public void removeAreaObject(AreaObject areaObj)
	{
		this.areaObjects.remove(areaObj);
	}

	public ArrayList<AreaObject> getAreaObjects()
	{
		return this.areaObjects;
	}

	public void addTableDataToAreaObjects()
	{
		TableData tableData = dManager.getTableData();
		String[] colNames = tableData.getColumnsNames();
		ArrayList<Float> wektor = null;
		String classAreaObject = "";

		for(int i=0 ; i< dManager.getDataList().size(); i++)
		{
			TableRecord tableRecord = (TableRecord)dManager.getDataList().get(i);
			RecordData recordData = tableRecord.getRecordData();
			wektor = new  ArrayList<Float>();
		   
			for (String colName : colNames){
				if(colName.equals("class")== false && colName.contains("_") == false)
				{
					Object obj = recordData.getFields().get(colName);
					if (obj instanceof Number)
						wektor.add((float)obj); 
				}
				
				if(colName.equals("class")){
					Object obj = recordData.getFields().get(colName);
					classAreaObject = obj.toString();
				}
			}
			
			AreaObject areaObjectDataList  = new AreaObject(wektor);
			areaObjectDataList.setAreaObjectClass(classAreaObject);
			this.addAreaObject(areaObjectDataList);
		}
	}
        
	private ArrayList<AreaObject> sortByValue(int valueIndex) 
	{ 
		return new ArrayList<>();
	}
}
