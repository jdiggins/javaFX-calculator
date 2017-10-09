/*
 * John Diggins
 * Reverse Polish Notation Calculator Project
 * Last Modified: 07/05/2017
 *
 */
package calculator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author John
 */
public class Calculator extends Application {
    
    @Override
    public void start(Stage primaryStage) {
       
        CalcPane root = new CalcPane();
        
        Scene scene = new Scene(root, 410, 715);
        
        primaryStage.setTitle("Reverse Polish Notation Calculator");
        primaryStage.setScene(scene);
        
        primaryStage.resizableProperty().set(false);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
