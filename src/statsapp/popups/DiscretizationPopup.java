package statsapp.popups;

import java.util.ArrayList;

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
 * @author Adrian Olszewski
 */
public class DiscretizationPopup extends BasePopup
{
	// Aktualnie wybrana kolumna
	private static String CURRENT_COLUMN = "";

	// Typ aktualnie wybranej kolumny
	private static String COLUMN_TYPE = "";

	// Podział na przedziały
	private static boolean FLAG_CHECKBOX_DIVIDE = false;
	
	// Zamiana na wartości numeryczne
	private static boolean FLAG_CHECKBOX_DIGITIZE = false;
	
	// Preferowanie najliczniejszych klas
	private static boolean FLAG_CHECKBOX_GROUP = false;

	// Ilość przedziałów
	private static int DIVISIONS_NUMBER = 0;

	// Ilość grup wartości
	private static int GROUPS_NUMBER = 0;

	private DataManager dManager = DataManager.getInstance();
    
	public DiscretizationPopup()
    {
        super(new GridPane(), "DYSKRETYZACJA");

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

				if(dManager.isNumbericColumn(CURRENT_COLUMN))
				{
					COLUMN_TYPE = "NUMBER";
				}
				else COLUMN_TYPE = "STRING";
            }    
        });
        
        contentPane.add(chooseColumnLabel, 0, 1);
        contentPane.add(columnsBox, 1, 1);

		// Dyskretyzacja na przedziały ------------------------------
        final Label divisionsNumberLabel = new Label(
                "Podaj ilość przedziałów:"
        );
        divisionsNumberLabel.setPrefSize(185, 25);
        
		final TextField divisionsNumberField = new TextField();
		
        divisionsNumberField.setPrefSize(185, 25);
        divisionsNumberField.textProperty().addListener(
				new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> ov,
				String oldVal, String newVal)
			{
                            if(newVal.equals("") == false)
				DIVISIONS_NUMBER = Integer.parseInt(newVal);
                            else
                                DIVISIONS_NUMBER = 1;
			}
		});
        final Button divideButton = new Button("Podziel na przedziały");
        divideButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
				int divisionsNumber = Integer.parseInt(
					divisionsNumberField.getText()
				);
				dManager.divideValues(
					CURRENT_COLUMN,
					divisionsNumber
				);
                hide();
            }
        });
        divideButton.setPrefSize(380, 25);
		
		divisionsNumberLabel.setDisable(true);		
		divisionsNumberField.setDisable(true);
		
		CheckBox divideCheckBox = new CheckBox(
				"dyskretyzacja zmiennych na przedziały"
		);
		divideCheckBox.setPrefSize(380, 25);
		divideCheckBox.selectedProperty().addListener(
				new ChangeListener<Boolean>()
		{
			public void changed(ObservableValue<? extends Boolean> ov,
				Boolean oldVal, Boolean newVal)
			{
				divisionsNumberLabel.setDisable(oldVal);		
				divisionsNumberField.setDisable(oldVal);
				divideButton.setDisable(oldVal);
				
				FLAG_CHECKBOX_DIVIDE = newVal;
			}
		});
        
		contentPane.add(divideCheckBox, 0, 2, 2, 1);
        contentPane.add(divisionsNumberLabel, 0, 3);
        contentPane.add(divisionsNumberField, 1, 3);

		// Preferowanie najliczniejszych klas
        final Label groupsNumberLabel = new Label(
                "Podaj ilość grup wartości:"
        );
        groupsNumberLabel.setPrefSize(185, 25);
        
		final TextField groupsNumberField = new TextField();
		
        groupsNumberField.setPrefSize(185, 25);
        groupsNumberField.textProperty().addListener(
				new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> ov,
				String oldVal, String newVal)
			{
                            if(newVal.equals("") == false)
				GROUPS_NUMBER = Integer.parseInt(newVal);
                            else
                                GROUPS_NUMBER = 1;
			}
		});
		groupsNumberLabel.setDisable(true);		
		groupsNumberField.setDisable(true);
		
		CheckBox groupCheckBox = new CheckBox(
				"preferowanie najliczniejszych klas"
		);
		groupCheckBox.setPrefSize(380, 25);
		groupCheckBox.selectedProperty().addListener(
				new ChangeListener<Boolean>()
		{
			public void changed(ObservableValue<? extends Boolean> ov,
				Boolean oldVal, Boolean newVal)
			{
				groupsNumberLabel.setDisable(oldVal);		
				groupsNumberField.setDisable(oldVal);

				FLAG_CHECKBOX_GROUP = newVal;
			}
		});

		contentPane.add(groupCheckBox, 0, 4, 2, 1);
        contentPane.add(groupsNumberLabel, 0, 5);
        contentPane.add(groupsNumberField, 1, 5);

		// Zamiana na wartości numeryczne
		CheckBox digitizeCheckBox = new CheckBox(
				"zamiana na wartości numeryczne"
		);
		digitizeCheckBox.setPrefSize(380, 25);
		digitizeCheckBox.selectedProperty().addListener(
				new ChangeListener<Boolean>()
		{
			public void changed(ObservableValue<? extends Boolean> ov,
				Boolean oldVal, Boolean newVal)
			{
				FLAG_CHECKBOX_DIGITIZE = newVal;
			}
		});
		contentPane.add(digitizeCheckBox, 0, 6, 2, 1);
        
		Button okButton = new Button("OK");
        okButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
				discretizeColumn();

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

	private void discretizeColumn()
	{
		if(FLAG_CHECKBOX_DIVIDE
				&& COLUMN_TYPE.equals("NUMBER"))
		{
			dManager.divideValues(
					CURRENT_COLUMN,
					DIVISIONS_NUMBER
			);
		}
		if(FLAG_CHECKBOX_GROUP)
		{
			dManager.groupByValueAmount(
					CURRENT_COLUMN,
					GROUPS_NUMBER
			);
		}
		if(FLAG_CHECKBOX_DIGITIZE
				&& COLUMN_TYPE.equals("STRING"))
		{
			dManager.digitizeValues(
					CURRENT_COLUMN
			);
		}
	}
}
