package com.mmsp.logic;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import com.mmsp.dao.impl.DAOImpl;
import com.mmsp.dao.impl.DAO_HandBookDiscipline;
import com.mmsp.dao.impl.DAO_WPDVersion;
import com.mmsp.model.HandbookDiscipline;
import com.mmsp.model.WPDVersion;
import com.mmsp.wpd.WPD;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
//import javafx.scene.control.Alert; // не работает, скорее всего связано с Maven
//import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Алексей
 * FXMLCtrlMain - класс контроллера и наследник VBox'а
 */

public class FXMLCtrlMain extends VBox {

	private Stage stage; // для FileChooser'а
	
	public static HandbookDiscipline hbD = new HandbookDiscipline();
	
	//public static String versionName;
	
	private final ObservableList<String> olDiscipline = FXCollections.observableArrayList(); // for cbDiscipline
	
	private final ObservableList<String> olVersion = FXCollections.observableArrayList(); // for cbVersion
	
	private final ObservableList<String> olTabName = FXCollections.observableArrayList(); // for TabPane

	@FXML
	private ChoiceBox<String> cbDiscipline;

	@FXML
	private ChoiceBox<String> cbVersion;
	
	@FXML
	private Button bOpenTab;
	
	@FXML
	private Button bAddTab;
	
	@FXML
    private Button bCreate;

	@FXML
    private Button bChange;

    @FXML
    private Button bDelete;

	@FXML
	private TabPane tpDiscipline;

	@FXML
    private MenuItem miAuth;
	
    @FXML
    private MenuItem mbClose;
    
    @FXML
    private ListView<String> lvDiscipline;

    @FXML
    public Label lStatus; // возможна работа из другого контроллера
    
	@FXML
    void clickMIAuth(ActionEvent event) throws IOException {
		Stage stageAuth = new Stage();
    	stageAuth.initModality(Modality.APPLICATION_MODAL);
    	Scene sceneAuth = new Scene(new FXMLCtrlAuth(stageAuth));
    	stageAuth.setScene(sceneAuth);
    	stageAuth.setTitle("Auth");
    	stageAuth.getIcons().add(new Image("Logo.png"));
    	stageAuth.showAndWait();
	}
    
    @FXML
    void clickBClose(ActionEvent event) {
    	stage.close();
    }
    
    @FXML
    void clickBOpenTab(ActionEvent event) throws IOException { // "Открыть" WPDVersion
    	Tab t = new Tab();
		t.setText(cbDiscipline.getValue().split(":")[0] + ":" + cbVersion.getValue());
		olTabName.add(t.getText()); // Добавим название таба в список
		for (int j = 0; j < olTabName.size(); j++) {
			System.err.println("WAS ADDED: " + olTabName.get(j));
		}
		System.err.println("AND NOW SIZE olTabName == " + olTabName.size());

		DAO_HandBookDiscipline dao_Disc = new DAO_HandBookDiscipline();
		Long id_Disc = dao_Disc.getIdByValueAndCode(cbDiscipline.getValue().split(":")[0], Integer.valueOf(cbDiscipline.getValue().split(":")[1]));
		DAO_WPDVersion dao_Vers = new DAO_WPDVersion();

		Long id_Vers = dao_Vers.getIdByNumber(id_Disc);
		
		VBox vbNewTab = new FXMLCtrlNewTab(stage, id_Vers, t.getText()); // передача WPDVersion, а не только лишь его названия
		vbNewTab.setAlignment(Pos.CENTER);
		t.setContent(vbNewTab);
		tpDiscipline.getTabs().add(t);
    }

