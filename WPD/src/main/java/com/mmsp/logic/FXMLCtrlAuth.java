package com.mmsp.logic;

import java.util.List;
import com.mmsp.dao.impl.DAO_WPDData;
import com.mmsp.model.WPDData;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Контроллер авторизационной формы
 * @author Alex
 */

public class FXMLCtrlAuth extends VBox {

	private Stage stage;

	@FXML
	private TextField tFMiddleName;

	@FXML
	private TextField tFFirstName;

	@FXML
	private TextField tFLastName;

	@FXML
	private Button bSave;

	@FXML
	private Button bCancel;

	public void init(Stage stageAuth) {

		DAO_WPDData dao_WPDData = new DAO_WPDData();
		WPDData wpdData;

		List<WPDData> li = dao_WPDData.getAll(WPDData.class);
		if (li.isEmpty()) {
			wpdData = new WPDData();
			bCancel.setDisable(true); // Если это первый запуск, то придётся заполнить поля
		} else
			wpdData = (dao_WPDData.getAll(WPDData.class)).get(0); 

		this.stage = stageAuth;

		/* конфигурируем окно для выхода */
		bSave.setOnAction(new EventHandler<ActionEvent>() { // на кнопку сохранения просто сохраняем и закрываем окно
			@Override
			public void handle(ActionEvent event) {
				wpdData.setFirstName(tFFirstName.getText());
				wpdData.setLastName(tFLastName.getText());
				wpdData.setMiddleName(tFMiddleName.getText());

				dao_WPDData.update(wpdData);

				stage.close();
			}
		});

		bCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.close();
			}
		});

		/* данный листенер следит за наличием текста в полях и делает доступной/недоступной кнопку сохранить */
		ChangeListener<String> cl2 = new ChangeListener<String>() { // http://stackoverflow.com/questions/12956061/javafx-oninputmethodtextchanged-not-called-after-focus-is-lost
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!tFFirstName.getText().equals("") && !tFLastName.getText().equals("") && !tFMiddleName.getText().equals("")) bSave.setDisable(false); else bSave.setDisable(true);
			}
		};

		tFFirstName.textProperty().addListener(cl2);
		tFLastName.textProperty().addListener(cl2);
		tFMiddleName.textProperty().addListener(cl2);

		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (bCancel.isDisable()) {
					(new Logic()).closeSessionFactory(); // Закрываем сессию
					Platform.exit();
					System.exit(0);
				} else {
					stage.close();
				}
			}
		});

		if (wpdData.getFirstName() != null)
			tFFirstName.setText(wpdData.getFirstName());
		if (wpdData.getLastName() != null)
			tFLastName.setText(wpdData.getLastName());
		if (wpdData.getMiddleName() != null)
			tFMiddleName.setText(wpdData.getMiddleName());

		if (tFFirstName.getText().equals("") || tFLastName.getText().equals("") || tFMiddleName.getText().equals("")) bSave.setDisable(true);

	}

}
