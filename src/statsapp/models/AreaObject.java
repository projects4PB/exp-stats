package statsapp.models;

import java.util.ArrayList;

/**
 * Klasa reprezentujaca obiekt w przestrzeni
 */
public class AreaObject
{
	// wektor zmiennych objasniajacych
	private ArrayList<Float> vars;
	
	// wektor binarny przypisany do obiektu
	private ArrayList<Boolean> binaryVector;

	// klasa obiektu
	private String objectClass;

	public AreaObject(ArrayList<Float> vars)
	{
		this.vars = vars;
	}

	public Float getVar(int varIndex)
	{
		return this.vars.get(varIndex);
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

	@Override
	public String toString()
	{
		return "\nObiekt: " + this.vars.toString()
			+ " klasa: " + objectClass + "\n";
	}
}
