package com.mmsp.wpd;

import com.mmsp.logic.FXMLCtrlAuth;
import com.mmsp.logic.FXMLCtrlMain;
import com.mmsp.logic.Logic;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * WPD - the work program of the discipline
 * @author Алексей
 */

public class WPD extends Application {
    
    @Override
    public void start(final Stage primaryStage) throws IOException {
        
        Logic core = new Logic();
        
        VBox vBMain = new FXMLCtrlMain(primaryStage); // подгружаем класс контроллера, расширенного VBox, заодно и запомним Stage (вроде нужен для FileChooser'а)
	    Scene scene = new Scene(vBMain);
	    primaryStage.setTitle("WPD");
	    primaryStage.setScene(scene); 
	    
	    primaryStage.show();
	    
        //TODO Проверить на сущесвование пользователя, если его нет - вызвать код ниже
        if (true) {
        	Stage stageAuth = new Stage();
        	stageAuth.initModality(Modality.APPLICATION_MODAL);
        	Scene sceneAuth = new Scene(new FXMLCtrlAuth(stageAuth));
        	stageAuth.setScene(sceneAuth);
        	stageAuth.setTitle("Auth");
        	stageAuth.showAndWait();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
