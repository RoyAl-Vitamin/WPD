package com.mmsp.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.table.TableCellEditor;

import org.apache.http.MethodNotSupportedException;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
//import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
//import org.controlsfx.control.spreadsheet.SpreadsheetCellType.StringType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.mmsp.dao.impl.DAO_HandBookDiscipline;
import com.mmsp.dao.impl.DAO_PoCM;
import com.mmsp.dao.impl.DAO_ThematicPlan;
import com.mmsp.dao.impl.DAO_WPDVersion;
import com.mmsp.model.HandbookDiscipline;
import com.mmsp.model.PoCM;
import com.mmsp.model.Record;
import com.mmsp.model.Section;
import com.mmsp.model.Semester;
import com.mmsp.model.ThematicPlan;
import com.mmsp.model.Module;
import com.mmsp.model.MyTreeSet;
import com.mmsp.model.WPDVersion;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
//import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class FXMLCtrlNewTab extends VBox {

	public static class RowSL { // класс строки // RowStudyLoad
		private final SimpleStringProperty viewOfStudyLoad; // Вид учебной нагрузки
		private final SimpleStringProperty numberOfHours; // Количество часов
		private final SimpleStringProperty ladderpointsUnit; // ЗЕ

		private RowSL(String fName, String lName, String email) {
			this.viewOfStudyLoad = new SimpleStringProperty(fName);
			this.numberOfHours = new SimpleStringProperty(lName);
			this.ladderpointsUnit = new SimpleStringProperty(email);
		}

		public String getViewOfStudyLoad() {
			return viewOfStudyLoad.get();
		}
		public void setViewOfStudyLoad(String fName) {
			viewOfStudyLoad.set(fName);
		}

		public String getNumberOfHours() {
			return numberOfHours.get();
		}
		public void setNumberOfHours(String fName) {
			numberOfHours.set(fName);
		}

		public String getLadderpointsUnit() {
			return ladderpointsUnit.get();
		}
		public void setLadderpointsUnit(String fName) {
			ladderpointsUnit.set(fName);
		}

		@Override
		public String toString() {
			return "view Of Study Load " + viewOfStudyLoad.toString() + ", number Of Hours " + numberOfHours;
		}
	}

	public class RowPoCM { // Класс строки компонента tableView вкладки "ПКМ"

		private final SimpleStringProperty sspCtrlMes; // Контрольное мероприятие
		private final SimpleStringProperty sspNuberOfSemester; // Номер семестра
		private final SimpleStringProperty sspNumberOfWeek; // Номер недели
		
		private RowPoCM(String sCtrlMes, String sNumberOfSemester, String sNumberOfWeek) {
			this.sspCtrlMes = new SimpleStringProperty(sCtrlMes);
			this.sspNuberOfSemester = new SimpleStringProperty(sNumberOfSemester);
			this.sspNumberOfWeek = new SimpleStringProperty(sNumberOfWeek);
		}

		public String getSspCtrlMes() {
			return sspCtrlMes.get();
		}

		public String getSspNuberOfSemester() {
			return sspNuberOfSemester.get();
		}

		public String getSspNumberOfWeek() {
			return sspNumberOfWeek.get();
		}
		
		public void setSspCtrlMes(String sValue) {
			sspCtrlMes.set(sValue);
		}

		public void setSspNuberOfSemester(String sValue) {
			sspNuberOfSemester.set(sValue);
		}

		public void setSspNumberOfWeek(String sValue) {
			sspNumberOfWeek.set(sValue);
		}
	}

	class EditingCell extends TableCell<RowSL, String> { // Для UX, что б не надо было после редактирования жать Enter
		 
		private TextField textField;

		public EditingCell() {
		}

		@Override
		public void startEdit() {
			if (!isEmpty()) {
				super.startEdit();
				createTextField();
				setText(null);
				setGraphic(textField);
				textField.selectAll();
			}
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();

			setText((String) getItem());
			setGraphic(null);
		}

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(null);
				}
			}
		}

		private void createTextField() {
			textField = new TextField(getString());
			textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
			textField.focusedProperty().addListener(new ChangeListener<Boolean>(){
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, 
						Boolean arg1, Boolean arg2) {
					if (!arg2) {
						commitEdit(textField.getText());
					}
				}
			});
		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
	}

	private final ObservableList<RowSL> olDataOfStudyLoad = FXCollections.observableArrayList();

	private final ObservableList<RowPoCM> olDataOfPoCM = FXCollections.observableArrayList();

	private Stage stage;

	private FXMLCtrlNewTab fxmlCtrlCurrTab; // Контроллер этой вкладки

	private FXMLCtrlMain parentCtrl;
	
	private String tabName; // здесь полное название вкладки, возможно стоило хранить только название версии, т.к. название дисциплины пока не менятся

	private int NUMBER_OF_SEMESTER; // количество семестров

	private int NUMBER_OF_MODULE; // количество модулей

	private int NUMBER_OF_SECTION; // количество разделов

	private int NUMBER_OF_WEEK; // количество недель в семестре

	private WPDVersion currWPDVersion;
	private PoCM currPoCM;
	private Set<ThematicPlan> setCurrThematicPlan;

	// шапка текущей вкладки

	@FXML
	private TextField tfVersion;

	@FXML
	private ListView<String> lvTypeOfControlMeasures;

	@FXML
	private DatePicker dpDateOfCreate;

	@FXML
	private TextField tfPath; // Путь до шаблона

	@FXML
	private Button bCallFileChooser;

	@FXML
	private Button bSemester;

	@FXML
	private Button bSave;

	@FXML
	private Button bGenerate;

	@FXML
	private Button bDelete;

	// Вкладка "Общие"

	@FXML
	private Button bAddRowStudyLoad;

	@FXML
	private Button bDeleteRowStudyLoad;

	@FXML
	private TableColumn<RowSL, String> colTVViewOfStudyLoad; // Вид учебной нагрузки

	@FXML
	private TableColumn<RowSL, String> colTVNumberOfHours; // Часов

	@FXML
	private TableColumn<RowSL, String> colTVLadderpointsUnit; // ЗЕ

	@FXML
	private TableView<RowSL> tvStudyLoad;

	// Переменные вкладки "Тематический план"

	@FXML
	private VBox vbThematicalPlan;

	@FXML
	private Button bAddRowTP;

	@FXML
	private Button bDelRowTP;
	
	@FXML
	private HBox hbReplacementThematicalPlan;

	@FXML
	private TreeView<String> tvRoot;

	final TreeItem<String> rootElement = new TreeItem<String>("Создать модуль");

	private SpreadsheetView ssvTableTP; // Таблица тематического плана

	private EventHandler<GridChange> ehTP; // OnGridChange for TableTP
	
	@FXML
	private Button bAddElement;

	@FXML
	private Button bSetElement;

	@FXML
	private Button bDelElement;

	// Переменные вкладки "ПКМ"

	@FXML
	private TableColumn<RowPoCM, String> tcCM;

	@FXML
	private TableColumn<RowPoCM, String> tcNoS;

	@FXML
	private TableColumn<RowPoCM, String> tcNoW;

	@FXML
	private TableView<RowPoCM> tvPoCM;

	@FXML
	private TextField tfCM;

	@FXML
	private TextField tfNoS;

	@FXML
	private TextField tfNoW;

	@FXML
	private Button bSaveRowPoCM; // Сохранение строки

	@FXML
	private Button bAddRowPoCM; // Добавление строки

	@FXML
	private Button bDelRowPoCM; // Удаление строки

	// Переменные вкладки "Таблица 7.1"

	@FXML
	private VBox vbT71;

	@FXML
	private Button bAddRowT71; // кнопка добавления строки в текущий семестр

	@FXML
	private Button bDelRowT71; // кнопка удаления текущей строки из текущего семестра

	private MyTreeSet treeRoot = new MyTreeSet(); // как за*бало всё переписывать

	private final ObservableList<String> olSemesters = FXCollections.observableArrayList(); // for cbSemester // список # семестров

	@FXML
	private ChoiceBox<String> cbSemesters;

	private Semester currSemester; // текущий семестр

	// https://bitbucket.org/panemu/tiwulfx
	// vs.
	// https://bitbucket.org/controlsfx/controlsfx
	private SpreadsheetView ssvTable71; // Замена TableView<?> tvTable71;

	private EventHandler<GridChange> ehT71; // OnGridChange for TableTP

	//*************************************************************************************************************************
	//*************************************************************************************************************************
	//**
	//** Контроллеры кнопок на верхней панельке
	//**
	//*************************************************************************************************************************
	//*************************************************************************************************************************

	/**
	 * Создаёт окно со списком ( № семестров ; количество недель )
	 * @param event
	 */
	@FXML
	// TODO move to Pop-over on ControlsFX
	// FIXME Написать проверку на ввод
	// FIXME Проследить уникальность номеров семестров
	void clickBSemester(ActionEvent event) {
		VBox vbForSemester = new VBox(5);
		vbForSemester.setAlignment(Pos.CENTER);
		for (Semester sem : treeRoot) {
			HBox hbForSem = new HBox(5);
			hbForSem.setAlignment(Pos.CENTER);

			hbForSem.setId(String.valueOf(sem.getNUMBER_OF_SEMESTER()));

			TextField tfForNumber = new TextField(String.valueOf(sem.getNUMBER_OF_SEMESTER()));
			tfForNumber.setId("numOfSem " + sem.getNUMBER_OF_SEMESTER());
			TextField tfForQuantity = new TextField(String.valueOf(sem.getQUANTITY_OF_WEEK()));
			tfForQuantity.setId("quaOfWeek " + sem.getNUMBER_OF_SEMESTER());
			Button bRemoveSem = new Button("-");

			bRemoveSem.setId(String.valueOf(sem.getNUMBER_OF_SEMESTER()));
			bRemoveSem.setOnAction(e -> clickRemoveSemester(e));

			hbForSem.getChildren().addAll(tfForNumber, tfForQuantity, bRemoveSem);
			hbForSem.setId(String.valueOf(sem.getNUMBER_OF_SEMESTER()));
			VBox.setMargin(hbForSem, new Insets(0, 15, 0, 15));
			vbForSemester.getChildren().add(hbForSem);
		}

		Button bAddSemester = new Button("Добавить семестр");
		bAddSemester.setOnAction(e -> {
			HBox hbForSem = new HBox(5);
			hbForSem.setAlignment(Pos.CENTER);

			TextField tfForNumber = new TextField();
			tfForNumber.setId("numOfSem");
			TextField tfForQuantity = new TextField();
			tfForQuantity.setId("quaOfWeek");
			Button bRemoveSem = new Button("-");
			bRemoveSem.setOnAction(ev -> clickRemoveSemester(ev));

			hbForSem.getChildren().addAll(tfForNumber, tfForQuantity, bRemoveSem);
			hbForSem.setId("");
			VBox.setMargin(hbForSem, new Insets(0, 15, 0, 15));
			vbForSemester.getChildren().add(vbForSemester.getChildren().size() - 2, hbForSem);
		});
		vbForSemester.getChildren().add(bAddSemester);

		Button bSaveSemester = new Button("Сохранить");
		bSaveSemester.setOnAction(e -> clickBSaveSemester(e));
		vbForSemester.getChildren().add(bSaveSemester);
		Scene scene = new Scene(vbForSemester, 300, 350);
		Stage stageForSemester = new Stage();
		stageForSemester.setScene(scene);
		stageForSemester.initModality(Modality.APPLICATION_MODAL);
		stageForSemester.setOnCloseRequest(e -> {
			if (treeRoot.size() != 0) bSemester.setText(toString(treeRoot));
			else bSemester.setText("Добавить");
			stageForSemester.close();
		});
		stageForSemester.setTitle("Изменить семестры");
		stageForSemester.getIcons().add(new Image("Logo.png"));
		stageForSemester.showAndWait();
	}

	/**
	 * Сохраняет текущее содержание семестров и удаляет из treeRoot удалённые из этого окна 
	 * @param e
	 */
	private void clickBSaveSemester(ActionEvent e) {
		VBox vbForSemester = (VBox) ((Button) e.getSource()).getParent();
		List<Semester> liSemForDelete = new ArrayList<>(); // список на  удаление
		for (Semester semesterTemp : treeRoot) {
			boolean delete = true;
			for (Node nodeTemp : vbForSemester.getChildren()) {
				if (nodeTemp instanceof HBox) {
					if (nodeTemp.getId().equals(String.valueOf(semesterTemp.getNUMBER_OF_SEMESTER()))) { // Изменить существующий
						semesterTemp.setNUMBER_OF_SEMESTER(getNumberOfSemester((HBox) nodeTemp));
						semesterTemp.setQUANTITY_OF_WEEK(getQuantityOfWeek((HBox) nodeTemp));
						delete = false;
					}
				}
			}
			if (delete) liSemForDelete.add(semesterTemp);
		}

		treeRoot.removeAll(liSemForDelete);

		for (Node nodeTemp : vbForSemester.getChildren()) { // Добаение новых узлов
			if (nodeTemp instanceof HBox) {
				if ("".equals(nodeTemp.getId())) {// Добавить семестр
					Semester newSem = new Semester();
					newSem.setNUMBER_OF_SEMESTER(getNumberOfSemester((HBox) nodeTemp));
					newSem.setQUANTITY_OF_WEEK(getQuantityOfWeek((HBox) nodeTemp));
					treeRoot.add(newSem);
					for (Node node : ((HBox) nodeTemp).getChildren()) { // раздадим id новым полям нового семестра
						if (node instanceof TextField) {
							if ("numOfSem".equals(node.getId()))
								node.setId("numOfSem " + newSem.getNUMBER_OF_SEMESTER());
							if ("quaOfWeek".equals(node.getId()))
								node.setId("quaOfWeek " + newSem.getNUMBER_OF_SEMESTER());
						}
					}
					nodeTemp.setId(String.valueOf(newSem.getNUMBER_OF_SEMESTER()));
				}
			}
		}

		olSemesters.clear();
		for (Semester sem : treeRoot) {
			olSemesters.add(String.valueOf(sem.getNUMBER_OF_SEMESTER()));
		}
		cbSemesters.getSelectionModel().selectFirst();
		loadTvT71();
	}

	/**
	 * Достаёт из нужного TextField количество недель
	 * @param nodeTemp
	 * @return
	 */
	private int getQuantityOfWeek(HBox hboxTemp) {
		for (Node node : hboxTemp.getChildren()) {
			if (node instanceof TextField && node.getId().contains("quaOfWeek")) {
				return Integer.parseInt(((TextField) node).getText());
			}
		}
		return 0;
	}

	/**
	 * Достаёт из нужного TextField номер семестра
	 * @param hboxTemp
	 * @return
	 */
	private int getNumberOfSemester(HBox hboxTemp) {
		for (Node node : hboxTemp.getChildren()) {
			if (node instanceof TextField && node.getId().contains("numOfSem")) {
				return Integer.parseInt(((TextField) node).getText());
			}
		}
		return 0;
	}

	/**
	 * Удаляет HBox на кнопку рядом с ним
	 * @param e
	 */
	private void clickRemoveSemester(ActionEvent e) {
		// Приводим к VBox                            // к hbForSem // к vbForSemester
		VBox vbForSemester = (VBox) ((Button) e.getSource()).getParent().getParent();
		for (Node nodeTemp : vbForSemester.getChildren()) {
			if (nodeTemp instanceof HBox && ((Button) e.getSource()).getId().equals(nodeTemp.getId())) {
				vbForSemester.getChildren().remove(nodeTemp);
				break;
			}
		}
	}

	@FXML
	void clickBFileChooser(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Открыть шаблон");
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			tfPath.setText(file.getPath());
		}
	}

	@FXML
	void clickBSave(ActionEvent event) {

		// TODO first достать данные из полей и вставить их в объекты PoCM and ThematicPlan
		currWPDVersion.setName(tfVersion.getText()); // Запоминаем название версии
		currWPDVersion.setTemplateName(tfPath.getText()); // Занесём путь шаблона

		/* http://stackoverflow.com/questions/20446026/get-value-from-date-picker */
		LocalDate localDate = dpDateOfCreate.getValue();
		Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
		currWPDVersion.setDate(Date.from(instant)); // Попробуем занести дату создания

		DAO_PoCM dao_pocm = new DAO_PoCM();
		dao_pocm.update(currPoCM);

		// ОБНОВЛЕНИЕ ТЕМАТИЧЕСКОГО ПЛАНА
		// Удалить все темы из тематического плана, которые принадлежат этой версии
		// Занести из таблицы Тематического плана во множество setCurrThematicPlan
		// Сохранить их всех в БД

		DAO_ThematicPlan dao_thematicPlan = new DAO_ThematicPlan();
		if (setCurrThematicPlan == null) {
			setCurrThematicPlan = new HashSet<>();
		}
		for (ThematicPlan tp : setCurrThematicPlan)
			dao_thematicPlan.remove(tp);

		for (int i = 1; i < ssvTableTP.getGrid().getRowCount(); i++) {
			ThematicPlan tp = new ThematicPlan();
			tp.setWPDVerion(currWPDVersion);
			tp.setTitle(ssvTableTP.getGrid().getRows().get(i).get(0).getText());
			tp.setDescription(ssvTableTP.getGrid().getRows().get(i).get(1).getText());
			tp.setBelongingToTheSemester(Integer.valueOf(1)); // FIXME
			tp.setBelongingToTheModule(Integer.valueOf(ssvTableTP.getGrid().getRows().get(i).get(2).getText()));
			tp.setBelongingToTheSection(Integer.valueOf(ssvTableTP.getGrid().getRows().get(i).get(3).getText()));
			try {
				tp.setL(Integer.valueOf(ssvTableTP.getGrid().getRows().get(i).get(4).getText()));
				tp.setPZ(Integer.valueOf(ssvTableTP.getGrid().getRows().get(i).get(5).getText()));
				tp.setLR(Integer.valueOf(ssvTableTP.getGrid().getRows().get(i).get(6).getText()));
				tp.setKSR(Integer.valueOf(ssvTableTP.getGrid().getRows().get(i).get(7).getText()));
				tp.setSRS(Integer.valueOf(ssvTableTP.getGrid().getRows().get(i).get(8).getText()));
			} catch (NumberFormatException ex) {
				System.err.println("При выделении числа из TableTP возникла ошибка");
				ex.printStackTrace();
			}

			setCurrThematicPlan.add(tp);

			tp.setId(dao_thematicPlan.add(tp));
		}


		DAO_WPDVersion dao_wpdVersion = new DAO_WPDVersion();
		dao_wpdVersion.update(currWPDVersion);

		// Блок обновления названия вкладки и списка Версий в cbVersion 
		if (!tabName.split(":")[1].equals(currWPDVersion.getName()))
		// Если сменилось название версии, то подгрузим контроллер
		// и изменим из него значение названия вкладки и обновим список названий версий
		{
			parentCtrl.updateOlVersion(currWPDVersion.getHbD().getId()); // Обновляет список, содержащийся в cbVersion
			if (!parentCtrl.updateTabName(tabName, currWPDVersion.getName())) { // обновляет название вкладки
				System.err.println("Возникла ошибка при обновлении названия вкадки");
			}
		}
	}

	/**
	 * Генерация по шаблону
	 * @param event
	 */
	@FXML
	void clickBGenerate(ActionEvent event) {
		String pathToTemplateFile = tfPath.getText(); // путь до шаблона
		File fInput = new File(pathToTemplateFile);
		WordprocessingMLPackage wordMLPackage = null;
		try {
			wordMLPackage = WordprocessingMLPackage.load(fInput);
		} catch (Docx4JException e) {
			System.err.println("Не удалось найти шаблон");
			e.printStackTrace();
		}

		// TODO Запилить хотя бы простую генерацию
		String pathToGenFile = pathToTemplateFile.substring(0, pathToTemplateFile.lastIndexOf(".")) + "_gen" + pathToTemplateFile.substring(pathToTemplateFile.lastIndexOf(".")); // путь до сгенерированного файла
		File fOutput = new File(pathToGenFile);
		try {
			wordMLPackage.save(fOutput);
		} catch (Docx4JException e) {
			System.err.println("Не удалось сохранить сгенирированый файл");
			e.printStackTrace();
		}
	}

	/**
	 * Удаление данной версии
	 * @param event
	 */
	@FXML
	void clickBDelete(ActionEvent event) {

		Long id = currWPDVersion.getId();

		// Закрываем вкладку
		parentCtrl.closeTab(id);

		DAO_HandBookDiscipline dao_hbd = new DAO_HandBookDiscipline();
		HandbookDiscipline hbd = dao_hbd.getById(HandbookDiscipline.class, currWPDVersion.getHbD().getId());
		if (!hbd.getVersions().removeIf(p -> p.getId() == id))
			System.err.println("Не смог удалить версии в hbd");
		else
			dao_hbd.update(hbd);

		System.err.println(hbd.toString()); // посмотрим, что там
		
		DAO_WPDVersion dao_vers = new DAO_WPDVersion();
		dao_vers.remove(currWPDVersion);

		// Вывод всех версий, которые ссылаются на данную дисциплину
		/*List<WPDVersion> listOfVersion = dao_vers.getAllByNumber(id);
		for (int i = 0; i < listOfVersion.size(); i++) {
			System.err.println("Name = " + listOfVersion.get(i).getName() + "\nID = " + listOfVersion.get(i).getId().toString());
			if (listOfVersion.get(i).getPoCM() != null) {
				System.err.println("PoCM == " + listOfVersion.get(i).getPoCM().toString());
			} else
				System.err.println("PoCM == null");
			if (listOfVersion.get(i).getThematicPlans() != null) {
				if (listOfVersion.get(i).getThematicPlans().size() != 0)
					for (ThematicPlan tp : listOfVersion.get(i).getThematicPlans())
						System.err.println("ThematicPlan == " + tp.toString());
			} else
				System.err.println("ThematicPlan == null");
		}*/
	}

	//*************************************************************************************************************************
	//*************************************************************************************************************************
	//**
	//** Вкладка "Общие"
	//**
	//*************************************************************************************************************************
	//*************************************************************************************************************************

	@FXML
	void clickBAddRowStudyLoad(ActionEvent event) {
		olDataOfStudyLoad.add(new RowSL("", "", ""));
	}

	@FXML
	void clickBDeleteRowStudyLoad(ActionEvent event) {
		int selectedIndex = tvStudyLoad.getSelectionModel().getSelectedIndex();
		//System.out.println("Del: " + selectedIndex);
		/*
		 * int row = myTableView.getSelectionRow();
		 * int col = myTableView.getSelectionCol();
		 * myTableView.getModel().getSelectedValue(row,col);
		 */
		if (selectedIndex >= 0) tvStudyLoad.getItems().remove(selectedIndex);
		// for (int i = 0; i < dataOfStudyLoad.size(); i++) System.out.println(dataOfStudyLoad.get(i).toString());
	}

	/*
	 * Создаёт строку типа "Виды работ" + часы на каждую неделю для указанной позиции
	 * @param posRow
	 * @return
	 */
	/*private ObservableList<SpreadsheetCell> createSimpleRow(int posRow) {
		ObservableList<SpreadsheetCell> olRow = FXCollections.observableArrayList();
		olRow.add(SpreadsheetCellType.STRING.createCell(posRow, 0, 1, 1,""));
		for (int column = 1; column < ssvTable71.getGrid().getColumnCount(); column++) {
			olRow.add(SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1, 0));
		}
		olRow.get(olRow.size() - 1).setEditable(false); // Последняя строка не доступна для редактирования
		return olRow;
	}*/

	//*************************************************************************************************************************
	//*************************************************************************************************************************
	//**
	//** Вкладка "Тематический план"
	//**
	//*************************************************************************************************************************
	//*************************************************************************************************************************

	/*@FXML
	void clickBAddRowTP(ActionEvent event) {
		GridBase newGrid = new GridBase(ssvTableTP.getGrid().getRowCount() + 1, ssvTableTP.getGrid().getColumnCount()); // Создадим сетку с +1 строкой
		int newRowPos = ssvTableTP.getGrid().getRowCount(); // и количество строк

		ObservableList<ObservableList<SpreadsheetCell>> newRows = ssvTableTP.getGrid().getRows(); // а так же существующие строки
		final ObservableList<SpreadsheetCell> olNew = createRowForTTP(newRowPos, null); // Добавление на место последней строки пустой строки
		newRows.add(olNew);

		newGrid.setRows(newRows);
		newGrid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight(newGrid.getRowCount())));
		newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTP);
		ssvTableTP.setGrid(newGrid);
	}*/

	/*@FXML
	void clickBDelRowTP(ActionEvent event) {
		int col = ssvTableTP.getSelectionModel().getFocusedCell().getColumn();
		int row = ssvTableTP.getSelectionModel().getFocusedCell().getRow();
		if (!(row > 0 && row < ssvTableTP.getGrid().getRowCount())) return; 
		GridBase newGrid = new GridBase(ssvTableTP.getGrid().getRowCount() - 1, ssvTableTP.getGrid().getColumnCount()); // Создадим сетку с -1 строкой
		ObservableList<ObservableList<SpreadsheetCell>> newRows = setHeaderForTTP(newGrid); // по новой инициализируем хедер таблицы
		
		for (int i = 1; i < ssvTableTP.getGrid().getRowCount(); i++) {
			if (i == row) continue; // та строка, которую нужно пропустить
			int k = i;
			if (i > row) k--; // перешагиваем i-ую строку для createStringRow() 
			ArrayList<String> lValueOfOldCell = new ArrayList<>();
			for (int j = 0; j < ssvTableTP.getGrid().getColumnCount(); j++) {
				lValueOfOldCell.add(ssvTableTP.getGrid().getRows().get(i).get(j).getText());
			}
			ObservableList<SpreadsheetCell> oldRow = createRowForTTP(k, lValueOfOldCell); // TTP - Table Thematical Plan
			newRows.add(oldRow);
		}
		newGrid.getColumnHeaders().addAll(ssvTableTP.getGrid().getColumnHeaders());
		newGrid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight(newGrid.getRowCount())));
		newGrid.setRows(newRows);
		newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTP);

		ssvTableTP.setGrid(newGrid);

		if (row == ssvTableTP.getGrid().getRowCount()) { // переставим фокус
			ssvTableTP.getSelectionModel().focus(row - 1, ssvTableTP.getColumns().get(col)); // фокус на предыдущую строку, но ту же колонку
		} else {
			ssvTableTP.getSelectionModel().focus(row, ssvTableTP.getColumns().get(col)); // фокус на ту же строку и ту же колонку
		}
	}*/

	//*************************************************************************************************************************
	//*************************************************************************************************************************
	//**
	//** Вкладка "Таблица 7.1"
	//**
	//*************************************************************************************************************************
	//*************************************************************************************************************************

	/**
	 * Создаёт строку общего типа для указанной позиции
	 * @param posRow позиция для новой строки
	 * @param lValueOfOldCell список значений ячеек. Обязательно String
	 * @return строку общего типа
	 */
	private ObservableList<SpreadsheetCell> createStringRow(int posRow, ArrayList<String> lValueOfOldCell) {
		ObservableList<SpreadsheetCell> olRow = FXCollections.observableArrayList();
		if (lValueOfOldCell == null) {
			for (int column = 0; column < ssvTable71.getGrid().getColumnCount(); column++) {
				olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1,""));
			}
		} else
			try {
				if (ssvTable71.getGrid().getColumnCount() != lValueOfOldCell.size())				
					throw new Exception("Количество столбцов не совпадает с размером переданного списка");

				for (int column = 0; column < ssvTable71.getGrid().getColumnCount(); column++) {
					olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1, lValueOfOldCell.get(column)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		olRow.get(olRow.size() - 1).setEditable(false); // Последняя строка не доступна для редактирования
		return olRow;
	}

	/**
	 * Добавляет строку общего типа в конец таблицы
	 * @param event
	 */
	@FXML
	void clickBAddRowT71(ActionEvent event)
	// https://bitbucket.org/controlsfx/controlsfx/issues/590/adding-new-rows-to-a-spreadsheetview
	// https://bitbucket.org/controlsfx/controlsfx/issues/151/dynamic-adding-rows-in-spreadsheetview-at
	{
		Record rec = new Record(currSemester.getQUANTITY_OF_WEEK(), ssvTable71.getGrid().getRowCount());
		currSemester.getRowT71().add(rec);
		addRowT71(null); // Создадим пусую строку
	}

	/**
	 * Удаляет строку в которой была выделена клетка
	 * @param event
	 */
	@FXML
	void clickBDelRowT71(ActionEvent event) {
		int col = ssvTable71.getSelectionModel().getFocusedCell().getColumn(); // получение фокуса
		int row = ssvTable71.getSelectionModel().getFocusedCell().getRow();

		for (Record rec : currSemester.getRowT71()) { // удаление из массива записей
			if (rec.getPos() == row) {
				currSemester.getRowT71().remove(rec);
				break;
			}
		}

		if (!(row > 3 && row < ssvTable71.getGrid().getRowCount())) return; 
		//ssvTable71.getSelectionModel().clearSelection(); // убрать фокус совсем
		GridBase newGrid = new GridBase(ssvTable71.getGrid().getRowCount() - 1, ssvTable71.getGrid().getColumnCount()); // Создадим сетку с -1 строкой
		ObservableList<ObservableList<SpreadsheetCell>> newRows = setHeaderForT71(newGrid, currSemester.getQUANTITY_OF_WEEK()); // по новой инициализируем хедер таблицы
		
		for (int i = 4; i < ssvTable71.getGrid().getRowCount(); i++) {
			if (i == row) continue; // та строка, которую нужно пропустить
			int k = i;
			if (i > row) k--; // перешагиваем i-ую строку для createStringRow() 
			ArrayList<String> lValueOfOldCell = new ArrayList<>();
			for (int j = 0; j < ssvTable71.getGrid().getColumnCount(); j++) {
				lValueOfOldCell.add(ssvTable71.getGrid().getRows().get(i).get(j).getText());
			}
			ObservableList<SpreadsheetCell> oldRow = createStringRow(k, lValueOfOldCell);
			newRows.add(oldRow);
		}
		newGrid.getColumnHeaders().addAll(ssvTable71.getGrid().getColumnHeaders());
		newGrid.setRows(newRows);
		newGrid.spanColumn(newGrid.getColumnCount() - 2, 0, 1); // объединение "Распределение по учебным неделям"
		newGrid.spanRow(2, 0, 0); // объединение "Виды работ"
		newGrid.spanRow(2, 0, newGrid.getColumnCount() - 1); // объединение "Итого"
		newGrid.spanColumn(currSemester.getQUANTITY_OF_WEEK(), 2, 1); // объединение "M1"
		newGrid.spanColumn(currSemester.getQUANTITY_OF_WEEK(), 3, 1); // объединение "P1"

		ssvTable71.setGrid(newGrid);
		newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehT71);
		
		if (row == ssvTable71.getGrid().getRowCount()) { // переставим фокус
			ssvTable71.getSelectionModel().focus(row - 1, ssvTable71.getColumns().get(col)); // фокус на предыдущую строку, но ту же колонку
		} else {
			ssvTable71.getSelectionModel().focus(row, ssvTable71.getColumns().get(col)); // фокус на ту же строку и ту же колонку
		}
		if (ssvTable71.getSelectionModel().getFocusedCell().getRow() < 4)
			bDelRowT71.setDisable(true);
	}

	//*************************************************************************************************************************
	//*************************************************************************************************************************
	//**
	//** Вкладка "Тематический план"
	//**
	//*************************************************************************************************************************
	//*************************************************************************************************************************

	/**
	 * Добавить элемент в TreeView
	 * @param event
	 */
	@FXML
	void clickBAddElement(ActionEvent event) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("ModalThematicalPlan.fxml"));
		Parent root = null;
		try {
			root = (Parent) fxmlLoader.load();
		} catch (IOException e) {
			System.err.println("Не удалось загрузить форму настройки таблицы тематического плана");
			e.printStackTrace();
		}
		Scene scene = new Scene(root);

		Stage stageModalThematicalPlan = new Stage();
		FXMLCtrlModalThematicalPlan fxmlCtrlModalThematicalPlan = fxmlLoader.getController();
		fxmlCtrlModalThematicalPlan.init(stageModalThematicalPlan);
		stageModalThematicalPlan.setResizable(false);
		fxmlCtrlModalThematicalPlan.setController(fxmlCtrlCurrTab); // Запомним контроллер новой вкладки для перерсовки
		fxmlCtrlModalThematicalPlan.setRoot(treeRoot, 0);
		fxmlCtrlModalThematicalPlan.setWPDVersion(currWPDVersion);
		stageModalThematicalPlan.setScene(scene);
		stageModalThematicalPlan.setTitle("Settings");
		stageModalThematicalPlan.getIcons().add(new Image("Logo.png"));
		stageModalThematicalPlan.initModality(Modality.APPLICATION_MODAL);
		//stageModalThematicalPlan.setResizable(false);
		stageModalThematicalPlan.showAndWait();
	}

	@FXML
	void clickBSetElement(ActionEvent event) {
		// передача выбранного пункта
	}

	@FXML
	void clickBDelElement(ActionEvent event) {
		System.err.println("OK!");
	}

	//*************************************************************************************************************************
	//*************************************************************************************************************************
	//**
	//** Контроллеры вкладки "ПКМ"
	//**
	//*************************************************************************************************************************
	//*************************************************************************************************************************

	@FXML
	void clickBAddRowPoCM(ActionEvent event) {
		olDataOfPoCM.add(new RowPoCM("","",""));
	}

	@FXML
	void clickBDelRowPoCM(ActionEvent event) {
		int selectedIndex = tvPoCM.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) tvPoCM.getItems().remove(selectedIndex);
		if (olDataOfPoCM.size() == 0) {
			tfCM.setText("");
			tfNoS.setText("");
			tfNoW.setText("");
		}
	}

	@FXML
	void clickBSaveRowPoCM(ActionEvent event) {
		/*tvPoCM.getSelectionModel().getSelectedItem().setSspCtrlMes(tfCM.getText());
		tvPoCM.getSelectionModel().getSelectedItem().setSspNuberOfSemester(tfNoS.getText());
		tvPoCM.getSelectionModel().getSelectedItem().setSspNumberOfWeek(tfNoW.getText());*/
		RowPoCM rowPoCM = olDataOfPoCM.get(tvPoCM.getSelectionModel().getSelectedIndex());
		rowPoCM.setSspCtrlMes(tfCM.getText());
		rowPoCM.setSspNuberOfSemester(tfNoS.getText());
		rowPoCM.setSspNumberOfWeek(tfNoW.getText());
		olDataOfPoCM.set(tvPoCM.getSelectionModel().getSelectedIndex(), rowPoCM);
	}

	//*************************************************************************************************************************
	//*************************************************************************************************************************
	//**
	//** Выгрузка в компоненты содержимого БД
	//**
	//*************************************************************************************************************************
	//*************************************************************************************************************************

	/**
	 * Создаёт строку для указанной позиции
	 * @param posRow позиция для новой строки
	 * @param lValueOfOldCell список значений ячеек.
	 * @return строку
	 */
	// FIXME переписать
	private ObservableList<SpreadsheetCell> createRowForTTP(int posRow, List<String> lValueOfOldCell) {
		ObservableList<SpreadsheetCell> olRow = FXCollections.observableArrayList();
		if (lValueOfOldCell == null) { // Используется для создания новой строки
			for (int column = 0; column < 4; column++) {
				SpreadsheetCell ssC = SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1, 0);
				ssC.setEditable(false);
				olRow.add(ssC);
			}
			for (int column = 4; column < 6; column++) {
				olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1, ""));
			}
			for (int column = 6; column < ssvTableTP.getGrid().getColumnCount(); column++) {
				olRow.add(SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1, 0));
			}
		} else { // Используется при удалении строки и переносе значений на строку выше, и вставки строки
			for (int column = 0; column < 4; column++) {
				int temp = 0;
				if (!lValueOfOldCell.get(column).equals("null"))
					 temp = Integer.parseInt(lValueOfOldCell.get(column));
				SpreadsheetCell ssC = SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1, temp);
				ssC.setEditable(false);
				olRow.add(ssC);
			}
			for (int column = 4; column < 6; column++) {
				olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1, lValueOfOldCell.get(column)));
			}
			for (int column = 6; column < ssvTableTP.getGrid().getColumnCount(); column++) {
				int temp = 0;
				if (!lValueOfOldCell.get(column).equals("null"))
					 temp = Integer.parseInt(lValueOfOldCell.get(column));
				olRow.add(SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1, temp));
			}
		}
		return olRow;
	}

	private void addSetToTTP (Set<ThematicPlan> sThematicPlan) {
		//if (sThematicPlan.size() == 0) return;
		GridBase newGrid = new GridBase(ssvTableTP.getGrid().getRowCount() + sThematicPlan.size(), ssvTableTP.getGrid().getColumnCount());
		//int newRowPos = ssvTableTP.getGrid().getRowCount(); // и количество строк
		int i = 1;

		ObservableList<ObservableList<SpreadsheetCell>> newRows = ssvTableTP.getGrid().getRows(); // а так же существующие строки
		for (ThematicPlan tp : sThematicPlan) {
			ArrayList<String> alRow = new ArrayList<>();
			alRow.add(tp.getTitle());
			alRow.add(tp.getDescription());
			//alRow.add(String.valueOf(tp.getBelongingToTheSemester()));
			alRow.add(String.valueOf( tp.getBelongingToTheModule() == null? "0" : tp.getBelongingToTheModule() ));
			alRow.add(String.valueOf( tp.getBelongingToTheSection() == null? "0" : tp.getBelongingToTheSection() ));
			alRow.add(String.valueOf( tp.getL() == null ? "0" : tp.getL() ));
			alRow.add(String.valueOf( tp.getPZ() == null ? "0" : tp.getPZ() ));
			alRow.add(String.valueOf( tp.getLR() == null ? "0" : tp.getLR() ));
			alRow.add(String.valueOf( tp.getKSR() == null ? "0" : tp.getKSR() ));
			alRow.add(String.valueOf( tp.getSRS() == null ? "0" : tp.getSRS() ));
			ObservableList<SpreadsheetCell> olNew = createRowForTTP(i++, alRow); // Добавление на место последней строки пустой строки
			newRows.add(olNew);
		}

		newGrid.setRows(newRows);
		newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTP);
		newGrid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight(newGrid.getRowCount())));

		ssvTableTP.setGrid(newGrid);
	}

	/**
	 * Загрузка по ID версии полей в этой вкладке
	 * @param id_Vers ID версии
	 */
	private void load(Long id_Vers) {
		currWPDVersion = new WPDVersion();

		DAO_WPDVersion dao_Vers = new DAO_WPDVersion();
		currWPDVersion = dao_Vers.getById(WPDVersion.class, id_Vers);

		if (currWPDVersion.getThematicPlans() != null) {
			if (currWPDVersion.getThematicPlans().size() != 0) {
				setCurrThematicPlan = currWPDVersion.getThematicPlans();
				addSetToTTP(setCurrThematicPlan);
			}
		}

		if (currWPDVersion.getPoCM() == null) {
			currPoCM = new PoCM();
			currWPDVersion.setPoCM(currPoCM);
			currPoCM.setWpdVersion(currWPDVersion);
			DAO_PoCM dao_PoCM = new DAO_PoCM();
			currPoCM.setId(dao_PoCM.add(currPoCM));
		} else {
			currPoCM = currWPDVersion.getPoCM();
			// TODO Выгрузить в список RowPoCM
			// TODO Загрузка полей во вкладку
		}

		tfVersion.setText(currWPDVersion.getName()); // Name должен всегда существовать
		if (currWPDVersion.getTemplateName() != null) // Грузим шаблон при открытии, но не при создании
			tfPath.setText(currWPDVersion.getTemplateName());

		/* http://stackoverflow.com/questions/21242110/convert-java-util-date-to-java-time-localdate */
		if (currWPDVersion.getDate() != null) {
			Instant instant = currWPDVersion.getDate().toInstant();
			ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
			dpDateOfCreate.setValue(zdt.toLocalDate()); // Попробуем достать дату создания
		} else
			dpDateOfCreate.setValue(LocalDate.now());

		//Вывод того, что есть
		System.err.println(currWPDVersion.toString());
		System.err.println(currPoCM.toString());
		if (setCurrThematicPlan != null)
			for (ThematicPlan tematicPlan : setCurrThematicPlan) {
				System.err.println(tematicPlan.toString());
			}
	}

	//*************************************************************************************************************************
	//*************************************************************************************************************************
	//**
	//** Блок инициализации компонентов
	//**
	//*************************************************************************************************************************
	//*************************************************************************************************************************

	/**
	 * Чтение "config.properties" 
	 */
	/*private void readProperties() {

		Properties prop = new Properties();
		InputStream input = null;
		try {
			File propFile = new File("config.properties");
			if (!propFile.exists()) {// Если этого файла нет, то создаём его
				propFile.createNewFile();
			}
			input = new FileInputStream(propFile);
			//input = getClass().getClassLoader().getResourceAsStream("config.properties");
			// load a properties file
			prop.load(input);
			NUMBER_OF_SEMESTER = Integer.parseInt(prop.getProperty("numberOfSemester"));
			NUMBER_OF_MODULE = Integer.parseInt(prop.getProperty("numberOfModule"));
			NUMBER_OF_SECTION = Integer.parseInt(prop.getProperty("numberOfSection"));
			NUMBER_OF_WEEK = Integer.parseInt(prop.getProperty("numberOfWeeks"));
		} catch (IOException | NumberFormatException ex) {
			ex.printStackTrace();
			// Стандартные значения в случае ошибки
			NUMBER_OF_SEMESTER = 1;
			NUMBER_OF_MODULE = 2;
			NUMBER_OF_SECTION = 4;
			NUMBER_OF_WEEK = 17;
			parentCtrl.setStatus("Файл \"config.properties\" не удалось найти, загружены вшитые параметры");
			rewriteProp(prop);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}*/

	/**
	 * Записывает все текущие значения в "config.properties"
	 * @param prop сам файл
	 */
	/*private void rewriteProp(Properties prop) {
		prop.setProperty("numberOfSemester", String.valueOf(NUMBER_OF_SEMESTER));
		prop.setProperty("numberOfModule", String.valueOf(NUMBER_OF_MODULE));
		prop.setProperty("numberOfSection", String.valueOf(NUMBER_OF_SECTION));
		prop.setProperty("numberOfWeeks", String.valueOf(NUMBER_OF_WEEK));
		try {
			prop.store(new FileOutputStream("config.properties"), "Study load");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * Задаёт header всей таблицы ssvTableTP
	 * @param grid BaseGrid этой таблицы
	 * @return первую строку
	 */
	private ObservableList<ObservableList<SpreadsheetCell>> setHeaderForTTP(GridBase grid) {
		ObservableList<ObservableList<SpreadsheetCell>> rowsHeader = FXCollections.observableArrayList();
		
		// 1-ая строка
		final ObservableList<SpreadsheetCell> olHeader = FXCollections.observableArrayList();
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 0, 1, 1, "№ семестра")); // Принадлежность к модулю
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 1, 1, 1, "№ модуля")); // Принадлежность к модулю
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 2, 1, 1, "№ раздела")); // Принадлежность к модулю
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 3, 1, 1, "№ темы")); // Принадлежность к модулю
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 4, 1, 1,"Название темы"));
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 5, 1, 1,"Описание темы"));
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 6, 1, 1, "Л"));
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 7, 1, 1, "ПЗ"));
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 8, 1, 1, "ЛР"));
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 9, 1, 1, "КСР"));
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 10, 1, 1, "СРС"));

		// запрещает редактирование
		for (SpreadsheetCell cell : olHeader) {
			cell.setEditable(false);
		}
		rowsHeader.add(olHeader); // первая строка заполнена
		return rowsHeader;
	}

	/**
	 * Specify a custom row height.
	 * @return Map
	 */
	private Map<Integer, Double> generateRowHeight(int val) {
		Map<Integer, Double> rowHeight = new HashMap<>();
		rowHeight.put(0, 24.0); // For Header
		for (int i = 1; i < val; i++) { // for other rows
			rowHeight.put(i, 35.0);
		}
		return rowHeight;
	}

	/**
	 * Инициализация компонента ssvTableTP
	 */
	private void initSSVTableTP()
	// FIXME Необходима ли ещё одна колонка для "Принадлежность к семестру?"
	// FIXME Необходима ли ещё одна колонка для "Номер темы?"
	// FIXME СРС должна быть String?
	{
		ehTP = new EventHandler<GridChange>() {
			public void handle(GridChange change) {
				System.err.println("TEST");
			}
		};
		int rowCount = 1;
		int columnCount = 11;
		GridBase grid = new GridBase(rowCount, columnCount);

		//GridBase.MapBasedRowHeightFactory rowHeightFactory = new GridBase.MapBasedRowHeightFactory(generateRowHeight(1));
		//grid.setRowHeightCallback(rowHeightFactory);
		grid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight(1)));
		ObservableList<ObservableList<SpreadsheetCell>> rows = setHeaderForTTP(grid);
		grid.setRows(rows);
		grid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTP);
		ssvTableTP = new SpreadsheetView(grid);
		ssvTableTP.getStylesheets().add(getClass().getResource("/SpreadSheetView.css").toExternalForm());
		ssvTableTP.setShowRowHeader(true);
		ssvTableTP.setShowColumnHeader(true);

		hbReplacementThematicalPlan.getChildren().add(ssvTableTP); // FIXME USE MasterDetailPane
		HBox.setHgrow(ssvTableTP, Priority.ALWAYS);
		HBox.setMargin(ssvTableTP, new Insets(15, 10, 15, 10));

		rootElement.setExpanded(true);
		tvRoot.setRoot(rootElement);
		tvRoot.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {

			@Override
			public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> old_val, TreeItem<String> new_val) {
				if (new_val == null) return;
				// Поиск выбранного элемента в treeRoot
				//System.err.println("AHTUNG new_value == " + new_val); 
				if (new_val.getValue().split(" ")[0].equals("Семестр")) {
					repaintSSVTableTP(
							Integer.parseInt(new_val.getValue().split(" ")[1]) // берём № семестра
					);
				}
				if (new_val.getValue().split(" ")[0].equals("Модуль")) {
					repaintSSVTableTP(
							Integer.parseInt(new_val.getParent().getValue().split(" ")[1]), // берём № семестра
							Integer.parseInt(new_val.getValue().split(" ")[1]) // берём № модуля
					);
				}
				if (new_val.getValue().split(" ")[0].equals("Раздел")) {
					repaintSSVTableTP(
							Integer.parseInt(new_val.getParent().getParent().getValue().split(" ")[1]), // берём № семестра
							Integer.parseInt(new_val.getParent().getValue().split(" ")[1]), // берём № модуля
							Integer.parseInt(new_val.getValue().split(" ")[1]) // берём № раздела
					);
				}
				if (new_val.getValue().split(" ")[0].equals("Тема")) {
					repaintSSVTableTP(
							Integer.parseInt(new_val.getParent().getParent().getParent().getValue().split(" ")[1]), // берём № семестра
							Integer.parseInt(new_val.getParent().getParent().getValue().split(" ")[1]), // берём № модуля
							Integer.parseInt(new_val.getParent().getValue().split(" ")[1]), // берём № раздела
							Integer.parseInt(new_val.getValue().split(" ")[1]) // берём № темы
					);
				}
            }

        });
		tvRoot.setShowRoot(false);
		createTree();
	}



	/**
	 * перерисовывает содержимое ssvTableTP для выбранного в tvRoot элемента, будь то модуль или раздел, или тема (тематический план)
	 * @param temp массив int, первое число определяет № модуля, 2-ое - № раздела, 3-е № темы
	 */
	private void repaintSSVTableTP(int... temp) {

		createSSVTableTP();

		List<ThematicPlan> liTheme = new ArrayList<>();
		try {
			switch (temp.length) {
			case 1:
				for (Module module : treeRoot.getSemester(temp[0]).getTreeModule()) {
					Set<Section> setSection = module.getTreeSection();
					for (Section section : setSection) {
						liTheme.addAll(section.getTreeTheme());
					}
				}
				break;
			case 2:
				Set<Section> setSection = treeRoot.getSemester(temp[0]).getModule(temp[1]).getTreeSection();
				for (Section section : setSection) {
					liTheme.addAll(section.getTreeTheme()); // скопируем у каждой секции
				}
				break;
			case 3:
				liTheme.addAll(treeRoot.getSemester(temp[0]).getModule(temp[1]).getSection(temp[2]).getTreeTheme());
				break;
			case 4:
				liTheme.add(treeRoot.getSemester(temp[0]).getModule(temp[1]).getSection(temp[2]).getTheme(temp[3]));
				break;
			default:
				throw new Exception("нет аргументов у массива тематического плана");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		pasteIntoSSVTableTP(liTheme);
	}

	/**
	 * Вставляет данные в ssvTableTP из liTheme
	 */
	private void pasteIntoSSVTableTP(List<ThematicPlan> liTheme) {
		DAO_ThematicPlan dao_theme = new DAO_ThematicPlan();
		for (ThematicPlan theme : liTheme) {
			dao_theme = new DAO_ThematicPlan();
			ThematicPlan thematicPlan = dao_theme.getById(ThematicPlan.class, theme.getId());
			addRowSSVTableTP(thematicPlan);
		}
	}

	/**
	 * Добавляет строку в ssvTableTP в соотвествии с № модуля, раздела, темы
	 * @param theme строка из liTheem или null, если хотим заполнить по стандарту
	 */
	private void addRowSSVTableTP(ThematicPlan theme) {

		GridBase newGrid = new GridBase(ssvTableTP.getGrid().getRowCount() + 1, ssvTableTP.getGrid().getColumnCount()); // Создадим сетку с +1 строкой
		int newRowPos = ssvTableTP.getGrid().getRowCount(); // и количество строк

		ObservableList<ObservableList<SpreadsheetCell>> newRows = ssvTableTP.getGrid().getRows(); // а так же существующие строки

		List<String> liValueOftheme = new ArrayList<String>();
		liValueOftheme.add(String.valueOf(theme.getBelongingToTheSemester()));
		liValueOftheme.add(String.valueOf(theme.getBelongingToTheModule()));
		liValueOftheme.add(String.valueOf(theme.getBelongingToTheSection()));
		liValueOftheme.add(String.valueOf(theme.getNumber()));
		liValueOftheme.add(theme.getTitle());
		liValueOftheme.add(theme.getDescription());
		liValueOftheme.add(String.valueOf(theme.getL()));
		liValueOftheme.add(String.valueOf(theme.getPZ()));
		liValueOftheme.add(String.valueOf(theme.getLR()));
		liValueOftheme.add(String.valueOf(theme.getKSR()));
		liValueOftheme.add(String.valueOf(theme.getSRS()));

		final ObservableList<SpreadsheetCell> olNew = createRowForTTP(newRowPos, liValueOftheme); // Добавление на место последней строки пустой строки
		newRows.add(olNew);

		newGrid.setRows(newRows);
		newGrid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight(newGrid.getRowCount())));
		newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTP);
		ssvTableTP.setGrid(newGrid);
	}

	/**
	 * отрисовка дерева в tvRoot
	 */
	public void createTree() {
		tvRoot.getSelectionModel().clearSelection();
		rootElement.getChildren().clear();
		for (Semester semester : treeRoot) {
			TreeItem<String> nodeSemester = new TreeItem<String>("Семестр " + semester.getNUMBER_OF_SEMESTER());
			nodeSemester.setExpanded(true);
			rootElement.getChildren().add(nodeSemester);
			for (Module module : semester.getTreeModule()) {
				TreeItem<String> nodeModule = new TreeItem<String>("Модуль " + module.getNumber());
				nodeModule.setExpanded(true);
				nodeSemester.getChildren().add(nodeModule);
				for (Section section : module.getTreeSection()) {
					TreeItem<String> nodeSection = new TreeItem<String>("Раздел " + section.getNumber());
					nodeSection.setExpanded(true);
					nodeModule.getChildren().add(nodeSection);
					for (ThematicPlan theme : section.getTreeTheme()) {
						TreeItem<String> nodeTheme = new TreeItem<String>("Тема " + theme.getNumber());
						nodeTheme.setExpanded(false);
						nodeSection.getChildren().add(nodeTheme);
					}
				}
			}
		}
	}

	/**
	 * Создаёт каркас таблицы ssvTableTP
	 */
	private void createSSVTableTP() {

		int rowCount = 1;
		int columnCount = 11;
		GridBase grid = new GridBase(rowCount, columnCount);

		ObservableList<ObservableList<SpreadsheetCell>> rows = setHeaderForTTP(grid);

		grid.setRows(rows);

		grid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTP);

		// Задание ширины колонкам
		ssvTableTP.getColumns().get(0).setPrefWidth(75);
		ssvTableTP.getColumns().get(1).setPrefWidth(65);
		ssvTableTP.getColumns().get(2).setPrefWidth(70);
		ssvTableTP.getColumns().get(3).setPrefWidth(55);
		ssvTableTP.getColumns().get(4).setPrefWidth(200);
		ssvTableTP.getColumns().get(5).setPrefWidth(100);
		for (int i = 6; i < ssvTable71.getColumns().size() - 1; i++)
			ssvTable71.getColumns().get(i).setPrefWidth(25);

		ssvTableTP.setGrid(grid);
		ssvTableTP.setShowRowHeader(true);
		ssvTableTP.setShowColumnHeader(true);

	}

	private void initTvStudyLoad() {
		Callback<TableColumn<RowSL, String>, TableCell<RowSL, String>> cellFactory =
			new Callback<TableColumn<RowSL, String>, TableCell<RowSL, String>>() {
				public TableCell<RowSL, String> call(TableColumn<RowSL, String> p) {
					return new EditingCell();
				}
			};
		//colTVViewOfStudyLoad.setMinWidth(150.0);
		colTVViewOfStudyLoad.setCellValueFactory(new PropertyValueFactory<RowSL, String>("viewOfStudyLoad"));
		//colViewOfStudyLoad.setCellFactory(TextFieldTableCell.forTableColumn());
		colTVViewOfStudyLoad.setCellFactory(cellFactory);
		colTVViewOfStudyLoad.setOnEditCommit(
			new EventHandler<CellEditEvent<RowSL, String>>() {
				@Override
				public void handle(CellEditEvent<RowSL, String> t) {
					((RowSL) t.getTableView().getItems().get(
						t.getTablePosition().getRow())
						).setViewOfStudyLoad(t.getNewValue());
				}
			}
		);
		colTVNumberOfHours.setCellValueFactory(new PropertyValueFactory<RowSL, String>("numberOfHours"));
		//colNumberOfHours.setCellFactory(TextFieldTableCell.forTableColumn());
		colTVNumberOfHours.setCellFactory(cellFactory);
		colTVNumberOfHours.setOnEditCommit(
			new EventHandler<CellEditEvent<RowSL, String>>() {
				@Override
				public void handle(CellEditEvent<RowSL, String> t) {
					((RowSL) t.getTableView().getItems().get(
						t.getTablePosition().getRow())
						).setNumberOfHours(t.getNewValue());
				}
			}
		);
		colTVLadderpointsUnit.setCellValueFactory(new PropertyValueFactory<RowSL, String>("ladderpointsUnit"));
		colTVLadderpointsUnit.setCellFactory(cellFactory);
		//colLadderpointsUnit.setCellFactory(TextFieldTableCell.forTableColumn());
		colTVLadderpointsUnit.setOnEditCommit(
			new EventHandler<CellEditEvent<RowSL, String>>() {
				@Override
				public void handle(CellEditEvent<RowSL, String> t) {
					((RowSL) t.getTableView().getItems().get(
						t.getTablePosition().getRow())
						).setLadderpointsUnit(t.getNewValue());
				}
			}
		);

		tvStudyLoad.setItems(olDataOfStudyLoad);  
	}

	/**
	 * Инициализация контроллера для TableView из PoCM
	 */
	private void initTvPoCM() {

		olDataOfPoCM.addListener(new ListChangeListener<RowPoCM>() {

			@Override
			public void onChanged(ListChangeListener.Change change) {
				if (olDataOfPoCM.size() == 0) {
					tfCM.setDisable(true);
					tfNoS.setDisable(true);
					tfNoW.setDisable(true);
					bSaveRowPoCM.setDisable(true);
				}
			}
		});

		tcCM.setCellValueFactory(cellData -> cellData.getValue().sspCtrlMes);
		tcCM.setCellFactory(TextFieldTableCell.forTableColumn());

		tcNoS.setCellValueFactory(cellData -> cellData.getValue().sspNuberOfSemester);
		tcNoS.setCellFactory(TextFieldTableCell.forTableColumn());

		tcNoW.setCellValueFactory(cellData -> cellData.getValue().sspNumberOfWeek);
		tcNoW.setCellFactory(TextFieldTableCell.forTableColumn());

		tvPoCM.setItems(olDataOfPoCM);
		tvPoCM.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
			if (tvPoCM.getSelectionModel().getSelectedItem() != null) {
				bSaveRowPoCM.setDisable(false);
				tfCM.setDisable(false);
				tfNoS.setDisable(false);
				tfNoW.setDisable(false);
				tfCM.setText(newValue.getSspCtrlMes());
				tfNoS.setText(newValue.getSspNuberOfSemester());
				tfNoW.setText(newValue.getSspNumberOfWeek());
			} else {
				bSaveRowPoCM.setDisable(true);
				tfCM.setDisable(true);
				tfNoS.setDisable(true);
				tfNoW.setDisable(true);
			}
		});
		
		tfCM.setDisable(true);
		tfNoS.setDisable(true);
		tfNoW.setDisable(true);
		bSaveRowPoCM.setDisable(true);
	};

	/**
	 * Задаёт header всей таблицы ssvTable71
	 * @param grid BaseGrid этой таблицы
	 * @param length количество недель
	 * @return первые 4 строки
	 */
	private ObservableList<ObservableList<SpreadsheetCell>> setHeaderForT71(GridBase grid, int length) {
		
		ObservableList<ObservableList<SpreadsheetCell>> rowsHeader = FXCollections.observableArrayList();
		
		// 1-ая строка
		final ObservableList<SpreadsheetCell> lh1 = FXCollections.observableArrayList();
		lh1.add(SpreadsheetCellType.STRING.createCell(0, 0, 1, 1,"Виды работ"));
		lh1.get(0).getStyleClass().add("span");
		lh1.add(SpreadsheetCellType.STRING.createCell(0, 1, 1, 1,"Распределение по учебным неделям"));
		lh1.get(1).getStyleClass().add("span");
		for (int i = 2; i < grid.getColumnCount() - 1; i++) {
			SpreadsheetCell ssc = SpreadsheetCellType.STRING.createCell(0, i, 1, 1,"");
			lh1.add(ssc);
		}
		lh1.add(SpreadsheetCellType.STRING.createCell(0, length + 1, 1, 1,"Итого"));
		lh1.get(length + 1).getStyleClass().add("span");
		rowsHeader.add(lh1); // первая строка заполнена

		// 2-ая строка
		final ObservableList<SpreadsheetCell> lh2 = FXCollections.observableArrayList();
		lh2.add(SpreadsheetCellType.STRING.createCell(1, 0, 1, 1,""));
		for (int column = 0; column < grid.getColumnCount() - 2; column++) {
			lh2.add(SpreadsheetCellType.INTEGER.createCell(1, column + 1, 1, 1, column + 1));
		}
		lh2.add(SpreadsheetCellType.STRING.createCell(1, length + 1, 1, 1,""));
		rowsHeader.add(lh2);

		// 3-ая строка
		final ObservableList<SpreadsheetCell> lh3 = FXCollections.observableArrayList();
		lh3.add(SpreadsheetCellType.STRING.createCell(2, 0, 1, 1,"Модули"));
		for (int column = 1; column < grid.getColumnCount(); column++) {
			lh3.add(SpreadsheetCellType.STRING.createCell(2, column, 1, 1, ""));
		}
		lh3.get(1).setItem("М1");
		lh3.get(1).getStyleClass().add("span");
		rowsHeader.add(lh3);
		
		// 4-ая строка
		final ObservableList<SpreadsheetCell> lh4 = FXCollections.observableArrayList();
		lh4.add(SpreadsheetCellType.STRING.createCell(3, 0, 1, 1,"Разделы"));
		for (int column = 1; column < grid.getColumnCount(); column++) {
			lh4.add(SpreadsheetCellType.STRING.createCell(3, column, 1, 1, ""));
		}
		lh4.get(1).setItem("Р1");
		lh4.get(1).getStyleClass().add("span");
		rowsHeader.add(lh4);
		
		// запрещает их редактирование
		for (ObservableList<SpreadsheetCell> row : rowsHeader) {
			for (SpreadsheetCell cell : row) {
				cell.setEditable(false);
			}
		}

		return rowsHeader;
	}

	/**
	 * Загружает данные из currSemester в таблицу 7.1 
	 * @param currSem
	 */
	private void loadTvT71() {
		createTvT71(currSemester); // Создание каркаса
		pasteIntoTvT71(currSemester); // Подгрузка в ячейки данных

		if (!vbT71.getChildren().contains(ssvTable71)) { // отображение, если ещё не отображено
			vbT71.getChildren().add(ssvTable71);
			VBox.setVgrow(ssvTable71, Priority.ALWAYS);
			VBox.setMargin(ssvTable71, new Insets(0, 15, 0, 0));
		}
	}

	/**
	 * Вставляет данные в талбицу 7.1 из currSemester
	 * @param currSem
	 */
	private void pasteIntoTvT71(Semester currSem) {
		for (Record row : currSem.getRowT71())
			addRowT71(row);
	}

	/**
	 * Добавляет строку в таблицу 7.1
	 * @param record строка из Semester.getRowT71() или null, если хотим заполнить её ""
	 */
	private void addRowT71(Record record) {
		GridBase newGrid = new GridBase(ssvTable71.getGrid().getRowCount() + 1, ssvTable71.getGrid().getColumnCount()); // Создадим сетку с +1 строкой
		int newRowPos = ssvTable71.getGrid().getRowCount(); // и количество строк

		ObservableList<ObservableList<SpreadsheetCell>> newRows = ssvTable71.getGrid().getRows(); // а так же существующие строки

		ArrayList<String> liRow = null;
		if (record != null) {
			liRow = new ArrayList<>();
			liRow.add(record.getCourseTitle()); // Скопируем навзавние предмета
			for (int i = 0; i < record.getArrWeek().length; i++) // Скоппируем содержимое массива распределения часов
				liRow.add(record.getArrWeek()[i]);
			liRow.add(""); // Заполняем последнюю строку столбца итого
		}

		final ObservableList<SpreadsheetCell> olNew = createStringRow(newRowPos, liRow); // Добавление строки на место последней строки
		//index++;
		newRows.add(olNew);

		newGrid.setRows(newRows);
		ssvTable71.setGrid(newGrid);
		newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehT71);
	}

	/**
	 * Создаёт каркас таблицы
	 */
	private void createTvT71(Semester sem) {
		int length = sem.getQUANTITY_OF_WEEK();
		ehT71 = new EventHandler<GridChange>() {
			public void handle(GridChange change) {
				int row = change.getRow();
				int col = change.getColumn();
				//System.err.println(" Set Focused == " + row + ":" + col); 
				// Будем суммировать часы и выводить в итого
				int summ = 0;
				for (int i = 1; i < ssvTable71.getGrid().getColumnCount() - 1; i++) {
					try {
						summ += Integer.parseInt(ssvTable71.getGrid().getRows().get(row).get(i).getText());
					} catch (NumberFormatException ex) {
						continue;
					}
				}
				ssvTable71.getGrid().getRows().get(row).get(ssvTable71.getGrid().getColumnCount() - 1).setEditable(true);
				ssvTable71.getGrid().setCellValue(row, ssvTable71.getGrid().getColumnCount() - 1, String.valueOf(summ));
				ssvTable71.getGrid().getRows().get(row).get(ssvTable71.getGrid().getColumnCount() - 1).setEditable(false);

				Record rec = currSemester.getRecord(row);
				if (rec != null) {
					System.err.println("NEW VALUE == " + (String) change.getNewValue() + " ON CELL( " + row + " : " + col +")");
					if (col == 0) rec.setCourseTitle((String) change.getNewValue());
					if (col > 0 && col <= rec.getArrWeek().length) rec.getArrWeek()[col - 1] = (String) change.getNewValue();
				}
			}
		};

		int rowCount = 4;
		int columnCount = length + 2;
		//index = 4;
		GridBase grid = new GridBase(rowCount, columnCount);

		ObservableList<ObservableList<SpreadsheetCell>> rows = setHeaderForT71(grid, length);

		grid.setRows(rows);
		grid.spanColumn(grid.getColumnCount() - 2, 0, 1); // объединение "Распределение по учебным неделям"
		grid.spanRow(2, 0, 0); // объединение "Виды работ"
		grid.spanRow(2, 0, grid.getColumnCount() - 1); // объединение "Итого"
		grid.spanColumn(length, 3, 1); // объединение "P1"
		// Вот так выглядит разъединение // grid.spanColumn(1, 3, 1); // разъединение "P1" 
		grid.spanColumn(length, 2, 1); // объединение "M1"
		grid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehT71);
		if (ssvTable71 != null)
			ssvTable71.setGrid(grid);
		else
			ssvTable71 = new SpreadsheetView(grid);

		// Задание ширины колонкам
		ssvTable71.getColumns().get(0).setPrefWidth(150);
		for (int i = 1; i < ssvTable71.getColumns().size() - 1; i++)
			ssvTable71.getColumns().get(i).setPrefWidth(35);
		ssvTable71.getColumns().get(ssvTable71.getColumns().size() - 1).setPrefWidth(140);

		ssvTable71.getStylesheets().add(getClass().getResource("/SpreadSheetView.css").toExternalForm());
		ssvTable71.setShowRowHeader(true);
		ssvTable71.setShowColumnHeader(true);
		ssvTable71.getSelectionModel().getSelectedCells().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable o) {
                for(TablePosition cell : ssvTable71.getSelectionModel().getSelectedCells()){
                    System.err.println(cell.getRow()+" / "+cell.getColumn()); // показывает индексы выделенных строк
                }
                if (ssvTable71.getSelectionModel().getSelectedCells().size() != 0)
                if (ssvTable71.getSelectionModel().getSelectedCells().get(ssvTable71.getSelectionModel().getSelectedCells().size() - 1).getRow() > 3)
                	bDelRowT71.setDisable(false);
                else
                	bDelRowT71.setDisable(true);
            }
        });
		if (sem.getRowT71() != null && sem.getRowT71().size() != 0) {
			for (Record rec : sem.getRowT71())
				addRowT71(rec);
		}
	}

	/**
	 * Описание методов поведения TableView, TreeTableView, а так же выделение памяти и установление связей
	 */
	private void initT() {
		//readProperties();
		initSSVTableTP(); // Инициализация вкладки Тематический план
		initTvStudyLoad();
		initTvPoCM();
	}

	public void init(Long id_Vers) {
		initT(); // Инициализация

		load(id_Vers); // Загрузка полей

		//mbNumberOfSemesters.setText(tsFNOS.toString());
		bSemester.setText( treeRoot.size() != 0 ? toString(treeRoot) : "" );

		olSemesters.addListener(new ListChangeListener<String>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends String> change) {
				if (olSemesters.size() != 0) {
					cbSemesters.setDisable(false);
				} else {
					cbSemesters.setDisable(true);
				}
			}
		});

		cbSemesters.getSelectionModel().selectedIndexProperty().addListener(
			new ChangeListener<Number>() {
				public void changed (ObservableValue<? extends Number> ov, Number value, Number new_value) {
					if (new_value.intValue() > -1) {
						bAddRowT71.setDisable(false);
						//bDelRowT71.setDisable(false);

						int iValue = Integer.parseInt(olSemesters.get((int) new_value));
						for (Semester sem : treeRoot) {
							if (sem.getNUMBER_OF_SEMESTER() == iValue) {
								currSemester = sem;
								loadTvT71();
								break;
							}
						}
					} else { // если пустое поле
						bAddRowT71.setDisable(true);
						bDelRowT71.setDisable(true);
					}
				}
			}
		);
		cbSemesters.setItems(olSemesters);
		if (treeRoot.size() != 0) bSemester.setText(toString(treeRoot));
		else bSemester.setText("Добавить");
	}

	//*************************************************************************************************************************
	//*************************************************************************************************************************
	//**
	//** Методы для работы со вкладкой и её контроллером
	//**
	//*************************************************************************************************************************
	//*************************************************************************************************************************

	private String toString(Set<Semester> tR) {
		String s = "";
		for (Semester sem : tR) {
			s += "," + sem.getNUMBER_OF_SEMESTER();
		}
		if (s.length() != 0) {
			return s.substring(1);
		}
		return null;
	}

	public void setParentCtrl(FXMLCtrlMain fxmlCtrlMain) {
		this.parentCtrl = fxmlCtrlMain;
	}

	public void setStage(Stage stage2) {
		this.stage = stage2;
	}

	public void setController(FXMLCtrlNewTab fxmlCtrlNewTab) {
		this.fxmlCtrlCurrTab = fxmlCtrlNewTab;
	}
	
	public String getTabName() {
		return tabName;
	}

	public void setTabName(String sValue) {
		this.tabName = sValue;
	}
}
