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
    	
    	if (!(hbD.getValue().equals(""))) { // проверка на отсутствие введённых данных
    		if (hbD.getCode().intValue() == 0) System.err.println("Код дисциплины == 0, возможно, это была ошибка?");
    		olDiscipline.add(hbD.getValue() + ":" + hbD.getCode().toString());
	    	DAOImpl dao = new DAOImpl();
	    	dao.add(hbD);
    	} else {
    		System.err.println("Не введено название дисциплины");
    	}
    	if ((olDiscipline.size() > 0) && (cbDiscipline.getSelectionModel().getSelectedIndex() == -1)) cbDiscipline.getSelectionModel().select(0); 
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
    	/*
    	 * после удаления дисциплины при наждатии "Изменить" вылетает NPE, т.к. пытается считать из hbD не загруженные в него данные
    	 */
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
		hbD.setId(dao.getId(hbD.getValue(), hbD.getCode()));
		dao.remove(hbD); // удаляем объект из БД
		
		String strWasRemoved = olDiscipline.remove(lvDiscipline.getSelectionModel().getSelectedIndex()); // Удаляем объект из списка

		System.out.println("Удалённая строка = " + strWasRemoved);
		
		if (hbD.getCode() != null || hbD.getValue() != null) System.out.println("Valut = " + hbD.getValue() + "\nCode = " + hbD.getCode().intValue());
		//cbDiscipline.getSelectionModel().selectFirst();
		
		// После удаления элемента, подгружаем тот, что находится перед ним, т.к. видимо lvDisc.onChanged не справляется с этим
		/*if (olDiscipline.size() > 0) {
			String temp = olDiscipline.get(lvDiscipline.getSelectionModel().getSelectedIndex());
			System.out.println("Элемент удалён, подгружаем новый cо значениями " + temp);
			hbD = dao.get(temp.split(":")[0], Integer.valueOf(temp.split(":")[1])); // FIXME java.lang.NullPointerException
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

        DAOImpl dao = new DAOImpl(); // FIXME DAOImpl
        List<HandbookDiscipline> li1 = dao.getAll(new HandbookDiscipline());
        for (int i = 0; i < li1.size(); i++) {
        	olDiscipline.add(li1.get(i).getValue() + ":" + li1.get(i).getCode().intValue());
        }
        
        lvDiscipline.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
        	
        	// МБ сделать так же? http://java-buddy.blogspot.ru/2013/05/implement-javafx-listview-for-custom.html
        	
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
					System.out.println("ObservableValue ov = " + ov.toString() + "\n Number value = " +  value.intValue()+ "\n Number new_value = " + new_value.intValue());
					if (li1.size() != 0) {
						Long i = li1.get(new_value.intValue()).getId(); // FIXME java.lang.IndexOutOfBoundsException: Index: 1, Size: 1
						DAO_WPDVersion dao = new DAO_WPDVersion(); 
						List<WPDVersion> liOfWDPVersion = dao.get("FROM WPDVersion WHERE number = " + i);
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
        if (olDiscipline.size() != 0)
        	cbDiscipline.setValue(olDiscipline.get(0));
        if (olVersion.size() != 0)
        	cbVersion.setValue(olVersion.get(0));
    }
}
