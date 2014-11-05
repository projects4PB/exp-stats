package statsapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import statsapp.panels.RootPanel;

/**
 * Aplikacja SWD
 * 
 * @author Adrian Olszewski
 */
public class StatSAPP extends Application
{
    @Override
    public void start(Stage primaryStage)
    {
        RootPanel rootPanel = new RootPanel();
        
        Scene scene = new Scene(rootPanel, 800, 600);
        
        primaryStage.setTitle("Aplikacja SWD");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
