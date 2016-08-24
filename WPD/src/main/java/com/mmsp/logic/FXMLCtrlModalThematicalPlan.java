package com.mmsp.logic;

import java.util.Set;

import com.mmsp.model.Module;
import com.mmsp.model.Section;
import com.mmsp.model.ThematicPlan;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Контроллер формы добавления модулей/раздело/тем
 * @author Alex
 */

public class FXMLCtrlModalThematicalPlan extends VBox {

	private Stage stage;

	private Set<Module> root;

	private final ObservableList<String> olSelectElement = FXCollections.observableArrayList("Модуль", "Раздел", "Тему");

	/**
	 * 0 - добаление/изменение модуля
	 * 1 - добаление/изменение раздела
	 * 2 - добаление/изменение темы
	 */
	private int key;

	/**
	 * 0 - добавление
	 * 1 - изменение
	 */
	private int mod;

	@FXML
	private VBox vbForComponents;

	@FXML
	private VBox vbForModule;

	@FXML
	private VBox vbForSection;

	@FXML
	private VBox vbForTheme;

	@FXML
	private ChoiceBox<String> cbSelectElement;

	@FXML
	private Button bSave;

	private Module module;

	private Section section;

	private ThematicPlan theme;

	public void init(Stage stageAuth) {

		this.stage = stageAuth;

		vbForComponents.getChildren().clear();

		/* конфигурируем окно для выхода */
		bSave.setOnAction(new EventHandler<ActionEvent>() { // на кнопку сохранения просто сохраняем и закрываем окно
			@Override
			public void handle(ActionEvent event) {
				// TODO manipulate whith root
				stage.close();
			}
		});

		/* данный листенер следит за наличием текста в полях и делает доступной/недоступной кнопку сохранить */
		/*ChangeListener<String> cl2 = new ChangeListener<String>() { // http://stackoverflow.com/questions/12956061/javafx-oninputmethodtextchanged-not-called-after-focus-is-lost
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!tFFirstName.getText().equals("") && !tFLastName.getText().equals("") && !tFMiddleName.getText().equals("")) bSave.setDisable(false); else bSave.setDisable(true);
			}
		};*/

		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
					stage.close();
			}
		});
		
		cbSelectElement.getSelectionModel().selectedIndexProperty().addListener(
			new ChangeListener<Number>() {
				public void changed (ObservableValue ov, Number value, Number new_value) {
					key = (int) new_value;
					open();
					bSave.setDisable(false);
					switch (key) {
					case 0: // Select Module
						break;
					case 1: // Select Section
						break;
					case 2: // Select Theme
						break;
					default:
						bSave.setDisable(true);
					}
				}
			}
		);
		cbSelectElement.setItems(olSelectElement);

	}

	/**
	 * Делает доступным только тот VBox, который собрались редактировать
	 */
	private void open() {
		vbForComponents.getChildren().clear();
		stage.setWidth(400.0);
		stage.setResizable(true);
		switch (key) {
		case 0:
			vbForComponents.getChildren().add(vbForModule);
			stage.setHeight(410.0);
			break;
		case 1:
			vbForComponents.getChildren().add(vbForSection);
			stage.setHeight(470.0);
			break;
		case 2:
			vbForComponents.getChildren().add(vbForTheme);
			stage.setHeight(230.0);
			stage.setResizable(false);
			break;
		}
	}

	/**
	 * инициализация Set
	 * @param root Set всех модулей
	 * @param mod 0 - добавление, 1 - изменение
	 */
	public void setRoot(Set<Module> root, int mod) {
		this.root = root;
		this.mod = mod;

		switch (this.mod) {
		case 0: // Добавление элемента
			cbSelectElement.setDisable(false);
			break;
		case 1: // Изменение элемента
			if (module != null) cbSelectElement.getSelectionModel().select(0);
			if (section != null) cbSelectElement.getSelectionModel().select(1);
			if (theme != null) cbSelectElement.getSelectionModel().select(2);
			cbSelectElement.setDisable(true);

			// TODO Подгрузить даные
			break;
		}
	}

	public void setElem(Module module) {
		this.module = module;
	}

	public void setElem(Section section) {
		this.section = section;
	}

	public void setElem(ThematicPlan theme) {
		this.theme = theme;
	}
	
	@FXML
	void clickBSave(ActionEvent event) {
		
	}

}
