package com.mmsp.logic.newtab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import com.mmsp.logic.FXMLCtrlModalModule;
import com.mmsp.logic.FXMLCtrlModalSection;
import com.mmsp.logic.FXMLCtrlModalTheme;
import com.mmsp.model.Module;
import com.mmsp.model.Section;
import com.mmsp.model.Semester;
import com.mmsp.model.ThematicPlan;
import com.mmsp.model.WPDVersion;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Переменные вкладки "Тематический план"
public class FXMLCtrlThematicalPlan extends HBox {

	/**
	 * {@link WPDVersion} принадлежащая этой вкладке
	 */
    private WPDVersion currWPDVersion;

    private FXMLCtrlNewTab fxmlCtrlCurrTab; // Контроллер этой вкладки

    private final TreeItem<String> rootElement = new TreeItem<String>("Создать модуль");

    private SpreadsheetView ssvTableTP; // Таблица тематического плана

    private EventHandler<GridChange> ehTP; // OnGridChange for TableTP

    @FXML
    private VBox vbThematicalPlan;

    @FXML
    private HBox hbReplacementThematicalPlan;

    @FXML
    private TreeView<String> tvRoot;

    private FXMLCtrlNewTab parentCtrl;

    private FXMLCtrlThematicalPlan fxmlCtrlThematicalPlan;

    private Stage stage;

    private WPDVersion wpdVersion;

    /**
     * отрисовка дерева в tvRoot
     */
    public void createTree() {
        tvRoot.getSelectionModel().clearSelection();
        rootElement.getChildren().clear();
        for (Semester semester : currWPDVersion.getTreeSemesters()) {
            TreeItem<String> nodeSemester = new TreeItem<String>("Семестр " + semester.getNUMBER_OF_SEMESTER());
            nodeSemester.setExpanded(true);
            rootElement.getChildren().add(nodeSemester);
            for (Module module : semester.getTreeModule()) {
                TreeItem<String> nodeModule = new TreeItem<String>("Модуль " + module.getNumber());
                nodeModule.setExpanded(true);
                nodeSemester.getChildren().add(nodeModule);
                for (Section section : module.getTreeSection()) {
                    TreeItem<String> nodeSection = new TreeItem<String>("Раздел " + section.getNumber());
                    nodeSection.setExpanded(true);
                    nodeModule.getChildren().add(nodeSection);
                    for (ThematicPlan theme : section.getTreeTheme()) {

                        // theme.setWPDVerion(currWPDVersion);

                        TreeItem<String> nodeTheme = new TreeItem<String>("Тема " + theme.getNumber());
                        nodeTheme.setExpanded(false);
                        nodeSection.getChildren().add(nodeTheme);
                    }
                }
            }
        }
    }

