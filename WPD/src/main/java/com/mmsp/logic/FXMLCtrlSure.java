package com.mmsp.logic;

import com.mmsp.dao.impl.DAO_HandBookDiscipline;
import com.mmsp.dao.impl.DAO_WPDVersion;
import com.mmsp.model.HandbookDiscipline;
import com.mmsp.model.WPDVersion;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FXMLCtrlSure extends VBox {

	private Stage stage;

	private HandbookDiscipline hbD;

	private WPDVersion wpdVers;

	@FXML
	private TextField tfVersionName;

	@FXML
	private Button bSave;

	@FXML
	private Button bCancel;

	@FXML
	void clickBSave(ActionEvent event) {
		wpdVers.setName(tfVersionName.getText());

		DAO_WPDVersion dao_WPDVers = new DAO_WPDVersion(); // Просто обновим запись
		dao_WPDVers.update(wpdVers);
		stage.close();
	}

	@FXML
	void clickBCancel(ActionEvent event) {
		stage.close();
	}

	public void init(Stage stage, Long id_wpdVers, Long id_HandbookDiscipline) {

		this.stage = stage;

		DAO_WPDVersion dao_WPDVers = new DAO_WPDVersion();
		wpdVers = dao_WPDVers.getById(WPDVersion.class, id_wpdVers);

		DAO_HandBookDiscipline dao_HBD = new DAO_HandBookDiscipline();
		hbD = dao_HBD.getById(HandbookDiscipline.class, id_HandbookDiscipline);

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
				if (isSatisfies(tfVersionName.getText()) && isNotExist()) bSave.setDisable(false); else bSave.setDisable(true);
			}

			private boolean isSatisfies(String sValue) {
				if (sValue.equals("") || sValue.length() > 256)
					return false;
				return true;
			}

			private boolean isNotExist() {
				for (WPDVersion vers : hbD.getVersions()) {
					if (vers.getName() != null && vers.getName().equals(tfVersionName.getText())) return false;
				}
				return true;
			}
		};

		tfVersionName.textProperty().addListener(cl2);
		
		if (wpdVers.getName() != null)
			tfVersionName.setText(String.valueOf(wpdVers.getName()));
	}
}
