package com.mmsp.logic;

import java.io.IOException;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * @author Alex
 *
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
    	stage.close();
    }
    
	public FXMLCtrlAuth(Stage stage) throws IOException {
		this.stage = stage;
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Auth.fxml")); // ��������� �����
        
        loader.setController(this); // ����� �����������, �.�. ���������� � �� ������ � Scene Builder'�
        
        loader.setRoot(this); // ��������� �����
        
        loader.load(); // ��������
	}

}