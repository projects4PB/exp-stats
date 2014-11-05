package statsapp.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

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

	private HashMap<String, Integer> classObjectsCounts = new HashMap<>();

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

		String areaObjClass = areaObj.getAreaObjectClass();

		if(this.classObjectsCounts.containsKey(areaObjClass))
		{
			int classObjectsCount = this.classObjectsCounts.get(areaObjClass);

			this.classObjectsCounts.put(areaObjClass, ++classObjectsCount);
		}
		else if(!areaObjClass.equals("") && areaObjClass != null)
		{	
			this.classObjectsCounts.put(areaObjClass, 1);
		}
	}
	
	public void removeAreaObject(AreaObject areaObj)
	{
		this.areaObjects.remove(areaObj);

		String areaObjClass = areaObj.getAreaObjectClass();

		int classObjectsCount = this.classObjectsCounts.get(areaObjClass);

		this.classObjectsCounts.put(areaObjClass, --classObjectsCount);
	}

	public ArrayList<AreaObject> getAreaObjects()
	{
		return this.areaObjects;
	}

	public void addTableDataToAreaObjects()
	{
		TableData tableData = dManager.getTableData();

		String[] colNames = tableData.getColumnsNames();

		String areaObjectClass = "";

		for(int i = 0 ; i < dManager.getDataList().size(); i++)
		{
			ArrayList<Float> objectVars = new  ArrayList<Float>();

			TableRecord tableRecord = (TableRecord)dManager.getDataList().get(i);

			RecordData recordData = tableRecord.getRecordData();

			for (String colName : colNames)
			{
				if(!colName.equals("class") && !colName.contains("_"))
				{
					Object obj = recordData.getFields().get(colName);

					if (obj instanceof Number) objectVars.add((float)obj); 
				}
				else if(colName.equals("class"))
				{
					Object obj = recordData.getFields().get(colName);

					areaObjectClass = obj.toString();
				}
			}
			AreaObject areaObjectDataList  = new AreaObject(objectVars);

			areaObjectDataList.setAreaObjectClass(areaObjectClass);

			this.addAreaObject(areaObjectDataList);
		}
	}
        
	private void sortAreaObjectsByVariable(final int varIndex) 
	{ 
		Collections.sort(this.areaObjects, new Comparator<AreaObject>()
		{
			public int compare(AreaObject a1, AreaObject a2)
			{
				return a1.getVar(varIndex).compareTo(a2.getVar(varIndex));
			}
		});	
	}

	private int[] countAreaObjects(
			int varIndex, float dividingValue, String objClass)
	{
		int LTObjectsCount = 0, GTObjectCount = 0;

		for(AreaObject areaObj : this.areaObjects)
		{
			if(areaObj.getAreaObjectClass().equals(objClass))
			{
				if(areaObj.getVar(varIndex) >= dividingValue)
				{
					GTObjectCount++;
				}
				else LTObjectsCount++;
			}
		}
		return new int[] { LTObjectsCount, GTObjectCount };
	}

	public void sliceObjectsArea()
	{
		this.sortAreaObjectsByVariable(0);
	}

	public void printAreaObjects()
	{
		for(AreaObject areaObj : this.areaObjects)
		{
			System.out.println(areaObj);
		}
	}
}
