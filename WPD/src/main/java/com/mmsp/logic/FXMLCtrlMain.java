package com.mmsp.logic;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author Алексей
 * FXMLCtrlMain - класс контроллера и наследник VBox'а
 */

public class FXMLCtrlMain extends VBox {

	private Stage stage; // для FileChooser'а
	
	@FXML
    private Button bCallFileChooser;

    @FXML
    private Button bSave;

    @FXML
    private Button bGenerate;

    @FXML
    private Button bDelete;

    @FXML
    private TextField tFPath;

    @FXML
    private MenuItem mbClose;

    @FXML
    private DatePicker dPDateOfCreate;

    @FXML
    private Label lStatus;
    
    @FXML
    private TextField tfNumberOfSemesters;

    @FXML
    private ListView<?> lvTypeOfControlMeasures;

    @FXML
    void clickBClose(ActionEvent event) {
    	stage.close();
    }

    @FXML
    void clickBFileChooser(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open template");
    	File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            tFPath.setText(file.getPath());
        }
    }

    @FXML
    void clickBSave(ActionEvent event) {

    }

    @FXML
    void clickBGenerate(ActionEvent event) {

    }

    @FXML
    void clickBDelete(ActionEvent event) {

    }

    public FXMLCtrlMain(Stage stage) throws IOException {
    	this.stage = stage;
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Main.fxml")); // подгрузка формы
        
        loader.setController(this); // связь контроллера, т.к. контроллер я не указал в Scene Builder'е
        
        loader.setRoot(this); // установка корня
        
        loader.load(); // загрузка
        
        dPDateOfCreate.setValue(LocalDate.now());
        
        lvTypeOfControlMeasures.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}