/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package statsapp.popups;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import statsapp.managers.DataManager;

/**
 *
 * @author admin
 */
public class StandarizationPopup extends BasePopup {
    
    	// Aktualnie wybrana kolumna
	private static String CURRENT_COLUMN = "";
        
        private float minValue, maxValue ;
                
	private DataManager dManager = DataManager.getInstance();
    
	public StandarizationPopup()
    {
        super(new GridPane(), "Standaryzacja");

        GridPane contentPane = (GridPane) this.getRootNode();
        
        contentPane.setPrefWidth(400);
        
        contentPane.getStyleClass().add("discretization");
        
        Label titleLabel = this.getTitleLabel();
        
        titleLabel.setPrefSize(380, 25);
        
        contentPane.add(titleLabel, 0, 0, 2, 1);
        
		// Wybieranie kolumny ---------------------------------------
        Label chooseColumnLabel = new Label(
                "Wybierz kolumnę:"
        );
        
        Label minLabel = new Label("Podaj Min");
        Label maxLabel = new Label("Podaj Max");
        chooseColumnLabel.setPrefSize(185, 25);
        minLabel.setPrefSize(185, 25);
        maxLabel.setPrefSize(185, 25);
        
        
        final TextField minValueField = new TextField();
        final TextField maxValueField = new TextField();
        
        ComboBox<String> columnsBox = new ComboBox<>();
        
        
        ObservableList<String> columnsItems = columnsBox.getItems();
        
        for(Object col : dManager.getDataTable().getColumns())
        {
            columnsItems.add(((TableColumn) col).getId());
        }
        columnsBox.setPrefSize(185, 25);
        
        columnsBox.valueProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue ov,
				String oldVal, String newVal)
            {                
                CURRENT_COLUMN = newVal;    
            }    
        });
        	
        minValueField.setPrefSize(185, 25);
        minValueField.textProperty().addListener(
				new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> ov,
				String oldVal, String newVal)
			{
                            if(newVal.equals("") == false)
				minValue = Float.parseFloat(newVal);
                            else
                                minValue = 1;
			}
		});
        
        maxValueField.setPrefSize(185, 25);
        maxValueField.textProperty().addListener(
				new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> ov,
				String oldVal, String newVal)
			{
                            if(newVal.equals("") == false)
				maxValue = Float.parseFloat(newVal);
                            else
                                maxValue = 1;
			}
		});
        
        
        
        contentPane.add(chooseColumnLabel, 0, 1);
        contentPane.add(columnsBox, 1, 1);
        contentPane.add(minLabel, 0, 4);
        contentPane.add(minValueField, 1, 4);
        contentPane.add(maxLabel, 0, 5);
        contentPane.add(maxValueField, 1, 5);
        Button okButton = new Button("OK");
        okButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                dManager.mapColumValues(CURRENT_COLUMN, minValue, maxValue);
                hide();
            }
        });
        okButton.setPrefSize(185, 25);
        
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
        
        contentPane.add(okButton, 0, 7);
        contentPane.add(cancelButton, 1, 7);
        
        this.getContent().add(contentPane);
         
    }        
}