    /**
     * Обновление содержимого вкладки "Тематический план"
     */
    private void loadTabThematicalPlan() {
        createTree();

        tvRoot.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

            private ContextMenu menu = new ContextMenu();

            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    TreeItem<String> selected = tvRoot.getSelectionModel().getSelectedItem();

                    if (selected != null) {
                        openContextMenu(selected, event.getScreenX(), event.getScreenY());
                    }
                } else {
                    menu.hide();
                }
            }

            private void openContextMenu(TreeItem<String> item, double x, double y) {
                menu.getItems().clear();
                MenuItem mI;

                List<MenuItem> liMI = new ArrayList<MenuItem>();
                if (item.getValue().contains("Семестр")) {
                    mI = new MenuItem("Добавить модуль");
                    mI.setOnAction(e -> {
                        showModalModule(item);
                    });
                    liMI.add(mI);
                }
                if (item.getValue().contains("Модуль")) {
                    mI = new MenuItem("Добавить раздел");
                    mI.setOnAction(e -> {
                        showModalSection(item);
                    });
                    liMI.add(mI);
                    mI = new MenuItem("Изменить модуль");
                    mI.setOnAction(e -> {
                        showModalModule(item);
                    });
                    liMI.add(mI);
                    mI = new MenuItem("Удалить модуль");
                    mI.setOnAction(e -> {
                        int numOfSem = Integer.parseInt(item.getParent().getValue().split(" ")[1]);
                        int numOfMod = Integer.parseInt(item.getValue().split(" ")[1]);

                        currWPDVersion.getSemester(numOfSem).getTreeModule()
                                .remove(currWPDVersion.getSemester(numOfSem).getModule(numOfMod));
                        createTree();

                        // TEST
                        System.err.println(currWPDVersion.toString());
                    });
                    liMI.add(mI);
                }
                if (item.getValue().contains("Раздел")) {
                    mI = new MenuItem("Добавить тему");
                    mI.setOnAction(e -> {
                        showModalTheme(item);
                    });
                    liMI.add(mI);
                    mI = new MenuItem("Изменить раздел");
                    mI.setOnAction(e -> {
                        showModalSection(item);
                    });
                    liMI.add(mI);
                    mI = new MenuItem("Удалить раздел");
                    mI.setOnAction(e -> {
                        int numOfSem = Integer.parseInt(item.getParent().getParent().getValue().split(" ")[1]);
                        int numOfMod = Integer.parseInt(item.getParent().getValue().split(" ")[1]);
                        int numOfSec = Integer.parseInt(item.getValue().split(" ")[1]);

                        currWPDVersion.getSemester(numOfSem).getModule(numOfMod).getTreeSection()
                                .remove(currWPDVersion.getSemester(numOfSem).getModule(numOfMod).getSection(numOfSec));
                        createTree();

                        // TEST
                        System.err.println(currWPDVersion.toString());
                    });
                    liMI.add(mI);
                }
                if (item.getValue().contains("Тема")) {
                    mI = new MenuItem("Изменить тему");
                    mI.setOnAction(e -> {
                        showModalTheme(item);
                    });
                    liMI.add(mI);
                    mI = new MenuItem("Удалить тему");
                    mI.setOnAction(e -> {
                        int numOfSem = Integer
                                .parseInt(item.getParent().getParent().getParent().getValue().split(" ")[1]);
                        int numOfMod = Integer.parseInt(item.getParent().getParent().getValue().split(" ")[1]);
                        int numOfSec = Integer.parseInt(item.getParent().getValue().split(" ")[1]);
                        int numOfTheme = Integer.parseInt(item.getValue().split(" ")[1]);

                        currWPDVersion.getSemester(numOfSem).getModule(numOfMod).getSection(numOfSec).getTreeTheme()
                                .remove(currWPDVersion.getSemester(numOfSem).getModule(numOfMod).getSection(numOfSec)
                                        .getTheme(numOfTheme));
                        createTree();

                        // TEST
                        System.err.println(currWPDVersion.toString());
                    });
                    liMI.add(mI);
                }

                menu.getItems().addAll(liMI);

                // show menu
                menu.show(tvRoot, x, y);
            }

            /**
             * Вызов диалогового окна добавления / изменения модуля
             * 
             * @param item
             *            если содежит в себе "Модуль", то редактирование, если
             *            "Семестр", то добавление
             */
            private void showModalModule(TreeItem<String> item) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("modal/ModalModule.fxml"));
                Parent root = null;
                try {
                    root = (Parent) fxmlLoader.load();
                } catch (IOException e) {
                    System.err.println("Не удалось загрузить форму настройки таблицы тематического плана");
                    e.printStackTrace();
                }
                Scene scene = new Scene(root);

                Stage stageModalModule = new Stage();
                FXMLCtrlModalModule fxmlCtrlModalModule = fxmlLoader.getController();
                fxmlCtrlModalModule.init(stageModalModule);
                stageModalModule.setResizable(false);
                // Запомним контроллер новой вкладки для перерсовки
                fxmlCtrlModalModule.setController(fxmlCtrlCurrTab);

                // установка корневого элемента
                if (item.getValue().contains("Модуль")) {
                    // Изменение выбранного модуля
                    int numOfSem = Integer.parseInt(item.getParent().getValue().split(" ")[1]);
                    int numOfMod = Integer.parseInt(item.getValue().split(" ")[1]);
                    fxmlCtrlModalModule.setRoot(currWPDVersion.getSemester(numOfSem), numOfMod);
                } else if (item.getValue().contains("Семестр")) {
                    // Добавление нового модуля
                    int numOfSem = Integer.parseInt(item.getValue().split(" ")[1]);
                    fxmlCtrlModalModule.setRoot(currWPDVersion.getSemester(numOfSem));
                } else
                    return;

                stageModalModule.setScene(scene);
                stageModalModule.setTitle("Добавление / изменение модуля");
                stageModalModule.getIcons().add(new Image("Logo.png"));
                stageModalModule.initModality(Modality.APPLICATION_MODAL);
                stageModalModule.showAndWait();
            }

            /**
             * Вызов диалогового окна добавления / изменения секции
             * 
             * @param item
             *            если содежит в себе "Раздел", то редактирование, если
             *            "Модуль", то добавление
             */
            private void showModalSection(TreeItem<String> item) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("modal/ModalSection.fxml"));
                Parent root = null;
                try {
                    root = (Parent) fxmlLoader.load();
                } catch (IOException e) {
                    System.err.println("Не удалось загрузить форму настройки таблицы тематического плана");
                    e.printStackTrace();
                }
                Scene scene = new Scene(root);

                Stage stageModalSection = new Stage();
                FXMLCtrlModalSection fxmlCtrlModalSection = fxmlLoader.getController();
                fxmlCtrlModalSection.init(stageModalSection);
                stageModalSection.setResizable(false);
                fxmlCtrlModalSection.setController(fxmlCtrlCurrTab); // Запомним
                                                                     // контроллер
                                                                     // новой
                                                                     // вкладки
                                                                     // для
                                                                     // перерсовки

                // установка корневого элемента
                if (item.getValue().contains("Раздел")) {
                    // Изменение выбранного раздела
                    int numOfSem = Integer.parseInt(item.getParent().getParent().getValue().split(" ")[1]);
                    int numOfMod = Integer.parseInt(item.getParent().getValue().split(" ")[1]);
                    int numOfSec = Integer.parseInt(item.getValue().split(" ")[1]);
                    fxmlCtrlModalSection.setRoot(currWPDVersion.getSemester(numOfSem), numOfMod, numOfSec);
                } else if (item.getValue().contains("Модуль")) {
                    // Добавление нового модуля
                    int numOfSem = Integer.parseInt(item.getParent().getValue().split(" ")[1]);
                    int numOfMod = Integer.parseInt(item.getValue().split(" ")[1]);
                    fxmlCtrlModalSection.setRoot(currWPDVersion.getSemester(numOfSem), numOfMod);
                } else
                    return;

                stageModalSection.setScene(scene);
                stageModalSection.setTitle("Добавление / изменение раздела");
                stageModalSection.getIcons().add(new Image("Logo.png"));
                stageModalSection.initModality(Modality.APPLICATION_MODAL);
                stageModalSection.showAndWait();
            }

            /**
             * Вызов диалогового окна добавления / изменения секции
             * 
             * @param item
             *            если содежит в себе "Раздел", то редактирование, если
             *            "Модуль", то добавление
             */
            private void showModalTheme(TreeItem<String> item) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("modal/ModalTheme.fxml"));
                Parent root = null;
                try {
                    root = (Parent) fxmlLoader.load();
                } catch (IOException e) {
                    System.err.println("Не удалось загрузить форму настройки таблицы тематического плана");
                    e.printStackTrace();
                }
                Scene scene = new Scene(root);

                Stage stageModalTheme = new Stage();
                FXMLCtrlModalTheme fxmlCtrlModalTheme = fxmlLoader.getController();
                fxmlCtrlModalTheme.init(stageModalTheme);
                stageModalTheme.setResizable(false);
                fxmlCtrlModalTheme.setController(fxmlCtrlCurrTab); // Запомним
                                                                   // контроллер
                                                                   // новой
                                                                   // вкладки
                                                                   // для
                                                                   // перерсовки

                // установка корневого элемента
                if (item.getValue().contains("Тема")) {
                    // Изменение выбранной темы
                    int numOfSem = Integer.parseInt(item.getParent().getParent().getParent().getValue().split(" ")[1]);
                    int numOfMod = Integer.parseInt(item.getParent().getParent().getValue().split(" ")[1]);
                    int numOfSec = Integer.parseInt(item.getParent().getValue().split(" ")[1]);
                    int numOfTheme = Integer.parseInt(item.getValue().split(" ")[1]);
                    fxmlCtrlModalTheme.setRoot(currWPDVersion.getSemester(numOfSem), numOfMod, numOfSec, numOfTheme);
                } else if (item.getValue().contains("Раздел")) {
                    // Добавление новой темы
                    int numOfSem = Integer.parseInt(item.getParent().getParent().getValue().split(" ")[1]);
                    int numOfMod = Integer.parseInt(item.getParent().getValue().split(" ")[1]);
                    int numOfSec = Integer.parseInt(item.getValue().split(" ")[1]);
                    fxmlCtrlModalTheme.setRoot(currWPDVersion.getSemester(numOfSem), numOfMod, numOfSec);
                } else
                    return;

                stageModalTheme.setScene(scene);
                stageModalTheme.setTitle("Добавление / изменение темы");
                stageModalTheme.getIcons().add(new Image("Logo.png"));
                stageModalTheme.initModality(Modality.APPLICATION_MODAL);
                stageModalTheme.showAndWait();
            }
        });
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
            for (int column = 6; column < ssvTableTP.getGrid().getColumnCount(); column++) {
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
            for (int column = 6; column < ssvTableTP.getGrid().getColumnCount(); column++) {
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
     * Инициализация компонента ssvTableTP
     */
    private void initSSVTableTP()
    // FIXME СРС должна быть String?
    {
        ehTP = new EventHandler<GridChange>() {
            public void handle(GridChange change) {
                System.err.println("TEST");
            }
        };
        int rowCount = 1;
        int columnCount = 11;
        GridBase grid = new GridBase(rowCount, columnCount);

        //GridBase.MapBasedRowHeightFactory rowHeightFactory = new GridBase.MapBasedRowHeightFactory(generateRowHeight(1));
        //grid.setRowHeightCallback(rowHeightFactory);
        grid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight(1)));
        ObservableList<ObservableList<SpreadsheetCell>> rows = setHeaderForTTP(grid);
        grid.setRows(rows);
        grid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTP);
        ssvTableTP = new SpreadsheetView(grid);
        ssvTableTP.getStylesheets().add(getClass().getResource("/SpreadSheetView.css").toExternalForm());
        ssvTableTP.setShowRowHeader(true);
        ssvTableTP.setShowColumnHeader(true);

        hbReplacementThematicalPlan.getChildren().add(ssvTableTP); // FIXME USE MasterDetailPane
        HBox.setHgrow(ssvTableTP, Priority.ALWAYS);
        HBox.setMargin(ssvTableTP, new Insets(15, 0, 15, 0));

        rootElement.setExpanded(true);
        tvRoot.setRoot(rootElement);
        tvRoot.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {

            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> old_val, TreeItem<String> new_val) {

                saveTheme();

                if (new_val == null) return;
                // Поиск выбранного элемента в treeRoot
                if (new_val.getValue().split(" ")[0].equals("Семестр")) {
                    repaintSSVTableTP(
                            Integer.parseInt(new_val.getValue().split(" ")[1]) // берём № семестра
                    );
                }
                if (new_val.getValue().split(" ")[0].equals("Модуль")) {
                    repaintSSVTableTP(
                            Integer.parseInt(new_val.getParent().getValue().split(" ")[1]), // берём № семестра
                            Integer.parseInt(new_val.getValue().split(" ")[1]) // берём № модуля
                    );
                }
                if (new_val.getValue().split(" ")[0].equals("Раздел")) {
                    repaintSSVTableTP(
                            Integer.parseInt(new_val.getParent().getParent().getValue().split(" ")[1]), // берём № семестра
                            Integer.parseInt(new_val.getParent().getValue().split(" ")[1]), // берём № модуля
                            Integer.parseInt(new_val.getValue().split(" ")[1]) // берём № раздела
                    );
                }
                if (new_val.getValue().split(" ")[0].equals("Тема")) {
                    repaintSSVTableTP(
                            Integer.parseInt(new_val.getParent().getParent().getParent().getValue().split(" ")[1]), // берём № семестра
                            Integer.parseInt(new_val.getParent().getParent().getValue().split(" ")[1]), // берём № модуля
                            Integer.parseInt(new_val.getParent().getValue().split(" ")[1]), // берём № раздела
                            Integer.parseInt(new_val.getValue().split(" ")[1]) // берём № темы
                    );
                }
            }

        });
        tvRoot.setShowRoot(false);
        //createTree();
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
    
    /**
     * Задаёт header всей таблицы ssvTableTP
     * @param grid BaseGrid этой таблицы
     * @return первую строку
     */
    private ObservableList<ObservableList<SpreadsheetCell>> setHeaderForTTP(GridBase grid) {
        ObservableList<ObservableList<SpreadsheetCell>> rowsHeader = FXCollections.observableArrayList();
        
        // 1-ая строка
        final ObservableList<SpreadsheetCell> olHeader = FXCollections.observableArrayList();
        olHeader.add(SpreadsheetCellType.STRING.createCell(0, 0, 1, 1, "№ семестра")); // Принадлежность к модулю
        olHeader.add(SpreadsheetCellType.STRING.createCell(0, 1, 1, 1, "№ модуля")); // Принадлежность к модулю
        olHeader.add(SpreadsheetCellType.STRING.createCell(0, 2, 1, 1, "№ раздела")); // Принадлежность к модулю
        olHeader.add(SpreadsheetCellType.STRING.createCell(0, 3, 1, 1, "№ темы")); // Принадлежность к модулю
        olHeader.add(SpreadsheetCellType.STRING.createCell(0, 4, 1, 1,"Название темы"));
        olHeader.add(SpreadsheetCellType.STRING.createCell(0, 5, 1, 1,"Описание темы"));
        olHeader.add(SpreadsheetCellType.STRING.createCell(0, 6, 1, 1, "Л"));
        olHeader.add(SpreadsheetCellType.STRING.createCell(0, 7, 1, 1, "ПЗ"));
        olHeader.add(SpreadsheetCellType.STRING.createCell(0, 8, 1, 1, "ЛР"));
        olHeader.add(SpreadsheetCellType.STRING.createCell(0, 9, 1, 1, "КСР"));
        olHeader.add(SpreadsheetCellType.STRING.createCell(0, 10, 1, 1, "СРС"));

        // запрещает редактирование
        for (SpreadsheetCell cell : olHeader) {
            cell.setEditable(false);
        }
        rowsHeader.add(olHeader); // первая строка заполнена
        return rowsHeader;
    }

    /**
     * сохраняет изменения темы из ssvTableTP
     */
    private void saveTheme() {
        int rowCount = ssvTableTP.getGrid().getRowCount();
        for (int i = 1; i < rowCount; i++) {
            int belongingToTheSemester = (int) ssvTableTP.getGrid().getRows().get(i).get(0).getItem();
            int belongingToTheModule = (int) ssvTableTP.getGrid().getRows().get(i).get(1).getItem();
            int belongingToTheSection = (int) ssvTableTP.getGrid().getRows().get(i).get(2).getItem();
            int numberOfTheme = (int) ssvTableTP.getGrid().getRows().get(i).get(3).getItem();

            ThematicPlan theme = currWPDVersion.getSemester(belongingToTheSemester).getModule(belongingToTheModule).getSection(belongingToTheSection).getTheme(numberOfTheme);

            if (theme != null) {
                //theme.setWPDVerion(currWPDVersion);
                theme.setTitle((String) ssvTableTP.getGrid().getRows().get(i).get(4).getItem());
                theme.setDescription((String) ssvTableTP.getGrid().getRows().get(i).get(5).getItem());
                theme.setL((Integer) ssvTableTP.getGrid().getRows().get(i).get(6).getItem());
                theme.setPZ((Integer) ssvTableTP.getGrid().getRows().get(i).get(7).getItem());
                theme.setLR((Integer) ssvTableTP.getGrid().getRows().get(i).get(8).getItem());
                theme.setKSR((Integer) ssvTableTP.getGrid().getRows().get(i).get(9).getItem());
                theme.setSRS((Integer) ssvTableTP.getGrid().getRows().get(i).get(10).getItem());
                System.out.println("Theme save\n" + theme.toString());
            }
        }
    }
    
    /**
     * Добавляет строку в ssvTableTP в соотвествии с № модуля, раздела, темы
     * @param theme строка из liTheem или null, если хотим заполнить по стандарту
     */
    private void addRowSSVTableTP(ThematicPlan theme) {

        GridBase newGrid = null;
        if (ssvTableTP.getGrid() != null)
            newGrid = new GridBase(ssvTableTP.getGrid().getRowCount() + 1, ssvTableTP.getGrid().getColumnCount()); // Создадим сетку с +1 строкой
        else newGrid = new GridBase(1, 11);
        int newRowPos = ssvTableTP.getGrid().getRowCount(); // и количество строк

        ObservableList<ObservableList<SpreadsheetCell>> newRows = ssvTableTP.getGrid().getRows(); // а так же существующие строки

        List<String> liValueOftheme = new ArrayList<String>();
        liValueOftheme.add(String.valueOf(theme.getBelongingToTheSemester()));
        liValueOftheme.add(String.valueOf(theme.getBelongingToTheModule()));
        liValueOftheme.add(String.valueOf(theme.getBelongingToTheSection()));
        liValueOftheme.add(String.valueOf(theme.getNumber()));
        liValueOftheme.add(theme.getTitle());
        liValueOftheme.add(theme.getDescription());
        liValueOftheme.add(String.valueOf(theme.getL()));
        liValueOftheme.add(String.valueOf(theme.getPZ()));
        liValueOftheme.add(String.valueOf(theme.getLR()));
        liValueOftheme.add(String.valueOf(theme.getKSR()));
        liValueOftheme.add(String.valueOf(theme.getSRS()));

        //liValueOftheme.forEach(System.err::println);

        final ObservableList<SpreadsheetCell> olNew = createRowForTTP(newRowPos, liValueOftheme); // Добавление на место последней строки пустой строки
        newRows.add(olNew);

        newGrid.setRows(newRows);
        newGrid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight(newGrid.getRowCount())));
        newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTP);
        ssvTableTP.setGrid(newGrid);
    }
    
    /**
     * Создаёт каркас таблицы ssvTableTP
     */
    private void createSSVTableTP() {

        int rowCount = 1;
        int columnCount = 11;
        GridBase grid = new GridBase(rowCount, columnCount);

        ObservableList<ObservableList<SpreadsheetCell>> rows = setHeaderForTTP(grid);

        grid.setRows(rows);

        grid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTP);

        // Задание ширины колонкам
        ssvTableTP.getColumns().get(0).setPrefWidth(75);
        ssvTableTP.getColumns().get(1).setPrefWidth(65);
        ssvTableTP.getColumns().get(2).setPrefWidth(70);
        ssvTableTP.getColumns().get(3).setPrefWidth(55);
        ssvTableTP.getColumns().get(4).setPrefWidth(200);
        ssvTableTP.getColumns().get(5).setPrefWidth(100);
        for (int i = 6; i < ssvTableTP.getColumns().size() - 1; i++)
            ssvTableTP.getColumns().get(i).setPrefWidth(25);

        ssvTableTP.setGrid(grid);
        ssvTableTP.setShowRowHeader(true);
        ssvTableTP.setShowColumnHeader(true);

    }
    
    /**
     * перерисовывает содержимое ssvTableTP для выбранного в tvRoot элемента, будь то модуль или раздел, или тема (тематический план)
     * @param temp массив int, первое число определяет № модуля, 2-ое - № раздела, 3-е № темы
     */
    private void repaintSSVTableTP(int... temp) {

        createSSVTableTP();

        List<ThematicPlan> liTheme = new ArrayList<>();
        try {
            switch (temp.length) {
            case 0: break;
            case 1:
                for (Module module : currWPDVersion.getSemester(temp[0]).getTreeModule()) {
                    Set<Section> setSection = module.getTreeSection();
                    for (Section section : setSection) {
                        liTheme.addAll(section.getTreeTheme());
                    }
                }
                break;
            case 2:
                Set<Section> setSection = currWPDVersion.getSemester(temp[0]).getModule(temp[1]).getTreeSection();
                for (Section section : setSection) {
                    liTheme.addAll(section.getTreeTheme()); // скопируем у каждой секции
                }
                break;
            case 3:
                liTheme.addAll(currWPDVersion.getSemester(temp[0]).getModule(temp[1]).getSection(temp[2]).getTreeTheme());
                break;
            case 4:
                liTheme.add(currWPDVersion.getSemester(temp[0]).getModule(temp[1]).getSection(temp[2]).getTheme(temp[3]));
                break;
            default:
                throw new Exception("не правильное количество аргументов у массива тематического плана");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        pasteIntoSSVTableTP(liTheme);
    }
    
    /**
     * Вставляет данные в ssvTableTP из liTheme
     */
    private void pasteIntoSSVTableTP(List<ThematicPlan> liTheme) {
        for (ThematicPlan theme : liTheme) {
            addRowSSVTableTP(theme);
        }
    }

    public void addTheme(ThematicPlan theme) {
        addRowSSVTableTP(theme);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setController(FXMLCtrlThematicalPlan fxmlCtrlThematicalPlan) {
        this.fxmlCtrlThematicalPlan = fxmlCtrlThematicalPlan;
    }

    public void setParentCtrl(FXMLCtrlNewTab fxmlCtrlCurrTab) {
        this.parentCtrl = fxmlCtrlCurrTab;
    }

    // UNDONE Инициализация компонента номером версии
    public void init(WPDVersion wpdVers) {
        wpdVersion = wpdVers;
    }

    public void refresh() {
        repaintSSVTableTP();
        loadTabThematicalPlan();
    }
}