	@FXML
	void clickBAddTab(ActionEvent event) throws IOException { // Кнопка "+" Добавление вкладки в вверхнй TabPane
		Tab t = new Tab();
		
		DAO_HandBookDiscipline dao_Disc = new DAO_HandBookDiscipline();
		hbD = dao_Disc.getByValueAndCode(cbDiscipline.getValue().split(":")[0], Integer.valueOf(cbDiscipline.getValue().split(":")[1]));

		DAO_WPDVersion dao_Vers = new DAO_WPDVersion();		
		WPDVersion wpdVers = new WPDVersion();

		wpdVers.setNumber(hbD.getId()); // Номер версии есть ID Дисциплины
		wpdVers.setDate(Date.valueOf(LocalDate.now())); // Используем сегодняшнюю дату при создании WPDVerison
		wpdVers.setWPDData(WPD.data); // Соединяем WPDVersion с WPDData
		wpdVers.setHbD(hbD); // кажется теперь онеи ссылаются друг на друга
		wpdVers.setId(dao_Vers.add(wpdVers)); // Запоминаем ID, попутно сохранив в БД WPDVerison
		hbD.addVersions(wpdVers); // Обновляем HandBookDiscipline, т.к. теперь у него зависимость с WDPVersion @OneToMany
		dao_Disc.update(hbD);

		Stage stageVersionName = new Stage();
		stageVersionName.initModality(Modality.APPLICATION_MODAL);
		Scene sceneDiscipline = new Scene(new FXMLCtrlVersionName(stageVersionName, wpdVers));
		stageVersionName.setScene(sceneDiscipline);
		stageVersionName.setTitle("Enter name version");
		stageVersionName.getIcons().add(new Image("Logo.png"));
		stageVersionName.showAndWait();

		dao_Vers.update(wpdVers); // Обновляем Версию после получения имени версии

		t.setText(cbDiscipline.getValue().split(":")[0] + ":" + wpdVers.getName());
		olTabName.add(t.getText()); // FIXME проблема описана в changeVersionName()
		for (String sValue : olTabName) {
			System.out.println("была добавлена вкладка: " + sValue + " в список olTabName");
		}
		System.out.println("и теперь размер olTabName == " + olTabName.size());

		VBox vbNewTab = new FXMLCtrlNewTab(stage, wpdVers.getId(), t.getText());
		vbNewTab.setAlignment(Pos.CENTER);

		t.setContent(vbNewTab);
		t.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				olTabName.remove(t.getText()); // удаление из списка olTabName закрытой вкладки
			}
		});
		tpDiscipline.getTabs().add(t);

		olVersion.add(wpdVers.getName());
		
		for (int j = 0; j < tpDiscipline.getTabs().size(); j++) {
			System.err.println(j + ": TAB Name == " + tpDiscipline.getTabs().get(j).getText());
		}
		
		System.err.println("olVers WAS ADDED: " + wpdVers.getName());
		if (olVersion.size() == 1) cbVersion.getSelectionModel().selectFirst(); // какой-то костыль
    }
	
	@FXML
    void clickBCreate(ActionEvent event) throws IOException { // "Создать" Дисциплину
		hbD.setCode(0);
		hbD.setValue("");
		hbD.getVersions().clear(); // почиситм версии при создании
		Stage stageDiscipline = new Stage();
		stageDiscipline.initModality(Modality.APPLICATION_MODAL);
    	Scene sceneDiscipline = new Scene(new FXMLCtrlDiscipline(stageDiscipline));
    	stageDiscipline.setScene(sceneDiscipline);
    	stageDiscipline.setTitle("Create Discipline");
    	stageDiscipline.getIcons().add(new Image("Logo.png"));
    	stageDiscipline.showAndWait();
    	
    	if (!(hbD.getValue().equals(""))) { // проверка на отсутствие введённых данных
    		if (hbD.getCode().intValue() == 0) System.err.println("Код дисциплины == 0, возможно, это была ошибка?");
    		olDiscipline.add(hbD.getValue() + ":" + hbD.getCode().toString());
	    	DAO_HandBookDiscipline dao_Disc = new DAO_HandBookDiscipline();
	    	//dao_Disc.add(hbD);
	    	hbD.setId(dao_Disc.add(hbD)); // Костыльно выглядит =_=
    	} else {
    		System.err.println("Не введено название дисциплины");
    	}

    	lvDiscipline.getSelectionModel().selectLast(); // т.к. добавление производится в конце
    	lStatus.setText("Дисциплина создана");
    }
	
	@FXML
    void clickBChange(ActionEvent event) throws IOException { // Изменить дисциплину

		// не меняем индекс у cbDisc, если индексы cbDisc and lvDisc совпадали до начала изменения
		boolean b = false;
		if (lvDiscipline.getSelectionModel().getSelectedIndex() == cbDiscipline.getSelectionModel().getSelectedIndex()) b = true; 
		
		DAO_HandBookDiscipline dao = new DAO_HandBookDiscipline();
		hbD = dao.getByValueAndCode(hbD.getValue(), hbD.getCode());
		if (hbD == null) System.err.println("НЕ НАЙДЕН!!!");
		
		Stage stageDiscipline = new Stage();
		stageDiscipline.initModality(Modality.APPLICATION_MODAL);
    	Scene sceneDiscipline = new Scene(new FXMLCtrlDiscipline(stageDiscipline));

    	stageDiscipline.setScene(sceneDiscipline);
    	stageDiscipline.setTitle("Change Discipline");
    	stageDiscipline.getIcons().add(new Image("Logo.png"));
    	stageDiscipline.showAndWait();

    	dao.update(hbD);
    	System.out.println("Select index = " + lvDiscipline.getSelectionModel().getSelectedIndex());
    	String res = olDiscipline.set(lvDiscipline.getSelectionModel().getSelectedIndex(), hbD.getValue() + ":" + hbD.getCode());
    	if ((res == null) || (res.equals(""))) System.err.println("Попытка заменить что-то не понятное на сторку");
    	if (b) cbDiscipline.getSelectionModel().select(lvDiscipline.getSelectionModel().getSelectedIndex()); // меняет занчение в cbDisc булевая переменная
    	lStatus.setText("Изменениея сохранены");
    }
	
	@FXML
    void clickBDelete(ActionEvent event) {
		// UNDONE Если есть версии, то уточнить у пользователя стоит ли удалять? UPD: Отловить Exception
		DAO_HandBookDiscipline dao = new DAO_HandBookDiscipline();
		hbD.setId(dao.getIdByValueAndCode(hbD.getValue(), hbD.getCode())); // FIXME Решить проблему: удаление предметов с одинаковыми параметрами или их добавление
		
		DAO_WPDVersion dao_Vers = new DAO_WPDVersion();
		
		List<WPDVersion> lWPDVers = dao_Vers.getAllByNumber(hbD.getId());
		if (!lWPDVers.isEmpty()) {
			System.err.println("Будет удалено " + lWPDVers.size() + " версий данной дисциплины");
			
			// FIXME Возможно стоит пересмотреть связь между Handbook'ом и Version???? OneToMany?? Тогда можно будет каскадно удалять
			// UPD: Связь есть, настроить Cascade
			
			for (int i = 0; i < lWPDVers.size(); i++)
				dao_Vers.remove(lWPDVers.get(i));
		}
		dao.remove(hbD); // удаляем объект из БД
		
		String strWasRemoved = olDiscipline.remove(lvDiscipline.getSelectionModel().getSelectedIndex()); // Удаляем объект из списка

		System.out.println("Удалённая строка = " + strWasRemoved);
		cbDiscipline.getSelectionModel().selectFirst();
		lStatus.setText("Дисциплина удалена");
    }
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	@FXML
    void initialize() {
        assert miAuth != null : "fx:id=\"miAuth\" was not injected: check your FXML file 'Main.fxml'.";
        assert cbDiscipline != null : "fx:id=\"cbDiscipline\" was not injected: check your FXML file 'Main.fxml'.";
        assert lvDiscipline != null : "fx:id=\"lvDiscipline\" was not injected: check your FXML file 'Main.fxml'.";
        assert bCreate != null : "fx:id=\"bCreate\" was not injected: check your FXML file 'Main.fxml'.";
        assert bDelete != null : "fx:id=\"bDelete\" was not injected: check your FXML file 'Main.fxml'.";
        assert mbClose != null : "fx:id=\"mbClose\" was not injected: check your FXML file 'Main.fxml'.";
        assert tpDiscipline != null : "fx:id=\"tpDiscipline\" was not injected: check your FXML file 'Main.fxml'.";
        assert bOpenTab != null : "fx:id=\"bOpenTab\" was not injected: check your FXML file 'Main.fxml'.";
        assert bAddTab != null : "fx:id=\"bAddTab\" was not injected: check your FXML file 'Main.fxml'.";
        assert bChange != null : "fx:id=\"bChange\" was not injected: check your FXML file 'Main.fxml'.";
        assert lStatus != null : "fx:id=\"lStatus\" was not injected: check your FXML file 'Main.fxml'.";
        assert cbVersion != null : "fx:id=\"cbVersion\" was not injected: check your FXML file 'Main.fxml'.";

		DAO_HandBookDiscipline dao_disc = new DAO_HandBookDiscipline();
		List<HandbookDiscipline> li = dao_disc.getAll(new HandbookDiscipline());
		for (int i = 0; i < li.size(); i++) {
			olDiscipline.add(li.get(i).getValue() + ":" + li.get(i).getCode().intValue());
		}

		olTabName.add("Дисциплины");
		olTabName.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(ListChangeListener.Change change) {
				// TODO Возможно стоит сюда перенести создание и удаление новой вкладки?
				/*System.out.println("Detected a change! ");
				while (change.next()) {
					System.out.println("Was added? " + change.wasAdded());
					System.out.println("Was removed? " + change.wasRemoved());
					System.out.println("Was replaced? " + change.wasReplaced());
					System.out.println("Was permutated? " + change.wasPermutated());
					//if (change.wasRemoved())
					//	olVersion.get(index);
				}*/
			}
		});

		lvDiscipline.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			// Подгрузка в hbD дисциплины при смене выделенной строки
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				System.out.println("Selected Index = " + lvDiscipline.getSelectionModel().getSelectedIndex());
				if (newValue != null) {
					bChange.setDisable(false);
					bDelete.setDisable(false);

					DAO_HandBookDiscipline DAO_HBD = new DAO_HandBookDiscipline();
					System.out.println("NEWVALUE = " + newValue);
					hbD = DAO_HBD.getByValueAndCode(newValue.split(":")[0], Integer.valueOf(newValue.split(":")[1]));
				} else {
					bChange.setDisable(true);
					bDelete.setDisable(true);
				}
			}
		});

		lvDiscipline.setItems(olDiscipline);

		cbDiscipline.getSelectionModel().selectedIndexProperty().addListener(
			new ChangeListener<Number>() {
				public void changed (ObservableValue ov, Number value, Number new_value) {
					if (new_value.intValue() > -1) {
						bAddTab.setDisable(false);
						olVersion.clear();
						updateOlVersion(olDiscipline.get(new_value.intValue()));
						//System.out.println("ObservableValue ov = " + ov.toString() + "\n Number value = " +  value.intValue()+ "\n Number new_value = " + new_value.intValue());
						/*
						List<HandbookDiscipline> li1 = dao_disc.getAll(new HandbookDiscipline());
						if (li1.size() != 0) {
							String temp_disc = olDiscipline.get(new_value.intValue());
							Long i = dao_disc.getIdByValueAndCode(temp_disc.split(":")[0], Integer.valueOf(temp_disc.split(":")[1]));
							DAO_WPDVersion dao_vers = new DAO_WPDVersion(); 
							List<WPDVersion> liOfWDPVersion = dao_vers.run("FROM WPDVersion WHERE number = " + i);
							for (int j = 0; j < liOfWDPVersion.size(); j++) {
								// FIXME on UpdateOlVersion()

								GregorianCalendar calDate = new GregorianCalendar(liOfWDPVersion.get(j).getDate().getYear(), liOfWDPVersion.get(j).getDate().getMonth(), liOfWDPVersion.get(j).getDate().getDay());
					        	String temp = String.valueOf(calDate.get(Calendar.YEAR)); // FIXME вытащить дату (год) String.valueOf(liOfWDPVersion.get(j).getDate().getYear());
					        	olVersion.add(temp);
					        }
						}
						*/
					} else { // если пустое поле
						bAddTab.setDisable(true);
						bOpenTab.setDisable(true);
						olVersion.clear();
					}
				}
			}
		);

		cbVersion.getSelectionModel().selectedIndexProperty().addListener(
			new ChangeListener<Number>() {
				public void changed (ObservableValue ov, Number value, Number new_value) {
					if (new_value.intValue() < 0)
						bOpenTab.setDisable(true);
					else
						bOpenTab.setDisable(false);
				}
			}
		);
		cbDiscipline.setItems(olDiscipline);
		cbVersion.setItems(olVersion);
		if (olDiscipline.isEmpty()) {
			bAddTab.setDisable(true);
			if (olVersion.isEmpty())
				bOpenTab.setDisable(true);
		}

		cbDiscipline.getSelectionModel().selectFirst();
		cbVersion.getSelectionModel().selectFirst();
    }

	/**
	 * Обновляет список olVersion => cbVersion
	 * @param sValue строка из cbDiscipline, если вызван во время переключения cbDiscipline, и null, если вызван из другого контроллера 
	 */
	public void updateOlVersion(String sValue) {
		String selectedItem = null;
		if (sValue != null) { // Если не null, то вызван из cbDiscipline.Listener
			selectedItem = sValue;
			System.out.println("Метод был вызван из этого контроллера");
		} else { // Если передан null, то обновление идёт из вкладки
			selectedItem = cbDiscipline.getSelectionModel().getSelectedItem();
			System.out.println("Метод был вызван из другого контроллера");
		}
		//if (cbDiscipline.getSelectionModel().getSelectedIndex() > -1) { // т.е. элемент выбранный есть //FIXME Проверить, а нужно ли здесь условие
			// FIXME если другая дисциплина загружена в cbDiscipline?
			DAO_HandBookDiscipline dao_disc = new DAO_HandBookDiscipline();
			//if (selectedItem != null) {
			HandbookDiscipline currHBD = dao_disc.getByValueAndCode(selectedItem.split(":")[0], Integer.valueOf(selectedItem.split(":")[1]));
			olVersion.clear();
			for (WPDVersion wpdVers: currHBD.getVersions()) {
				olVersion.add(wpdVers.getName());
			}
			cbVersion.getSelectionModel().select(-1);
			/*} else {
				System.err.println("ERROR: selectedItem == " + selectedItem);
			}*/
			cbVersion.setItems(olVersion);
			//System.out.println("СМЕНИЛАСЬ ВЕРСИЯ В cbVersion? кажись нет?");
			System.out.println("Я знаю, что есть вот такие пункты в компоненте cbVersion:");
			for (String sValueVers: cbVersion.getItems()) {
				System.out.println(sValueVers);
			}
			System.out.println("\nЯ знаю, что есть вот такие пункты в спсике olVersion:");
			for (String sValueVers: olVersion) {
				System.out.println(sValueVers);
			}
			
			System.out.println("\nЗаглянем во вкладки, которые имеются в компоненте tpDiscipline:");
			for (Tab tValue: tpDiscipline.getTabs()) {
				System.out.println(tValue.getText());
			}
			
			System.out.println("\nЗаглянем во вкладки, которые имеются в списке olTabName:");
			for (String tValue: olTabName) {
				System.out.println(tValue);
			}
			System.out.println("\nПросто взгялните, какие вкладки открыты T_T, количество вкладок не совпадёт, если этот метод был вызван из другого контроллера");
		/*} else {
			System.err.println("ERROR: olVersion не содержит выбранного элемента");
		}*/
	}
	
	/**
	 * Обновляет название вкладки. Вызывается из самой вкладки
	 * @param oldTabName старое название вкладки
	 * @param newVersName новое название версии
	 */
	public void updateTabName(String oldTabName, String newVersName) {
		int i = tpDiscipline.getTabs().indexOf(oldTabName);
		if (i > 0) {
			tpDiscipline.getTabs().get(i).setText(oldTabName.split(":")[0] + ":" + newVersName);
			System.out.println("СМЕНИЛАСЬ ВЕРСИЯ В Tab?");
		} else {
			System.err.println("ERROR: tab with name == " + oldTabName + " not found");
		}
		for (int j = 0; j < tpDiscipline.getTabs().size(); j++) {
			System.err.println(j + ": TAB Name == " + tpDiscipline.getTabs().get(j).getText());
		}
	}
}
