package com.mmsp.logic.newtab;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import com.mmsp.model.Module;
import com.mmsp.model.Record;
import com.mmsp.model.Section;
import com.mmsp.model.Semester;
import com.mmsp.model.WPDVersion;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// *************************************************************************************************************************
// *************************************************************************************************************************
// **
// ** Вкладка "Таблица 7.1"
// **
// *************************************************************************************************************************
// *************************************************************************************************************************

public class FXMLCtrlTable71 extends HBox {

    static final Logger log = LogManager.getLogger(FXMLCtrlTable71.class);

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

    private Semester currSemester; // текущий выбранный в комбобоксе семестр

    // https://bitbucket.org/panemu/tiwulfx
    // vs.
    // https://bitbucket.org/controlsfx/controlsfx
    private SpreadsheetView ssvTable71; // Замена TableView<?> tvTable71;

    private EventHandler<GridChange> ehT71; // OnGridChange for TableTP

    private FXMLCtrlTable71 fxmlCtrlTable71;

    private Stage stage;

    private FXMLCtrlNewTab parentCtrl;

    private WPDVersion wpdVersion; // версия соответствующая вкладке

    /**
     * Работает по нажатию кнопки "Сохранить" на главной форме
     */
    // TODO реаизовать сохранение
    public void save() {
        throw new UnsupportedOperationException();
    }

    /**
     * Создаёт строку общего типа для указанной позиции
     * 
     * @param posRow
     *            позиция для новой строки
     * @param lValueOfOldCell
     *            список значений ячеек. Обязательно String
     * @return строку общего типа
     */
    private ObservableList<SpreadsheetCell> createStringRow(int posRow, ArrayList<String> lValueOfOldCell) {
        ObservableList<SpreadsheetCell> olRow = FXCollections.observableArrayList();
        if (lValueOfOldCell == null) {
            for (int column = 0; column < ssvTable71.getGrid().getColumnCount(); column++) {
                olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1, ""));
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

        // Последняя строка не доступна для редактирования
        olRow.get(olRow.size() - 1).setEditable(false);
        return olRow;
    }

    /**
     * Добавляет строку общего типа в конец таблицы
     * 
     * @param event
     */
    @FXML
    void clickBAddRowT71(ActionEvent event)
    // https://bitbucket.org/controlsfx/controlsfx/issues/590/adding-new-rows-to-a-spreadsheetview
    // https://bitbucket.org/controlsfx/controlsfx/issues/151/dynamic-adding-rows-in-spreadsheetview-at
    {
        Record rec = new Record(currSemester.getQUANTITY_OF_WEEK(), ssvTable71.getGrid().getRowCount());
        currSemester.getRowT71().add(rec);
        addRowT71(null); // Создадим пусую строку
    }

    /**
     * Удаляет строку в которой была выделена клетка
     * 
     * @param event
     */
    @FXML
    void clickBDelRowT71(ActionEvent event) {
        // получение фокуса
        int col = ssvTable71.getSelectionModel().getFocusedCell().getColumn();
        int row = ssvTable71.getSelectionModel().getFocusedCell().getRow();

        // удаление из массива записей
        for (Record rec : currSemester.getRowT71()) {
            if (rec.getPos() == row) {
                currSemester.getRowT71().remove(rec);
                break;
            }
        }

        if (!(row > 3 && row < ssvTable71.getGrid().getRowCount()))
            return;
        // убрать фокус совсем
        // ssvTable71.getSelectionModel().clearSelection();
        
        // Создадим сетку с -1 строкой
        GridBase newGrid = new GridBase(ssvTable71.getGrid().getRowCount() - 1, ssvTable71.getGrid().getColumnCount());
        ObservableList<ObservableList<SpreadsheetCell>> newRows = setHeaderForT71(newGrid,
                // по новой инициализируем хедер таблицы
                currSemester.getQUANTITY_OF_WEEK());

        for (int i = 4; i < ssvTable71.getGrid().getRowCount(); i++) {
            if (i == row)
                continue; // та строка, которую нужно пропустить
            int k = i;
            if (i > row)
                k--; // перешагиваем i-ую строку для createStringRow()
            ArrayList<String> lValueOfOldCell = new ArrayList<>();
            for (int j = 0; j < ssvTable71.getGrid().getColumnCount(); j++) {
                lValueOfOldCell.add(ssvTable71.getGrid().getRows().get(i).get(j).getText());
            }
            ObservableList<SpreadsheetCell> oldRow = createStringRow(k, lValueOfOldCell);
            // newRows.add(oldRow);
        }
        newGrid.getColumnHeaders().addAll(ssvTable71.getGrid().getColumnHeaders());
        // newGrid.setRows(newRows);
        // объединение "Распределение по учебным неделям"
        newGrid.spanColumn(newGrid.getColumnCount() - 2, 0, 1);
        // объединение "Виды работ"
        newGrid.spanRow(2, 0, 0);
        // объединение "Итого"
        newGrid.spanRow(2, 0, newGrid.getColumnCount() - 1);
        // объединение "M1"
        newGrid.spanColumn(currSemester.getQUANTITY_OF_WEEK(), 2, 1);
        // объединение "P1"
        newGrid.spanColumn(currSemester.getQUANTITY_OF_WEEK(), 3, 1);

        ssvTable71.setGrid(newGrid);
        newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehT71);

