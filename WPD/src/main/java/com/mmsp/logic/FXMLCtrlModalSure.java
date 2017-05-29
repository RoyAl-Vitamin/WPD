package com.mmsp.logic;

import com.mmsp.logic.newtab.FXMLCtrlNewTab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXMLCtrlModalSure extends VBox {

	private FXMLCtrlNewTab parentCtrl; // Parent controller

	private Stage stage; // current Stage

	@FXML
	private Button bOk;

	@FXML
	private Button bCancel;

	@FXML
	void clickBOk(ActionEvent event) {
		parentCtrl.delete();
		this.stage.close();
	}

	@FXML
	void clickBCancel(ActionEvent event) {
		this.stage.close();
	}

	public void setController(FXMLCtrlNewTab fxmlCtrlParentTab) {
		this.parentCtrl = fxmlCtrlParentTab;
	}

	public void init(Stage stageModalSure) {
		this.stage = stageModalSure;
	}
}
