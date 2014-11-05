package statsapp.models;

/**
 *
 * @author Adrian Olszewski, Dariusz Obuchowski
 */
public class Division
{
    private int divisionID;
    
    private final float startValue;
    private final float endValue;
    
    public Division(int divisionID,
            float startValue, float endValue)
    {
        this.divisionID = divisionID;
        
        this.startValue = startValue;
        
        this.endValue = endValue;
    }
    
    public int getDivisionID()
    {
        return this.divisionID;
    }
    
    public float getStartValue()
    {
        return this.startValue;
    }
    
    public float getEndValue()
    {
        return this.endValue;
    }
}
