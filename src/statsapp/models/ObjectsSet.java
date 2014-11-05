package statsapp.models;

import java.util.ArrayList;

import statsapp.managers.DataManager;

public class ObjectsSet
{
	private ArrayList<AreaObject> areaObjects;

	private AreaObject setAveragePoint;

	private String setClass;

	public ObjectsSet(AreaObject setAveragePoint, String setClass)
	{
		this.areaObjects = new ArrayList<>();

		this.setAveragePoint = setAveragePoint;

		this.setClass = setClass;
	}

	public void addAreaObject(AreaObject areaObj)
	{
		this.areaObjects.add(areaObj);

		areaObj.setAreaObjectSet(this);

		areaObj.setAreaObjectClass(this.setClass);
	}
	
	public void removeAreaObject(AreaObject areaObj)
	{
		this.areaObjects.remove(areaObj);

		areaObj.setAreaObjectSet(null);
		
		areaObj.setAreaObjectClass(null);
	}

	public ArrayList<AreaObject> getAreaObjects()
	{
		return this.areaObjects;
	}

	public void setAveragePoint(AreaObject setAveragePoint)
	{
		this.setAveragePoint = setAveragePoint;
	}

	public AreaObject getAveragePoint()
	{
		return this.setAveragePoint;
	}

	public String getObjectsSetClass()
	{
		return this.setClass;
	}

	public void calculateAveragePoint(GroupingType groupingType)
	{
		// lista na ktorej znajduja sie wlasciwosci wszystkich obiektow ze zbioru
		ArrayList<ArrayList<Float>> averagePointVarsValues = new ArrayList<>();

		// uzupelnij liste wszytkich obiektow ze zbioru
		for(AreaObject areaObj : this.areaObjects)
		{
			// lista wlasciwosci aktualnego obiektu
			ArrayList<Float> areaObjectVars = areaObj.getVars();
			
			// podaj poszczegolne wlasciwosci do listy
			for(int i = 0; i < areaObjectVars.size(); i++)
			{
				if(averagePointVarsValues.size() < i + 1)
				{
					averagePointVarsValues.add(new ArrayList<Float>());
				}
				averagePointVarsValues.get(i).add(areaObjectVars.get(i));
			}
		}
		// lista wlasciwosci nowego srodka zbioru
		ArrayList<Float> averagePointVars = new ArrayList<>();

		// w zaleznosci od typu grupowania oblicz
		// wlasciwosci nowego srodka zbioru
		switch(groupingType)
		{
			case K_AVERAGES:
				
				for(ArrayList<Float> values : averagePointVarsValues)
				{
					float average = DataManager.getInstance().getAverage(values);

					averagePointVars.add(average);
				}
				break;

			case K_MEDIANS:

				for(ArrayList<Float> values : averagePointVarsValues)
				{
					float median = DataManager.getInstance().getMedian(values);

					averagePointVars.add(median);
				}
				break;
		}
		// ustaw nowy srodek zbioru
		this.setAveragePoint = new AreaObject(averagePointVars);
	}
}
