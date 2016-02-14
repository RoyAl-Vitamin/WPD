package com.mmsp.logic;

import java.io.IOException;

import com.mmsp.wpd.WPD;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Контроллер авторизационной формы
 * @author Alex
 */

public class FXMLCtrlAuth extends VBox {

	private Stage stage;
	
    @FXML
    private Button bSave;

    @FXML
    private TextField tFMiddleName;

    @FXML
    private TextField tFFirstName;

    @FXML
    private TextField tFLastName;

    @FXML
    void clickBSave(ActionEvent event) {
    	WPD.data.setFirstName(tFFirstName.getText());
    	WPD.data.setLastName(tFLastName.getText());
    	WPD.data.setMiddleName(tFMiddleName.getText());
    	this.stage.close();
    }
    
	public FXMLCtrlAuth(Stage stage) throws IOException {
		this.stage = stage;
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Auth.fxml"));
        
        loader.setController(this);
        
        loader.setRoot(this);
        
        loader.load();
        
        tFFirstName.setText(WPD.data.getFirstName());
        tFLastName.setText(WPD.data.getLastName());
        tFMiddleName.setText(WPD.data.getMiddleName());
	}

}