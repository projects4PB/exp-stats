package statsapp.models;

import java.util.ArrayList;

/**
 * Klasa reprezentujaca obiekt w przestrzeni
 */
public class AreaObject
{
	// wektor zmiennych objasniajacych
	private ArrayList<Float> vars;
	
	// klasa obiektu
	private String objectClass;

	// zbior obiektu
	private ObjectsSet objectSet;

	public AreaObject(ArrayList<Float> vars)
	{
		this.vars = vars;
	}

	public ArrayList<Float> getVars()
	{
		return this.vars;
	}
	
	public String getAreaObjectClass()
	{
		return this.objectClass;
	}

	public void setAreaObjectClass(String objClass)
	{
		this.objectClass = objClass;
	}
	
	public ObjectsSet getAreaObjectSet()
	{
		return this.objectSet;
	}

	public void setAreaObjectSet(ObjectsSet objSet)
	{
		this.objectSet = objSet;
	}
}
