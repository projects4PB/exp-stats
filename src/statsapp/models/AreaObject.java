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
	private ArrayList<Integer> binaryVector = new ArrayList<>();

	// klasa obiektu
	private String objectClass;
	
	public static final int STATUS_EXISTING = 0;
	public static final int STATUS_REMOVED = 1;

	private int status = STATUS_EXISTING;

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
	
	public void setBinaryValue(int value)
	{
		this.binaryVector.add(value);
	}
	
	public String getBinaryVector()
	{
		return this.binaryVector.toString();
	}
	
	public void setStatus(int s)
	{
		status = s;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	@Override
	public String toString()
	{
		return "\nObiekt: " + this.vars.toString()
			+ " klasa: " + objectClass + "\n";
	}
}
