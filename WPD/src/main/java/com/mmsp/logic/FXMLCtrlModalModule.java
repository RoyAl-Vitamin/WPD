package com.mmsp.logic;

import com.mmsp.model.Module;
import com.mmsp.model.Semester;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXMLCtrlModalModule extends VBox {

	private Stage stage; // Для Resize

	private FXMLCtrlNewTab fxmlCtrlParentTab; // Для Repaint

	private Semester semester = null;

	private Module module = null;

	@FXML
	private Button bSave;

	@FXML
	private Button bCancel;

	@FXML
	private Label lNumberOfSemester;

	@FXML
	private TextField tfNumberOfModule;

	@FXML
	private TextArea taDescOfModule;

	@FXML
	void clickBSave(ActionEvent event) {
		if (module != null) {
			boolean b = false;
			if (module.getNumber() != Integer.parseInt(tfNumberOfModule.getText())) {
				b = true;
			}
			module.setNumber(Integer.parseInt(tfNumberOfModule.getText()));
			module.setName(taDescOfModule.getText());
			if (b) { // требуется копирование существующего в новый, удаление его из семестра и вставка нового в семестр
				Module mod = new Module();
				mod.setName(module.getName());
				mod.setNumber(module.getNumber());
				mod.setTreeSection(module.getTreeSection());
				semester.getTreeModule().remove(module);
				semester.getTreeModule().add(mod);
			}
		} else {
			Module mod = new Module();
			mod.setNumber(Integer.parseInt(tfNumberOfModule.getText()));
			mod.setName(taDescOfModule.getText());
			semester.getTreeModule().add(mod);
		}

		fxmlCtrlParentTab.createTree();
		stage.close();
	}

	@FXML
	void clickBCancel(ActionEvent e) {
		stage.close();
	}

	public void init(Stage stageModalM) {
		this.stage = stageModalM;

		ChangeListener<String> cl2 = new ChangeListener<String>() { // http://stackoverflow.com/questions/12956061/javafx-oninputmethodtextchanged-not-called-after-focus-is-lost

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (isInteger(tfNumberOfModule.getText()) && isNotExist(Integer.parseInt(tfNumberOfModule.getText()))) bSave.setDisable(false); else bSave.setDisable(true);
			}

			private boolean isInteger(String sValue) { // проверка на ввод и что б в Integer помещалось
				if (sValue.length() > 2) return false;
				try {
					Integer.parseInt(sValue);
					return true;
				} catch (NumberFormatException ex) {
					return false;
				}
			}

			private boolean isNotExist(int numOfMod) { // проверка на существование модуля
				if (semester.getModule(numOfMod) != null) return false;
				return true;
			}
		};

		tfNumberOfModule.textProperty().addListener(cl2);
	}

	public void setController(FXMLCtrlNewTab fxmlCtrlParentTab) {
		this.fxmlCtrlParentTab = fxmlCtrlParentTab;
	}

	/**
	 * Передача семестра в случае добавления нового модуля
	 * @param semester сам семестр
	 */
	public void setRoot(Semester semester) {
		this.semester = semester;
		this.lNumberOfSemester.setText(String.valueOf(semester.getNUMBER_OF_SEMESTER()));
		bSave.setDisable(true);
	}

	/**
	 * Передача модуля в случае его изменения
	 * @param semeser сам семестр
	 * @param numOfMod номер модуля
	 */
	public void setRoot(Semester semester, int numOfMod) {
		this.module = semester.getModule(numOfMod);
		this.semester = semester;
		this.lNumberOfSemester.setText(String.valueOf(semester.getNUMBER_OF_SEMESTER()));
		this.tfNumberOfModule.setText(String.valueOf(module.getNumber()));
		this.taDescOfModule.setText(module.getName());
		bSave.setDisable(false);
	}

}
