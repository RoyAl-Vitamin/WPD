package com.mmsp.logic;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.mmsp.dao.impl.DAO_HandBookDiscipline;
import com.mmsp.dao.impl.DAO_WPDData;
import com.mmsp.dao.impl.DAO_WPDVersion;
import com.mmsp.model.HandbookDiscipline;
import com.mmsp.model.WPDData;
import com.mmsp.model.WPDVersion;
import com.mmsp.util.Ctrl;
import com.mmsp.util.ObservableCtrlArrayList;

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
 * FXMLCtrlMain - класс контроллера главной вкладки
 */

public class FXMLCtrlMain extends VBox {

	private Stage stage; // для FileChooser'а

	private FXMLCtrlMain fxmlCtrlMain; // контроллер данной вкладки

	private final ObservableList<String> olDiscipline = FXCollections.observableArrayList(); // for cbDiscipline

	private final ObservableList<String> olVersion = FXCollections.observableArrayList(); // for cbVersion

	private final ObservableList<Ctrl> olCtrl = new ObservableCtrlArrayList(); // for TabPane // Список текущих открытых вкладок

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

	/**
	 * Нажатие на кнопку авторизации
	 * @param event
	 * @throws IOException
	 */
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
	
	// Функция открытия существующей версии
	@FXML
	void clickBOpenTab(ActionEvent event) throws IOException { // "Открыть" WPDVersion

		//DAO_HandBookDiscipline dao_Disc = new DAO_HandBookDiscipline();
		//Long id_Disc = dao_Disc.getIdByValueAndCode(cbDiscipline.getValue().split(":")[0], Integer.valueOf(cbDiscipline.getValue().split(":")[1]));
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
					Ctrl ctrlTemp = ((ObservableCtrlArrayList) olCtrl).getCtrlById(id_Vers);
					if (ctrlTemp != null)
						olCtrl.remove(ctrlTemp); // удаление из списка olCtrl закрытой вкладки
					else
						System.err.println("Не удалось удалить Ctrl из списка");
					showDialogSave();
				}

