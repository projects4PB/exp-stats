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
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.GridPane;
import statsapp.managers.DataManager;

/**
 *
 * @author admin
 */
public class NormalizationPopup extends BasePopup {

	// Aktualnie wybrana kolumna
	private static String CURRENT_COLUMN = "";

	private DataManager dManager = DataManager.getInstance();
    
	public NormalizationPopup()
    {
        super(new GridPane(), "Normalizacja");

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
        chooseColumnLabel.setPrefSize(185, 25);
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
        
        contentPane.add(chooseColumnLabel, 0, 1);
        contentPane.add(columnsBox, 1, 1);

               
        Button okButton = new Button("OK");
        okButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
               dManager.getStandardization(CURRENT_COLUMN);
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
