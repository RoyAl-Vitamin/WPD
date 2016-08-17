package com.mmsp.logic;

import java.io.IOException;

import com.mmsp.dao.impl.DAO_HandBookDiscipline;
import com.mmsp.dao.impl.DAO_WPDVersion;
import com.mmsp.model.HandbookDiscipline;
import com.mmsp.model.WPDVersion;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FXMLCtrlVersionName extends VBox {

	private Stage stage;

	private WPDVersion wpdVers;

	@FXML
	private TextField tfVersionName;

	@FXML
	private Button bSave;

	@FXML
	void clickBSave(ActionEvent event) {
		wpdVers.setName(tfVersionName.getText());

		DAO_WPDVersion dao_WPDVers = new DAO_WPDVersion(); // Просто обновим запись
		dao_WPDVers.update(wpdVers);
		System.err.println("Version have been saved with name == " + wpdVers.getName());
		stage.close();
	}

	public void init(Stage stage, Long id_wpdVers) {

		this.stage = stage;

		DAO_WPDVersion dao_WPDVers = new DAO_WPDVersion();
		wpdVers = dao_WPDVers.getById(WPDVersion.class, id_wpdVers);

		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				stage.close();
			}
		});

		/* данный листенер следит за наличием текста в поле и делает доступной/недоступной кнопку сохранить */
		ChangeListener<String> cl2 = new ChangeListener<String>() { // http://stackoverflow.com/questions/12956061/javafx-oninputmethodtextchanged-not-called-after-focus-is-lost

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (isSatisfies(tfVersionName.getText())) bSave.setDisable(false); else bSave.setDisable(true);
			}

			private boolean isSatisfies(String sValue) {
				if (sValue != null) {
					if (!sValue.equals("")) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		};

		tfVersionName.textProperty().addListener(cl2);
		
		if (wpdVers.getName() != null)
			tfVersionName.setText(String.valueOf(wpdVers.getName()));
	}
}
