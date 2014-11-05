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
import java.util.Random;

import org.math.array.LinearAlgebra;

import statsapp.data.RecordData;
import statsapp.data.TableData;
import statsapp.data.records.TableRecord;
import statsapp.models.AreaObject;
import statsapp.models.GroupingType;
import statsapp.models.MetricType;
import statsapp.models.ObjectsSet;

/*
 * Klasa zarządzająca obiekami w przestrzeni obiektów
 */
public class AreaManager
{
    private static volatile AreaManager instance = null;

    private DataManager dManager = DataManager.getInstance();

	private ArrayList<AreaObject> areaObjects;

	private ArrayList<ObjectsSet> objectsSets;

	private AreaManager()
	{
		this.areaObjects = new ArrayList<>();

		this.objectsSets = new ArrayList<>();
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
	
	public void addObjectsSet(ObjectsSet objsSet)
	{
		this.objectsSets.add(objsSet);
	}

	public void removeAreaObject(AreaObject areaObj)
	{
		this.areaObjects.remove(areaObj);
	}

	public void removeObjectsSet(ObjectsSet objsSet)
	{
		this.areaObjects.remove(objsSet);
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
                    if(colName.equals("class")== false && colName.contains("_") == false){
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
        
		public ArrayList<ObjectsSet> getObjectsSets()
		{
			return this.objectsSets;
		}
		
        public HashMap calculateAllLengths(AreaObject areaObj, MetricType metricType)
        {
            HashMap hashMap = new HashMap();
            double length = 0;
            ArrayList<AreaObject> areaObjects = this.getAreaObjects();
            
            switch(metricType)
            {
                case METRIC_MANHATTAN :
                    
                    for(int i=0 ; i< areaObjects.size(); i++){
                        if(areaObjects.get(i).equals(areaObj) == false){
                            length = this.calculateManhattanLength(areaObjects.get(i), areaObj);
                            hashMap.put(areaObjects.get(i), length);
                        }
                    }
               
                break;
                    
                case METRIC_EUKLIDES :
                   for(int i=0 ; i< areaObjects.size(); i++){
                        if(areaObjects.get(i).equals(areaObj) == false){
                            length = this.calculateEuklidesLength(areaObjects.get(i), areaObj);
                            hashMap.put(areaObjects.get(i), length);
                        }
                   }

                break;  
                    
                case METRIC_L_INFINITY :
                     for(int i=0 ; i< areaObjects.size(); i++){
                        if(areaObjects.get(i).equals(areaObj) == false){
                            length = this.calculateLInfinityLength(areaObjects.get(i), areaObj);
                            hashMap.put(areaObjects.get(i), length);
                        }
                    }
                     
                break;  
                    
                case METRIC_MAHALANOBIS :
                for(int i=0 ; i< areaObjects.size(); i++){
                        if (areaObjects.get(i).equals(areaObj) == false) {
                            length = this.calculateMahalanobisLength(areaObjects.get(i), areaObj);
                            hashMap.put(areaObjects.get(i), length);
                        }
                    }
                
                break;       
            }
            
            return hashMap;
        }
        
        public String classifyObject(AreaObject areaObj, int numOfNeighbours, MetricType metricType)
        {
            HashMap hashMap = this.calculateAllLengths(areaObj,metricType);
            hashMap = this.sortByValues(hashMap);
            ArrayList<String> classNeigbours  = new  ArrayList<String>();
            
            int i = 0;

            Iterator iter = hashMap.keySet().iterator();
            
            while(iter.hasNext()) {
                AreaObject key = (AreaObject)iter.next();
                double val = (double)hashMap.get(key);
                
                if(i < numOfNeighbours)
                {
                    String classAreaObject = key.getAreaObjectClass();
                    classNeigbours.add(classAreaObject);
                }

                i++;                
            }
                        
            HashMap<Object, Integer> classAmounts =  new HashMap<>( );
                
            for(String obj : classNeigbours)
            {
                if(classAmounts.containsKey(obj))
                {
                    classAmounts.put(obj, classAmounts.get(obj) + 1);
                }
                
                else classAmounts.put(obj, 1);
            }
                       
            int max = Collections.max(classAmounts.values());
            String newObjectClass = "";
            
            for (Object objClass : classAmounts.keySet()) {
                if (classAmounts.get(objClass).equals(max))  {
                    newObjectClass = objClass.toString();
                }
            }
            
            return newObjectClass;
        }
        
        public float getClassifyQuantity(int numOfNeighbours, MetricType metricType)
        {
            String oldClass = "";
            String newClass = "";
            boolean IsCorrect = false;
            float quantity = 0 ;
            int countCorrect = 0;
            ArrayList<AreaObject> areaObjects = this.getAreaObjects();
            HashMap hashQuantity = new HashMap();
            
            for(int i=0 ; i< areaObjects.size(); i++)
            {
                oldClass = areaObjects.get(i).getAreaObjectClass();
                newClass = this.classifyObject(areaObjects.get(i), numOfNeighbours, metricType);
                
                if(oldClass.equals(newClass))
                {
                   IsCorrect = true;
                   countCorrect++;
                }
                
                else
                {
                    IsCorrect = false;
                }
                
              //hashQuantity.put(areaObjects.get(i), IsCorrect);
            }
            
           quantity = (float) countCorrect / (float) areaObjects.size();
             
            return quantity;
        }

		public void initializeObjectsSets(int numOfSets)
		{
			Random random = new Random();

			// utworz nowe zbiory z losowymi srodkami
			for(int i = 0; i < numOfSets; i++)
			{
				int rndIndex = random.nextInt(this.areaObjects.size());

				AreaObject averagePoint = this.areaObjects.get(rndIndex);

				this.objectsSets.add(new ObjectsSet(
							averagePoint, "" + (i + 1)));
			}
		}

		public void assignObjectsToSets(
				GroupingType groupingType, MetricType metricType)
		{
			boolean flagSetsStateChanged = true;

			while(flagSetsStateChanged)
			{
				// inicjujemy wartosc flagi na false
				flagSetsStateChanged = false;
				
				for(AreaObject areaObj : this.areaObjects)
				{
					// zbior do ktorego aktualnie nalezy obiekt
					ObjectsSet objSet = areaObj.getAreaObjectSet();

					// zbior do ktorego zostal przypisany obiekt
					ObjectsSet assignedSet =
						this.assignObjectToSet(
								areaObj, metricType
						);

					// jesli nastapila zmiana zbioru
					// to ustaw wartosc flagi na true
					if(objSet != assignedSet)
					{
						flagSetsStateChanged = true;
					}
				}
				// jesli byly jakies zmiany
				// ustal nowe srodki zbiorow
				if(!flagSetsStateChanged)
				{
					this.recalculateSetsAveragePoints(groupingType);
				}
			}
		}

		public void recalculateSetsAveragePoints(GroupingType groupingType)
		{
			// przelicz srodki dla wszystkich zbiorow
			for(ObjectsSet objSet : this.objectsSets)
			{
				objSet.calculateAveragePoint(groupingType);
			}
		}

		public ObjectsSet assignObjectToSet(
				AreaObject areaObj, MetricType metricType)
		{
			ObjectsSet objSet = areaObj.getAreaObjectSet();
			
			if(objSet == null)
			{
				// przypisz obiekt do losowego zbioru
				Random random = new Random();
				
				int rndIndex = random.nextInt(
						this.objectsSets.size());

				objSet = this.objectsSets.get(rndIndex);
				
				objSet.addAreaObject(areaObj);
			}

			ObjectsSet assignSet = objSet;
			
			double minLength = this.calculateObjectToSetLength(
					areaObj, assignSet, metricType);

			// ustal najblizej jakiego zbioru znajduje sie obiekt
			for(int i = 1; i < this.objectsSets.size(); i++)
			{
				ObjectsSet objsSet = this.objectsSets.get(i);

				double length = this.calculateObjectToSetLength(
						areaObj, objsSet, metricType);

				if(length < minLength)
				{
					minLength = length;

					assignSet = objsSet;
				}
			}

			// przypisz obiekt do odpowiedniego zbioru
			if(objSet != assignSet)
			{
				objSet.removeAreaObject(areaObj);

				assignSet.addAreaObject(areaObj);
			}

			return assignSet;
		}

		public void getAreaObjectsClasses()
		{
			for(AreaObject areaObj : this.areaObjects)
			{
				System.out.println(areaObj.getAreaObjectClass());
			}
		}

       public static HashMap sortByValues(HashMap map) 
       { 
           List list = new LinkedList(map.entrySet());
           Collections.sort(list, new Comparator() {
               public int compare(Object o1, Object o2) 
               {
                   return ((Comparable) ((Map.Entry) (o1)).getValue())
                  .compareTo(((Map.Entry) (o2)).getValue());
                }
           });
           HashMap sortedHashMap = new LinkedHashMap();
           
           for (Iterator it = list.iterator(); it.hasNext();) {
              Map.Entry entry = (Map.Entry) it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
           }
           
           return sortedHashMap;
       }

	   public double calculateObjectToSetLength(
			   AreaObject areaObj,
			   ObjectsSet objsSet,
			   MetricType metricType)
	   {
		   double length = -1;

		   switch(metricType)
		   {
			   case METRIC_EUKLIDES:
				   length = calculateEuklidesLength(areaObj,
						   objsSet.getAveragePoint());
					break;

			   case METRIC_MANHATTAN:
				   length = calculateManhattanLength(areaObj,
						   objsSet.getAveragePoint());
				   break;

			   case METRIC_L_INFINITY:
				   length = calculateLInfinityLength(areaObj,
						   objsSet.getAveragePoint());
					break;

			   case METRIC_MAHALANOBIS:
				   length = calculateMahalanobisLength(areaObj,
						   objsSet.getAveragePoint());
					break;
		   }
		   return length;
	   }

	public double calculateEuklidesLength(
			AreaObject obj1, AreaObject obj2)
	{
		double pows_amount = 0;

		for(int i = 0; i < obj1.getVars().size(); i++)
		{
			pows_amount += Math.pow(
					obj1.getVars().get(i) -
					obj2.getVars().get(i), 2);
		}
		return Math.sqrt(pows_amount);
	}

	public double calculateManhattanLength(
			AreaObject obj1, AreaObject obj2)
	{
		double abs_amount = 0;

		for(int i = 0; i < obj1.getVars().size(); i++)
		{
			abs_amount += Math.abs(
					obj1.getVars().get(i) -
					obj2.getVars().get(i));
		}
		return abs_amount;
	}
	
	public double calculateLInfinityLength(
			AreaObject obj1, AreaObject obj2)
	{
		double max_length = -1;

		for(int i = 0; i < obj1.getVars().size(); i++)
		{
			double length = Math.abs(
					obj1.getVars().get(i) -
					obj2.getVars().get(i));
			
			if(length > max_length)
			{
				max_length = length;
			}
		}
		return max_length;
	}
    
    public double calculateMahalanobisLength(
			AreaObject obj1, AreaObject obj2)
	{
		double[][] S;

		int dim = obj1.getVars().size();
    
        S = new double[dim][dim];

        for(int i = 0; i < dim; i++)
            for(int j = 0; j < dim; j++)
                if(i == j)
                    S[i][j] = 1.0;
                else
                    S[i][j] = 0.0;
        
		double[][] diff = new double[1][dim];
        
		for(int i = 0; i < dim; i++)
            diff[0][i] = obj1.getVars().get(i)
				- obj2.getVars().get(i);
        
		double result[][] = LinearAlgebra.times(
				diff, LinearAlgebra.inverse(S)
		);
        result = LinearAlgebra.times(
				result, LinearAlgebra.transpose(diff)
		);
        
		return Math.sqrt(result[0][0]);
    }
}
