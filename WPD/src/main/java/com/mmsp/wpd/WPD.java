package com.mmsp.wpd;

import com.mmsp.dao.impl.DAO_WPDData;
import com.mmsp.logic.*;
import com.mmsp.model.WPDData;

import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;

/**
 * WPD - the work program of the discipline
 * @author Алексей
 */

public class WPD extends Application {
    
	public static WPDData data = new WPDData();
	
    @Override
    public void start(final Stage primaryStage) throws IOException {
        
        final Logic core = new Logic();
        
        VBox vBMain = new FXMLCtrlMain(primaryStage); // подгружаем класс контроллера, расширенного VBox, заодно запомним Stage (вроде нужен для FileChooser'а)
	    Scene scene = new Scene(vBMain);
	    primaryStage.setTitle("WPD");
	    primaryStage.getIcons().add(new Image("Logo.png"));
	    primaryStage.setScene(scene); 
	    
	    primaryStage.show();

	    DAO_WPDData daoS = new DAO_WPDData();

	    List<WPDData> li = daoS.getAll(data);

        if (li.isEmpty()) {
        	Stage stageAuth = new Stage();
        	stageAuth.initModality(Modality.APPLICATION_MODAL);
        	Scene sceneAuth = new Scene(new FXMLCtrlAuth(stageAuth));
        	stageAuth.setScene(sceneAuth);
        	stageAuth.setTitle("Auth");
        	stageAuth.getIcons().add(new Image("Logo.png"));
        	stageAuth.showAndWait();
        	daoS.add(data); // TODO Определить: нужно ли сохранение?
        } else {
        	if (li.size() == 1) {
        		data = li.get(0);
        	} else {
        		System.err.println("ERROR: number of Subject == " + li.size());
        		// FIXME как такое обработать?
        		data = li.get(0);
        		/*Alert alert = new Alert(AlertType.ERROR); // Почему не работает?
        		alert.setTitle("Ошибка");
        		alert.setHeaderText("К сожалению, ...");
        		alert.setContentText("...возникла ошибка при выборке");
        		alert.showAndWait();*/
        	}
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
