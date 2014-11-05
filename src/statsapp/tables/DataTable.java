package statsapp.tables;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import statsapp.data.records.TableRecord;
import statsapp.tables.cells.EditableCell;

/**
 *
 * @author Adrian Olszewski
 */
public class DataTable extends TableView
{
    public DataTable()
    {
        this.setProperties();
    }
    
    private void setProperties()
    {
        this.setPrefSize(800, 575);
        
        this.setEditable(true);
        
    }
    
    public void setColumns(String[] colNames)
    {
        for(final String colName : colNames)
        {
            TableColumn column = new TableColumn(colName);
            
            column.setCellValueFactory(
                    new Callback<CellDataFeatures<TableRecord, Object>,
                    ObservableValue<Object>>()
            {
                @Override
                public ObservableValue<Object> call(
                        CellDataFeatures<TableRecord, Object> tableRecord
                ) {
                    return tableRecord.getValue()
                            .getRecordData()
                            .getField(colName);
                }
            });
            Callback<TableColumn, TableCell> cellFactory =
                    new Callback<TableColumn, TableCell>()
                    {
                        @Override
                        public TableCell call(TableColumn col)
                        {
                            return new EditableCell();
                        }
                    };
            column.setCellFactory(cellFactory);
            
            column.setOnEditCommit(
                new EventHandler<CellEditEvent<TableRecord, Object>>()
                {
                    @Override
                    public void handle(CellEditEvent<TableRecord, Object> t)
                    {
                        TableRecord tableRecord = ((TableRecord)
                                t.getTableView().getItems().get(
                                        t.getTablePosition().getRow()
                                ));
                        tableRecord.getRecordData().setFieldValue(
                                colName, t.getNewValue()
                        );
                    }
                }
            );
            
            column.setId(colName);

            this.getColumns().add(column);
        }
    }
    
    public void addColumn(final String colName)
    {
        final TableColumn column = new TableColumn(colName);
        
		column.setCellValueFactory(
				new Callback<CellDataFeatures<TableRecord, Object>,
				ObservableValue<Object>>()
		{
			@Override
			public ObservableValue<Object> call(
					CellDataFeatures<TableRecord, Object> tableRecord
			) {
				return tableRecord.getValue()
						.getRecordData()
						.getField(colName);
			}
		});
		Callback<TableColumn, TableCell> cellFactory =
				new Callback<TableColumn, TableCell>()
				{
					@Override
					public TableCell call(TableColumn col)
					{
						return new EditableCell();
					}
				};
		column.setCellFactory(cellFactory);
		
		column.setOnEditCommit(
			new EventHandler<CellEditEvent<TableRecord, Object>>()
			{
				@Override
				public void handle(CellEditEvent<TableRecord, Object> t)
				{
					TableRecord tableRecord = ((TableRecord)
							t.getTableView().getItems().get(
									t.getTablePosition().getRow()
							));
					tableRecord.getRecordData().setFieldValue(
							colName, t.getNewValue()
					);
				}
			}
		);
        column.setId(colName);
        
		Platform.runLater(new Runnable()
		{
			@Override public void run()
			{
				getColumns().add(column);
			}
		});
    }
    
    public void removeColumn(final String colName)
    {
		Platform.runLater(new Runnable()
		{
			@Override public void run()
			{
				for(Object tableCol : getColumns())
				{
					if(((TableColumn) tableCol).getId().equals(colName))
					{
						getColumns().remove(tableCol);
					}
				}
			}
		});
    }
}