        if (row == ssvTable71.getGrid().getRowCount()) { // переставим фокус
            // фокус на предыдущую строку, но ту же колонку
            ssvTable71.getSelectionModel().focus(row - 1, ssvTable71.getColumns().get(col));
        } else {
            // фокус на ту же строку и ту же колонку
            ssvTable71.getSelectionModel().focus(row, ssvTable71.getColumns().get(col));
        }
        if (ssvTable71.getSelectionModel().getFocusedCell().getRow() < 4)
            bDelRowT71.setDisable(true);
    }

    /**
     * Задаёт header всей таблицы ssvTable71
     * 
     * @param grid
     *            BaseGrid этой таблицы
     * @param length
     *            количество недель
     * @return первые 4 строки
     */
    private ObservableList<ObservableList<SpreadsheetCell>> setHeaderForT71(GridBase grid, int length) {

        ObservableList<ObservableList<SpreadsheetCell>> rowsHeader = FXCollections.observableArrayList();

        // 1-ая строка
        final ObservableList<SpreadsheetCell> lh1 = FXCollections.observableArrayList();
        lh1.add(SpreadsheetCellType.STRING.createCell(0, 0, 1, 1, "Виды работ"));
        lh1.get(0).getStyleClass().add("span");
        lh1.add(SpreadsheetCellType.STRING.createCell(0, 1, 1, 1, "Распределение по учебным неделям"));
        lh1.get(1).getStyleClass().add("span");
        for (int i = 2; i < grid.getColumnCount() - 1; i++) {
            SpreadsheetCell ssc = SpreadsheetCellType.STRING.createCell(0, i, 1, 1, "");
            lh1.add(ssc);
        }
        lh1.add(SpreadsheetCellType.STRING.createCell(0, length + 1, 1, 1, "Итого"));
        lh1.get(length + 1).getStyleClass().add("span");
        rowsHeader.add(lh1); // первая строка заполнена

        // 2-ая строка
        final ObservableList<SpreadsheetCell> lh2 = FXCollections.observableArrayList();
        lh2.add(SpreadsheetCellType.STRING.createCell(1, 0, 1, 1, ""));
        for (int column = 0; column < grid.getColumnCount() - 2; column++) {
            lh2.add(SpreadsheetCellType.INTEGER.createCell(1, column + 1, 1, 1, column + 1));
        }
        lh2.add(SpreadsheetCellType.STRING.createCell(1, length + 1, 1, 1, ""));
        rowsHeader.add(lh2);

        // 3-ая строка
        final ObservableList<SpreadsheetCell> lh3 = FXCollections.observableArrayList();
        lh3.add(SpreadsheetCellType.STRING.createCell(2, 0, 1, 1, "Модули"));
        for (int column = 1; column < grid.getColumnCount(); column++) {
            lh3.add(SpreadsheetCellType.STRING.createCell(2, column, 1, 1, ""));
        }
        lh3.get(1).setItem("М1");
        lh3.get(1).getStyleClass().add("span");
        rowsHeader.add(lh3);

        // 4-ая строка
        final ObservableList<SpreadsheetCell> lh4 = FXCollections.observableArrayList();
        lh4.add(SpreadsheetCellType.STRING.createCell(3, 0, 1, 1, "Разделы"));
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

    // UNDONE Инициализация компонента
    public void init(WPDVersion wpdVers) {
        log.info("wpdVersion == null? " + wpdVers);
        wpdVersion = wpdVers;

        olSemesters.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends String> change) {
                if (olSemesters.size() != 0) {
                    cbSemesters.setDisable(false);
                } else {
                    cbSemesters.setDisable(true);
                }
            }
        });

        cbSemesters.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number value, Number new_value) {
                if (new_value.intValue() > -1) {
                    bAddRowT71.setDisable(false);
                    // bDelRowT71.setDisable(false);

                    int iValue = Integer.parseInt(olSemesters.get((int) new_value));
                    for (Semester sem : wpdVersion.getTreeSemesters()) {
                        if (sem.getNUMBER_OF_SEMESTER() == iValue) {
                            currSemester = sem;
                            loadTvT71();
                            break;
                        }
                    }
                } else { // если пустое поле
                    bAddRowT71.setDisable(true);
                    bDelRowT71.setDisable(true);
                }
            }
        });
        cbSemesters.setItems(olSemesters);
    }

    /**
     * Загружает данные из currSemester в таблицу 7.1, если он null, то убирает
     * таблицу
     * 
     * @param currSem
     */
    private void loadTvT71() {
        if (currSemester == null) {
            vbT71.getChildren().remove(ssvTable71);
        } else {
            createTvT71(currSemester); // Создание каркаса
            pasteIntoTvT71(currSemester); // Подгрузка в ячейки данных

            // отображение, если ещё не отображено
            if (!vbT71.getChildren().contains(ssvTable71)) {
                vbT71.getChildren().add(ssvTable71);
                VBox.setVgrow(ssvTable71, Priority.ALWAYS);
                VBox.setMargin(ssvTable71, new Insets(0, 15, 0, 0));
            }
        }
    }

    /**
     * Обновляет данные в компоненте
     */
    // ERROR wpdVersion is null
    public void refresh() {
        olSemesters.clear();
        if (wpdVersion == null) {
            log.error("wpdVersion is null");
        }
        for (Semester sem : wpdVersion.getTreeSemesters()) {
            olSemesters.add(String.valueOf(sem.getNUMBER_OF_SEMESTER()));
        }
        if (olSemesters.size() == 0) {
            currSemester = null;
        } else {
            cbSemesters.getSelectionModel().selectFirst();
            currSemester = wpdVersion.getTreeSemesters().iterator().next();
        }
        loadTvT71();
    }

    /**
     * Создаёт каркас таблицы
     */
    private void createTvT71(Semester sem) {
        if (sem == null) {
            ssvTable71.setGrid(null);
            return;
        }
        int length = sem.getQUANTITY_OF_WEEK();

        ehT71 = new EventHandler<GridChange>() {
            public void handle(GridChange change) {
                int row = change.getRow();
                int col = change.getColumn();
                // System.err.println(" Set Focused == " + row + ":" + col);
                // Будем суммировать часы и выводить в итого
                int summ = 0;
                for (int i = 1; i < ssvTable71.getGrid().getColumnCount() - 1; i++) {
                    try {
                        summ += Integer.parseInt(ssvTable71.getGrid().getRows().get(row).get(i).getText());
                    } catch (NumberFormatException ex) {
                        continue;
                    }
                }
                ssvTable71.getGrid().getRows().get(row).get(ssvTable71.getGrid().getColumnCount() - 1)
                        .setEditable(true);
                ssvTable71.getGrid().setCellValue(row, ssvTable71.getGrid().getColumnCount() - 1, String.valueOf(summ));
                ssvTable71.getGrid().getRows().get(row).get(ssvTable71.getGrid().getColumnCount() - 1)
                        .setEditable(false);

                Record rec = currSemester.getRecord(row);
                if (rec != null) {
                    // System.err.println("NEW VALUE == " + (String)
                    // change.getNewValue() + " ON CELL( " + row + " : " + col
                    // +")");
                    if (col == 0)
                        rec.setCourseTitle((String) change.getNewValue());
                    if (col > 0 && col <= rec.getArrWeek().length)
                        rec.getArrWeek()[col - 1] = (String) change.getNewValue();
                }

                // Пока только разделы
                int pos = 0; // номер строки, в которой нашли "Лекции"
                for (int i = 0; i < ssvTable71.getGrid().getRowCount(); i++)
                    // Смотрим, есть ли в первом столбце "лекции" и запоминаем
                    // эту позицию строки
                    if (ssvTable71.getGrid().getRows().get(i).get(0).getText().equalsIgnoreCase("лекции"))
                        pos = i;
                if (pos > 1) {
                    // System.out.println("POS == " + pos);
                    for (int i = 1; i < ssvTable71.getGrid().getColumnCount() - 1; i++) {
                        if (ssvTable71.getGrid().getRows().get(pos).get(i).getText() == null
                                || ssvTable71.getGrid().getRows().get(pos).get(i).getText().equals(""))
                            ssvTable71.getGrid().getRows().get(pos).get(i).setItem("0");
                    }
                    // в идеальном случае, размеры следующих массивов
                    // одинаковые,
                    // иначе список секций по количеству элементов будет такой
                    // же, но последние элементы будут заполнены 0-ми

                    // FIXME слишком сложно, лучше переписать
                    List<Section> liSec = new ArrayList<Section>();
                    // хранится список секций, который надо отобразить
                    List<Integer> liColSec = new ArrayList<Integer>();
                    // количесвто колонок, которое занимет каждая секция
                    for (Module mod : wpdVersion
                            .getSemester(Integer.parseInt(cbSemesters.getSelectionModel().getSelectedItem()))
                            .getTreeModule())
                        liSec.addAll(mod.getTreeSection());
                    for (int i = 0; i < liSec.size(); i++)
                        liColSec.add(0);
                    int j = 0; // индекс текущей выбранной секции из списка
                               // liSec
                    int oldPos = 0; // позиция последней объединённой ячейки
                    int count = 0; // собранное количество часов в
                                   // предполагаемом разделе
                    for (int i = 1; i < ssvTable71.getGrid().getColumnCount() - 1; i++) {
                        count += Integer.parseInt(ssvTable71.getGrid().getRows().get(pos).get(i).getText());
                        if (j >= liSec.size())
                            break;
                        if (count >= liSec.get(j).getL()) {
                            liColSec.set(j, i - oldPos);
                            count = 0;
                            oldPos = i;
                            j++;
                        }
                    }

                    setHeaderSection(liSec, liColSec);

                    // хранится список секций, который надо отобразить
                    List<Module> liMod = new ArrayList<Module>();
                    // количество колонок, которое занимет каждая секция
                    List<Integer> liColMod = new ArrayList<Integer>();
                    liMod.addAll(
                            wpdVersion.getSemester(Integer.parseInt(cbSemesters.getSelectionModel().getSelectedItem()))
                                    .getTreeModule());
                    for (int i = 0; i < liSec.size(); i++)
                        liColMod.add(0);
                    j = 0; // индекс текущей выбранной секции из списка liSec
                    oldPos = 0; // позиция последней объединённой ячейки
                    count = 0; // собранное количество часов в предполагаемом
                               // разделе
                    for (int i = 1; i < ssvTable71.getGrid().getColumnCount() - 1; i++) {
                        count += Integer.parseInt(ssvTable71.getGrid().getRows().get(pos).get(i).getText());
                        if (j >= liMod.size())
                            break;
                        if (count >= liMod.get(j).getL()) {
                            liColMod.set(j, i - oldPos);
                            count = 0;
                            oldPos = i;
                            j++;
                        }
                    }

                    setHeaderModule(liMod, liColMod);
                }
            }

            /**
             * Инициализирует все ячейки 2-ой строки значениями вида "М" + номер
             * модуля и объединяет нужные
             * 
             * @param liMod
             * @param liColMod
             */
            private void setHeaderModule(List<Module> liMod, List<Integer> liColMod) {
                if (liMod.size() == 0)
                    return;
                ssvTable71.getGrid().spanColumn(1, 2, 1); // разъединение "М1"
                int posSec = 0; // отвечает за нужный раздел
                int count = 0; // отвечает за количество нужных клеток в данном
                               // разделе
                for (int column = 1; column < ssvTable71.getGrid().getColumnCount() - 1; column++) {
                    if (posSec < liMod.size() && count < liColMod.get(posSec)) {
                        ssvTable71.getGrid().getRows().get(2).set(column, SpreadsheetCellType.STRING.createCell(2,
                                column, 1, 1, "М" + liMod.get(posSec).getNumber()));
                        count++;
                        if (count >= liColMod.get(posSec)) {
                            posSec++;
                            count = 0;
                        }
                    } else {
                        ssvTable71.getGrid().getRows().get(2).set(column, SpreadsheetCellType.STRING.createCell(2,
                                column, 1, 1, "М" + liMod.get(liMod.size() - 1).getNumber()));
                    }
                    ssvTable71.getGrid().getRows().get(2).get(column).getStyleClass().add("span");
                    ssvTable71.getGrid().getRows().get(2).get(column).setEditable(false);
                }
                posSec = 1; // теперь отвечает за позицию последней объединённой
                            // ячейки + 1
                for (int i = 0; i < liMod.size(); i++) {
                    if (i < liMod.size() - 1) {
                        // объединяем все кроме последних
                        ssvTable71.getGrid().spanColumn(liColMod.get(i), 2, posSec);
                        posSec += liColMod.get(i);
                    } else {
                        // объединяем все остальные клетки
                        ssvTable71.getGrid().spanColumn(ssvTable71.getGrid().getColumnCount() - 1 - posSec, 2, posSec);
                    }
                }
            }

            /**
             * Инициализирует все ячейки 3-ей строки значениями вида "Р" + номер
             * раздела и объединяет нужные
             * 
             * @param liSec
             * @param liColSec
             */
            private void setHeaderSection(List<Section> liSec, List<Integer> liColSec) {
                if (liSec.size() == 0)
                    return;
                ssvTable71.getGrid().spanColumn(1, 3, 1); // разъединение "P1"
                int posSec = 0; // отвечает за нужный раздел
                int count = 0; // отвечает за количество нужных клеток в данном
                               // разделе
                for (int column = 1; column < ssvTable71.getGrid().getColumnCount() - 1; column++) {
                    if (posSec < liSec.size() && count < liColSec.get(posSec)) {
                        ssvTable71.getGrid().getRows().get(3).set(column, SpreadsheetCellType.STRING.createCell(3,
                                column, 1, 1, "Р" + liSec.get(posSec).getNumber()));
                        count++;
                        if (count >= liColSec.get(posSec)) {
                            posSec++;
                            count = 0;
                        }
                    } else {
                        ssvTable71.getGrid().getRows().get(3).set(column, SpreadsheetCellType.STRING.createCell(3,
                                column, 1, 1, "Р" + liSec.get(liSec.size() - 1).getNumber()));
                    }
                    ssvTable71.getGrid().getRows().get(3).get(column).getStyleClass().add("span");
                    ssvTable71.getGrid().getRows().get(3).get(column).setEditable(false);
                }
                posSec = 1; // теперь отвечает за позицию последней объединённой
                            // ячейки + 1
                for (int i = 0; i < liSec.size(); i++) {
                    if (i < liSec.size() - 1) {
                        // объединяем все кроме последних
                        ssvTable71.getGrid().spanColumn(liColSec.get(i), 3, posSec);
                        posSec += liColSec.get(i);
                    } else {
                        // объединяем все остальные клетки
                        ssvTable71.getGrid().spanColumn(ssvTable71.getGrid().getColumnCount() - 1 - posSec, 3, posSec);
                    }
                }
            }
        };

        int rowCount = 4;
        int columnCount = length + 2;
        GridBase grid = new GridBase(rowCount, columnCount);

        ObservableList<ObservableList<SpreadsheetCell>> rows = setHeaderForT71(grid, length);

        grid.setRows(rows);
        // объединение "Распределение по учебным неделям"
        grid.spanColumn(grid.getColumnCount() - 2, 0, 1);
        grid.spanRow(2, 0, 0); // объединение "Виды работ"
        grid.spanRow(2, 0, grid.getColumnCount() - 1); // объединение "Итого"
        grid.spanColumn(length, 3, 1); // объединение "P1"
        // Вот так выглядит разъединение // grid.spanColumn(1, 3, 1); //
        // разъединение "P1"
        grid.spanColumn(length, 2, 1); // объединение "M1"
        grid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehT71);
        if (ssvTable71 != null)
            ssvTable71.setGrid(grid);
        else
            ssvTable71 = new SpreadsheetView(grid);

        // Задание ширины колонкам
        ssvTable71.getColumns().get(0).setPrefWidth(150);
        for (int i = 1; i < ssvTable71.getColumns().size() - 1; i++)
            ssvTable71.getColumns().get(i).setPrefWidth(35);
        ssvTable71.getColumns().get(ssvTable71.getColumns().size() - 1).setPrefWidth(140);

        ssvTable71.getStylesheets().add(getClass().getResource("/SpreadSheetView.css").toExternalForm());
        ssvTable71.setShowRowHeader(true);
        ssvTable71.setShowColumnHeader(true);
        ssvTable71.getSelectionModel().getSelectedCells().addListener(new InvalidationListener() {

            @Override
            // UNDONE мб это использовать на замену ehT71?
            public void invalidated(Observable o) {
                /*
                 * for(TablePosition<?, ?> cell :
                 * ssvTable71.getSelectionModel().getSelectedCells()){
                 * System.err.println(cell.getRow()+" / "+cell.getColumn()); //
                 * показывает индексы выделенных строк }
                 */
                if (ssvTable71.getSelectionModel().getSelectedCells().size() != 0)
                    if (ssvTable71.getSelectionModel().getSelectedCells()
                            .get(ssvTable71.getSelectionModel().getSelectedCells().size() - 1).getRow() > 3)
                        bDelRowT71.setDisable(false);
                    else
                        bDelRowT71.setDisable(true);
            }
        });
    }

    /**
     * Вставляет данные в талбицу 7.1 из currSemester
     * 
     * @param semester
     */
    private void pasteIntoTvT71(Semester semester) {
        if (semester == null)
            return;

        // TEST
        System.err.println("RECORD SIZE == " + semester.getRowT71().size());

        for (Record row : semester.getRowT71())
            addRowT71(row);
    }

    /**
     * Добавляет строку в таблицу 7.1
     * 
     * @param record
     *            строка из Semester.getRowT71() или null, если хотим заполнить
     *            её ""
     */
    private void addRowT71(Record record) {
        // Создадим сетку с +1 строкой
        GridBase newGrid = new GridBase(ssvTable71.getGrid().getRowCount() + 1, ssvTable71.getGrid().getColumnCount());
        // и количество строк
        int newRowPos = ssvTable71.getGrid().getRowCount();

        // а так же существующие строки
        ObservableList<ObservableList<SpreadsheetCell>> newRows = ssvTable71.getGrid().getRows();

        ArrayList<String> liRow = null;
        if (record != null) {
            liRow = new ArrayList<>();
            // Скопируем навзавние предмета
            liRow.add(record.getCourseTitle());
            // Скоппируем содержимое массива распределения часов
            for (int i = 0; i < record.getArrWeek().length; i++)
                liRow.add(record.getArrWeek()[i]);
            liRow.add(""); // Заполняем последнюю строку столбца итого
        }

        // Добавление строки на место последней строки
        final ObservableList<SpreadsheetCell> olNew = createStringRow(newRowPos, liRow);
        newRows.add(olNew);

        newGrid.setRows(newRows);
        newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehT71);
        ssvTable71.setGrid(newGrid);
    }
}
