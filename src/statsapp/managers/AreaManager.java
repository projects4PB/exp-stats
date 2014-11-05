package statsapp.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

		ArrayList<Float> objectVars = new  ArrayList<Float>();

		String areaObjectClass = "";

		for(int i = 0 ; i < dManager.getDataList().size(); i++)
		{
			TableRecord tableRecord = (TableRecord)dManager.getDataList().get(i);

			RecordData recordData = tableRecord.getRecordData();

			objectVars.removeAll(objectVars);

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
}
