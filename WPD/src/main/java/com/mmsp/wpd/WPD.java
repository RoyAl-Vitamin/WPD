package com.mmsp.wpd;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Label;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.time.LocalDate;

import com.mmsp.logic.Logic;
import com.mmsp.model.PoCM;
import com.mmsp.model.ThematicPlan;

/**
 * WPD - the work program of the discipline
 * @author Алексей
 */

@SuppressWarnings("restriction") // Костыль здесь

public class WPD extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        
        Logic core = new Logic();

        VBox root = new VBox();
        
        MenuBar menuBar = new MenuBar();
        
        Menu mFile = new Menu("Файл"); // пункт меню
        MenuItem mIClose = new MenuItem("Закрыть"); // подпункт меню
        mIClose.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent e) {
        		System.out.println("Выбран выход");
        		primaryStage.close();
        	}
        });
        mFile.getItems().addAll(mIClose); // добавим подпункт в пункт меню
        
        Menu mEdit = new Menu("Редактировать");
        
        Menu mHelp = new Menu("Справка");
        
        menuBar.getMenus().addAll(mFile, mEdit, mHelp); // Собираем меню
        
        Label lDiscipline = new Label("Дисциплина");
        ObservableList<String> oLDiscipline = FXCollections.observableArrayList("1","2","3");
        ChoiceBox<String> cBDiscipline = new ChoiceBox<String>(oLDiscipline);
        cBDiscipline.getSelectionModel().selectFirst();
        cBDiscipline.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
        	
    		public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
	    		if(new_val.equals("1")){
	        		System.out.println("1_1 выбрано");
	    		}
	    		if(new_val.equals("2")){
	    			System.out.println("2_1 выбрано");
	    		}
	    		if(new_val.equals("3")){
	    			System.out.println("3_1 выбрано");
	    		}
    		}
        });
        
        Label lVersion = new Label("Дисциплина");
        ObservableList<String> oLVersion = FXCollections.observableArrayList("1","2","3");
        ChoiceBox<String> cBVersion = new ChoiceBox<String>(oLVersion);
        cBVersion.getSelectionModel().selectFirst();
        cBVersion.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
        	
    		public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
	    		if(new_val.equals("1")){
	        		System.out.println("1_2 выбрано");
	    		}
	    		if(new_val.equals("2")){
	    			System.out.println("2_2 выбрано");
	    		}
	    		if(new_val.equals("3")){
	    			System.out.println("3_2 выбрано");
	    		}
    		}
        });
        
        Button bOpen = new Button("Открыть");
        Button bAdd = new Button("+");
        ToolBar toolBar = new ToolBar(
            lDiscipline,
            cBDiscipline,
            lVersion,
            cBVersion,
            bOpen,
            new Separator(),
            bAdd
        );
        
        Tab tab1 = new Tab("БДиЭС: 2001"); //FIXME изменить имена
        
        GridPane gPTab1 = new GridPane();
        gPTab1.setVgap(10);
        gPTab1.setHgap(10);
        gPTab1.setAlignment(Pos.CENTER);
		gPTab1.setPadding(new Insets(25, 25, 25, 25));
        
        Label lTabVersion = new Label("Версия");
        lTabVersion.setMinWidth(39.0);
        Label lTabTemplate = new Label("Шаблон");
        lTabTemplate.setMinWidth(45.0);
        Label lTabDateOfCreation = new Label("Дата создания");
        lTabDateOfCreation.setMinWidth(80.0);
        Label lTabSemester = new Label("Семестры");
        lTabSemester.setMinWidth(55.0);
        Button bTabChoose = new Button("Выбрать");
        bTabChoose.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent e) {
        		System.out.println(lTabSemester.getWidth());
        	}
        });
        bTabChoose.setMinWidth(63.0);
        TextField tFTabPath = new TextField();
        TextField tFTabSemestr = new TextField();
        TextField tFTabVersion = new TextField();
        tFTabVersion.setEditable(false);
        DatePicker dPTabDate = new DatePicker(LocalDate.now());
        gPTab1.add(lTabVersion, 0, 0, 1, 1);
        gPTab1.add(lTabTemplate, 0, 1, 1, 1);
        gPTab1.add(lTabDateOfCreation, 2, 0, 1, 1);
        gPTab1.add(lTabSemester, 4, 1, 1, 1);
        gPTab1.add(bTabChoose, 3, 1, 1, 1);
        gPTab1.add(tFTabPath, 1, 1, 2, 1);
        gPTab1.add(tFTabSemestr, 5, 1, 1, 1);
        gPTab1.add(tFTabVersion, 1, 0, 1, 1);
        gPTab1.add(dPTabDate, 3, 0, 3, 1);
        
        Button bSave = new Button("Сохранить");
        bSave.setMinWidth(120.0);
        Button bGenerate = new Button("Генерировать");
        bGenerate.setMinWidth(120.0);
        Button bDelete = new Button("Удалить");
        bDelete.setMinWidth(120.0);
        VBox vBforButton = new VBox();
        vBforButton.setSpacing(5.0);
        vBforButton.setPadding(new Insets(10, 10, 10, 10));
        vBforButton.getChildren().addAll(bSave, bGenerate, bDelete);
        
        BorderPane bPGenerate = new BorderPane();
        bPGenerate.setCenter(gPTab1);
        bPGenerate.setRight(vBforButton);
        //HBox hBGenerate = new HBox();
        //hBGenerate.getChildren().addAll(gPTab1, vBforButton);
        
        tab1.setContent(bPGenerate);
        
        Tab tab2 = new Tab("Дисциплины"); //FIXME изменить имена
        
        TabPane tPGenerate = new TabPane();
        tPGenerate.setMinHeight(140);
        tPGenerate.setMaxHeight(140);
        tPGenerate.getTabs().addAll(tab1, tab2);
        
        Tab tab3 = new Tab("Общие"); //FIXME изменить имена
        
        
        VBox vBLeft = new VBox();
        vBLeft.setSpacing(15.0);
        vBLeft.setPadding(new Insets(15, 15, 15, 15));
        Label lStudyLoad = new Label("Учебная нагрузка");
        TableView<ThematicPlan> tVTP = new TableView<ThematicPlan>();
        tVTP.setMinSize(350.0, 150.0);
        tVTP.setMaxSize(640.0, 400.0); //FIXME подогнать размер
        TableColumn tCKindSL = new TableColumn("Вид учебной нагрузки"); // SL - Study Load
        TableColumn tCHours = new TableColumn("Часов");
        TableColumn tCLU = new TableColumn("ЗЕ"); // LU - Ladderpoints Unit
        tVTP.getColumns().addAll(tCKindSL, tCHours, tCLU);
        vBLeft.getChildren().addAll(lStudyLoad, tVTP);
        
        VBox vBRight = new VBox();
        vBRight.setSpacing(15.0);
        vBRight.setPadding(new Insets(5, 5, 10, 10));
        HBox hBAboveTheTable = new HBox();
        hBAboveTheTable.setSpacing(5.0);
        hBAboveTheTable.setPadding(new Insets(15, 2, 5, 5));
        Label lControlMeasures = new Label("Контрольные мероприятия");
        Button bPlus = new Button("+");
        Button bMinus = new Button("-");
        hBAboveTheTable.getChildren().addAll(lControlMeasures, bPlus, bMinus);
        
        TableView<PoCM> tVCM = new TableView<PoCM>();
        tVCM.setMinSize(350.0, 150.0);
        tVCM.setMaxSize(640.0, 400.0); //FIXME подогнать размер
        TableColumn tCKindCM = new TableColumn("Вид КМ");
        TableColumn tCSemestr = new TableColumn("Семестр");
        tVCM.getColumns().addAll(tCKindCM, tCSemestr);
        vBRight.getChildren().addAll(hBAboveTheTable, tVCM);
        
        BorderPane bPCommon = new BorderPane();
        bPCommon.setLeft(vBLeft);
        bPCommon.setRight(vBRight);
        tab3.setContent(bPCommon);
        
        Tab tab4 = new Tab("Тематический план"); //FIXME изменить имена
        Tab tab5 = new Tab("ПКМ"); //FIXME изменить имена

        TabPane tPDescription = new TabPane();
        tPDescription.setMinHeight(100);
        //tPDescription.setMaxHeight(150);
        tPDescription.getTabs().addAll(tab3, tab4, tab5);

        root.getChildren().addAll(menuBar, toolBar, tPGenerate, tPDescription);
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //TODO Проверить на сущесвование пользователя, если его нет - вызвать wAuth.windowAuth("Auth"); 
        //com.mmsp.model.Subject sub = ((com.mmsp.model.Subject) core.getSessionFactory()).load(com.mmsp.model.Subject.class, 1);
        //if () {wAuth.windowAuth("Auth");}
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
