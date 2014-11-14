package statsapp.popups;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import statsapp.data.TableData;
import statsapp.managers.AreaManager;
import statsapp.managers.DataManager;

public class CreateObjectPopup extends BasePopup
{
	DataManager dManager = DataManager.getInstance();
	AreaManager areaManager = AreaManager.getInstance();
        
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

        Button okButton = new Button("Dodaj");
        okButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
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
