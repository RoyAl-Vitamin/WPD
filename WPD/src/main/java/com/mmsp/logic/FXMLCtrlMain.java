package com.mmsp.logic;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.mmsp.dao.impl.DAOImpl;
import com.mmsp.dao.impl.DAO_HandBookDiscipline;
import com.mmsp.dao.impl.DAO_WPDVersion;
import com.mmsp.model.HandbookDiscipline;
import com.mmsp.model.WPDVersion;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
	
	private final ObservableList<String> olDiscipline = FXCollections.observableArrayList(); // for cbDiscipline
	
	private final ObservableList<String> olVersion = FXCollections.observableArrayList(); // for cbVersion

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
    void clickBOpenTab(ActionEvent event) throws IOException {
    	Tab t = new Tab();
		t.setText(cbDiscipline.getValue().split(":")[0] + ":" + cbVersion.getValue());
		DAO_HandBookDiscipline dao_Disc = new DAO_HandBookDiscipline();
		Long id_Disc = dao_Disc.getId(cbDiscipline.getValue().split(":")[0], Integer.valueOf(cbDiscipline.getValue().split(":")[1]));
		DAO_WPDVersion dao_Vers = new DAO_WPDVersion();
		
		// UNDONE по какому критерию искать РПДВерсию? 
		Long id_Vers = dao_Vers.getId(id_Disc);
		
		VBox vbNewTab = new FXMLCtrlNewTab(stage, id_Disc, id_Vers); // передача WPDVersion, а не только лишь его названия
		vbNewTab.setAlignment(Pos.CENTER);
		t.setContent(vbNewTab);
		tpDiscipline.getTabs().add(t);
    }

	@FXML
	void clickBAddTab(ActionEvent event) throws IOException { // Кнопка "+" Добавление вкладки в вверхнй TabPane
		Tab t = new Tab();
		t.setText(cbDiscipline.getValue().split(":")[0] + ":" + cbVersion.getValue());
		
		DAO_HandBookDiscipline dao_Disc = new DAO_HandBookDiscipline();
		Long id_Disc = dao_Disc.getId(cbDiscipline.getValue().split(":")[0], Integer.valueOf(cbDiscipline.getValue().split(":")[1]));
		
		DAO_WPDVersion dao_Vers = new DAO_WPDVersion();		
		WPDVersion wpdVers = new WPDVersion();
		wpdVers.setNumber(id_Disc);
		// FIXME NPE! Какое-то число 70
		// FIXME Попросить пользователя ввести дату
		wpdVers.setDate(new Date(0));
		dao_Vers.add(wpdVers);
		
		VBox vbNewTab = new FXMLCtrlNewTab(stage, id_Disc, wpdVers.getId());
		vbNewTab.setAlignment(Pos.CENTER);
		t.setContent(vbNewTab);
		tpDiscipline.getTabs().add(t);
    }
	
	@FXML
    void clickBCreate(ActionEvent event) throws IOException {
		hbD.setCode(0);
		hbD.setValue("");
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
	    	DAOImpl dao = new DAOImpl();
	    	dao.add(hbD);
    	} else {
    		System.err.println("Не введено название дисциплины");
    	}

    	lvDiscipline.getSelectionModel().selectLast(); // т.к. добавление производится в конце
    	/*
    	 * Исправлен баг:
    	 * После создания не перефокусировался lvDiscipline, из-за чего возникала ситуация с загрузкой не выделенного элемента, а только что созданного, при этом редактирование производилось для первого
    	 */
    }
	
	@FXML
    void clickBChange(ActionEvent event) throws IOException {
		System.out.println("Select index = " + lvDiscipline.getSelectionModel().getSelectedIndex() + "\nId = " + hbD.getId() + "\nValue = " + hbD.getValue() + "\nCode = " + hbD.getCode()); // посмотрим, что внутри
		
		// не меняем индекс у cbDisc, если индексы cbDisc and lvDisc совпадали до начала изменения
		boolean b = false;
		if (lvDiscipline.getSelectionModel().getSelectedIndex() == cbDiscipline.getSelectionModel().getSelectedIndex()) b = true; 
		
		DAO_HandBookDiscipline dao = new DAO_HandBookDiscipline();
		hbD = dao.get(hbD.getValue(), hbD.getCode());
		if (hbD == null) System.err.println("НЕ НАЙДЕН!!!");
		
		Stage stageDiscipline = new Stage();
		stageDiscipline.initModality(Modality.APPLICATION_MODAL);
    	Scene sceneDiscipline = new Scene(new FXMLCtrlDiscipline(stageDiscipline));

    	stageDiscipline.setScene(sceneDiscipline);
    	stageDiscipline.setTitle("Change Discipline");
    	stageDiscipline.getIcons().add(new Image("Logo.png"));
    	stageDiscipline.showAndWait();

    	/*
    	HandbookDiscipline obj = dao.get(hbD.getValue(), hbD.getCode());
    	System.out.println("Value = " + hbD.getValue() + " Code = " + hbD.getCode()); 
    	System.out.println("Obj.ID = " + obj.getId() + "\nObj.Code = " + obj.getCode() + "\nObj.Value = " + obj.getValue());
    	*/
    	dao.update(hbD);
    	System.out.println("Select index = " + lvDiscipline.getSelectionModel().getSelectedIndex());
    	String res = olDiscipline.set(lvDiscipline.getSelectionModel().getSelectedIndex(), hbD.getValue() + ":" + hbD.getCode());
    	if ((res == null) || (res.equals(""))) System.err.println("Попытка заменить что-то не понятное на сторку");
    	if (b) cbDiscipline.getSelectionModel().select(lvDiscipline.getSelectionModel().getSelectedIndex()); // меняет занчение в cbDisc булевая переменная
    }
	
	@FXML
    void clickBDelete(ActionEvent event) {

		//hbD.setValue(olDiscipline.get(lvDiscipline.getSelectionModel().getSelectedIndex()).split(":")[0]);
		//hbD.setCode(Integer.valueOf(olDiscipline.get(lvDiscipline.getSelectionModel().getSelectedIndex()).split(":")[1]));
		System.out.println("Select index = " + lvDiscipline.getSelectionModel().getSelectedIndex());

		//System.out.println("Индекс выбранного удалённого элемента == " + lvDiscipline.getSelectionModel().getSelectedIndex());
		
		//System.out.println("ID = " + hbD.getId() + "\nValue = " + hbD.getValue() + "\nCode = " + hbD.getCode());
		
		DAO_HandBookDiscipline dao = new DAO_HandBookDiscipline();
		hbD.setId(dao.getId(hbD.getValue(), hbD.getCode())); // FIXME удаление предметов с одинаковыми параметрами
		dao.remove(hbD); // удаляем объект из БД
		
		String strWasRemoved = olDiscipline.remove(lvDiscipline.getSelectionModel().getSelectedIndex()); // Удаляем объект из списка

		System.out.println("Удалённая строка = " + strWasRemoved);
		
		if (hbD.getCode() != null || hbD.getValue() != null) System.out.println("Valut = " + hbD.getValue() + "\nCode = " + hbD.getCode().intValue()); // FIXME java.lang.NullPointerException не понятно когда возникает

		cbDiscipline.getSelectionModel().selectFirst();
		
		// После удаления элемента, подгружаем тот, что находится перед ним, т.к. видимо lvDisc.onChanged не справляется с этим
		/*if (olDiscipline.size() > 0) {
			String temp = olDiscipline.get(lvDiscipline.getSelectionModel().getSelectedIndex());
			System.out.println("Элемент удалён, подгружаем новый cо значениями " + temp);
			hbD = dao.get(temp.split(":")[0], Integer.valueOf(temp.split(":")[1]));
			System.out.println("\nId = " + hbD.getId() + "\nValue = " + hbD.getValue() + "\nCode = " + hbD.getCode());
			System.out.println("Элемент = " + hbD.toString());
		}*/
    }

    public FXMLCtrlMain(Stage stage) throws IOException {
    	this.stage = stage;
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Main.fxml")); // подгрузка формы
        
        loader.setController(this); // связь контроллера, т.к. контроллер я не указал в Scene Builder'е
        
        loader.setRoot(this); // установка корня
        
        loader.load(); // загрузка

        DAO_HandBookDiscipline dao_disc = new DAO_HandBookDiscipline();
        List<HandbookDiscipline> li = dao_disc.getAll(new HandbookDiscipline());
        for (int i = 0; i < li.size(); i++) {
        	olDiscipline.add(li.get(i).getValue() + ":" + li.get(i).getCode().intValue());
        }
        
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
        			hbD = DAO_HBD.get(newValue.split(":")[0], Integer.valueOf(newValue.split(":")[1]));
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
					if (new_value.intValue() < 0) new_value = 0;
					olVersion.clear();
					System.out.println("ObservableValue ov = " + ov.toString() + "\n Number value = " +  value.intValue()+ "\n Number new_value = " + new_value.intValue());
					List<HandbookDiscipline> li1 = dao_disc.getAll(new HandbookDiscipline());
					if (li1.size() != 0) {
						String temp_disc = olDiscipline.get(new_value.intValue());
						Long i = dao_disc.getId(temp_disc.split(":")[0], Integer.valueOf(temp_disc.split(":")[1]));
						DAO_WPDVersion dao_vers = new DAO_WPDVersion(); 
						List<WPDVersion> liOfWDPVersion = dao_vers.get("FROM WPDVersion WHERE number = " + i);
						for (int j = 0; j < liOfWDPVersion.size(); j++) {
							GregorianCalendar calDate = new GregorianCalendar(liOfWDPVersion.get(j).getDate().getYear(), liOfWDPVersion.get(j).getDate().getMonth(), liOfWDPVersion.get(j).getDate().getDay());
				        	String temp = String.valueOf(calDate.get(Calendar.YEAR)); // FIXME вытащить дату (год) String.valueOf(liOfWDPVersion.get(j).getDate().getYear());
				        	olVersion.add(temp);
				        }
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
        
        // <Test // UNDONE Delete
        olVersion.addAll("2001_Test", "1994_Test", "1997_Test", "2010_Test");
        // Test/>
        
        cbDiscipline.setItems(olDiscipline);
        cbVersion.setItems(olVersion);

        cbDiscipline.getSelectionModel().selectFirst();
        cbVersion.getSelectionModel().selectFirst();
    }
}
