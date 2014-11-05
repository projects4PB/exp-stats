package statsapp.popups;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.GridPane;
import statsapp.managers.DataManager;

/**
 *
 * @author Adrian Olszewski
 */
public class StatisticsPopup extends BasePopup
{
    private final GridPane detailsPanel = new GridPane();
    
    public StatisticsPopup()
    {
        super(new GridPane(), "STATYSTYKI");
        
        GridPane contentPane = (GridPane) this.getRootNode();
        
        contentPane.setPrefSize(400, 400);
        
        contentPane.getStyleClass().add("statistics");
        
        Label titleLabel = this.getTitleLabel();
        
        titleLabel.setPrefSize(380, 25);
        
        contentPane.add(titleLabel, 0, 0, 2, 1);
        
        Label fieldLabel = new Label(
                "Wybierz kolumnę:"
        );
        fieldLabel.setPrefSize(185, 25);
        
        ComboBox<String> comboBox = new ComboBox<>();
        
        DataManager dManager = DataManager.getInstance();
        
        ObservableList<String> comboItems = comboBox.getItems();
        
        for(Object col : dManager.getDataTable().getColumns())
        {
            comboItems.add(((TableColumn) col).getId());
        }
        comboBox.setPrefSize(185, 25);
        
        comboBox.valueProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue ov, String t, String t1)
            {                
                showDetails(t1);                
            }    
        });
        
        contentPane.add(fieldLabel, 0, 1);
        contentPane.add(comboBox, 1, 1);
        
        contentPane.add(this.detailsPanel, 0, 2, 2, 1);
        
        this.getContent().add(contentPane);
        
        
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
        contentPane.add(cancelButton, 0, 5);  
        
    }
    
    private void showDetails(String col_id)
    {
        DataManager dManager = DataManager.getInstance();
        
        this.detailsPanel.getChildren().removeAll(
                this.detailsPanel.getChildren()
        );
        Label averageLabel = new Label();
        
        averageLabel.setText("" + dManager.getAverage(col_id));
        
        this.detailsPanel.add(new Label("ŚREDNIA:"), 0, 0);
        this.detailsPanel.add(averageLabel, 1, 0);
        
        Label medianLabel = new Label();
        
        medianLabel.setText("" + dManager.getMedian(col_id));
        
        this.detailsPanel.add(new Label("MEDIANA:"), 0, 1);
        this.detailsPanel.add(medianLabel, 1, 1);
        
        Label minValueLabel = new Label();
        
        minValueLabel.setText("" + dManager.getMinValue(col_id));
        
        this.detailsPanel.add(new Label("MIN:"), 0, 2);
        this.detailsPanel.add(minValueLabel, 1, 2);
        
        Label maxValueLabel = new Label();
        
        maxValueLabel.setText("" + dManager.getMaxValue(col_id));
        
        this.detailsPanel.add(new Label("MAX:"), 0, 3);
        this.detailsPanel.add(maxValueLabel, 1, 3);
        
        Label Q1ValueLabel = new Label();
        
        Q1ValueLabel.setText("" + dManager.getQ1Value(col_id));
        
        this.detailsPanel.add(new Label("Q1:"), 0, 4);
        this.detailsPanel.add(Q1ValueLabel, 1, 4);
        
        Label Q3ValueLabel = new Label();
        
        Q3ValueLabel.setText("" + dManager.getQ3Value(col_id));
        
        this.detailsPanel.add(new Label("Q3:"), 0, 5);
        this.detailsPanel.add(Q3ValueLabel, 1, 5);
        
        float[] percentileCValues = {0.05f, 0.1f, 0.9f, 0.95f};
        
        for(int i = 0; i < percentileCValues.length; i++)
        {
            Label percentileValueLabel = new Label();

            percentileValueLabel.setText("" + dManager
                    .getPercentileValue(
                            col_id, percentileCValues[i]
                    ));

            Label valueLabel = new Label();
            
            valueLabel.setText("P[" + percentileCValues[i] + "]:");
            
            this.detailsPanel.add(valueLabel, 0, 6 + i);
            this.detailsPanel.add(percentileValueLabel, 1, 6 + i);
        }
    }
}
