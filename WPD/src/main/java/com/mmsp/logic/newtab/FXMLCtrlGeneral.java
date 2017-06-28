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
import com.mmsp.model.Record;
import com.mmsp.model.WPDVersion;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
        addRowSSVTableMeasures(null);
    }

    @FXML
    /**
     * Удалить строку контрольного мероприятия
     * @param event
     */
    void clickBDeleteRowControlMesaures(ActionEvent event) {
        delRowSSVTableMeasures();
    }

    private void delRowSSVTableMeasures() {
        // получение фокуса
        int col = ssvTMeasures.getSelectionModel().getFocusedCell().getColumn();
        int row = ssvTMeasures.getSelectionModel().getFocusedCell().getRow();

        // удаление из массива записей PoCM
        

        if (!(row > 0 && row < ssvTMeasures.getGrid().getRowCount()))
            return;
        // убрать фокус совсем
        // ssvTable71.getSelectionModel().clearSelection();
        
        // Создадим сетку с -1 строкой
        GridBase newGrid = new GridBase(ssvTMeasures.getGrid().getRowCount() - 1, ssvTMeasures.getGrid().getColumnCount());
        ObservableList<ObservableList<SpreadsheetCell>> newRows = setHeaderForSpreadsheetView(newGrid, "Вид КМ", "Семестр");

        for (int i = 1; i < ssvTMeasures.getGrid().getRowCount(); i++) {
            if (i == row)
                continue; // та строка, которую нужно пропустить
            int k = i;
            if (i > row)
                k--; // перешагиваем i-ую строку
            List<String> lValueOfOldCell = new ArrayList<>();
            for (int j = 0; j < ssvTMeasures.getGrid().getColumnCount(); j++) {
                lValueOfOldCell.add(ssvTMeasures.getGrid().getRows().get(i).get(j).getText());
            }
//            log.debug("lValueOfOldCell");
//            lValueOfOldCell.forEach(log::debug);

            ObservableList<SpreadsheetCell> oldRow = createRowForTTP(k, lValueOfOldCell);
            newRows.add(oldRow);
        }
        newGrid.getColumnHeaders().addAll(ssvTMeasures.getGrid().getColumnHeaders());
        newGrid.setRows(newRows);
        
        ssvTMeasures.setGrid(newGrid);
        newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTMeasures);

        if (row == ssvTMeasures.getGrid().getRowCount()) { // переставим фокус
            // фокус на предыдущую строку, но ту же колонку
            ssvTMeasures.getSelectionModel().focus(row - 1, ssvTMeasures.getColumns().get(col));
        } else {
            // фокус на ту же строку и ту же колонку
            ssvTMeasures.getSelectionModel().focus(row, ssvTMeasures.getColumns().get(col));
        }
        if (ssvTMeasures.getSelectionModel().getFocusedCell().getRow() < 1)
            bDeleteRowControlMesaures.setDisable(true);
    }

    /**
     * Добавляет строку в ssvTableTP в соотвествии с № модуля, раздела, темы
     * @param theme строка из liTheem или null, если хотим заполнить по стандарту
     */
    private void addRowSSVTableMeasures(PoCM pocm) {

        if (pocm == null) {
            pocm = new PoCM();
            wpdVersion.setPoCM(pocm);
            pocm.setWpdVersion(wpdVersion);
        }
        GridBase newGrid = null;
        if (ssvTMeasures.getGrid() != null) {
            newGrid = new GridBase(ssvTMeasures.getGrid().getRowCount() + 1, ssvTMeasures.getGrid().getColumnCount()); // Создадим сетку с +1 строкой
        } else newGrid = new GridBase(1, 11);
        int newRowPos = ssvTMeasures.getGrid().getRowCount(); // и количество строк

        ObservableList<ObservableList<SpreadsheetCell>> newRows = ssvTMeasures.getGrid().getRows(); // а так же существующие строки

        List<String> liValueOfMeasures = new ArrayList<String>();
        liValueOfMeasures.add(String.valueOf(pocm.getName() != null ? pocm.getName() : "")); // Вид КМ
        liValueOfMeasures.add(String.valueOf(pocm.getNumber() != null ? pocm.getNumber() : "")); // № Семестра

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
        ssvTMeasures.getSelectionModel().getSelectedCells().addListener(new InvalidationListener() {

            @Override
            // UNDONE мб это использовать на замену ehT71?
            public void invalidated(Observable o) {
                /*
                 * for(TablePosition<?, ?> cell :
                 * ssvTable71.getSelectionModel().getSelectedCells()){
                 * System.err.println(cell.getRow()+" / "+cell.getColumn()); //
                 * показывает индексы выделенных строк }
                 */
                if (ssvTMeasures.getSelectionModel().getSelectedCells().size() != 0)
                    if (ssvTMeasures.getSelectionModel().getSelectedCells()
                            .get(ssvTMeasures.getSelectionModel().getSelectedCells().size() - 1).getRow() > 0)
                        bDeleteRowControlMesaures.setDisable(false);
                    else
                        bDeleteRowControlMesaures.setDisable(true);
            }
        });

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
    // UNDONE количество колонок не то
    private ObservableList<SpreadsheetCell> createRowForTTP(int posRow, List<String> lValueOfOldCell) {
        ObservableList<SpreadsheetCell> olRow = FXCollections.observableArrayList();
        if (lValueOfOldCell == null) { // Используется для создания новой строки
            for (int column = 0; column < 2; column++) {
                olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1, ""));
            }
        } else { // Используется при удалении строки и переносе значений на строку выше, и вставки строки
            for (int column = 0; column < 2; column++) {
                SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1, lValueOfOldCell.get(column));
//                ((SpreadsheetCellBase) cell).setTooltip(lValueOfOldCell.get(column)); // add tooltip
                olRow.add(cell);
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
