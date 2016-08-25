package com.mmsp.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import com.mmsp.model.Semester;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FXMLCtrlSettings extends VBox {

	private Stage stage;

	private Semester semester;
	
	private final ObservableList<String> olAviableSemester = FXCollections.observableArrayList(); // Список выбранных семестров

	private List<Semester> semesters;

	@FXML
	private ChoiceBox<String> cbNumberOfSemester;

	@FXML
	private TextField tfQuantityOfWeeks;

	@FXML
	private TextField tfQuantityOfSection;

	@FXML
	private TextField tfQuantityOfModule;

	@FXML
	private Button bSave;

	@FXML
	private Label lError;

	@FXML
	void clickBSave(ActionEvent event) {

		semester.setNUMBER_OF_SEMESTER(Integer.parseInt(cbNumberOfSemester.getSelectionModel().getSelectedItem()));
		semester.setQUANTITY_OF_MODULE(Integer.parseInt(tfQuantityOfModule.getText()));
		semester.setQUANTITY_OF_SECTION(Integer.parseInt(tfQuantityOfSection.getText()));
		semester.setQUANTITY_OF_WEEK(Integer.parseInt(tfQuantityOfWeeks.getText()));
		
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
			if (!cbNumberOfSemester.getSelectionModel().getSelectedItem().equals(""))
				properties.setProperty("numberOfSemester", cbNumberOfSemester.getSelectionModel().getSelectedItem()); // запись в конфигурационный файл
			if (!cbNumberOfSemester.getSelectionModel().getSelectedItem().equals(""))
				properties.setProperty("numberOfModule", tfQuantityOfModule.getText()); // запись в конфигурационный файл
			if (!cbNumberOfSemester.getSelectionModel().getSelectedItem().equals(""))
				properties.setProperty("numberOfSection", tfQuantityOfSection.getText()); // запись в конфигурационный файл
			if (!cbNumberOfSemester.getSelectionModel().getSelectedItem().equals(""))
				properties.setProperty("numberOfWeeks", tfQuantityOfWeeks.getText()); // запись в конфигурационный файл
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

	public void init(Stage external_stage, TreeSet<Integer> tsFNOS) {
		stage = external_stage;

		for (Integer integer : tsFNOS)
			olAviableSemester.add(integer.toString());
		if (olAviableSemester.size() > 0) {
			cbNumberOfSemester.setItems(olAviableSemester);
			cbNumberOfSemester.getSelectionModel().selectFirst();
		} else
			cbNumberOfSemester.setDisable(true);

		/* данный листенер следит за наличием текста в полях и делает доступной/недоступной кнопку сохранить */
		ChangeListener<String> cl2 = new ChangeListener<String>() { // http://stackoverflow.com/questions/12956061/javafx-oninputmethodtextchanged-not-called-after-focus-is-lost

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (allFieldNotNull() && allFieldIsInteger() && semesterIsSelected()) bSave.setDisable(false); else bSave.setDisable(true);
			}

			private boolean semesterIsSelected() {
				if (cbNumberOfSemester.getSelectionModel().getSelectedIndex() > -1) {
					lError.setText("");
					return true;
				} else {
					lError.setText("Необходимо выбрать семестр");
					return false;
				}
			}

			private boolean allFieldIsInteger() {
				if (isInteger(tfQuantityOfModule.getText()) && isInteger(tfQuantityOfSection.getText()) && isInteger(tfQuantityOfWeeks.getText())) {
					lError.setText("");
					return true;
				} else {
					lError.setText("Введите число");
					return false;
				}
			}

			private boolean allFieldNotNull() {
				boolean b = true;
				if (tfQuantityOfModule.getText().equals("0")) b = false;
				if (tfQuantityOfSection.getText().equals("0")) b = false;
				if (tfQuantityOfWeeks.getText().equals("0")) b = false;
				if (b) lError.setText(""); else lError.setText("Данные значения должны быть больше 0");
				return b;
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

		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				stage.close();
			}
		});

		tfQuantityOfModule.textProperty().addListener(cl2);
		tfQuantityOfSection.textProperty().addListener(cl2);
		tfQuantityOfWeeks.textProperty().addListener(cl2);
	}

	public void setSemesters(Semester s, List<Semester> semesters) {
		this.semester = s;
		this.semesters = semesters;

		if (this.semester != null) {
			if (olAviableSemester.indexOf(String.valueOf(semester.getNUMBER_OF_SEMESTER())) != -1)
				cbNumberOfSemester.getSelectionModel().select(olAviableSemester.indexOf(String.valueOf(semester.getNUMBER_OF_SEMESTER())));
			else cbNumberOfSemester.getSelectionModel().selectFirst();
			tfQuantityOfModule.setText(String.valueOf(this.semester.getQUANTITY_OF_MODULE()));
			tfQuantityOfSection.setText(String.valueOf(this.semester.getQUANTITY_OF_SECTION()));
			tfQuantityOfWeeks.setText(String.valueOf(this.semester.getQUANTITY_OF_WEEK()));
		}
	}
}
