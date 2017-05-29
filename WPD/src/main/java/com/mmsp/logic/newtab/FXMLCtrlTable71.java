package com.mmsp.logic.newtab;

import java.util.ArrayList;

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import com.mmsp.model.Record;
import com.mmsp.model.Semester;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Переменные вкладки "Таблица 7.1"
public class FXMLCtrlTable71 extends HBox {

    @FXML
    private VBox vbT71;

    @FXML
    // кнопка добавления строки в текущий семестр
    private Button bAddRowT71;

    @FXML
    // кнопка удаления текущей строки из текущего семестра
    private Button bDelRowT71;

    // for cbSemester список #семестров
    private final ObservableList<String> olSemesters = FXCollections.observableArrayList();

    @FXML
    private ChoiceBox<String> cbSemesters;

    private Semester currSemester; // текущий семестр

    // https://bitbucket.org/panemu/tiwulfx
    // vs.
    // https://bitbucket.org/controlsfx/controlsfx
    private SpreadsheetView ssvTable71; // Замена TableView<?> tvTable71;

    private EventHandler<GridChange> ehT71; // OnGridChange for TableTP

    private FXMLCtrlTable71 fxmlCtrlTable71;

    private Stage stage;

    private FXMLCtrlNewTab parentCtrl;
    
    /**
     * Работает по нажатию кнопки "Сохранить" на главной форме 
     */
    public void save() {
        
    }
    
