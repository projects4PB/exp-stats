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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import statsapp.managers.AreaManager;
import statsapp.models.MetricType;

/**
 *
 * @author admin
 */
public class ClassifyQuantityPopup extends BasePopup {
    
    
    private static final String METRIC_EUKLIDES =
            "odległość Euklidesowa";
    private static final String METRIC_MAHALANOBIS =
            "odległość Mahalanobisa";
    private static final String METRIC_L_INFINITY =
            "metryka L_nieskończoność";
    private static final String METRIC_MANHATTAN =
            "metryka Manhattan"; 

    private MetricType metricChoice ;
    
    String numberOfNeighbours = "";
    
    private AreaManager areaManager  = AreaManager.getInstance();
    
    public ClassifyQuantityPopup()
    {
        super(new GridPane(), "Jakość klasyfikacji");

        GridPane contentPane = (GridPane) this.getRootNode();
        
        contentPane.setPrefSize(400, 200);
        
        contentPane.getStyleClass().add("create-object");
        
        Label neighbourNumber = new Label("Ilość sąsiadujących obiektów:");
                       
        neighbourNumber.setPrefSize(300, 25);

        TextField textFieldNeighbour = new TextField();

        textFieldNeighbour.setPrefSize(100, 25);

        contentPane.add(neighbourNumber, 0, 2);
        contentPane.add(textFieldNeighbour, 1, 2);
        
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
        
        contentPane.add(metricType, 0, 1);
        contentPane.add(comboBox, 1, 1);
        final Label quanlityLabel = new Label();
        contentPane.add(quanlityLabel, 0, 4);
        
        Button okButton = new Button("OK");
        okButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {

                float quanlity = areaManager.getClassifyQuantity(Integer.parseInt(numberOfNeighbours), metricChoice);
                quanlityLabel.setText(Float.toString(quanlity));
                
                //hide();
            }
        });
        
        okButton.setPrefSize(185, 25);                  
        contentPane.add(okButton, 0, 3);
        
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
        contentPane.add(cancelButton, 1, 3);
        
        this.getContent().add(contentPane);
    }
}

