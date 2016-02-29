package com.mmsp.logic;

import java.io.IOException;

import com.mmsp.model.WPDVersion;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FXMLCtrlVersionName extends VBox {

	private Stage stage;
	
	@FXML
    private TextField tfVersionName;

    public FXMLCtrlVersionName(Stage stage, WPDVersion wpdVers) throws IOException {

		this.stage = stage;

    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("VersionName.fxml"));

        loader.setController(this);

        loader.setRoot(this);

        loader.load();
        
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
            	if (tfVersionName.getText().equals("")) { // не дадим закрыть, если поле пусто
            		event.consume();
            	} else {
            		wpdVers.setName(tfVersionName.getText());
            		stage.close();
            	}
            }
        });
	}
}
