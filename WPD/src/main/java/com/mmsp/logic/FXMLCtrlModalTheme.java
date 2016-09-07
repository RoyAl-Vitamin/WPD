package com.mmsp.logic;

import com.mmsp.model.Module;
import com.mmsp.model.Section;
import com.mmsp.model.Semester;
import com.mmsp.model.ThematicPlan;

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

public class FXMLCtrlModalTheme extends VBox {

	private FXMLCtrlNewTab fxmlCtrlParentTab;

	private Semester semester;

	private Module module;

	private Section section;

	private ThematicPlan theme;

	private Stage stage;

	@FXML
	private Label lNumberOfSemester;

	@FXML
	private Label lNumberOfModule;

	@FXML
	private Label lNumberOfSection;

	@FXML
	private TextField tfNumberOfTheme;
	
	@FXML
	private TextField tfTitleOfTheme;

	@FXML
	private TextArea taDescOfTheme;

	@FXML
	private Button bSave;

	@FXML
	private Button bCancel;

	@FXML
	void clickBSave(ActionEvent event) {

		if (theme != null) {
			boolean b = false;
			if (theme.getNumber() != Integer.parseInt(tfNumberOfTheme.getText())) {
				b = true;
			}
			theme.setNumber(Integer.parseInt(tfNumberOfTheme.getText()));
			theme.setTitle(tfTitleOfTheme.getText());
			theme.setDescription(taDescOfTheme.getText());
			if (b) { // требуется копирование существующего в новый, удаление его из модуля и вставка нового в модуля
				ThematicPlan tempTheme = new ThematicPlan();
				tempTheme.setNumber(theme.getNumber());
				tempTheme.setTitle(theme.getTitle());
				tempTheme.setDescription(theme.getDescription());
				tempTheme.setBelongingToTheSemester(semester.getNUMBER_OF_SEMESTER());
				tempTheme.setBelongingToTheModule(module.getNumber());
				tempTheme.setBelongingToTheSection(theme.getBelongingToTheSection());
				tempTheme.setL(theme.getL());
				tempTheme.setPZ(theme.getPZ());
				tempTheme.setLR(theme.getLR());
				tempTheme.setKSR(theme.getKSR());
				tempTheme.setSRS(theme.getSRS());
				section.getTreeTheme().remove(theme);
				section.getTreeTheme().add(tempTheme);
			}
		} else {
			ThematicPlan tempTheme = new ThematicPlan();
			tempTheme.setNumber(Integer.parseInt(tfNumberOfTheme.getText()));
			tempTheme.setTitle(tfTitleOfTheme.getText());
			tempTheme.setDescription(taDescOfTheme.getText());
			tempTheme.setBelongingToTheSemester(semester.getNUMBER_OF_SEMESTER());
			tempTheme.setBelongingToTheModule(module.getNumber());
			tempTheme.setBelongingToTheSection(section.getNumber());
			section.getTreeTheme().add(tempTheme);
		}

		fxmlCtrlParentTab.createTree();
		stage.close();
	}

	@FXML
	void clickBCancel(ActionEvent event) {
		stage.close();
	}

	public void init(Stage stageTheme) {

		this.stage = stageTheme;

		ChangeListener<String> cl2 = new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (isInteger(tfNumberOfTheme.getText()) && isNotExist(Integer.parseInt(tfNumberOfTheme.getText()))) bSave.setDisable(false); else bSave.setDisable(true);
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

			private boolean isNotExist(int numOfTheme) { // проверка на существование темы
				if (section.getTheme(numOfTheme) != null) return false;
				return true;
			}
		};

		tfNumberOfTheme.textProperty().addListener(cl2);
	}

	public void setController(FXMLCtrlNewTab fxmlCtrlParentTab) {
		this.fxmlCtrlParentTab = fxmlCtrlParentTab;
	}

	/**
	 * Передача семестра и номера модуля в случае добавления новой секции
	 * @param semester сам семестр
	 */
	public void setRoot(Semester semester, int numOfMod, int numOfSec) {
		this.semester = semester;
		this.module = semester.getModule(numOfMod);
		this.section = module.getSection(numOfSec);
		this.lNumberOfSemester.setText(String.valueOf(semester.getNUMBER_OF_SEMESTER()));
		this.lNumberOfModule.setText(String.valueOf(numOfMod));
		this.lNumberOfSection.setText(String.valueOf(numOfSec));
		bSave.setDisable(true);
	}

	/**
	 * Передача семестра, номера модуля, номера раздела в случае его изменения
	 * @param semeser сам семестр
	 * @param numOfMod номер модуля
	 * @param numOfSec номер раздела
	 */
	public void setRoot(Semester semester, int numOfMod, int numOfSec, int numOfTheme) {
		this.semester = semester;
		this.module = semester.getModule(numOfMod);
		this.section = module.getSection(numOfSec);
		this.theme = section.getTheme(numOfTheme);
		this.lNumberOfSemester.setText(String.valueOf(semester.getNUMBER_OF_SEMESTER()));
		this.lNumberOfModule.setText(String.valueOf(numOfMod));
		this.lNumberOfSection.setText(String.valueOf(numOfSec));
		this.tfNumberOfTheme.setText(String.valueOf(theme.getNumber()));
		this.tfTitleOfTheme.setText(theme.getTitle());
		this.taDescOfTheme.setText(theme.getDescription());
		
		this.taDescOfTheme.setText(theme.getDescription());
		bSave.setDisable(false);
	}
}
