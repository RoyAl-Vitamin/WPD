package com.mmsp.logic;

import java.io.IOException;
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
    	// FIXME Сделать адекватное закрытие, это работает, но не верно
    	stage.close();
    }

	@FXML
	void clickBAddTab(ActionEvent event) throws IOException { // Кнопка "+" Добавление вкладки в вверхнй TabPane
		Tab t = new Tab();
		t.setText(cbDiscipline.getValue() + ":" + cbVersion.getValue());
		VBox vbNewTab = new FXMLCtrlNewTab(stage, cbVersion.getValue()); // передача WPDVersion, а не только лишь его названия
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
    	
    	olDiscipline.add(hbD.getValue());
    	
    	DAOImpl dao = new DAOImpl();
    	dao.add(hbD);
    }
	
	@FXML
    void clickBChange(ActionEvent event) throws IOException {
		System.out.println("Select index = " + lvDiscipline.getSelectionModel().getSelectedIndex());
		Stage stageDiscipline = new Stage();
		stageDiscipline.initModality(Modality.APPLICATION_MODAL);
    	Scene sceneDiscipline = new Scene(new FXMLCtrlDiscipline(stageDiscipline));
    	stageDiscipline.setScene(sceneDiscipline);
    	stageDiscipline.setTitle("Change Discipline");
    	stageDiscipline.getIcons().add(new Image("Logo.png"));
    	stageDiscipline.showAndWait();
    	DAO_HandBookDiscipline dao = new DAO_HandBookDiscipline();
    	dao.update(hbD);
    	System.out.println("Select index = " + lvDiscipline.getSelectionModel().getSelectedIndex());
    	String res = olDiscipline.set(lvDiscipline.getSelectionModel().getSelectedIndex(), hbD.getValue());
    	if ((res == null) || (res.equals(""))) System.err.println("Попытка заменить что-то не понятное на сторку");
    }
	
	@FXML
    void clickBDelete(ActionEvent event) {
		System.out.println("Select index = " + lvDiscipline.getSelectionModel().getSelectedIndex());
		//olDiscipline.remove(hbD.getValue()); // FIXME java.lang.ArrayIndexOutOfBoundsException: -1

		String errStr = olDiscipline.remove(lvDiscipline.getSelectionModel().getSelectedIndex()); // FIXME На этом моменте он стреляет мне в ногу // FIXME удаляет только первое вхождение
		System.out.println("Deleted = " + errStr);
		System.out.println("ID = " + hbD.getId() + "\nValue = " + hbD.getValue() + "\nCode = " + hbD.getCode());
		DAO_HandBookDiscipline dao = new DAO_HandBookDiscipline();
		List<HandbookDiscipline> liHBD = dao.getByValue(hbD.getValue());
		for (int i = 0; i < liHBD.size(); i++) {
			System.out.println("ID = " + liHBD.get(i).getId() + "\nValue = " + liHBD.get(i).getValue() + "\nCode = " + liHBD.get(i).getCode());
			dao.remove(liHBD.get(i));
		}
		cbDiscipline.getSelectionModel().selectFirst();
    }

    public FXMLCtrlMain(Stage stage) throws IOException {
    	this.stage = stage;
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Main.fxml")); // подгрузка формы
        
        loader.setController(this); // связь контроллера, т.к. контроллер я не указал в Scene Builder'е
        
        loader.setRoot(this); // установка корня
        
        loader.load(); // загрузка

        // TODO Загрузка из справочников значений Дисциплина и Версия
        DAOImpl dao = new DAOImpl(); // FIXME DAOImpl
        List<HandbookDiscipline> li1 = dao.getAll(new HandbookDiscipline());
        for (int i = 0; i < li1.size(); i++) {
        	String temp = li1.get(i).getValue();
        	olDiscipline.add(temp);
        }
        
        lvDiscipline.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
        	
        	// МБ сделать так же? http://java-buddy.blogspot.ru/2013/05/implement-javafx-listview-for-custom.html
        	
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	//System.out.println("ObservableValue = " + observable.toString());
            	System.out.println("Selected Index = " + lvDiscipline.getSelectionModel().getSelectedIndex());
                //System.out.println("ListView selection changed from oldValue = " + oldValue + " to newValue = " + newValue);
            	if (newValue != null) {
            		bChange.setDisable(false);
            		bDelete.setDisable(false);
            		// FIXME? если дисциплины по названию не могут совпадать
        			hbD.setValue(newValue);
        			DAO_HandBookDiscipline DAO_HBD = new DAO_HandBookDiscipline();
        			List<HandbookDiscipline> li2 = DAO_HBD.getByValue(newValue);
        			System.out.println("Size of list(deleted) = " + li2.size());
        			if (li2.size() != 0) {
        				// FIXME Для предметов с одинаковыми названиями, но разными кодами, можем полиучить список, что в таком случае выгружать?
        				hbD.setCode(li2.get(0).getCode());
        				System.out.println(hbD.getValue() + ":" + hbD.getCode().toString());
        			}
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
					if (new_value.intValue() < 0) new_value = 0; // КАЖИСЬ ПАЧИНИЛ. АА
					System.out.println("ObservableValue ov = " + ov.toString() + "\n Number value = " +  value.intValue()+ "\n Number new_value = " + new_value.intValue());
					if (li1.size() != 0) {
						Long i = li1.get(new_value.intValue()).getId(); // FIXME
						DAO_WPDVersion dao = new DAO_WPDVersion(); 
						List<WPDVersion> liOfWDPVersion = dao.get("FROM WPDVersion WHERE number = " + i); // Вроде работает // Кажется, на этом моменте должно выстрелить в ногу // WPD_VERSION_NUMBER || number?
						for (int j = 0; j < liOfWDPVersion.size(); j++) {
				        	String temp = String.valueOf(liOfWDPVersion.get(j).getDate().getYear());
				        	olVersion.add(temp);
				        }
					}
				}
	        }
        );
        
        // <Test
        olVersion.addAll("2001_Test", "1994_Test", "1997_Test", "2010_Test");
        // Test/>
        
        cbDiscipline.setItems(olDiscipline);
        cbVersion.setItems(olVersion);
        cbDiscipline.setValue(olDiscipline.get(0));
        cbVersion.setValue(olVersion.get(0));
    }
}