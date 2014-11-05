package statsapp.popups;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import statsapp.data.RecordData;
import statsapp.data.TableData;
import statsapp.data.records.TableRecord;
import statsapp.managers.AreaManager;
import statsapp.managers.DataManager;
import statsapp.models.AreaObject;
import statsapp.models.MetricType;

public class CreateObjectPopup extends BasePopup
{
	private static final String METRIC_EUKLIDES =
		"odległość Euklidesowa";
	private static final String METRIC_MAHALANOBIS =
		"odległość Mahalanobisa";
	private static final String METRIC_L_INFINITY =
		"metryka L_nieskończoność";
	private static final String METRIC_MANHATTAN =
		"metryka Manhattan"; 
                
        private MetricType metricChoice ;
        
        DataManager dManager = DataManager.getInstance();
        AreaManager areaManager = AreaManager.getInstance();
        String numberOfNeighbours = "";
        
	public CreateObjectPopup()
	{
            super(new GridPane(), "NOWY OBIEKT");

        GridPane contentPane = (GridPane) this.getRootNode();
        
        contentPane.setPrefSize(500, 400);
        
        contentPane.getStyleClass().add("create-object");
         
        TableData tableData = dManager.getTableData();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        HBox  content;
        TextField textField;
        Label  columnLabel;
        final String[] colNames = tableData.getColumnsNames();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(500, 300);
        int index = 0;       
        final ArrayList<TextField> textFieldsList = new  ArrayList<TextField>();

        for (String colName : colNames){
            
            if(colName.equals("class")== false && colName.contains("_") == false){
                
                content = new HBox ();
                content.setSpacing(10);
                columnLabel  = new Label(colName);
                textField = new TextField();
                textField.setPrefSize(150, 25);
                textField.setId(Integer.toString(index));
                columnLabel.setPrefSize(70, 25);
                content.getChildren().add(columnLabel);
                content.getChildren().add(textField);
                grid.add(content, 0, index);
                textFieldsList.add(textField);   
                
                index++;
            }
        }

        scrollPane.setContent(grid);
        
        Label titleLabel = this.getTitleLabel();        
        titleLabel.setPrefSize(480, 25);

        contentPane.add(titleLabel, 0, 0, 2, 1);
        contentPane.add(scrollPane, 0, 1,2,1);
        Label metricType = new Label("Metryka:");

        metricType.setPrefSize(200, 25);

        ComboBox<String> comboBox = new ComboBox<>();
        
        ObservableList<String> comboItems = comboBox.getItems();
        
        comboItems.add(METRIC_EUKLIDES);
        comboItems.add(METRIC_MAHALANOBIS);
        comboItems.add(METRIC_MANHATTAN);
        comboItems.add(METRIC_L_INFINITY);
        
	comboBox.setPrefSize(250, 25);
        
        comboBox.valueProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue ov, String t, String t1)
            {
                switch(t1)
                {
                    case "odległość Euklidesowa" :
                        metricChoice = MetricType.METRIC_EUKLIDES;
                    break;

                    case "odległość Mahalanobisa":
                         metricChoice = MetricType.METRIC_MAHALANOBIS;
                    break;   
                    
                    case "metryka L_nieskończoność" :
                         metricChoice = MetricType.METRIC_L_INFINITY;
                    break; 
                    
                    case "metryka Manhattan":
                         metricChoice = MetricType.METRIC_MANHATTAN;
                    break;       
                }   
            }    
        });
        
        contentPane.add(metricType, 0, 3);
        contentPane.add(comboBox, 1, 3);

        Label neighbourNumber = new Label(
                        "Ilość sąsiadujących obiektów:");

        neighbourNumber.setPrefSize(300, 25);

        TextField textFieldNeighbour = new TextField();

        textFieldNeighbour.setPrefSize(100, 25);

        contentPane.add(neighbourNumber, 0, 4);
        contentPane.add(textFieldNeighbour, 1, 4);
        
        textFieldNeighbour.textProperty().addListener(
				new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> ov,
                String oldVal, String newVal)
            {
                numberOfNeighbours =  newVal;
            }
        });

        Button okButton = new Button("Dodaj");
        okButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                ArrayList<Float> wektor = new ArrayList<Float>();
                
                for (TextField object : textFieldsList){
                    float valueTextField = Float.parseFloat(object.getText());
                    wektor.add(valueTextField);
                }
                
                AreaObject areaObject = new AreaObject(wektor);
                String newClassObject = areaManager.classifyObject(areaObject, Integer.parseInt(numberOfNeighbours), metricChoice);
                areaObject.setAreaObjectClass(newClassObject);
                
                HashMap<String, Object> fields = new HashMap<String, Object>();
                int index = 0;
                
                for (String colName : colNames){
                    
                    if(index < areaObject.getVars().size()){
                        fields.put(colName, areaObject.getVars().get(index));
                    }

                    if(colName.equals("class")){
                        fields.put("class", areaObject.getClass());
                    }
                    
                    index ++;
                }
                
                RecordData recordData = new RecordData(fields);
                TableData tableData = dManager.getTableData();
                tableData.addRecord(recordData);
                hide();
            }
        });
        okButton.setPrefSize(200, 25);                  
        contentPane.add(okButton, 0, 5);
                          
        Button cancelButton = new Button("ANULUJ");
        cancelButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                hide();
            }
        });
        cancelButton.setPrefSize(185, 25);                  
        contentPane.add(cancelButton, 1, 5);
                
        for (final TextField object : textFieldsList){
            final int i = textFieldsList.indexOf(object);
            object.textProperty().addListener(
                new ChangeListener<String>()
            {
            @Override
                public void changed(ObservableValue<? extends String> ov,
                    String oldVal, String newVal)
		{
                   object.setText(newVal);
                   textFieldsList.set(i, object);
                }
		});
        }
   
        this.getContent().add(contentPane);
        
	}
}
