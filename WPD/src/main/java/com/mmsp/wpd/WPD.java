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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * WPD - the work program of the discipline
 * @author Алексей
 */

public class WPD extends Application {

	private WPDData data = new WPDData();

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

		DAO_WPDData dao_WPDData = new DAO_WPDData();

		List<WPDData> li = dao_WPDData.getAll(WPDData.class);

		if (li.isEmpty()) { // Следим за тем, что бы было не больше 1 WPDData!
			showDlgAuth();
			data = (dao_WPDData.getAll(WPDData.class)).get(0);
		} else {
			if (li.size() == 1) {
				data = li.get(0);
			} else {
				System.err.println("WARNING: number of WPDData == " + li.size());
				data = li.get(0);

				for (int i = 1; i < li.size(); i++)
					dao_WPDData.remove(li.get(i));
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

	private void showDlgAuth() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Auth.fxml"));
		Parent root = null;
		try {
			root = (Parent) fxmlLoader.load();
		} catch (IOException e) {
			System.err.println("Не удалось загрузить форму авторизации");
			e.printStackTrace();
		}
		Scene scene = new Scene(root);

		Stage stageAuth = new Stage();
		FXMLCtrlAuth fxmlCtrlAuth = fxmlLoader.getController();
		fxmlCtrlAuth.init(stageAuth);
		stageAuth.setScene(scene);
		stageAuth.setTitle("Auth");
		stageAuth.getIcons().add(new Image("Logo.png"));
		stageAuth.initModality(Modality.APPLICATION_MODAL);
		stageAuth.setResizable(false);
		stageAuth.showAndWait();
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
