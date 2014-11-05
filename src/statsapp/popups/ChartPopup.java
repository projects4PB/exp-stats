package statsapp.popups;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import statsapp.data.RecordData;
import statsapp.data.records.TableRecord;
import statsapp.managers.DataManager;

public class ChartPopup extends BasePopup
{
	private DataManager dManager = DataManager.getInstance();

	// Pierwsza zmienna
	private static String FIRST_COLUMN = "";

	// Druga zmienna
	private static String SECOND_COLUMN = "";

	public ChartPopup()
	{
		super(new StackPane(), "WYKRES");

		Button closeButton = new Button("X");
        closeButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                hide();
            }
        });
        closeButton.setPrefSize(25, 25);

        Label chooseFirstVarLabel = new Label("Zmienna X:");
        chooseFirstVarLabel.setPrefSize(115, 25);
    
		Label chooseSecondVarLabel = new Label("Zmienna Y:");
        chooseSecondVarLabel.setPrefSize(115, 25);
        
        ComboBox<String> firstColumnsBox = new ComboBox<>();
        firstColumnsBox.valueProperty().addListener(
				new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue ov,
				String oldVal, String newVal)
            {                
                FIRST_COLUMN = newVal;

				ScatterChart sChart = createChart();

				getContent().set(0, sChart);
            }    
        });
        firstColumnsBox.setPrefSize(100, 25);

        ComboBox<String> secondColumnsBox = new ComboBox<>();
        secondColumnsBox.valueProperty().addListener(
				new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue ov,
				String oldVal, String newVal)
            {                
                SECOND_COLUMN = newVal;

				ScatterChart sChart = createChart();

				getContent().set(0, sChart);
            }    
        });
		secondColumnsBox.setPrefSize(100, 25);
        
        ObservableList<String> firstColumnsItems =
			firstColumnsBox.getItems();
        ObservableList<String> secondColumnsItems =
			secondColumnsBox.getItems();
        
        for(Object col : dManager.getDataTable().getColumns())
        {
			if(dManager.isNumbericColumn(
					((TableColumn) col).getId()))
			{
				firstColumnsItems.add(
						((TableColumn) col).getId()
				);
				secondColumnsItems.add(
						((TableColumn) col).getId()
				);
			}
        }
		HBox chartTools = new HBox();

		chartTools.getStylesheets().add(
				POPUPS_STYLESHEET
		);
		chartTools.getStyleClass().add("chart-tools");

		chartTools.getChildren().addAll(
				closeButton,
				chooseFirstVarLabel,
				firstColumnsBox,
				chooseSecondVarLabel,
				secondColumnsBox
		);
		ScatterChart scatterChart = this.createChart();

		this.getContent().addAll(
				scatterChart,
				chartTools
		);
	}

	private ScatterChart createChart()
	{
		if(FIRST_COLUMN.equals("") ||
				SECOND_COLUMN.equals(""))
		{
			ScatterChart<Number, Number> sChart = new
				ScatterChart<Number, Number>(
						new NumberAxis(),
						new NumberAxis()
				);
			sChart.getStylesheets().add(POPUPS_STYLESHEET);
			sChart.getStyleClass().add("chart");
			
			return sChart;
		}
		
		float firstVarMinValue = dManager
			.getMinValue(FIRST_COLUMN);
		float firstVarMaxValue = dManager
			.getMaxValue(FIRST_COLUMN);
		
		float secondVarMinValue = dManager
			.getMinValue(SECOND_COLUMN);
		float secondVarMaxValue = dManager
			.getMaxValue(SECOND_COLUMN);

		final NumberAxis xAxis = new NumberAxis(
				firstVarMinValue,
				firstVarMaxValue, 1);

		final NumberAxis yAxis = new NumberAxis(
				secondVarMinValue,
				secondVarMaxValue, 1);
        
        final ScatterChart<Number, Number> sChart = new
            ScatterChart<Number, Number>(xAxis, yAxis);
        
		sChart.getStylesheets().add(POPUPS_STYLESHEET);
		sChart.getStyleClass().add("chart");
		
		xAxis.setLabel(FIRST_COLUMN);                
        yAxis.setLabel(SECOND_COLUMN);

		ArrayList<String> objsClasses = new ArrayList<>();

		HashMap<String, XYChart.Series> dataSeries = new HashMap<>();

		for(Object obj : dManager.getDataList())
		{
			TableRecord tableRecord = (TableRecord) obj;

			RecordData recordData = tableRecord.getRecordData();

			String objectClass = recordData
				.getFields().get("class").toString();

			boolean classExits = false;

			for(String objClass : objsClasses)
			{
				if(objectClass.equals(objClass))
				{
					classExits = true;
				}
			}
			XYChart.Series series;
			
			if(classExits)
			{
				series = dataSeries.get(objectClass);
			}
			else
			{
				XYChart.Series newSeries = new XYChart.Series();

				newSeries.setName(objectClass);

				dataSeries.put(objectClass, newSeries);

				objsClasses.add(objectClass);
				
				series = newSeries;
			}
			series.getData().add(new XYChart.Data(
				recordData.getFields().get(FIRST_COLUMN),
				recordData.getFields().get(SECOND_COLUMN)
			));
		}
		for(XYChart.Series series : dataSeries.values())
		{
			sChart.getData().add(series);
		}
		return sChart;
	}
}
