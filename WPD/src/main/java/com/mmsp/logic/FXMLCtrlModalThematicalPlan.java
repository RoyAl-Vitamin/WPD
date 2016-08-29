package com.mmsp.logic;

import java.util.Set;
import com.mmsp.model.Module;
import com.mmsp.model.MyTreeSet;
import com.mmsp.model.Section;
import com.mmsp.model.Semester;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Контроллер формы добавления модулей/раздело/тем
 * @author Alex
 */

public class FXMLCtrlModalThematicalPlan extends VBox {

	private Stage stage;

	private Set<Semester> root;

	private final ObservableList<String> olSelectElement = FXCollections.observableArrayList("Модуль", "Раздел", "Тему");

	private final ObservableList<String> olAviableSemester = FXCollections.observableArrayList(); // Список доступных семестров

	private final ObservableList<String> olAviableModule = FXCollections.observableArrayList(); // Список доступных модулей

	private FXMLCtrlNewTab fxmlCtrlParnetTab; // Контроллер этой вкладки

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

	//

	@FXML
	private ChoiceBox<String> cbSelectElement; // Выбор модуля/раздела/темы

	@FXML
	private ChoiceBox<String> cbSelectSemester; // выбор семестра

	// Для VBox для модуля

	@FXML
	private TextField tfNumberOfModule;

	@FXML
	private TextArea taDescOfModule;

	// Для VBox для раздела

	@FXML
	private ChoiceBox<String> cbSelectModuleAM; // выбор модуля в добавлении и изменении модуля

	@FXML
	private TextField tfNumberOfSection;
	
	@FXML
	private TextArea taDescOfSection;
	
	// Для VBox для темы

	@FXML
	private ChoiceBox<String> cbSelectModuleAT; // выбор модуля в добавлении и изменении темы

	//

	@FXML
	private Button bSave;

	private Module module;

	private Section section;

	private ThematicPlan theme;

	public void init(Stage stageAuth) {

		this.stage = stageAuth;

		vbForComponents.getChildren().clear();

		/* конфигурируем окно для выхода */

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
		
		cbSelectElement.getSelectionModel().selectedIndexProperty().addListener( // Выбор модуля темы раздела
			new ChangeListener<Number>() {
				public void changed (ObservableValue<? extends Number> ov, Number value, Number new_value) {
					key = (int) new_value;
					if (key < 0) {
						bSave.setDisable(true);
						cbSelectSemester.setDisable(true);
						return;
					}
					bSave.setDisable(false);
					cbSelectSemester.setDisable(false);
					/*switch (key) {
					case 0: // Select Module
						break;
					case 1: // Select Section
						break;
					case 2: // Select Theme
						break;
					default:
						bSave.setDisable(true); // Dead Code
					}*/
				}
			}
		);
		cbSelectElement.setItems(olSelectElement);

		cbSelectSemester.getSelectionModel().selectedIndexProperty().addListener( // Выбор семестра
				new ChangeListener<Number>() {
					public void changed (ObservableValue<? extends Number> ov, Number value, Number new_value) {
						int curr = (int) new_value; // индекс выбранного семестра
						if (curr < 0) {
							bSave.setDisable(true);
							cbSelectSemester.setDisable(true);
							return;
						}
						int numSem = Integer.parseInt(olAviableSemester.get(cbSelectSemester.getSelectionModel().getSelectedIndex()));
						//int numMod = Integer.parseInt(olAviableSemester.get(cbSelectModuleAM.getSelectionModel().getSelectedIndex()));
						for (Module mod : ((MyTreeSet) root).getSemester(numSem).getTreeModule())
							olAviableModule.add(String.valueOf(mod.getNumber()));
						cbSelectModuleAM.setItems(olAviableModule);
						open();
						bSave.setDisable(false);
						cbSelectSemester.setDisable(false);
					}
				}
			);
	}

	/**
	 * Делает доступным только тот VBox, который собрались редактировать
	 */
	private void open() {
		vbForComponents.getChildren().clear();
		stage.setWidth(400.0);
		//stage.setResizable(true);
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
			stage.setHeight(400.0);
			//stage.setResizable(false);
			break;
		}
	}

	/**
	 * инициализация Set
	 * @param treeRoot Set всех семестров
	 * @param mod 0 - добавление, 1 - изменение
	 */
	public void setRoot(Set<Semester> treeRoot, int mod) {
		this.root = treeRoot;
		this.mod = mod;

		for (Semester sem : treeRoot)
			olAviableSemester.add(String.valueOf(sem.getNUMBER_OF_SEMESTER()));
		cbSelectSemester.setItems(olAviableSemester);

		switch (this.mod) {
		case 0: // Добавление элемента
			cbSelectElement.setDisable(false);
			break;
		case 1: // Изменение элемента
			if (module != null) cbSelectElement.getSelectionModel().select(0);
			if (section != null) cbSelectElement.getSelectionModel().select(1);
			if (theme != null) cbSelectElement.getSelectionModel().select(2);
			cbSelectElement.setDisable(true);

			// TODO Подгрузить данные
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
		int num = cbSelectElement.getSelectionModel().getSelectedIndex();
		try {
			switch (mod) {
			case 0: // Добавление
				int numSem; // номер семестра в который добавляем/изменяем
				int numMod; // номер модуля в который добавляем/изменяем
				switch (num) {
				case 0: // сохраняем модуль
					numSem = Integer.parseInt(cbSelectSemester.getSelectionModel().getSelectedItem());
					module = new Module();
					module.setNumber(Integer.parseInt(tfNumberOfModule.getText()));
					module.setName(taDescOfModule.getText());
					((MyTreeSet) root).getSemester(numSem).getTreeModule().add(module);
					break;
				case 1: // сохраняем раздел
					numSem = Integer.parseInt(cbSelectSemester.getSelectionModel().getSelectedItem());
					numMod = Integer.parseInt(cbSelectModuleAM.getSelectionModel().getSelectedItem());
					Section section = new Section();
					section.setNumber(Integer.parseInt(tfNumberOfSection.getText()));
					section.setName(taDescOfSection.getText());
					((MyTreeSet) root).getSemester(numSem).getModule(numMod).getSetSection().add(section);
					break;
				case 2: // сохраняем тему
					break;
				}
				break;

			case 1: // Изменение
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.err.println(((MyTreeSet) root).toString());

		fxmlCtrlParnetTab.createTree();
		stage.close();
	}

	public void setController(FXMLCtrlNewTab fxmlCtrlCurrTab) {
		this.fxmlCtrlParnetTab = fxmlCtrlCurrTab;
	}

}