    /**
     * Создаёт строку общего типа для указанной позиции
     * @param posRow позиция для новой строки
     * @param lValueOfOldCell список значений ячеек. Обязательно String
     * @return строку общего типа
     */
    private ObservableList<SpreadsheetCell> createStringRow(int posRow, ArrayList<String> lValueOfOldCell) {
        ObservableList<SpreadsheetCell> olRow = FXCollections.observableArrayList();
        if (lValueOfOldCell == null) {
            for (int column = 0; column < ssvTable71.getGrid().getColumnCount(); column++) {
                olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1,""));
            }
        } else
            try {
                if (ssvTable71.getGrid().getColumnCount() != lValueOfOldCell.size())                
                    throw new Exception("Количество столбцов не совпадает с размером переданного списка");

                for (int column = 0; column < ssvTable71.getGrid().getColumnCount(); column++) {
                    olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1, lValueOfOldCell.get(column)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        
        olRow.get(olRow.size() - 1).setEditable(false); // Последняя строка не доступна для редактирования
        return olRow;
    }

    /**
     * Добавляет строку общего типа в конец таблицы
     * @param event
     */
    @FXML
    void clickBAddRowT71(ActionEvent event)
    // https://bitbucket.org/controlsfx/controlsfx/issues/590/adding-new-rows-to-a-spreadsheetview
    // https://bitbucket.org/controlsfx/controlsfx/issues/151/dynamic-adding-rows-in-spreadsheetview-at
    {
        Record rec = new Record(currSemester.getQUANTITY_OF_WEEK(), ssvTable71.getGrid().getRowCount());
        currSemester.getRowT71().add(rec);
//        addRowT71(null); // Создадим пусую строку
    }

    /**
     * Удаляет строку в которой была выделена клетка
     * @param event
     */
    @FXML
    void clickBDelRowT71(ActionEvent event) {
        int col = ssvTable71.getSelectionModel().getFocusedCell().getColumn(); // получение фокуса
        int row = ssvTable71.getSelectionModel().getFocusedCell().getRow();

        for (Record rec : currSemester.getRowT71()) { // удаление из массива записей
            if (rec.getPos() == row) {
                currSemester.getRowT71().remove(rec);
                break;
            }
        }

        if (!(row > 3 && row < ssvTable71.getGrid().getRowCount())) return; 
        //ssvTable71.getSelectionModel().clearSelection(); // убрать фокус совсем
        GridBase newGrid = new GridBase(ssvTable71.getGrid().getRowCount() - 1, ssvTable71.getGrid().getColumnCount()); // Создадим сетку с -1 строкой
        ObservableList<ObservableList<SpreadsheetCell>> newRows = setHeaderForT71(newGrid, currSemester.getQUANTITY_OF_WEEK()); // по новой инициализируем хедер таблицы
        
        for (int i = 4; i < ssvTable71.getGrid().getRowCount(); i++) {
            if (i == row) continue; // та строка, которую нужно пропустить
            int k = i;
            if (i > row) k--; // перешагиваем i-ую строку для createStringRow() 
            ArrayList<String> lValueOfOldCell = new ArrayList<>();
            for (int j = 0; j < ssvTable71.getGrid().getColumnCount(); j++) {
                lValueOfOldCell.add(ssvTable71.getGrid().getRows().get(i).get(j).getText());
            }
            ObservableList<SpreadsheetCell> oldRow = createStringRow(k, lValueOfOldCell);
//            newRows.add(oldRow);
        }
        newGrid.getColumnHeaders().addAll(ssvTable71.getGrid().getColumnHeaders());
//        newGrid.setRows(newRows);
        newGrid.spanColumn(newGrid.getColumnCount() - 2, 0, 1); // объединение "Распределение по учебным неделям"
        newGrid.spanRow(2, 0, 0); // объединение "Виды работ"
        newGrid.spanRow(2, 0, newGrid.getColumnCount() - 1); // объединение "Итого"
        newGrid.spanColumn(currSemester.getQUANTITY_OF_WEEK(), 2, 1); // объединение "M1"
        newGrid.spanColumn(currSemester.getQUANTITY_OF_WEEK(), 3, 1); // объединение "P1"

        ssvTable71.setGrid(newGrid);
        newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehT71);
        
        if (row == ssvTable71.getGrid().getRowCount()) { // переставим фокус
            ssvTable71.getSelectionModel().focus(row - 1, ssvTable71.getColumns().get(col)); // фокус на предыдущую строку, но ту же колонку
        } else {
            ssvTable71.getSelectionModel().focus(row, ssvTable71.getColumns().get(col)); // фокус на ту же строку и ту же колонку
        }
        if (ssvTable71.getSelectionModel().getFocusedCell().getRow() < 4)
            bDelRowT71.setDisable(true);
    }
    
    /**
     * Задаёт header всей таблицы ssvTable71
     * @param grid BaseGrid этой таблицы
     * @param length количество недель
     * @return первые 4 строки
     */
    private ObservableList<ObservableList<SpreadsheetCell>> setHeaderForT71(GridBase grid, int length) {
        
        ObservableList<ObservableList<SpreadsheetCell>> rowsHeader = FXCollections.observableArrayList();
        
        // 1-ая строка
        final ObservableList<SpreadsheetCell> lh1 = FXCollections.observableArrayList();
        lh1.add(SpreadsheetCellType.STRING.createCell(0, 0, 1, 1,"Виды работ"));
        lh1.get(0).getStyleClass().add("span");
        lh1.add(SpreadsheetCellType.STRING.createCell(0, 1, 1, 1,"Распределение по учебным неделям"));
        lh1.get(1).getStyleClass().add("span");
        for (int i = 2; i < grid.getColumnCount() - 1; i++) {
            SpreadsheetCell ssc = SpreadsheetCellType.STRING.createCell(0, i, 1, 1,"");
            lh1.add(ssc);
        }
        lh1.add(SpreadsheetCellType.STRING.createCell(0, length + 1, 1, 1,"Итого"));
        lh1.get(length + 1).getStyleClass().add("span");
        rowsHeader.add(lh1); // первая строка заполнена

        // 2-ая строка
        final ObservableList<SpreadsheetCell> lh2 = FXCollections.observableArrayList();
        lh2.add(SpreadsheetCellType.STRING.createCell(1, 0, 1, 1,""));
        for (int column = 0; column < grid.getColumnCount() - 2; column++) {
            lh2.add(SpreadsheetCellType.INTEGER.createCell(1, column + 1, 1, 1, column + 1));
        }
        lh2.add(SpreadsheetCellType.STRING.createCell(1, length + 1, 1, 1,""));
        rowsHeader.add(lh2);

        // 3-ая строка
        final ObservableList<SpreadsheetCell> lh3 = FXCollections.observableArrayList();
        lh3.add(SpreadsheetCellType.STRING.createCell(2, 0, 1, 1,"Модули"));
        for (int column = 1; column < grid.getColumnCount(); column++) {
            lh3.add(SpreadsheetCellType.STRING.createCell(2, column, 1, 1, ""));
        }
        lh3.get(1).setItem("М1");
        lh3.get(1).getStyleClass().add("span");
        rowsHeader.add(lh3);
        
        // 4-ая строка
        final ObservableList<SpreadsheetCell> lh4 = FXCollections.observableArrayList();
        lh4.add(SpreadsheetCellType.STRING.createCell(3, 0, 1, 1,"Разделы"));
        for (int column = 1; column < grid.getColumnCount(); column++) {
            lh4.add(SpreadsheetCellType.STRING.createCell(3, column, 1, 1, ""));
        }
        lh4.get(1).setItem("Р1");
        lh4.get(1).getStyleClass().add("span");
        rowsHeader.add(lh4);
        
        // запрещает их редактирование
        for (ObservableList<SpreadsheetCell> row : rowsHeader) {
            for (SpreadsheetCell cell : row) {
                cell.setEditable(false);
            }
        }

        return rowsHeader;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setController(FXMLCtrlTable71 fxmlCtrlTable71) {
        this.fxmlCtrlTable71 = fxmlCtrlTable71;
    }

    public void setParentCtrl(FXMLCtrlNewTab fxmlCtrlCurrTab) {
        this.parentCtrl = fxmlCtrlCurrTab;
    }

    // UNDONE Инициализация компонента номером версии
    public void init(Long id_Vers) {
        
    }
    
}
