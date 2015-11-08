package com.mmsp.wpd;

import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.nio.file.attribute.PosixFilePermission;

/**
 * @author Алексей
 * окно для авторизации
 */

@SuppressWarnings("restriction")
public class wAuth {

    private String firstName; // Имя
    
    private String lastName; // Фамилия

    private String middleName; // Отчество
	
    //UNDONE запись в БД полей, описанных выше
	public static void windowAuth(String title) {
		
		Stage additionalStage = new Stage();
		additionalStage.initModality(Modality.APPLICATION_MODAL); // задаёт модальность окна
		
		GridPane gP = new GridPane();
		
		Label lFirstName = new Label("Имя");
		TextField tFFirstName = new TextField();
		
		Label lLastName = new Label("Фамилия");
		TextField tFLastName = new TextField();
		
		Label lMiddleName = new Label("Отчество");
		TextField tFMiddleName = new TextField();	
		
		Button bOk = new Button("Принять");
		bOk.setOnAction(event->additionalStage.close());
		
		gP.add(lFirstName, 0, 0);
		gP.add(lLastName, 0, 1);
		gP.add(lMiddleName, 0, 2);
		gP.add(tFFirstName, 1, 0);
		gP.add(tFLastName, 1, 1);
		gP.add(tFMiddleName, 1, 2);
		gP.add(bOk, 1, 3);
		gP.setVgap(20);
		gP.setHgap(20);
		//gridPane.setGridLinesVisible(true);
		gP.setAlignment(Pos.CENTER);
		gP.setPadding(new Insets(25, 25, 25, 25));
		
		Pane root = new Pane();
		
		Scene scene = new Scene(root);

		root.getChildren().add(gP);
		additionalStage.setScene(scene);
		additionalStage.setTitle(title);
		additionalStage.showAndWait();
	}
}
