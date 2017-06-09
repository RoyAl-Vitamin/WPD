package com.mmsp.logic.newtab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import com.mmsp.model.PoCM;
import com.mmsp.model.WPDVersion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    private EventHandler<GridChange> ehTStudyLoad; // OnGridChange for ssvTStudyLoad

    private SpreadsheetView ssvTStudyLoad; // Таблица
    
    private EventHandler<GridChange> ehTMeasures; // OnGridChange for ssvTStudyLoad

    private SpreadsheetView ssvTMeasures; // Таблица

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
     * Добавляет строку в ssvTableTP в соотвествии с № модуля, раздела, темы
     * @param theme строка из liTheem или null, если хотим заполнить по стандарту
     */
    private void addRowSSVTableMeasures(PoCM pocm) {

        GridBase newGrid = null;
        if (ssvTMeasures.getGrid() != null) {
            newGrid = new GridBase(ssvTMeasures.getGrid().getRowCount() + 1, ssvTMeasures.getGrid().getColumnCount()); // Создадим сетку с +1 строкой
        } else newGrid = new GridBase(1, 11);
        int newRowPos = ssvTMeasures.getGrid().getRowCount(); // и количество строк

        ObservableList<ObservableList<SpreadsheetCell>> newRows = ssvTMeasures.getGrid().getRows(); // а так же существующие строки

        List<String> liValueOfMeasures = new ArrayList<String>();
        liValueOfMeasures.add(String.valueOf(pocm.getName())); // Вид КМ
        liValueOfMeasures.add(String.valueOf(pocm.getNumber())); // № Семестра

//        liValueOfMeasures.forEach(log::info);

        final ObservableList<SpreadsheetCell> olNew = createRowForTTP(newRowPos, liValueOfMeasures); // Добавление на место последней строки пустой строки
        newRows.add(olNew);

        newGrid.setRows(newRows);
        newGrid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight(newGrid.getRowCount())));
        newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTMeasures);
        ssvTMeasures.setGrid(newGrid);
    }

    /**
     * Задаёт header всей таблицы ssvTableTP
     * @param grid BaseGrid этой таблицы
     * @return первую строку
     */
    private ObservableList<ObservableList<SpreadsheetCell>> setHeaderForSpreadsheetView(GridBase grid, String... sHeader) {
        ObservableList<ObservableList<SpreadsheetCell>> rowsHeader = FXCollections.observableArrayList();
        
        // 1-ая строка
        final ObservableList<SpreadsheetCell> olHeader = FXCollections.observableArrayList();
        for (int i = 0; i < sHeader.length; i++) {
            olHeader.add(SpreadsheetCellType.STRING.createCell(0, i, 1, 1, sHeader[i])); // Принадлежность к модулю
        }

        // запрещает редактирование
        for (SpreadsheetCell cell : olHeader) {
            cell.setEditable(false);
        }
        rowsHeader.add(olHeader); // первая строка заполнена
        return rowsHeader;
    }

    /**
     * Инициализация таблицы Учебной нагрузки. Столбцы: Вид учебной нагрузки, Часов, ЗЕ
     */
    private void initTvStudyLoad() {

        ehTStudyLoad = new EventHandler<GridChange>() {
            public void handle(GridChange change) {
                log.debug("TEST");
            }
        };
        int rowCount = 1;
        int columnCount = 11;
        GridBase grid = new GridBase(rowCount, columnCount);

        //GridBase.MapBasedRowHeightFactory rowHeightFactory = new GridBase.MapBasedRowHeightFactory(generateRowHeight(1));
        //grid.setRowHeightCallback(rowHeightFactory);
        grid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight(1)));
        ObservableList<ObservableList<SpreadsheetCell>> rows = setHeaderForSpreadsheetView(grid, "Вид учебной нагрузки", "Часов", "ЗЕ");
        grid.setRows(rows);
        grid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTStudyLoad);
        ssvTStudyLoad = new SpreadsheetView(grid);
        ssvTStudyLoad.getStylesheets().add(getClass().getResource("/SpreadSheetView.css").toExternalForm());
        ssvTStudyLoad.setShowRowHeader(true);
        ssvTStudyLoad.setShowColumnHeader(true);

        // FIXME USE MasterDetailPane
        vbStudyLoad.getChildren().add(ssvTStudyLoad);
        VBox.setVgrow(ssvTStudyLoad, Priority.ALWAYS);
        VBox.setMargin(ssvTStudyLoad, new Insets(15, 0, 15, 0));
    }

    /**
     * Инициализация таблицы Контрольных мероприятий. Столбцы: Вид КМ, Семестр
     */
    private void initTvMeasures() {
        ehTMeasures = new EventHandler<GridChange>() {
            public void handle(GridChange change) {
                log.debug("TEST");
            }
        };
        int rowCount = 1;
        int columnCount = 11;
        GridBase grid = new GridBase(rowCount, columnCount);

        //GridBase.MapBasedRowHeightFactory rowHeightFactory = new GridBase.MapBasedRowHeightFactory(generateRowHeight(1));
        //grid.setRowHeightCallback(rowHeightFactory);
        grid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight(1)));
        ObservableList<ObservableList<SpreadsheetCell>> rows = setHeaderForSpreadsheetView(grid, "Вид КМ", "Семестр");
        grid.setRows(rows);
        grid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTMeasures);
        ssvTMeasures = new SpreadsheetView(grid);
        ssvTMeasures.getStylesheets().add(getClass().getResource("/SpreadSheetView.css").toExternalForm());
        ssvTMeasures.setShowRowHeader(true);
        ssvTMeasures.setShowColumnHeader(true);

        // FIXME USE MasterDetailPane
        vbControlMesaures.getChildren().add(ssvTMeasures);
        VBox.setVgrow(ssvTMeasures, Priority.ALWAYS);
        VBox.setMargin(ssvTMeasures, new Insets(15, 0, 15, 0));
    }

    /**
     * Создаёт строку для указанной позиции
     * @param posRow позиция для новой строки
     * @param lValueOfOldCell список значений ячеек.
     * @return строку
     */
    private ObservableList<SpreadsheetCell> createRowForTTP(int posRow, List<String> lValueOfOldCell) {
        ObservableList<SpreadsheetCell> olRow = FXCollections.observableArrayList();
        if (lValueOfOldCell == null) { // Используется для создания новой строки
            for (int column = 0; column < 4; column++) {
                SpreadsheetCell ssC = SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1, 0);
                ssC.setEditable(false);
                olRow.add(ssC);
            }
            for (int column = 4; column < 6; column++) {
                olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1, ""));
            }
            for (int column = 6; column < ssvTStudyLoad.getGrid().getColumnCount(); column++) {
                olRow.add(SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1, 0));
            }
        } else { // Используется при удалении строки и переносе значений на строку выше, и вставки строки
            for (int column = 0; column < 4; column++) {
                int temp = 0;
                try {
                    temp = Integer.parseInt(lValueOfOldCell.get(column));
                } catch (NumberFormatException | NullPointerException e) {
                    temp = 0;
                }
                
                SpreadsheetCell ssC = SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1, temp);
                ssC.setEditable(false);
                olRow.add(ssC);
            }
            for (int column = 4; column < 6; column++) {
                SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1, lValueOfOldCell.get(column));
                ((SpreadsheetCellBase) cell).setTooltip(lValueOfOldCell.get(column)); // add tooltip
                olRow.add(cell);
            }
            for (int column = 6; column < ssvTStudyLoad.getGrid().getColumnCount(); column++) {
                int temp = 0;
                try {
                    temp = Integer.parseInt(lValueOfOldCell.get(column));
                } catch (NumberFormatException | NullPointerException e) {
                    temp = 0;
                }
                olRow.add(SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1, temp));
            }
        }
        return olRow;
    }

    /**
     * Specify a custom row height.
     * @return Map
     */
    private Map<Integer, Double> generateRowHeight(int val) {
        Map<Integer, Double> rowHeight = new HashMap<>();
        rowHeight.put(0, 24.0); // For Header
        for (int i = 1; i < val; i++) { // for other rows
            rowHeight.put(i, 35.0);
        }
        return rowHeight;
    }

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
