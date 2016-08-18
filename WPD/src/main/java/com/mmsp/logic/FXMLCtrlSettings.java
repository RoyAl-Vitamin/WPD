package com.mmsp.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXMLCtrlSettings extends VBox {

	private Stage stage;

	@FXML
	private TextField tfNumberOfWeeks;

	@FXML
	private Button bSave;

	@FXML
	private TextField tfNumberOfSection;

	@FXML
	private TextField tfNumberOfModule;

	@FXML
	private TextField tfNumberOfSemester;

	@FXML
	private Button bCancel;

	@FXML
	void clickBSave(ActionEvent event) {
		File propFile = new File("config.properties");
		if (!propFile.exists())
			try {
				propFile.createNewFile();
			} catch (IOException e) {
				System.err.println("Не удалось создать файл при сохранении данных об учебном плане");
				e.printStackTrace();
			}
		Properties properties = new Properties();
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream("config.properties");
			properties.load(fis);
			if (!tfNumberOfSemester.getText().equals(""))
				properties.setProperty("numberOfSemester", tfNumberOfSemester.getText()); // запись в конфигурационный файл
			if (!tfNumberOfSemester.getText().equals(""))
				properties.setProperty("numberOfModule", tfNumberOfModule.getText()); // запись в конфигурационный файл
			if (!tfNumberOfSemester.getText().equals(""))
				properties.setProperty("numberOfSection", tfNumberOfSection.getText()); // запись в конфигурационный файл
			if (!tfNumberOfSemester.getText().equals(""))
				properties.setProperty("numberOfWeeks", tfNumberOfWeeks.getText()); // запись в конфигурационный файл
			fos = new FileOutputStream("config.properties");
			properties.store(fos, "Study load");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				System.err.println("Не удалось закрыть потоки ввода/вывода при сохранении данных об учебном плане");
				e.printStackTrace();
			}
		}

		stage.close();
	}

	@FXML
	void clickBCancel(ActionEvent event) {
		stage.close();
	}

	public void init(Stage external_stage) {
		stage = external_stage;

		/* данный листенер следит за наличием текста в полях и делает доступной/недоступной кнопку сохранить */
		ChangeListener<String> cl2 = new ChangeListener<String>() { // http://stackoverflow.com/questions/12956061/javafx-oninputmethodtextchanged-not-called-after-focus-is-lost
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (isInteger(tfNumberOfModule.getText()) && isInteger(tfNumberOfSection.getText()) && isInteger(tfNumberOfSemester.getText()) && isInteger(tfNumberOfWeeks.getText())) bSave.setDisable(false); else bSave.setDisable(true);
			}

			private boolean isInteger(String sValue) { // проверка на ввод и что б в Integer помещалось
				try {
					Integer.parseInt(sValue);
					return true;
				} catch (NumberFormatException ex) {
					return false;
				}
			}
		};

		tfNumberOfModule.textProperty().addListener(cl2);
		tfNumberOfSection.textProperty().addListener(cl2);
		tfNumberOfSemester.textProperty().addListener(cl2);
		tfNumberOfWeeks.textProperty().addListener(cl2);

		Properties prop = new Properties();
		InputStream input = null;
		try {
			File propFile = new File("config.properties");
			if (!propFile.exists()) {// Если этого файла нет, то создаём его
				propFile.createNewFile();
			}

			input = new FileInputStream(propFile);

			// load a properties file
			prop.load(input);

			// set the property value and print it out
			String sValue = prop.getProperty("numberOfSemester");
			if (sValue != null)	tfNumberOfSemester.setText(sValue); // достанем и внесём в тексфилд, если оно существует

			sValue = prop.getProperty("numberOfModule");
			if (sValue != null)	tfNumberOfModule.setText(sValue);

			sValue = prop.getProperty("numberOfSection");
			if (sValue != null)	tfNumberOfSection.setText(sValue);

			sValue = prop.getProperty("numberOfWeeks");
			if (sValue != null)	tfNumberOfWeeks.setText(sValue);

		} catch (IOException ex) {
			ex.printStackTrace();
			// Стандартные значения в случае ошибки?
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
