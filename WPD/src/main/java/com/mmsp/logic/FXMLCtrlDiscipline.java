package com.mmsp.logic;

import com.mmsp.dao.impl.DAO_HandBookDiscipline;
import com.mmsp.model.HandbookDiscipline;

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

public class FXMLCtrlDiscipline extends VBox {

	private Stage stage;

	private HandbookDiscipline hbD;

	@FXML
	private TextField tfDisciplineName;

	@FXML
	private TextField tfCode;

	@FXML
	private Button bSave;

	@FXML
	private Button bCancel;

	public void init(Stage external_stage, Long id_HBD) {
		stage = external_stage;
		DAO_HandBookDiscipline dao_HBD = new DAO_HandBookDiscipline();
		hbD = dao_HBD.getById(HandbookDiscipline.class, id_HBD);

		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				hbD.setValue("");
				hbD.setCode("");
				dao_HBD.update(hbD);
				stage.close();
			}
		});

		/* данный листенер следит за наличием текста в полях и делает доступной/недоступной кнопку сохранить */
		ChangeListener<String> cl2 = new ChangeListener<String>() { // http://stackoverflow.com/questions/12956061/javafx-oninputmethodtextchanged-not-called-after-focus-is-lost

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (isSatisfies(tfCode.getText()) && isSatisfies(tfDisciplineName.getText())) bSave.setDisable(false); else bSave.setDisable(true);
			}
			
			private boolean isSatisfies(String sValue) {
				if (sValue.equals("") || sValue.length() > 256) {
					return false;
				}
				return true;
			}
		};

		tfDisciplineName.textProperty().addListener(cl2);
		tfCode.textProperty().addListener(cl2);
		if (hbD.getCode() != null)
			tfCode.setText(String.valueOf(hbD.getCode()));
		if (hbD.getValue() != null)
			tfDisciplineName.setText(String.valueOf(hbD.getValue()));

		if ("".equals(tfCode.getText()) || "".equals(tfDisciplineName.getText())) bSave.setDisable(true);
	}

	@FXML
	void clickBSave(ActionEvent event) {

		// TODO Проверить, а не существует ли точно такой же версии в БД?

		hbD.setValue(tfDisciplineName.getText());
		hbD.setCode(tfCode.getText());

		DAO_HandBookDiscipline dao_HBD = new DAO_HandBookDiscipline(); // Просто обновим запись
		dao_HBD.update(hbD);
		System.err.println(hbD.getValue() + " : " + hbD.getCode());
		stage.close();
	}

	@FXML
	void clickBCancel(ActionEvent event) {
		stage.close();
	}
}