				// UNDONE
				private void showDialogSave() {
					// TODO Спросить о сохранении
				}
			});
			tpDiscipline.getTabs().add(t);
			tpDiscipline.getSelectionModel().select(t);
			
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
		return ((ObservableCtrlArrayList) olCtrl).getTabById(id);
	}

	/**
	 * Функция добавления новой версии и открытия для неё вкладки (Tab)
	 * @param event
	 * @throws IOException
	 */
	@FXML
	void clickBAddTab(ActionEvent event) throws IOException { // Кнопка "+" Добавление вкладки в вверхнй TabPane
		Tab t = new Tab();

		DAO_HandBookDiscipline dao_Disc = new DAO_HandBookDiscipline();
		HandbookDiscipline hbD = dao_Disc.getByValueAndCode(cbDiscipline.getValue().split(":")[0], cbDiscipline.getValue().split(":")[1]);

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

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("VersionName.fxml"));
		Parent root = null;
		try {
			root = (Parent) fxmlLoader.load();
		} catch (IOException e) {
			System.err.println("Не удалось загрузить форму ввода имени версии");
			e.printStackTrace();
		}
		Scene scene = new Scene(root);

		Stage stageVersionName = new Stage();
		FXMLCtrlVersionName fxmlCtrlVersionName = fxmlLoader.getController();
		fxmlCtrlVersionName.init(stageVersionName, wpdVers.getId(), hbD.getId());
		stageVersionName.setScene(scene);
		stageVersionName.setTitle("Добавление версии");
		stageVersionName.getIcons().add(new Image("Logo.png"));
		stageVersionName.initModality(Modality.APPLICATION_MODAL);
		stageVersionName.setResizable(false);
		stageVersionName.showAndWait();

		wpdVers.setName(dao_Vers.getById(WPDVersion.class, wpdVers.getId()).getName()); // подгрузим изменнённую в FXMLCtrlDiscipline
		// TODO выше наверное нужна ленивая подгрузка?

		if (wpdVers.getName() == null || wpdVers.getName().equals("")) { // Пользователь передумал вводить/изменять имя версии
			hbD.remVersion(wpdVers); // Удаляем версию из множества версий в HandBookDiscipline
			dao_Vers.remove(wpdVers); // Удаляем версию из БД
			return;
		}

		t.setText(cbDiscipline.getValue().split(":")[0] + ":" + wpdVers.getName());

		fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("NewTab.fxml"));
		root = (Parent) fxmlLoader.load();

		FXMLCtrlNewTab fxmlCtrlNewTab = fxmlLoader.getController();

		fxmlCtrlNewTab.setStage(stage); // Запомним Stage главного окна
		fxmlCtrlNewTab.setTabName(t.getText()); // Начальное название вкладки
		fxmlCtrlNewTab.setController(fxmlCtrlNewTab); // Запомним контроллер главного окна
		fxmlCtrlNewTab.setParentCtrl(fxmlCtrlMain);
		fxmlCtrlNewTab.init(wpdVers.getId()); // инициализируем

		t.setContent(root);
		System.err.println("Ctrl: " + fxmlCtrlNewTab + " version ID == " + wpdVers.getId());
		olCtrl.add(new Ctrl(fxmlCtrlNewTab, wpdVers.getId(), t)); // Добавим контроллер и Id, саму вкладку в список

		t.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				Ctrl ctrlTemp = ((ObservableCtrlArrayList) olCtrl).getCtrlById(wpdVers.getId());
				if (ctrlTemp != null)
					olCtrl.remove(ctrlTemp); // удаление из списка olCtrl закрытой вкладки
				else
					System.err.println("Не удалось удалить Ctrl из списка");
				// TODO Спросить о сохранении
			}
		});
		tpDiscipline.getTabs().add(t);
		tpDiscipline.getSelectionModel().select(t);

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
		return ((ObservableCtrlArrayList) olCtrl).tabIsOpen(id);
	}

	/**
	 * Создание дисциплины
	 * @param event
	 * @throws IOException
	 */
	@FXML
	void clickBCreate(ActionEvent event) throws IOException {

		DAO_HandBookDiscipline dao_HBD = new DAO_HandBookDiscipline();

		HandbookDiscipline hbD = new HandbookDiscipline(); // Если открыл в первый раз
		hbD.setCode("");
		hbD.setValue("");
		hbD.getVersions().clear(); // почистим версии при создании
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
		stageDiscipline.setTitle("Создать дисциплину");
		stageDiscipline.getIcons().add(new Image("Logo.png"));
		stageDiscipline.initModality(Modality.APPLICATION_MODAL);
		stageDiscipline.setResizable(false);
		stageDiscipline.showAndWait();

		hbD = dao_HBD.getById(HandbookDiscipline.class, hbD.getId());
		if ("".equals(hbD.getValue()) && "".equals(hbD.getCode())) // Пользователь передумал создавать дисциплину
			dao_HBD.remove(hbD);
		else {
			olDiscipline.add(hbD.getValue() + ":" + hbD.getCode().toString());

			lvDiscipline.getSelectionModel().selectLast(); // т.к. добавление производится в конец
			if (olDiscipline.size() == 1) cbDiscipline.getSelectionModel().selectLast();
			lStatus.setText("Дисциплина создана");
		}
	}

	/**
	 * Изменение дисциплины
	 * @param event
	 * @throws IOException
	 */
	@FXML
	void clickBChange(ActionEvent event) throws IOException {

		// не меняем индекс у cbDisc, если индексы cbDisc and lvDisc совпадали до начала изменения
		boolean b = false;
		if (lvDiscipline.getSelectionModel().getSelectedIndex() == cbDiscipline.getSelectionModel().getSelectedIndex()) b = true; 

		DAO_HandBookDiscipline dao_HBD = new DAO_HandBookDiscipline();
		String value = lvDiscipline.getSelectionModel().getSelectedItem().split(":")[0];
		String code = lvDiscipline.getSelectionModel().getSelectedItem().split(":")[1];
		HandbookDiscipline hbD = dao_HBD.getByValueAndCode(value, code);
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
		stageDiscipline.setTitle("Изменить дисциплину");
		stageDiscipline.getIcons().add(new Image("Logo.png"));
		stageDiscipline.initModality(Modality.APPLICATION_MODAL);
		stageDiscipline.setResizable(false);
		stageDiscipline.showAndWait();

		System.out.println("Select index = " + lvDiscipline.getSelectionModel().getSelectedIndex());
		hbD = dao_HBD.getById(HandbookDiscipline.class, hbD.getId());
		int pos = lvDiscipline.getSelectionModel().getSelectedIndex();
		String res = olDiscipline.set(pos, hbD.getValue() + ":" + hbD.getCode());
		System.err.println("было " + res + "\nстало " + hbD.getValue() + ":" + hbD.getCode());
		//if ((res == null) || (res.equals(""))) System.err.println("Попытка заменить что-то не понятное на сторку");
		if (b) cbDiscipline.getSelectionModel().select(pos); // меняет занчение в cbDisc булевая переменная
		lStatus.setText("Изменениея сохранены");
	}

	/**
	 * Удаление дисциплины
	 * @param event
	 */
	@FXML
	void clickBDelete(ActionEvent event) {
		// UNDONE Если есть версии, то уточнить у пользователя стоит ли удалять?
		// UPD: Отловить Exception
		String value = lvDiscipline.getSelectionModel().getSelectedItem().split(":")[0];
		String code = lvDiscipline.getSelectionModel().getSelectedItem().split(":")[1];
		DAO_HandBookDiscipline dao_hbd = new DAO_HandBookDiscipline();
		HandbookDiscipline hbD = dao_hbd.getByValueAndCode(value, code); // Обновим содержимое hbD

		/*DAO_WPDVersion dao_Vers = new DAO_WPDVersion();

		List<WPDVersion> lWPDVers = dao_Vers.getAllByNumber(hbD.getId());
		if (!lWPDVers.isEmpty()) {
			System.err.println("Будет удалено " + lWPDVers.size() + " версий данной дисциплины");
			
			// TODO Возможно стоит пересмотреть связь между Handbook'ом и Version???? OneToMany?? Тогда можно будет каскадно удалять
			// UPD: Связь есть, настроить Cascade
			
			for (int i = 0; i < lWPDVers.size(); i++)
				dao_Vers.remove(lWPDVers.get(i));
		}*/

		for (Iterator<WPDVersion> it = hbD.getVersions().iterator(); it.hasNext(); ) { // Если были открыты вкладки (например, когда версия не была заранее удалена), то закроем эти вкладки
			WPDVersion f = it.next();
			closeTab(f.getId());
		}

		dao_hbd.remove(hbD); // удаляем объект из БД // FIXME Удаление дисциплины с созданной версией приводит к Caused by: org.hibernate.StaleStateException: Batch update returned unexpected row count from update [0]; actual row count: 0; expected: 1

		String strWasRemoved = olDiscipline.remove(lvDiscipline.getSelectionModel().getSelectedIndex()); // Удаляем объект из списка

		System.out.println("Удалённая строка = " + strWasRemoved);
		cbDiscipline.getSelectionModel().selectFirst();
		//cbVersion.getSelectionModel().selectFirst();
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
			olDiscipline.add(li.get(i).getValue() + ":" + li.get(i).getCode());
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
			public void onChanged(ListChangeListener.Change<? extends String> change) {
				if (olDiscipline.size() != 0) {
					cbDiscipline.setDisable(false);
					//cbVersion.setDisable(false);
				} else {
					cbDiscipline.setDisable(true);
					cbVersion.setDisable(true);
				}
			}
		});

		olVersion.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends String> change) {
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
				} else {
					bChange.setDisable(true);
					bDelete.setDisable(true);
				}
			}
		});

		lvDiscipline.setItems(olDiscipline);

		cbDiscipline.getSelectionModel().selectedIndexProperty().addListener(
			new ChangeListener<Number>() {
				public void changed (ObservableValue<? extends Number> ov, Number value, Number new_value) {
					if (new_value.intValue() > -1) {
						bAddTab.setDisable(false);
						olVersion.clear();
						String temp_disc = olDiscipline.get(new_value.intValue());
						Long id = dao_disc.getIdByValueAndCode(temp_disc.split(":")[0], temp_disc.split(":")[1]);
						cbDiscipline.getSelectionModel().select(new_value.intValue());
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
				public void changed (ObservableValue<? extends Number> ov, Number value, Number new_value) {
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

		if (olVersion.size() == 0) {
			cbVersion.setDisable(true);
			bOpenTab.setDisable(true);
		}
		if (olDiscipline.size() == 0)
			cbDiscipline.setDisable(true);
	}

	/**
	 * Обновляет список olVersion => cbVersion
	 * @param idHBD ID HandbookDiscipline
	 */
	public void updateOlVersion(Long idHBD) {
		DAO_HandBookDiscipline dao_disc = new DAO_HandBookDiscipline();
		HandbookDiscipline currHBD = dao_disc.getById(HandbookDiscipline.class, idHBD);
		if ((currHBD.getValue() + ":" + currHBD.getCode()).equals(cbDiscipline.getSelectionModel().getSelectedItem())) {
			olVersion.clear();
			for (WPDVersion wpdVers: currHBD.getVersions()) {
				olVersion.add(wpdVers.getName());
			}
			cbVersion.getSelectionModel().selectFirst();
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

		Ctrl t = ((ObservableCtrlArrayList) olCtrl).getCtrlByTabText(oldTabName);
		if (t != null) {
			int i = tpDiscipline.getTabs().indexOf(t.getTab());
			if (i >= 0) {
				String sTemp = oldTabName.split(":")[0] + ":" + newVersName;
				tpDiscipline.getTabs().get(i).setText(sTemp);
				t.getTab().setText(sTemp);
				t.getCtrl().setTabName(sTemp); // костыльно выглядит
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

	/**
	 * Закрывает вкладку, ID версии которой == idVers и удаляем из списка olVersion
	 * @param idVers ID версии
	 */
	public void closeTab(Long idVers) {
		DAO_WPDVersion dao_Vers = new DAO_WPDVersion();
		WPDVersion wpdVers = dao_Vers.getById(WPDVersion.class, idVers); 
		if (wpdVers != null) {
			String sDisc = wpdVers.getHbD().getValue() + ":" + wpdVers.getHbD().getCode(); // Название дисциплины и её код

			Ctrl t = ((ObservableCtrlArrayList) olCtrl).getCtrlById(idVers); // Находим ту самую вкадку

			if (t != null) { // А мб закрыта
				tpDiscipline.getTabs().remove(t.getTab()); // удаляем из списка и удаляем из TabPane
				olCtrl.remove(t);
			} else {
				System.err.println("ERROR: Кажется не смогли найти нужную вкладку");
			}

			if (cbDiscipline.getSelectionModel().getSelectedIndex() == olDiscipline.indexOf(sDisc)) { // Если выбрана та самая дисциплина, то надо удалить из списка версию
				olVersion.remove(wpdVers.getName());
				if ((wpdVers.getHbD().getValue() + ":" + wpdVers.getHbD().getCode()).equals(cbDiscipline.getSelectionModel().getSelectedItem()) // Если эта версия была выбрана в cbDiscipline and cbVersion
						&& wpdVers.getName().equals(cbVersion.getSelectionModel().getSelectedItem())
						) {
					cbVersion.getSelectionModel().selectFirst();
				}
			}
		} else {
			System.err.println("WARNING: WPDVersion с ID == " + idVers + " не был найден");
		}
	}
	
	/**
	 * Задаёт значение Label низу справа окна
	 * @param sValue параметр
	 */
	public void setStatus(String sValue) {
		lStatus.setText(sValue);
	}

}
