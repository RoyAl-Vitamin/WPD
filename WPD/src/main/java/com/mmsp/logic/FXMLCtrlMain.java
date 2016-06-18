package com.mmsp.logic;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.controlsfx.dialog.Wizard;

import com.mmsp.dao.impl.DAO_HandBookDiscipline;
import com.mmsp.dao.impl.DAO_WPDData;
import com.mmsp.dao.impl.DAO_WPDVersion;
import com.mmsp.model.HandbookDiscipline;
import com.mmsp.model.WPDData;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert; // не работает, скорее всего связано с Maven
import javafx.scene.control.Alert.AlertType;
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
import javafx.stage.Window;

/**
 * @author Алексей
 * FXMLCtrlMain - класс контроллера и наследник VBox'а
 */

public class FXMLCtrlMain extends VBox {

	private Stage stage; // для FileChooser'а

	private FXMLCtrlMain fxmlCtrlMain; // контроллер данной вкладки

	private HandbookDiscipline hbD;

	private final ObservableList<String> olDiscipline = FXCollections.observableArrayList(); // for cbDiscipline

	private final ObservableList<String> olVersion = FXCollections.observableArrayList(); // for cbVersion

	private final ObservableList<Ctrl> olCtrl = FXCollections.observableArrayList(); // for TabPane // Список текущих открытых вкладок

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
	private Label lStatus; // возможна работа из другого контроллера

	private class Ctrl {
		private FXMLCtrlNewTab ctrl;
		private Long id;
		private Tab t;
		
		public Ctrl(FXMLCtrlNewTab ctrl, Long id, Tab t) {
			this.ctrl = ctrl;
			this.id = id;
			this.t = t;
		}
		
		public FXMLCtrlNewTab getFXMLCtrlNewTab() {
			return ctrl;
		}

		public Long getId() {
			return id;
		}

		public void setFXMLCtrlNewTab(FXMLCtrlNewTab ctrl) {
			this.ctrl = ctrl;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Tab getTab() {
			return t;
		}

		public void setTab(Tab t) {
			this.t = t;
		}
		
	}

	@FXML
	void clickMIAuth(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Auth.fxml"));
		Parent root = null;
		try {
			root = (Parent) fxmlLoader.load();
		} catch (IOException e) {
			System.err.println("Не удалось загрузить форму авторизации");
			e.printStackTrace();
		}
		Scene scene = new Scene(root);

		Stage stageAuth = new Stage();
		FXMLCtrlAuth fxmlCtrlAuth = fxmlLoader.getController();
		fxmlCtrlAuth.init(stageAuth);
		stageAuth.setScene(scene);
		stageAuth.setTitle("Auth");
		stageAuth.getIcons().add(new Image("Logo.png"));
		stageAuth.initModality(Modality.APPLICATION_MODAL);
		stageAuth.setResizable(false);
		stageAuth.showAndWait();
	}

	@FXML
	void clickBClose(ActionEvent event) {
		stage.close();
	}

	/**
	 * Находит по ID нужный Ctrl из olCtrl
	 * @param id_Vers ID дисциплины
	 * @return найденный Ctrl
	 */
	private Ctrl getCtrlById(Long id_Vers) {
		Ctrl t = null;
		for (Ctrl c: olCtrl) {
			if (c.getId() == id_Vers) {
				t = c;
				break;
			}
		}
		return t;
	}
	
