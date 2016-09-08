package com.mmsp.logic;

import com.mmsp.model.Module;
import com.mmsp.model.Section;
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

public class FXMLCtrlModalSection extends VBox {

	private Semester semester;

	private Module module;

	private Section section;

	private FXMLCtrlNewTab fxmlCtrlParentTab;

	private Stage stage;

	@FXML
	private Button bSave;

	@FXML
	private Button bCancel;

	@FXML
	private TextField tfNumberOfSection;

	@FXML
	private Label lNumberOfSemester;

	@FXML
	private Label lNumberOfModule;

	@FXML
	private TextArea taDescOfSection;

	@FXML
	void clickBSave(ActionEvent event) {
		if (section != null) {
			boolean b = false;
			if (section.getNumber() != Integer.parseInt(tfNumberOfSection.getText())) {
				b = true;
			}
			section.setNumber(Integer.parseInt(tfNumberOfSection.getText()));
			section.setName(taDescOfSection.getText());
			if (b) { // требуется копирование существующего в новый, удаление его из модуля и вставка нового в модуля
				Section sec = new Section();
				sec.setName(section.getName());
				sec.setNumber(section.getNumber());
				sec.setTreeTheme(section.getTreeTheme());
				sec.setModule(section.getModule());
				module.getTreeSection().remove(section);
				module.getTreeSection().add(sec);
			}
		} else {
			Section sec = new Section();
			sec.setNumber(Integer.parseInt(tfNumberOfSection.getText()));
			sec.setName(taDescOfSection.getText());
			sec.setModule(module);
			module.getTreeSection().add(sec);
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

		ChangeListener<String> cl2 = new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (isInteger(tfNumberOfSection.getText()) && isNotExist(Integer.parseInt(tfNumberOfSection.getText()))) bSave.setDisable(false); else bSave.setDisable(true);
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

			private boolean isNotExist(int numOfSec) { // проверка на существование раздела
				if (module.getSection(numOfSec) != null) return false;
				return true;
			}
		};

		tfNumberOfSection.textProperty().addListener(cl2);
	}

	public void setController(FXMLCtrlNewTab fxmlCtrlParentTab) {
		this.fxmlCtrlParentTab = fxmlCtrlParentTab;
	}

	/**
	 * Передача семестра и номера модуля в случае добавления новой секции
	 * @param semester сам семестр
	 */
	public void setRoot(Semester semester, int numOfMod) {
		this.semester = semester;
		this.module = semester.getModule(numOfMod);
		this.lNumberOfSemester.setText(String.valueOf(semester.getNUMBER_OF_SEMESTER()));
		this.lNumberOfModule.setText(String.valueOf(numOfMod));
		bSave.setDisable(true);
	}

	/**
	 * Передача семестра, номера модуля, номера раздела в случае его изменения
	 * @param semeser сам семестр
	 * @param numOfMod номер модуля
	 * @param numOfSec номер раздела
	 */
	public void setRoot(Semester semester, int numOfMod, int numOfSec) {
		this.semester = semester;
		this.module = semester.getModule(numOfMod);
		this.section = module.getSection(numOfSec);
		this.lNumberOfSemester.setText(String.valueOf(semester.getNUMBER_OF_SEMESTER()));
		this.lNumberOfModule.setText(String.valueOf(numOfMod));
		this.tfNumberOfSection.setText(String.valueOf(section.getNumber()));
		this.taDescOfSection.setText(section.getName());
		bSave.setDisable(false);
	}
}
