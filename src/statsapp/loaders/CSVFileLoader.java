package statsapp.loaders;

import statsapp.data.TableData;

/**
 *
 * @author Adrian Olszewski, Dariusz Obuchowski
 */
public class CSVFileLoader implements Loader
{
	@Override
	public TableData loadData(String filePath,
			boolean hasColumnsHeader,
			boolean loadClassFlag,
			String classColumnName)
	{
		return new TableData();
	}	
}
