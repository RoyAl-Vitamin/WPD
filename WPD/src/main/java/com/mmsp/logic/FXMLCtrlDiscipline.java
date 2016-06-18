package com.mmsp.logic;

import java.io.IOException;

import com.mmsp.dao.impl.DAO_HandBookDiscipline;
import com.mmsp.model.HandbookDiscipline;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    
    /*public FXMLCtrlDiscipline(Stage stage) throws IOException {

		this.stage = stage;

    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Discipline.fxml"));

        loader.setController(this);

        loader.setRoot(this);

        loader.load();
		
		tfDisciplineName.setText(com.mmsp.logic.FXMLCtrlMain.hbD.getValue());
		tfCode.setText(com.mmsp.logic.FXMLCtrlMain.hbD.getCode().toString());

		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
            	if (isInteger(tfCode.getText()) && isSatisfies(tfDisciplineName.getText())) {
	            	com.mmsp.logic.FXMLCtrlMain.hbD.setValue(tfDisciplineName.getText());
	            	com.mmsp.logic.FXMLCtrlMain.hbD.setCode(Integer.valueOf(tfCode.getText()));
	            	
	            	// FIXME Убедится, что нет дисциплины с такими же кодом и именем
	            	
	            	stage.close();
            	} else { // не дадим закрыть, если не Integer
            		event.consume();
            	}
            }

			private boolean isInteger(String sValue) { // проверка на ввод и что б в Integer помещалось
				try {
					Integer.parseInt(sValue);
			        return true;
				} catch (NumberFormatException ex) {
					return false;
				}
			}
			
			private boolean isSatisfies(String sValue) {
            	if (sValue != null) {
            		if (!sValue.equals("")) {
            			//System.err.println("String != \"\"");
            			return true;
            		} else {
            			//System.err.println("String == \"\"");
            			return false;
            		}
            	} else {
            		//System.err.println("String == NULL");
            		return false;
            	}
            }
        });
	}*/

	public void init(Stage external_stage, Long id_HBD) {
		stage = external_stage;
		DAO_HandBookDiscipline dao_HBD = new DAO_HandBookDiscipline();
		hbD = dao_HBD.getById(HandbookDiscipline.class, id_HBD);

		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				stage.close();
			}
		});

		/* данный листенер следит за наличием текста в полях и делает доступной/недоступной кнопку сохранить */
		ChangeListener<String> cl2 = new ChangeListener<String>() { // http://stackoverflow.com/questions/12956061/javafx-oninputmethodtextchanged-not-called-after-focus-is-lost

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (isInteger(tfCode.getText()) && isSatisfies(tfDisciplineName.getText())) bSave.setDisable(false); else bSave.setDisable(true);
			}

			private boolean isInteger(String sValue) { // проверка на ввод и что б в Integer помещалось
				try {
					Integer.parseInt(sValue);
			        return true;
				} catch (NumberFormatException ex) {
					return false;
				}
			}
			
			private boolean isSatisfies(String sValue) {
            	if (sValue != null) {
            		if (!sValue.equals("")) {
            			return true;
            		} else {
            			return false;
            		}
            	} else {
            		return false;
            	}
            }
		};

		tfDisciplineName.textProperty().addListener(cl2);
		tfCode.textProperty().addListener(cl2);
		if (hbD.getCode() != null)
			tfCode.setText(String.valueOf(hbD.getCode()));
		if (hbD.getValue() != null)
			tfDisciplineName.setText(String.valueOf(hbD.getValue()));
	}

	@FXML
    void clickBSave(ActionEvent event) {
		hbD.setValue(tfDisciplineName.getText());
		hbD.setCode(Integer.valueOf(tfCode.getText()));

		DAO_HandBookDiscipline dao_HBD = new DAO_HandBookDiscipline(); // Просто обновим запись
		dao_HBD.update(hbD);
		System.err.println(hbD.getValue() + " : " + hbD.getCode());
		stage.close();
    }
}