	// Функция открытия существующей версии
	@FXML
	void clickBOpenTab(ActionEvent event) throws IOException { // "Открыть" WPDVersion

		DAO_HandBookDiscipline dao_Disc = new DAO_HandBookDiscipline();
		Long id_Disc = dao_Disc.getIdByValueAndCode(cbDiscipline.getValue().split(":")[0], Integer.valueOf(cbDiscipline.getValue().split(":")[1]));
		DAO_WPDVersion dao_Vers = new DAO_WPDVersion();

		Long id_Vers = dao_Vers.getIdByName(cbVersion.getValue()); // TODO Проверить то ли возвращает

		// проверяем открыта ли вкладка
		// Если вкладка не открыта, то откроем
		if (!tabIsOpen(id_Vers)) {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("NewTab.fxml"));
			Parent root = (Parent) fxmlLoader.load();

			Tab t = new Tab();
			t.setText(cbDiscipline.getValue().split(":")[0] + ":" + cbVersion.getValue());
			FXMLCtrlNewTab fxmlCtrlNewTab = fxmlLoader.getController();
			Ctrl ctrlTemp = new Ctrl(fxmlCtrlNewTab, id_Vers, t);

			fxmlCtrlNewTab.setStage(stage); // Запомним Stage главного окна
			fxmlCtrlNewTab.setTabName(t.getText()); // Начальное название вкладки
			fxmlCtrlNewTab.setController(fxmlCtrlNewTab); // Запомним контроллер главного окна
			fxmlCtrlNewTab.setParentCtrl(fxmlCtrlMain);
			fxmlCtrlNewTab.init(id_Vers); // инициализируем

			t.setContent(root);
			t.setOnClosed(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					Ctrl ctrlTemp = getCtrlById(id_Vers);
					if (ctrlTemp != null)
						olCtrl.remove(ctrlTemp); // удаление из списка olCtrl закрытой вкладки
					else
						System.err.println("Не удалось удалить Ctrl из списка");
					// TODO Спросить о сохранении
					showDialogSave();
				}

				private void showDialogSave() {
					// UNDONE					
				}
			});
			tpDiscipline.getTabs().add(t);
			
