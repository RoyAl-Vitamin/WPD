package com.mmsp.logic.newtab;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

// *************************************************************************************************************************
// *************************************************************************************************************************
// **
// ** Вкладка "Общие"
// **
// *************************************************************************************************************************
// *************************************************************************************************************************

/**
 * Вкладка "Общие" для FXMLCtrlNewTab
 * 
 * @author rav
 *
 */
public class FXMLCtrlGeneral extends HBox {
    
    static final Logger log = LogManager.getLogger(FXMLCtrlGeneral.class);

    @FXML
    private Button bAddRowStudyLoad;

    @FXML
    private Button bDeleteRowStudyLoad;

    @FXML
    private Button bAddRowControlMesaures;

    @FXML
    private Button bDeleteRowControlMesaures;
    
    @FXML
    private VBox vbStudyLoad;

    @FXML
    private VBox vbControlMesaures;

    private Stage stage;

    private FXMLCtrlGeneral fxmlCtrlGeneral;

    private FXMLCtrlNewTab parentCtrl;

    private WPDVersion wpdVersion;

    @FXML
    /**
     * Добавить строку учебной нагрузки
     * @param event
     */
    void clickBAddRowStudyLoad(ActionEvent event) {
    }

    @FXML
    /**
     * Удалить строку учебной нагрузки
     * @param event
     */
    void clickBDeleteRowStudyLoad(ActionEvent event) {
    }
    
    @FXML
    /**
     * Добавить строку контрольного мероприятия
     * @param event
     */
    void clickBAddRowControlMesaures(ActionEvent event) {
    }

    @FXML
    /**
     * Удалить строку контрольного мероприятия
     * @param event
     */
    void clickBDeleteRowControlMesaures(ActionEvent event) {
    }

    /**
     * Инициализация таблицы Учебной нагрузки. Столбцы: Вид учебной нагрузки, Часов, ЗЕ
     */
    private void initTvStudyLoad() {}

    /**
     * Инициализация таблицы Контрольных мероприятий. Столбцы: Вид КМ, Семестр
     */
    private void initTvMeasures() {}

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setController(FXMLCtrlGeneral fxmlCtrlGeneral) {
        this.fxmlCtrlGeneral = fxmlCtrlGeneral;
    }

    public void setParentCtrl(FXMLCtrlNewTab fxmlCtrlCurrTab) {
        this.parentCtrl = fxmlCtrlCurrTab;
    }

    // UNDONE Инициализация компонента версией
    public void init(WPDVersion wpdVers) {
        wpdVersion = wpdVers;
        // Инициализация таблицы Учебной нагрузки. Столбцы: Вид учебной нагрузки, Часов, ЗЕ
        initTvStudyLoad();
        // Инициализация таблицы Контрольных мероприятий. Столбцы: Вид КМ, Семестр
        initTvMeasures();
    }

}
