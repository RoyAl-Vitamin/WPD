package com.mmsp.wpd;

import com.mmsp.dao.impl.DAO_WPDData;
import com.mmsp.logic.*;
import com.mmsp.model.WPDData;

import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
	
	final Logic core = new Logic();
	
    @Override
    public void start(final Stage primaryStage) throws IOException {

    	try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Main.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			
			FXMLCtrlMain fxmlCtrlMain = fxmlLoader.getController();
			fxmlCtrlMain.setStage(primaryStage);
			fxmlCtrlMain.setController(fxmlCtrlMain); // Запомним контроллер главного окна
			
			Scene scene = new Scene(root);
			
			primaryStage.setScene(scene);
			primaryStage.setTitle("WPD");
		    primaryStage.getIcons().add(new Image("Logo.png"));
		    
    	} catch(Exception e) {
    		e.printStackTrace();
    	}

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
        	daoS.add(data); 
        	// TODO Определить: нужно ли сохранение?
        } else {
        	if (li.size() == 1) {
        		data = li.get(0);
        	} else {
        		System.err.println("ERROR: number of WPDData == " + li.size());
        		// FIXME как такое обработать?
        		data = li.get(0);
        		/*Alert alert = new Alert(AlertType.ERROR); // Почему не работает? UPD: Update JDK to 8u40
        		alert.setTitle("Ошибка");
        		alert.setHeaderText("К сожалению, ...");
        		alert.setContentText("...возникла ошибка при выборке");
        		alert.showAndWait();*/
        	}
        }
        
        // Посмотрим, что он сохранил =_=
        System.err.println(data.toString());
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() { // Работает только на закрытие окна по крестику
            public void handle(WindowEvent t) {
            	stop();
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() { // адекватное закрытие окна
    	core.closeSessionFactory(); // Закрываем сессию
    	Platform.exit();
        System.exit(0);
    }
    
}
