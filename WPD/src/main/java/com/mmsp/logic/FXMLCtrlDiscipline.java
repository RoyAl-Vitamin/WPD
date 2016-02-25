package com.mmsp.logic;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FXMLCtrlDiscipline extends VBox {

	private Stage stage;
	
	@FXML
    private TextField tfDisciplineName;

    @FXML
    private TextField tfCode;
    
    public FXMLCtrlDiscipline(Stage stage) throws IOException {

		this.stage = stage;

    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Discipline.fxml"));

        loader.setController(this);

        loader.setRoot(this);

        loader.load();
		
		tfDisciplineName.setText(com.mmsp.logic.FXMLCtrlMain.hbD.getValue());
		tfCode.setText(com.mmsp.logic.FXMLCtrlMain.hbD.getCode().toString()); // FIXME проверка на ввод и что б в Integer помещалось

		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent t) {
            	com.mmsp.logic.FXMLCtrlMain.hbD.setValue(tfDisciplineName.getText());
            	com.mmsp.logic.FXMLCtrlMain.hbD.setCode(Integer.valueOf(tfCode.getText()));
            	stage.close();
            }
        });
	}
}
