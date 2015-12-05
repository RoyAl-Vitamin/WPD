package com.mmsp.wpd;

import com.mmsp.logic.FXMLCtrlAuth;
import com.mmsp.logic.FXMLCtrlMain;
import com.mmsp.logic.Logic;
import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * WPD - the work program of the discipline
 * @author Алексей
 */

public class WPD extends Application {
    
    @Override
    public void start(final Stage primaryStage) throws IOException {
        
        final Logic core = new Logic();
        
        VBox vBMain = new FXMLCtrlMain(primaryStage); // подгружаем класс контроллера, расширенного VBox, заодно и запомним Stage (вроде нужен для FileChooser'а)
	    Scene scene = new Scene(vBMain);
	    primaryStage.setTitle("WPD");
	    primaryStage.setScene(scene); 
	    
	    primaryStage.show();
	    
        // TODO Проверить на сущесвование пользователя, если его нет - вызвать код ниже
	    com.mmsp.repository.impl.SubjectRepositoryImpl S1 = new com.mmsp.repository.impl.SubjectRepositoryImpl();
	    
	    // Start Test
		    com.mmsp.model.Subject Su1 = new com.mmsp.model.Subject();
		    Su1.setFirstName("Al");
		    Su1.setLastName("Ro");
		    Su1.setMiddleName("Vit");
		    Su1.setName("Inform");
		    Su1.setTeacher(null);
		    Su1.setVersions(null);
		    S1.addSubject(Su1);
	    // End Test
	    
	    List<com.mmsp.model.Subject> li = S1.getAllSubject();
        if (li.isEmpty()) {
        	Stage stageAuth = new Stage();
        	stageAuth.initModality(Modality.APPLICATION_MODAL);
        	Scene sceneAuth = new Scene(new FXMLCtrlAuth(stageAuth));
        	stageAuth.setScene(sceneAuth);
        	stageAuth.setTitle("Auth");
        	stageAuth.showAndWait();
        }
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent t) {
            	core.closeSessionFactory(); // Закрываем сессию
                Platform.exit();
                System.exit(0);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
