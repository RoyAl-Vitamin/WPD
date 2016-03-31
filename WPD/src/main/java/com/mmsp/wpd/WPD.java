package com.mmsp.wpd;

import com.mmsp.dao.impl.DAO_WPDData;
import com.mmsp.logic.*;
import com.mmsp.model.WPDData;

import java.io.IOException;
import java.util.List;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.controlsfx.dialog.Wizard.LinearFlow;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;

/**
 * WPD - the work program of the discipline
 * @author Алексей
 */

public class WPD extends Application {

	public static WPDData data = new WPDData();
	private Stage currStage;
	
	final Logic core = new Logic();
	
    @Override
    public void start(final Stage primaryStage) throws IOException {
    	currStage = primaryStage;
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
        	//showDlgAuth();
        	Stage stageAuth = new Stage();
        	stageAuth.initModality(Modality.APPLICATION_MODAL);
        	Scene sceneAuth = new Scene(new FXMLCtrlAuth(stageAuth));
        	stageAuth.setScene(sceneAuth);
        	stageAuth.setTitle("Auth");
        	stageAuth.getIcons().add(new Image("Logo.png"));
        	stageAuth.setResizable(false);
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

    private void showDlgAuth() { // UNDONE
    	Window owner = currStage;
		Wizard wizard = new Wizard(owner);
		wizard.setTitle("Auth");
		
		WizardPane page1 = new WizardPane() {
            ValidationSupport vs = new ValidationSupport();
            {
                vs.initInitialDecoration();
                int row = 0;
                GridPane page1Grid = new GridPane();
                page1Grid.setVgap(10);
                page1Grid.setHgap(10);
                page1Grid.add(new Label("Username:"), 0, row);
                TextField txUsername = new TextField();
                txUsername.setId("userName");
                GridPane.setHgrow(txUsername, Priority.ALWAYS);
                vs.registerValidator(txUsername, Validator.createEmptyValidator("Please enter name EMPTY!"));
                page1Grid.add(txUsername, 1, row++);
                page1Grid.add(new Label("Full Name:"), 0, row);
                TextField txFullName = new TextField();
                txFullName.setId("fullName");
                //vs.registerValidator(txFullName, Validator.createEmptyValidator("EMPTY!"));
                GridPane.setHgrow(txFullName, Priority.ALWAYS);
                page1Grid.add(txFullName, 1, row);
                setContent(page1Grid);
            }
            @Override
            public void onEnteringPage(Wizard wizard) {
                wizard.invalidProperty().unbind();
                wizard.invalidProperty().bind(vs.invalidProperty());
            }
        };
        wizard.setFlow(new LinearFlow(page1));
        // show wizard and wait for response
        wizard.showAndWait().ifPresent(result -> {
            if (result == ButtonType.FINISH) {
                System.out.println("Wizard finished, settings: " + wizard.getSettings());
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
