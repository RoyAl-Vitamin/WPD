package com.mmsp.wpd;

/*
 * for Ant
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;*/
import com.mmsp.logic.Logic;

// for Maven
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * WPD - the work program of the discipline
 * @author Алексей
 */
public class WPD extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        
        Logic core = new Logic();
        core.initialization();
        
        Button btn = new Button();
        btn.setText("Say 'WPD Generator'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            //@Override
            public void handle(ActionEvent event) {
                System.out.println("РПД Генератор");
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
