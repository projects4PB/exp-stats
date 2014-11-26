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
	
	private final int DIR_LT = 0;
	
	private final int DIR_GT = 1;

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
		ArrayList<AreaObject> resultList = new ArrayList<>();
		
		for(AreaObject obj : this.areaObjects)
		{
			if(obj.getStatus() == AreaObject.STATUS_EXISTING)
			{
				resultList.add(obj);
			}
		}
		return resultList;
	}
	
	public ArrayList<AreaObject> getAllAreaObjects()
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
        
	private void sortASCAreaObjectsByVariable(final int varIndex) 
	{ 
		Collections.sort(this.areaObjects, new Comparator<AreaObject>()
		{
			public int compare(AreaObject a1, AreaObject a2)
			{
				return a1.getVar(varIndex).compareTo(a2.getVar(varIndex));
			}
		});	
	}
	
	private void sortDSCAreaObjectsByVariable(final int varIndex) 
	{ 
		Collections.sort(this.areaObjects, new Comparator<AreaObject>()
		{
			public int compare(AreaObject a1, AreaObject a2)
			{
				return - a1.getVar(varIndex).compareTo(a2.getVar(varIndex));
			}
		});	
	}
	
	private ArrayList<AreaObject> getAreaObjectsForClass(String objClass)
	{
		ArrayList<AreaObject> resultList = new ArrayList<>();
		
		for(AreaObject areaObj : this.getAreaObjects())
		{
			if(areaObj.getAreaObjectClass().equals(objClass))
			{
				resultList.add(areaObj);
			}
		}
		return resultList;
	}
	
	private ArrayList<String> getAreaClasses()
	{
		ArrayList<String> resultList = new ArrayList<>();
		
		for(AreaObject areaObj : this.getAreaObjects())
		{
			if(!resultList.contains(areaObj.getAreaObjectClass()))
			{
				resultList.add(areaObj.getAreaObjectClass());
			}
		}
		return resultList;
	}
	
	private boolean iSListHasOnlyTheSomeClasses()
	{
		ArrayList<String> classesObj = this.getAreaClasses();
		
		String previousClass = "";
		
		String nextClass = "";
		
		for(int i = 0; i < classesObj.size() ; i++) 
		{
			previousClass = classesObj.get(i);
			
			if(i + 1 != classesObj.size())
				nextClass = classesObj.get(i + 1);
			else
				nextClass = previousClass;
			
			if(!previousClass.equals(nextClass))
			{
				return false;
			}
		}
		
		return true;
	}

	private int[] countAreaObjects(
			int varIndex, float dividingValue, String objClass)
	{
		int LTObjectsCount = 0, GTObjectsCount = 0, sliceDIR = this.DIR_LT;

		boolean hasLTOtherClassObj = false;
		boolean hasGTOtherClassObj = false;
		
		for(AreaObject areaObj : this.getAreaObjects())
		{
			if(areaObj.getVar(varIndex) > dividingValue)
			{
				if(areaObj.getAreaObjectClass().equals(objClass))
				{
					GTObjectsCount++;
				}
				else hasGTOtherClassObj = true;
			}
			else
			{
				if(areaObj.getAreaObjectClass().equals(objClass))
				{
					LTObjectsCount++;
				}
				else hasLTOtherClassObj = true;
			}
		}
		if(LTObjectsCount >= GTObjectsCount)
		{
			if(!hasLTOtherClassObj)
				sliceDIR = this.DIR_LT;
			else sliceDIR = this.DIR_GT;
		}
		else{
			if(!hasGTOtherClassObj)
				sliceDIR = this.DIR_GT;
			else sliceDIR = this.DIR_LT;
		}
		
		int otherClassObjFlag;
		
		if(sliceDIR == this.DIR_LT)
		{
			otherClassObjFlag = hasLTOtherClassObj ? 1 : 0;
		}
		else
		{
			otherClassObjFlag = hasGTOtherClassObj ? 1 : 0;
		}
		return new int[] {
				sliceDIR, otherClassObjFlag, LTObjectsCount, GTObjectsCount
		};
	}

	public void sliceObjectsArea()
	{
		int[] countAreaOBjects;
		
		int varCount = this.getAreaObjects().get(0).getVars().size();

		int countRemoveObjects = -1;
		
		AreaObject slicingObj = null;
		
		int varIndex = 0, slideVar = 0;
		
		int slicingDIR = this.DIR_LT;
				
		int slideMaxCount = 0, maxLTGTObjectCount = 0;
		
		int prevSlicingDIR = this.DIR_LT;
		
		int prevMaxLTGTObjectCount = 0;
		 
		AreaObject prevSlicingObj = null;

		boolean hasOtherClassesOnSlideLine = false;
		
		boolean iSListHasOnlyTheSomeClasses = false;
		
		int c = 0;
		
		while(this.getAreaClasses().size() != 0)
		{
			iSListHasOnlyTheSomeClasses = this.iSListHasOnlyTheSomeClasses();
			
			if(iSListHasOnlyTheSomeClasses)
			{
				break;
			}
			
			ArrayList<AreaObject> classObjects = null;
			
			for(int i = 0; i < varCount; i++)
			{
				varIndex = i;

				this.sortASCAreaObjectsByVariable(varIndex);
				
				for(String objClass : this.getAreaClasses())
				{
					classObjects = this.getAreaObjectsForClass(objClass);
				
					for(AreaObject areaObj : classObjects)
					{
						float varValue = areaObj.getVar(varIndex);

						countAreaOBjects = this.countAreaObjects(
								varIndex, varValue, objClass);
						
						if(countAreaOBjects[0] == this.DIR_LT)
						{
							maxLTGTObjectCount = countAreaOBjects[2];
						}
						else maxLTGTObjectCount = countAreaOBjects[3];
						
						if(maxLTGTObjectCount >= slideMaxCount)
						{
							hasOtherClassesOnSlideLine =
									this.hasOtherClassesOnSlide(
											varValue, objClass, varIndex
									);
							
							if(countAreaOBjects[1] == 1 ||
									hasOtherClassesOnSlideLine)
							{
								slicingDIR = prevSlicingDIR;
								
								slideMaxCount = prevMaxLTGTObjectCount;
								 
								slicingObj = prevSlicingObj;

								break;
							}
							else
							{
								slicingDIR = countAreaOBjects[0];
								
								slideMaxCount = maxLTGTObjectCount;
								 
								slicingObj = areaObj;
							}	
							if(areaObj.getVar(varIndex) == classObjects
									.get(classObjects.size() - 1)
									.getVar(varIndex))
							{
								slicingDIR = countAreaOBjects[0];
								
								slideMaxCount = maxLTGTObjectCount;
								 
								slicingObj = areaObj;
							}
							slideVar = varIndex;
							
							prevMaxLTGTObjectCount = maxLTGTObjectCount;
							
							prevSlicingDIR = slicingDIR;
							
							prevSlicingObj = areaObj;
						}
					 }
				 }

				this.sortDSCAreaObjectsByVariable(varIndex);
				
				for(String objClass : this.getAreaClasses())
				{
					classObjects = this.getAreaObjectsForClass(objClass);
				
					for(AreaObject areaObj : classObjects)
					{
						float varValue = areaObj.getVar(varIndex);

						countAreaOBjects = this.countAreaObjects(
								varIndex, varValue, objClass);

						if(countAreaOBjects[0] == this.DIR_LT)
						{
							maxLTGTObjectCount = countAreaOBjects[2];
						}
						else maxLTGTObjectCount = countAreaOBjects[3];
						
						if(maxLTGTObjectCount >= slideMaxCount)
						{
							hasOtherClassesOnSlideLine =
									this.hasOtherClassesOnSlide(
											varValue, objClass, varIndex
									);
							
							if(countAreaOBjects[1] == 1 ||
									hasOtherClassesOnSlideLine)
							{
								slicingDIR = prevSlicingDIR;
								
								slideMaxCount = prevMaxLTGTObjectCount;
								 
								slicingObj = prevSlicingObj;

								break;
							}
							else
							{
								slicingDIR = countAreaOBjects[0];
								
								slideMaxCount = maxLTGTObjectCount;
								 
								slicingObj = areaObj;
							}
							if(areaObj.getVar(varIndex) == classObjects
									.get(classObjects.size() - 1)
									.getVar(varIndex))
							{								
								slicingDIR = countAreaOBjects[0];
								
								slideMaxCount = maxLTGTObjectCount;
								 
								slicingObj = areaObj;
							}
							slideVar = varIndex;
							
							prevMaxLTGTObjectCount = maxLTGTObjectCount;
							
							prevSlicingDIR = slicingDIR;
							
							prevSlicingObj = areaObj;
						}
					 }
				 }
				prevMaxLTGTObjectCount = 0;
			}
			slideMaxCount = 0;

			countRemoveObjects = this.removeSlidesAreaObjects(
					slicingObj, slideVar, slicingDIR);
						
			System.out.println("V: " + slideVar + " - " + slicingDIR);
			System.out.println("C: " + slicingObj.getVar(slideVar));
			
			c++;
		}
	}
	
	private boolean hasOtherClassesOnSlide(
			float slideVal, String slideClass, int slideVar)
	{
		float areObjVal = 0;
		
		String classareaObj = "";
				
		for(AreaObject areaObj : this.getAreaObjects())
		{
			classareaObj = areaObj.getAreaObjectClass();
			
			areObjVal = areaObj.getVar(slideVar);
			
			if(classareaObj.equals(slideClass) == false
					&& slideVal == areObjVal)
			{
				return true;
			}
		}
		return false;
	}
		
	private int removeSlidesAreaObjects(
			AreaObject areaObject, int sliderVar, int direction)
	{		
		//int countRemoveObjects = 0;
		
		ArrayList<AreaObject> areaObjectsToRemove = new ArrayList<AreaObject>();
				
		float valueAreaObject = areaObject.getVar(sliderVar);
		
		String classAreaObject = areaObject.getAreaObjectClass();
						
		for(AreaObject areaObj : this.getAreaObjects())
		{
			float valueAreaObj = areaObj.getVar(sliderVar);
			
			String classAreaObj = areaObj.getAreaObjectClass();
			
			if(direction == this.DIR_LT)
			{
				if(classAreaObject.equals(classAreaObj) 
						&& valueAreaObject >= valueAreaObj )
				{	
					areaObjectsToRemove.add(areaObj);
				}
			}
			else
			{
				if(classAreaObject.equals(classAreaObj) 
						&& valueAreaObject <= valueAreaObj )
				{					
					areaObjectsToRemove.add(areaObj);
				}				
			}			
		}
		
		/*
		for(AreaObject areaObj : areaObjectsToRemove)
		{
			areaObj.setBinaryValue(1);
			areaObj.setStatus(AreaObject.STATUS_REMOVED);
			countRemoveObjects++;
		}
		*/
		
		System.out.println("weszlo");
		
		for(AreaObject areaObj : this.areaObjects)
		{
			if(areaObjectsToRemove.contains(areaObj))
			{
				areaObj.setStatus(AreaObject.STATUS_REMOVED);
				areaObj.setBinaryValue(1);
			}
			
			else
			{
				areaObj.setBinaryValue(0);
			}
		}
		
		return areaObjectsToRemove.size();
	}
		
	public void printAreaObjects()
	{		
		for(AreaObject areaObj : this.areaObjects)
		{
			System.out.println(areaObj.getBinaryVector());
		}
	}
}
