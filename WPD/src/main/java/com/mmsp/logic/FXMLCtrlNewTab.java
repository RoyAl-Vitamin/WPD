package com.mmsp.logic;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TreeSet;

import com.mmsp.dao.impl.DAO_PoCM;
import com.mmsp.dao.impl.DAO_ThematicPlan;
import com.mmsp.dao.impl.DAO_WPDVersion;
import com.mmsp.model.PoCM;
import com.mmsp.model.ThematicPlan;
import com.mmsp.model.WPDVersion;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
//import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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

	public static class RowT71 { // класс строки
		private final SimpleStringProperty sspNumberOfWeek; // Номер недели
		private final SimpleStringProperty sspTotal; // Итого
		private final SimpleStringProperty ssp; // i-ая колонка

		private RowT71(String sNumberOfWeek, String sTotal, String sValue) {
			this.sspNumberOfWeek = new SimpleStringProperty(sNumberOfWeek);
			this.sspTotal = new SimpleStringProperty(sTotal);
			this.ssp = new SimpleStringProperty(sValue);
		}

		public String getSspNumberOfWeek() {
			return sspNumberOfWeek.get();
		}

		public String getSspTotal() {
			return sspTotal.get();
		}

		public String getSsp() {
			return ssp.get();
		}
		
		public void setSspNumberOfWeek(String sValue) {
			sspNumberOfWeek.set(sValue);
		}

		public void setSspTotal(String sValue) {
			sspTotal.set(sValue);
		}

		public void setSsp(String sValue) {
			ssp.set(sValue);
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

	private final ObservableList<RowT71> olDataOfTableT71 = FXCollections.observableArrayList();
	
	private final ObservableList<RowPoCM> olDataOfPoCM = FXCollections.observableArrayList();

	private TreeSet<Integer> tsFNOS = new TreeSet<Integer>();

	private Stage stage;

	private FXMLCtrlNewTab fxmlCtrlNewTab; // Контроллер этой вкладки

	private FXMLCtrlMain parentCtrl;
	
	private String tabName; // здесь полное название вкладки, возможно стоило хранить только название версии, т.к. название дисциплины пока не менятся

	private WPDVersion currWPDVersion;
	private PoCM currPoCM;
	private ThematicPlan currThematicPlan;

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
	
	@FXML
	private TextField tfTitleOfThematicPlan;
	
	@FXML
	private TextArea tfDescriptionOfThematicPlan;

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
	private Button bAddRowT71;

	@FXML
	private Button bDelRowT71;

	@FXML
	private Button bSetT71;

	@FXML
	private TableView<?> tvTable71;

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

	void initTvPoCM() {

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
	};

	/**
	 * Описание методов поведения TableView, TreeTableView, а так же выделение памяти и установление связей
	 */
	private void initT() {
		initTvStudyLoad();
		initTvPoCM();
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

		DAO_ThematicPlan dao_thematicPlan = new DAO_ThematicPlan();
		currThematicPlan.setTitle(tfTitleOfThematicPlan.getText());
		currThematicPlan.setDescription(tfDescriptionOfThematicPlan.getText());
		dao_thematicPlan.update(currThematicPlan);

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
    	
    	DAO_WPDVersion dao_vers = new DAO_WPDVersion();
    	dao_vers.remove(currWPDVersion);

    	List<WPDVersion> listOfVersion = dao_vers.getAllByNumber(id);
    	for (int i = 0; i < listOfVersion.size(); i++) {
    		System.err.println("Name = " + listOfVersion.get(i).getName() + "\nID = " + listOfVersion.get(i).getId().toString());
    		if (listOfVersion.get(i).getPoCM() != null) {
    			System.err.println("PoCM == " + listOfVersion.get(i).getPoCM().toString());
    		} else
    			System.err.println("PoCM == null");
    		if (listOfVersion.get(i).getThematicPlan() != null) {
    			System.err.println("ThematicPlan == " + listOfVersion.get(i).getThematicPlan().toString());
    		} else
    			System.err.println("ThematicPlan == null");
    	}
    	
    	// TODO Каскадное удаление Тематического плана и Плана контрольных мероприятий 
    	// UPD[1]: Найти ошибку 
    	// UPD[2]: Ошибку нашёл, но так как нет зависимости @OneToMany каскадное удаление не будет производиться
    	// UPD[3]: Сделал зависимость, но не смотрел как удаляется
    	
    	// TODO Закрыть вкладку?
    }

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

	@FXML // TODO http://stackoverflow.com/questions/19160715/javafx-2-tableview-dynamic-column?rq=1
	void clickBAddRowT71(ActionEvent event) {
		olDataOfTableT71.add(new RowT71("", "", ""));
	}

	@FXML
	void clickBDelRowT71(ActionEvent event) {
		int selectedIndex = tvTable71.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) tvTable71.getItems().remove(selectedIndex);
	}

	@FXML
	void clickBSetT71(ActionEvent event) {

	}

	// Контроллеры вкладки "ПКМ"
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

	private void load(Long id_Vers) {
		currWPDVersion = new WPDVersion();

		DAO_WPDVersion dao_Vers = new DAO_WPDVersion();
		currWPDVersion = dao_Vers.getById(new WPDVersion(), id_Vers);

		if (currWPDVersion.getThematicPlan() == null) {
			currThematicPlan = new ThematicPlan(); // При создании
			currWPDVersion.setThematicPlan(currThematicPlan);
			currThematicPlan.setWPDVerion(currWPDVersion);
			DAO_ThematicPlan dao_TP = new DAO_ThematicPlan();
			currThematicPlan.setId(dao_TP.add(currThematicPlan));
		} else {
			currThematicPlan = currWPDVersion.getThematicPlan();
			if (currThematicPlan.getTitle() != null) // Загрузка "Название Дисциплины"
				tfTitleOfThematicPlan.setText(currThematicPlan.getTitle());
			if (currThematicPlan.getDescription() != null) 
				tfDescriptionOfThematicPlan.setText(currThematicPlan.getDescription());
		}

		if (currWPDVersion.getPoCM() == null) {
			currPoCM = new PoCM();
			currWPDVersion.setPoCM(currPoCM);
			currPoCM.setWpdVersion(currWPDVersion);
			DAO_PoCM dao_PoCM = new DAO_PoCM();
			currPoCM.setId(dao_PoCM.add(currPoCM));
		} else {
			currPoCM = currWPDVersion.getPoCM();
			// TODO Загрузка полей во вкладку
		}

		tfVersion.setText(currWPDVersion.getName()); // Name должен всегда существовать
		if (currWPDVersion.getTemplateName() != null) // Грузим шаблон при открытии, но не при создании
			tfPath.setText(currWPDVersion.getTemplateName());

		/* http://stackoverflow.com/questions/21242110/convert-java-util-date-to-java-time-localdate */
		if (currWPDVersion.getDate() != null) {
			Instant instant = currWPDVersion.getDate().toInstant();
			ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault()); // FIXME ZoneId должен быть не стандартным?
			dpDateOfCreate.setValue(zdt.toLocalDate()); // Попробуем достать дату создания
		} else
			dpDateOfCreate.setValue(LocalDate.now());

		//Вывод того, что есть
		System.err.println(currWPDVersion.toString());
		System.err.println(currPoCM.toString());
		System.err.println(currThematicPlan.toString());
	}

	public void setStage(Stage stage2) {
		this.stage = stage2;
	}

	public void setController(FXMLCtrlNewTab fxmlCtrlNewTab) {
		this.fxmlCtrlNewTab = fxmlCtrlNewTab;
	}
	
	public String getTabName() {
		return tabName;
	}

	public void setTabName(String sValue) {
		this.tabName = sValue;
	}

	public void init(Long id_Vers) {
		initT(); // Инициализация
		
		load(id_Vers); // Загрузка полей
		
		mbNumberOfSemesters.setText(tsFNOS.toString());

		bSaveRowPoCM.setDisable(true);
	}

	public void setParentCtrl(FXMLCtrlMain fxmlCtrlMain) {
		this.parentCtrl = fxmlCtrlMain;
	}
}
