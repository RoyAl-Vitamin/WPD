package com.mmsp.logic.newtab;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Вкладка "Общие" для FXMLCtrlNewTab
 * @author rav
 *
 */
public class FXMLCtrlGeneral extends HBox {

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
    
    private final ObservableList<RowSL> olDataOfStudyLoad = FXCollections.observableArrayList();

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

    private Stage stage;

    private FXMLCtrlGeneral fxmlCtrlGeneral;

    private FXMLCtrlNewTab parentCtrl;
    
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

    public void setStage(Stage stage) {
        this.stage= stage;
    }

    public void setController(FXMLCtrlGeneral fxmlCtrlGeneral) {
        this.fxmlCtrlGeneral = fxmlCtrlGeneral;
    }

    public void setParentCtrl(FXMLCtrlNewTab fxmlCtrlCurrTab) {
        this.parentCtrl = fxmlCtrlCurrTab;
    }

    // UNDONE Инициализация компонента номером версии
    public void init(Long id_Vers) {
        
    }
}
