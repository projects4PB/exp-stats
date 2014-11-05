package statsapp.data;

import java.util.HashMap;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Adrian Olszewski
 */
public class RecordData
{
    private final HashMap<String, Object> fields;

	public RecordData()
	{
		this.fields = new HashMap<>();
	}

    public RecordData(HashMap<String, Object> fields)
    {
        this.fields = fields;
    }
    
    public ObservableValue getField(String colName)
    {
        Object fieldProperty = this.fields.get(colName);
        
        if(fieldProperty instanceof Integer)
        {
            return new SimpleIntegerProperty((int) fieldProperty);
        }
        else if(fieldProperty instanceof Float)
        {
            return new SimpleFloatProperty((float) fieldProperty);
        }
        return new SimpleStringProperty((String) fieldProperty);
    }
    
    public void addField(String colName, Object value)
    {
        this.fields.put(colName, value);
    }

    public void setFieldValue(String colName, Object value)
    {
        this.fields.put(colName, value);
    }

    public HashMap<String, Object> getFields()
    {
        return this.fields;
    }
}
