package com.mmsp.logic;

import java.io.File;
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

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType.StringType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

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
import com.mmsp.model.ThematicPlan;
import com.mmsp.model.WPDVersion;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Priority;
//import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
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

	private TreeSet<Integer> tsFNOS = new TreeSet<Integer>();

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

	@FXML
	private MenuButton mbNumberOfSemesters;

	@FXML
	private TextField tfVersion;

	@FXML
	private ListView<String> lvTypeOfControlMeasures;

	@FXML
	private DatePicker dpDateOfCreate;

	@FXML
	private Button bGenerate;

	@FXML
	private Button bDelete;

	@FXML
	private TextField tfPath;

	@FXML
	private Button bCallFileChooser;

	@FXML
	private Button bSave;

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

	private SpreadsheetView ssvTableTP; // Таблица тематического плана

	private EventHandler<GridChange> ehTP; // OnGridChange for TableTP

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

	// Переменные выбора семестров

	@FXML
	private CheckMenuItem cmi1;

	@FXML
	private CheckMenuItem cmi2;

	@FXML
	private CheckMenuItem cmi3;

	@FXML
	private CheckMenuItem cmi4;

	@FXML
	private CheckMenuItem cmi5;

	@FXML
	private CheckMenuItem cmi6;

	@FXML
	private CheckMenuItem cmi7;

	@FXML
	private CheckMenuItem cmi8;

	// Переменные вкладки "Таблица 7.1"

	@FXML
	private VBox vbT71;

	@FXML
	private Button bAddRowT71;

	@FXML
	private Button bDelRowT71;

	@FXML
	private Button bSetT71;

	// https://bitbucket.org/panemu/tiwulfx
	// vs.
	// https://bitbucket.org/controlsfx/controlsfx
	private SpreadsheetView ssvTable71; // Замена TableView<?> tvTable71;

	private EventHandler<GridChange> ehT71; // OnGridChange for TableTP

	//private int index; // # строки "Модули" в ssvTableT71

	//*************************************************************************************************************************
	//*************************************************************************************************************************
	//**
	//** Контроллеры кнопок на верхней панельке
	//**
	//*************************************************************************************************************************
	//*************************************************************************************************************************

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
	void ccmi1(ActionEvent event) {
		if (cmi1.isSelected()) tsFNOS.add(1); else tsFNOS.remove(1);
		mbNumberOfSemesters.setText(tsFNOS.toString());
	}

	@FXML
	void ccmi2(ActionEvent event) {
		if (cmi2.isSelected()) tsFNOS.add(2); else tsFNOS.remove(2);
		mbNumberOfSemesters.setText(tsFNOS.toString());
	}

	@FXML
	void ccmi3(ActionEvent event) {
		if (cmi3.isSelected()) tsFNOS.add(3); else tsFNOS.remove(3);
		mbNumberOfSemesters.setText(tsFNOS.toString());
	}

	@FXML
	void ccmi4(ActionEvent event) {
		if (cmi4.isSelected()) tsFNOS.add(4); else tsFNOS.remove(4);
		mbNumberOfSemesters.setText(tsFNOS.toString());
	}

	@FXML
	void ccmi5(ActionEvent event) {
		if (cmi5.isSelected()) tsFNOS.add(5); else tsFNOS.remove(5);
		mbNumberOfSemesters.setText(tsFNOS.toString());
	}

	@FXML
	void ccmi6(ActionEvent event) {
		if (cmi6.isSelected()) tsFNOS.add(6); else tsFNOS.remove(6);
		mbNumberOfSemesters.setText(tsFNOS.toString());
	}

	@FXML
	void ccmi7(ActionEvent event) {
		if (cmi7.isSelected()) tsFNOS.add(7);  else tsFNOS.remove(7);
		mbNumberOfSemesters.setText(tsFNOS.toString());
	}

	@FXML
	void ccmi8(ActionEvent event) {
		if (cmi8.isSelected()) tsFNOS.add(8); else tsFNOS.remove(8);
		mbNumberOfSemesters.setText(tsFNOS.toString());
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

	@FXML
	void clickBGenerate(ActionEvent event) {
		// TODO Генерация РПД по атомарным данным
	}

	@FXML
	void clickBDelete(ActionEvent event) {

		Long id = currWPDVersion.getNumber();

		// Закрываем вкладку
		parentCtrl.closeTab(id);

		DAO_HandBookDiscipline dao_hbd = new DAO_HandBookDiscipline();
		HandbookDiscipline hbd = dao_hbd.getById(HandbookDiscipline.class, currWPDVersion.getHbD().getId());
		hbd.getVersions().removeIf(p -> p.getId() == id); // FIXME Проверить: удалит или нет?
		
		DAO_WPDVersion dao_vers = new DAO_WPDVersion();
		dao_vers.remove(currWPDVersion);

		// перестал понимать зачем нужен код ниже в этом методе
		List<WPDVersion> listOfVersion = dao_vers.getAllByNumber(id);
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
		}
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

	@FXML
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
	}

	/**
	 * Создаёт строку для указанной позиции
	 * @param posRow позиция для новой строки
	 * @param lValueOfOldCell список значений ячеек.
	 * @return строку
	 */
	private ObservableList<SpreadsheetCell> createRowForTTP(int posRow, ArrayList<String> lValueOfOldCell) {
		ObservableList<SpreadsheetCell> olRow = FXCollections.observableArrayList();
		if (lValueOfOldCell == null) { // Используется для создания новой строки
			for (int column = 0; column < 2; column++) {
				olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1,""));
			}
			for (int column = 2; column < ssvTableTP.getGrid().getColumnCount(); column++) {
				olRow.add(SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1, 0));
			}
		} else { // Используется при удалении строки и переносе значений на строку выше
			for (int column = 0; column < 2; column++) {
				olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1, lValueOfOldCell.get(column)));
			}
			for (int column = 2; column < ssvTableTP.getGrid().getColumnCount(); column++) {
				olRow.add(SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1, Integer.parseInt(lValueOfOldCell.get(column))));
			}
		}
		return olRow;
	}

	@FXML
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
	}

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
		} else {
			for (int column = 0; column < ssvTable71.getGrid().getColumnCount(); column++) {
				olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1, lValueOfOldCell.get(column)));
			}
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
		GridBase newGrid = new GridBase(ssvTable71.getGrid().getRowCount() + 1, ssvTable71.getGrid().getColumnCount()); // Создадим сетку с +1 строкой
		int newRowPos = ssvTable71.getGrid().getRowCount(); // и количество строк

		ObservableList<ObservableList<SpreadsheetCell>> newRows = ssvTable71.getGrid().getRows(); // а так же существующие строки
		final ObservableList<SpreadsheetCell> olNew = createStringRow(newRowPos, null); // Добавление на место последней строки пустой строки
		//index++;
		newRows.add(olNew);

		newGrid.setRows(newRows);
		ssvTable71.setGrid(newGrid);
		newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehT71);
	}

	/**
	 * Удаляет строку в которой была выделена клетка
	 * @param event
	 */
	@FXML
	void clickBDelRowT71(ActionEvent event) {
		int col = ssvTable71.getSelectionModel().getFocusedCell().getColumn();
		int row = ssvTable71.getSelectionModel().getFocusedCell().getRow();
		if (!(row > 3 && row < ssvTable71.getGrid().getRowCount())) return; 
		//ssvTable71.getSelectionModel().clearSelection(); // убрать фокус совсем
		GridBase newGrid = new GridBase(ssvTable71.getGrid().getRowCount() - 1, ssvTable71.getGrid().getColumnCount()); // Создадим сетку с -1 строкой
		ObservableList<ObservableList<SpreadsheetCell>> newRows = setHeaderForT71(newGrid); // по новой инициализируем хедер таблицы
		
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
		newGrid.spanColumn(17, 2, 1); // объединение "M1"
		newGrid.spanColumn(17, 3, 1); // объединение "P1"

		ssvTable71.setGrid(newGrid);
		newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehT71);
		
		if (row == ssvTable71.getGrid().getRowCount()) { // переставим фокус
			ssvTable71.getSelectionModel().focus(row - 1, ssvTable71.getColumns().get(col)); // фокус на предыдущую строку, но ту же колонку
		} else {
			ssvTable71.getSelectionModel().focus(row, ssvTable71.getColumns().get(col)); // фокус на ту же строку и ту же колонку
		}
	}

	@FXML
	void clickBSetT71(ActionEvent event) {
		// UNDONE
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
			//alRow.add(String.valueOf(tp.getBelongingToTheSemester())); // FIXME
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
	private void readProperties() {

		Properties prop = new Properties();
		InputStream input = null;
		try {
			//input = new FileInputStream(".\\src\\main\\resources\\config.properties");
			input = getClass().getClassLoader().getResourceAsStream("config.properties");
			// load a properties file
			prop.load(input);
			// get the property value and print it out
			NUMBER_OF_SEMESTER = Integer.parseInt(prop.getProperty("numberOfSemester"));
			NUMBER_OF_MODULE = Integer.parseInt(prop.getProperty("numberOfModule"));
			NUMBER_OF_SECTION = Integer.parseInt(prop.getProperty("numberOfSection"));
			NUMBER_OF_WEEK = Integer.parseInt(prop.getProperty("numberOfWeeks"));
		} catch (IOException ex) {
			ex.printStackTrace();
			// Стандартные значения в случае ошибки
			NUMBER_OF_SEMESTER = 1;
			NUMBER_OF_MODULE = 2;
			NUMBER_OF_SECTION = 4;
			NUMBER_OF_WEEK = 17;
			parentCtrl.setStatus("Файл \"config.properties\" не удалось найти, загружены вшитые параметры");
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

	/**
	 * Задаёт header всей таблицы ssvTableTP
	 * @param grid BaseGrid этой таблицы
	 * @return первую строку
	 */
	private ObservableList<ObservableList<SpreadsheetCell>> setHeaderForTTP(GridBase grid) {
		ObservableList<ObservableList<SpreadsheetCell>> rowsHeader = FXCollections.observableArrayList();
		
		// 1-ая строка
		final ObservableList<SpreadsheetCell> olHeader = FXCollections.observableArrayList();
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 0, 1, 1,"Название темы"));
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 1, 1, 1,"Описание темы"));
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 2, 1, 1, "№ модуля")); // Принадлежность к модулю
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 3, 1, 1, "№ раздела")); // Принадлежность к модулю
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 4, 1, 1, "Л"));
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 5, 1, 1, "ПЗ"));
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 6, 1, 1, "ЛР"));
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 7, 1, 1, "КСР"));
		olHeader.add(SpreadsheetCellType.STRING.createCell(0, 8, 1, 1, "СРС"));

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
	private void initThematicalPlan()
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
		int columnCount = 9;
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

		vbThematicalPlan.getChildren().add(ssvTableTP);
		VBox.setVgrow(ssvTableTP, Priority.ALWAYS);
		VBox.setMargin(ssvTableTP, new Insets(0, 10, 5, 10));
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
	 * @return первые 4 строки
	 */
	private ObservableList<ObservableList<SpreadsheetCell>> setHeaderForT71(GridBase grid) {
		
		ObservableList<ObservableList<SpreadsheetCell>> rowsHeader = FXCollections.observableArrayList();
		
		// 1-ая строка
		final ObservableList<SpreadsheetCell> lh1 = FXCollections.observableArrayList();
		lh1.add(SpreadsheetCellType.STRING.createCell(0, 0, 1, 1,"Виды работ"));
		lh1.add(SpreadsheetCellType.STRING.createCell(0, 1, 1, 1,"Распределение по учебным неделям"));
		lh1.get(1).getStyleClass().add("span");
		for (int i = 2; i < grid.getColumnCount() - 1; i++) {
			SpreadsheetCell ssc = SpreadsheetCellType.STRING.createCell(0, i, 1, 1,"");
			lh1.add(ssc);
		}
		lh1.add(SpreadsheetCellType.STRING.createCell(0, 18, 1, 1,"Итого"));
		lh1.get(18).getStyleClass().add("span");
		lh1.get(0).getStyleClass().add("span");
		rowsHeader.add(lh1); // первая строка заполнена

		// 2-ая строка
		final ObservableList<SpreadsheetCell> lh2 = FXCollections.observableArrayList();
		lh2.add(SpreadsheetCellType.STRING.createCell(1, 0, 1, 1,""));
		for (int column = 0; column < grid.getColumnCount() - 2; column++) {
			lh2.add(SpreadsheetCellType.INTEGER.createCell(1, column + 1, 1, 1, column + 1));
		}
		lh2.add(SpreadsheetCellType.STRING.createCell(1, 18, 1, 1,""));
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
	 * Инициализация компонента ssvTable71
	 */
	private void initTvT71() { // UNDONE Контроллер на focus
		ehT71 = new EventHandler<GridChange>() {
			public void handle(GridChange change) {
				int row = change.getRow();
				//int col = change.getColumn();
				// Будем суммировать часы и выводить в итого
				int summ = 0;
				for (int i = 1; i < ssvTable71.getGrid().getColumnCount() - 1; i++) {
					try {
						summ += Integer.parseInt(ssvTable71.getGrid().getRows().get(row).get(i).getText());
					} catch (NumberFormatException ex) {
						continue;
					}
				}
				System.err.println(summ);
				ssvTable71.getGrid().getRows().get(row).get(ssvTable71.getGrid().getColumnCount() - 1).setEditable(true);
				ssvTable71.getGrid().setCellValue(row, ssvTable71.getGrid().getColumnCount() - 1, String.valueOf(summ));
				ssvTable71.getGrid().getRows().get(row).get(ssvTable71.getGrid().getColumnCount() - 1).setEditable(false);
			}
		};
		int rowCount = 4;
		int columnCount = NUMBER_OF_WEEK + 2;
		//index = 4;
		GridBase grid = new GridBase(rowCount, columnCount);

		ObservableList<ObservableList<SpreadsheetCell>> rows = setHeaderForT71(grid);

		// строка-кнопка для добавления в Модули
		/*final ObservableList<SpreadsheetCell> lhB = FXCollections.observableArrayList();
		SpreadsheetCellBase cellB = new SpreadsheetCellBase(4, 0, 1, 1);
		// http://stackoverflow.com/questions/30125610/how-to-add-a-button-in-the-spreadsheetview-table
		// http://codenav.org/code.html?project=/org/controlsfx/controlsfx/8.0.6&path=/Source%20Packages/org.controlsfx.control.spreadsheet/SpreadsheetCellEditor.java
		Button b = new Button("Добавить в модули");
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				Grid oldGrid = ssvTable71.getGrid(); // Получим сетку
				int newRowPos = oldGrid.getRowCount(); // и количество строк
				ObservableList<ObservableList<SpreadsheetCell>> rows = oldGrid.getRows(); // а так же существующие строки

				//rows.remove(newRowPos - 1); // Отчиска последней строки
				final ObservableList<SpreadsheetCell> olNew = createSimpleRow(newRowPos); // Добавление на место последней строки пустой строки
				rows.add(newRowPos - 1, olNew); // добавление в список новой строки

				//final ObservableList<SpreadsheetCell> olLast = createButtonRow(newRowPos); // Добавление на место последней строки пустой строки
				//rows.add(olLast); // добавление в список новой строки

				oldGrid.setRows(rows);
				ssvTable71.setGrid(oldGrid);
			}

			private ObservableList<SpreadsheetCell> createButtonRow(int posRow) {
				final ObservableList<SpreadsheetCell> olRow = FXCollections.observableArrayList();
				SpreadsheetCellBase cBut = new SpreadsheetCellBase(posRow, 0, 1, 1);
				Button bR = new Button("Добавить в модули");
				cBut.setGraphic(b);
				olRow.add(cBut);
				for (int column = 1; column < ssvTable71.getGrid().getColumnCount(); column++) {
					olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1, ""));
				}
				return olRow;
			}

			private ObservableList<SpreadsheetCell> createSimpleRow(int posRow) {
				ObservableList<SpreadsheetCell> olRow = FXCollections.observableArrayList();
				olRow.add(SpreadsheetCellType.STRING.createCell(posRow, 0, 1, 1,""));
				for (int column = 1; column < ssvTable71.getGrid().getColumnCount(); column++) {
					olRow.add(SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1, 0));
				}
				return olRow;
			}
		});
		cellB.setGraphic(b);
		lhB.add(cellB);
		for (int column = 1; column < grid.getColumnCount(); column++) {
			lhB.add(SpreadsheetCellType.STRING.createCell(4, column, 1, 1, ""));
		}
		rows.add(lhB);*/

		// ТЕСТ SpreadsheetCellType.OBJECT.createEditor(ssvTable71);
		
		/*for (int i = 3; i < grid.getRowCount(); i++) {
			ObservableList<SpreadsheetCell> lh = FXCollections.observableArrayList();
			lh.add(SpreadsheetCellType.STRING.createCell(i, 0, 1, 1, "VALUE"));
			for (int j = 1; j < grid.getColumnCount(); j++) {
				lh.add(SpreadsheetCellType.INTEGER.createCell(i, j, 1, 1, 0));
			}
			lh.addListener(new ListChangeListener<SpreadsheetCell>() {
				@Override
				public void onChanged(Change<? extends SpreadsheetCell> change) {
					int res = 0;
					for (int k = 1; k < grid.getColumnCount(); k++) {
						res += Integer.parseInt(lh.get(k).getText());
					}
					lh.get(grid.getColumnCount() - 1).setItem(res);
					System.err.println("RES == " + res);
				}
			});
			lh.get(grid.getColumnCount() - 1).setEditable(false); // Запрет на редактирование "Итого"
			//lh.get(0).setEditable(false);
			rows.add(lh);
		}*/

		grid.setRows(rows);
		grid.spanColumn(grid.getColumnCount() - 2, 0, 1); // объединение "Распределение по учебным неделям"
		grid.spanRow(2, 0, 0); // объединение "Виды работ"
		grid.spanRow(2, 0, grid.getColumnCount() - 1); // объединение "Итого"
		grid.spanColumn(17, 3, 1); // объединение "P1"
		// Вот так выглядит разъединение // grid.spanColumn(1, 3, 1); // разъединение "P1" 
		grid.spanColumn(17, 2, 1); // объединение "M1"
		//grid.spanColumn(19, 4, 0); // объединение Добавление в модули
		grid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehT71);
		ssvTable71 = new SpreadsheetView(grid);
		ssvTable71.getStylesheets().add(getClass().getResource("/SpreadSheetView.css").toExternalForm());
		ssvTable71.setShowRowHeader(true);
		ssvTable71.setShowColumnHeader(true);
		
		vbT71.getChildren().add(ssvTable71);
		VBox.setVgrow(ssvTable71, Priority.ALWAYS);
		VBox.setMargin(ssvTable71, new Insets(0, 10, 5, 10));
	}

	/**
	 * Описание методов поведения TableView, TreeTableView, а так же выделение памяти и установление связей
	 */
	private void initT() {
		readProperties();
		initThematicalPlan(); // Инициализация вкладки Тематичеуский план
		initTvStudyLoad();
		initTvPoCM();
		initTvT71();
	}

	public void init(Long id_Vers) {
		initT(); // Инициализация

		load(id_Vers); // Загрузка полей

		mbNumberOfSemesters.setText(tsFNOS.toString());

	}

	//*************************************************************************************************************************
	//*************************************************************************************************************************
	//**
	//** Методы для работы со вкладкой и её контроллером
	//**
	//*************************************************************************************************************************
	//*************************************************************************************************************************

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