			olCtrl.add(ctrlTemp); // Добавим контроллер и Id, Tab в список
		} else
		// Если вкладка открыта, найдём её и дадим ей фокус
		{
			// Проверить открыта ли она сейчас?
			tpDiscipline.getSelectionModel().select(getTabById(id_Vers));
		}
	}

	/**
	 * Возвращает вкладку по ID версии
	 * @param id id версии
	 * @return существующая вкладка или null, если вкладка не найдена/не существует
	 */
	private Tab getTabById(Long id) {
		Tab tab = null;
		for (Ctrl c: olCtrl) {
			if (c.getId() == id) {
				tab = c.getTab();
				break;
			}
		}
		return tab;
	}

	// Функция добавления новой версии и открытия для неё вкладки (Tab)
	@FXML
	void clickBAddTab(ActionEvent event) throws IOException { // Кнопка "+" Добавление вкладки в вверхнй TabPane
		Tab t = new Tab();

		DAO_HandBookDiscipline dao_Disc = new DAO_HandBookDiscipline();
		hbD = dao_Disc.getByValueAndCode(cbDiscipline.getValue().split(":")[0], Integer.valueOf(cbDiscipline.getValue().split(":")[1]));

		DAO_WPDVersion dao_Vers = new DAO_WPDVersion();
		WPDVersion wpdVers = new WPDVersion();

		wpdVers.setNumber(hbD.getId()); // Номер версии есть ID Дисциплины
		wpdVers.setDate(Calendar.getInstance().getTime()); // Используем сегодняшнюю дату при создании WPDVerison

		DAO_WPDData dao_WPDData = new DAO_WPDData();
		WPDData wpdData = (dao_WPDData.getAll()).get(0);
		wpdVers.setWPDData(wpdData); // Соединяем WPDVersion с WPDData

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
		stageVersionName.setResizable(false);
		stageVersionName.showAndWait();

		dao_Vers.update(wpdVers); // Обновляем Версию после получения имени версии

		t.setText(cbDiscipline.getValue().split(":")[0] + ":" + wpdVers.getName());

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("NewTab.fxml"));
		Parent root = (Parent) fxmlLoader.load();

		FXMLCtrlNewTab fxmlCtrlNewTab = fxmlLoader.getController();

		fxmlCtrlNewTab.setStage(stage); // Запомним Stage главного окна
		fxmlCtrlNewTab.setTabName(t.getText()); // Начальное название вкладки
		fxmlCtrlNewTab.setController(fxmlCtrlNewTab); // Запомним контроллер главного окна
		fxmlCtrlNewTab.setParentCtrl(fxmlCtrlMain);
		fxmlCtrlNewTab.init(wpdVers.getId()); // инициализируем

		t.setContent(root);
		olCtrl.add(new Ctrl(fxmlCtrlNewTab, wpdVers.getId(), t)); // Добавим контроллер и Id, саму вкладку в список

		t.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				Ctrl ctrlTemp = getCtrlById(wpdVers.getId());
				if (ctrlTemp != null)
					olCtrl.remove(ctrlTemp); // удаление из списка olCtrl закрытой вкладки
				else
					System.err.println("Не удалось удалить Ctrl из списка");
				// TODO Спросить о сохранении
			}
		});
		tpDiscipline.getTabs().add(t);

		olVersion.add(wpdVers.getName());
		
		for (int j = 0; j < tpDiscipline.getTabs().size(); j++) {
			System.err.println(j + ": TAB Name == " + tpDiscipline.getTabs().get(j).getText());
		}

		if (olVersion.size() == 1) cbVersion.getSelectionModel().selectFirst(); // какой-то костыль
	}

	/**
	 * Находит открытую вкладку (для того что бы не пытались открыть ещё одну такую же)
	 * @param id id версии
	 * @return открыта ли вкладка (Y/N)
	 */
	private boolean tabIsOpen(Long id) {
		boolean b = false;
		for (Ctrl c: olCtrl) {
			if (c.getId() == id) {
				b = true;
				break;
			}
		}
		return b;
	}

	@FXML
    void clickBCreate(ActionEvent event) throws IOException { // "Создать" Дисциплину

		DAO_HandBookDiscipline dao_HBD = new DAO_HandBookDiscipline();

		if (hbD == null) hbD = new HandbookDiscipline(); // Если открыл в первый раз
		hbD.setCode(0);
		hbD.setValue("");
		hbD.getVersions().clear(); // почиситм версии при создании
		hbD.setId(dao_HBD.add(hbD));

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Discipline.fxml"));
		Parent root = null;
		try {
			root = (Parent) fxmlLoader.load();
		} catch (IOException e) {
			System.err.println("Не удалось загрузить форму авторизации");
			e.printStackTrace();
		}
		Scene scene = new Scene(root);

		Stage stageDiscipline = new Stage();
		FXMLCtrlDiscipline fxmlCtrlDiscipline = fxmlLoader.getController();
		fxmlCtrlDiscipline.init(stageDiscipline, hbD.getId());
		stageDiscipline.setScene(scene);
		stageDiscipline.setTitle("Discipline");
		stageDiscipline.getIcons().add(new Image("Logo.png"));
		stageDiscipline.initModality(Modality.APPLICATION_MODAL);
		stageDiscipline.setResizable(false);
		stageDiscipline.showAndWait();

		hbD = dao_HBD.getById(HandbookDiscipline.class, hbD.getId());
		olDiscipline.add(hbD.getValue() + ":" + hbD.getCode().toString());

    	lvDiscipline.getSelectionModel().selectLast(); // т.к. добавление производится в конце
    	if (olDiscipline.size() == 1) cbDiscipline.getSelectionModel().selectLast();
    	lStatus.setText("Дисциплина создана");
    }
	
	@FXML
    void clickBChange(ActionEvent event) throws IOException { // Изменить дисциплину

		// не меняем индекс у cbDisc, если индексы cbDisc and lvDisc совпадали до начала изменения
		boolean b = false;
		if (lvDiscipline.getSelectionModel().getSelectedIndex() == cbDiscipline.getSelectionModel().getSelectedIndex()) b = true; 
		
		DAO_HandBookDiscipline dao_HBD = new DAO_HandBookDiscipline();
		hbD = dao_HBD.getByValueAndCode(hbD.getValue(), hbD.getCode());
		if (hbD == null) System.err.println("НЕ НАЙДЕН!!!");
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Discipline.fxml"));
		Parent root = null;
		try {
			root = (Parent) fxmlLoader.load();
		} catch (IOException e) {
			System.err.println("Не удалось загрузить форму авторизации");
			e.printStackTrace();
		}
		Scene scene = new Scene(root);

		Stage stageDiscipline = new Stage();
		FXMLCtrlDiscipline fxmlCtrlDiscipline = fxmlLoader.getController();
		fxmlCtrlDiscipline.init(stageDiscipline, hbD.getId());
		stageDiscipline.setScene(scene);
		stageDiscipline.setTitle("Discipline");
		stageDiscipline.getIcons().add(new Image("Logo.png"));
		stageDiscipline.initModality(Modality.APPLICATION_MODAL);
		stageDiscipline.setResizable(false);
		stageDiscipline.showAndWait();

    	System.out.println("Select index = " + lvDiscipline.getSelectionModel().getSelectedIndex());
    	hbD = dao_HBD.getById(HandbookDiscipline.class, hbD.getId());
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
		
		/*DAO_WPDVersion dao_Vers = new DAO_WPDVersion();

		List<WPDVersion> lWPDVers = dao_Vers.getAllByNumber(hbD.getId());
		if (!lWPDVers.isEmpty()) {
			System.err.println("Будет удалено " + lWPDVers.size() + " версий данной дисциплины");
			
			// FIXME Возможно стоит пересмотреть связь между Handbook'ом и Version???? OneToMany?? Тогда можно будет каскадно удалять
			// UPD: Связь есть, настроить Cascade
			
			for (int i = 0; i < lWPDVers.size(); i++)
				dao_Vers.remove(lWPDVers.get(i));
		}*/

		for (Iterator<WPDVersion> it = hbD.getVersions().iterator(); it.hasNext(); ) {
	        WPDVersion f = it.next();
	        closeTab(f.getId());
	        //for (Ctrl ctrlValue : olCtrl) {
	        /*for (Iterator<Ctrl> ctrlIter = olCtrl.iterator(); ctrlIter.hasNext(); ) {
	        	Ctrl ctrl = ctrlIter.next();
	        	if (f.getId() == ctrl.getId()) {
	        		closeTab(ctrl.getId()); // FIXME нельзя удалять на ходу в списке olCtrl, с этим даже итератор.ремув не справляется
	        		//// Скорее всего это из-за того, что в CloseTab() удаляется элемент из olCtrl с помощью remuve();
	        	}
			}*/
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
	
	public void setController(FXMLCtrlMain fxmlCtrlMain) {
		this.fxmlCtrlMain = fxmlCtrlMain;
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
		List<HandbookDiscipline> li = dao_disc.getAll(HandbookDiscipline.class);
		for (int i = 0; i < li.size(); i++) {
			olDiscipline.add(li.get(i).getValue() + ":" + li.get(i).getCode().intValue());
		}

		/*olCtrl.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(ListChangeListener.Change change) {
				// TODO Возможно стоит сюда перенести создание и удаление новой вкладки?
				System.out.println("Detected a change! ");
				while (change.next()) {
					System.out.println("Was added? " + change.wasAdded());
					System.out.println("Was removed? " + change.wasRemoved());
					System.out.println("Was replaced? " + change.wasReplaced());
					System.out.println("Was permutated? " + change.wasPermutated());
					//if (change.wasRemoved())
					//	olVersion.get(index);
				}
			}
		});*/
		
		olDiscipline.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(ListChangeListener.Change change) {
				if (olDiscipline.size() != 0) {
					cbDiscipline.setDisable(false);
				} else {
					cbDiscipline.setDisable(true);
				}
			}
		});
		
		olVersion.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(ListChangeListener.Change change) {
				if (olVersion.size() != 0) {
					cbVersion.setDisable(false);
				} else {
					cbVersion.setDisable(true);
				}
			}
		});

		lvDiscipline.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			// Подгрузка в hbD дисциплины при смене выделенной строки
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null) {
					bChange.setDisable(false);
					bDelete.setDisable(false);

					DAO_HandBookDiscipline DAO_HBD = new DAO_HandBookDiscipline();
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
						String temp_disc = olDiscipline.get(new_value.intValue());
						Long id = dao_disc.getIdByValueAndCode(temp_disc.split(":")[0], Integer.valueOf(temp_disc.split(":")[1]));
						updateOlVersion(id);
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
			cbVersion.setDisable(true);
			cbDiscipline.setDisable(true);
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
	public void updateOlVersion(Long id) {
		if (id == hbD.getId()) { // То в cbDiscipline содержится та дисциплина, версию которой надо обновить
			DAO_HandBookDiscipline dao_disc = new DAO_HandBookDiscipline();
			HandbookDiscipline currHBD = dao_disc.getById(HandbookDiscipline.class, id);
			olVersion.clear();
			for (WPDVersion wpdVers: currHBD.getVersions()) {
				olVersion.add(wpdVers.getName());
			}
			cbVersion.getSelectionModel().selectFirst();
			//cbVersion.setItems(olVersion);
		}
	}
	
	/**
	 * Обновляет название вкладки. Вызывается из самой вкладки
	 * @param oldTabName старое название вкладки
	 * @param newVersName новое название версии
	 * @return true в случае если смена названия прошла успешно
	 */
	public boolean updateTabName(String oldTabName, String newVersName) {
		boolean b = false;
		Ctrl t = null;
		for (Ctrl ctrlValue : olCtrl) {
			if (ctrlValue.getTab().getText().equals(oldTabName)) {
				t = ctrlValue;
				break;
			}
		}
		if (t != null) {
			int i = tpDiscipline.getTabs().indexOf(t.getTab());
			if (i >= 0) {
				String sTemp = oldTabName.split(":")[0] + ":" + newVersName;
				tpDiscipline.getTabs().get(i).setText(sTemp);
				t.getTab().setText(sTemp);
				t.ctrl.setTabName(sTemp); // костыльно выглядит
				b = true;
			} else {
				System.err.println("ERROR: tab with name == " + oldTabName + " not found, i == " + i);
			}
		} else {
			System.err.println("Вкладка с таким названием не найдена");
		}
		for (int j = 0; j < tpDiscipline.getTabs().size(); j++) {
			System.err.println(j + ": TAB Name == " + tpDiscipline.getTabs().get(j).getText());
		}
		return b;
	}

	public void setStatus(String sValue) {
		lStatus.setText(sValue);
	}

	/**
	 * Закрывает вкладку, ID версии которой == idVers и удаляем из списка olVersion
	 * @param idVers ID версии
	 */
	public void closeTab(Long idVers) {
		DAO_WPDVersion dao_Vers = new DAO_WPDVersion();
		WPDVersion wpdVers = dao_Vers.getById(WPDVersion.class, idVers); // FIXME NPE видимо не правильно сохраняет 
		if (wpdVers != null) {
			String sDisc = wpdVers.getHbD().getValue() + ":" + wpdVers.getHbD().getCode(); // Название дисциплины и её код
			//String sTabName = wpdVers.getHbD().getValue() + ":" + wpdVers.getName(); // Название вкладки

			Ctrl t = getCtrlById(idVers); // Находим ту самую вкадку

			if (t != null) { // А мб закрыта
				tpDiscipline.getTabs().remove(t.getTab()); // удаляем из списка и удаляем из TabPane
				olCtrl.remove(t);
			} else {
				System.err.println("ERROR: Кажется не смогли найти нужную вкладку");
			}

			if (cbDiscipline.getSelectionModel().getSelectedIndex() == olDiscipline.indexOf(sDisc)) { // Если выбрана та самая дисциплина, то надо удалить из списка версию
				olVersion.remove(wpdVers.getName());
			}
		} else {
			System.err.println("WARNING: WPDVersion с ID == " + idVers + " не был найден");
		}
	}

}
