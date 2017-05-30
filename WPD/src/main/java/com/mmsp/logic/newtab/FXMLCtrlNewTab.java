package com.mmsp.logic.newtab;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Optional;
import java.util.TreeSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import com.mmsp.dao.DAO;
import com.mmsp.dao.impl.DAO_HandBookDiscipline;
import com.mmsp.dao.impl.DAO_Module;
import com.mmsp.dao.impl.DAO_PoCM;
import com.mmsp.dao.impl.DAO_Section;
import com.mmsp.dao.impl.DAO_Semester;
import com.mmsp.dao.impl.DAO_ThematicPlan;
import com.mmsp.dao.impl.DAO_WPDVersion;
import com.mmsp.logic.FXMLCtrlMain;
import com.mmsp.model.HandbookDiscipline;
import com.mmsp.model.Module;
import com.mmsp.model.PoCM;
import com.mmsp.model.Record;
import com.mmsp.model.Section;
import com.mmsp.model.Semester;
import com.mmsp.model.ThematicPlan;
import com.mmsp.model.WPDVersion;
import com.mmsp.util.GenerateDoc;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FXMLCtrlNewTab extends VBox {

    // класс строки
    public static class RowSL { // RowStudyLoad
        private final SimpleStringProperty viewOfStudyLoad; // Вид учебной
                                                            // нагрузки
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

    static final Logger log = LogManager.getLogger(FXMLCtrlNewTab.class);

    private enum Response {
        SAVE, CANCEL
    } // Кнопки диалогового окна при закрытии вкладки WPDVersion

    private Stage stage;

    private FXMLCtrlNewTab fxmlCtrlCurrTab; // Контроллер этой вкладки

    private FXMLCtrlGeneral fxmlCtrlGeneral; // Вкладка "Общие"

    private FXMLCtrlThematicalPlan fxmlCtrlThematicalPlan; // Вкладка
                                                           // "Тематический
                                                           // план"

    private FXMLCtrlTable71 fxmlCtrlTable71; // Вкладка "Таблица 7.1"

    private FXMLCtrlMain parentCtrl; // Контроллер родительской вкладки

    private String tabName; // здесь полное название вкладки, возможно стоило
                            // хранить только название версии, т.к. название
                            // дисциплины пока не менятся

    private WPDVersion currWPDVersion; // WPDVersion принадлежащая этой вкладке

    // шапка текущей вкладки

    @FXML
    private TextField tfVersion;

    @FXML
    private ListView<String> lvTypeOfControlMeasures;

    @FXML
    private DatePicker dpDateOfCreate;

    @FXML
    private TextField tfPath; // Путь до шаблона

    @FXML
    private Button bCallFileChooser;

    private PopOver popOver; // Окно добавления/изменения № семестров и их
                             // недель

    @FXML
    private Button bSemester;

    @FXML
    private Button bSave;

    // класс, отвечающий за генерацию
    private GenerateDoc gDoc = new GenerateDoc();

    @FXML
    private Button bGenerate;

    @FXML
    private Button bDelete;

    @FXML
    private TabPane mainTabPane;

    // *************************************************************************************************************************
    // *************************************************************************************************************************
    // **
    // ** Контроллеры кнопок на верхней панельке
    // **
    // *************************************************************************************************************************
    // *************************************************************************************************************************

    /**
     * Создаёт окно со списком ( № семестров ; количество недель )
     * 
     * @param event
     */
    @FXML
    void clickBSemester(ActionEvent event) {
        VBox vbForSemester = new VBox(5);
        vbForSemester.setAlignment(Pos.CENTER);
        Button bSaveSemester = new Button("Сохранить");

        ChangeListener<String> cl2 = new ChangeListener<String>() { // http://stackoverflow.com/questions/12956061/javafx-oninputmethodtextchanged-not-called-after-focus-is-lost

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (isInteger(newValue) && isSatisfies(newValue))
                    bSaveSemester.setDisable(false);
                else
                    bSaveSemester.setDisable(true);
            }

            private boolean isInteger(String sValue) { // проверка на ввод и что
                                                       // б в Integer помещалось
                try {
                    Integer.parseInt(sValue);
                    return true;
                } catch (NumberFormatException ex) {
                    return false;
                }
            }

            private boolean isSatisfies(String sValue) {
                if (sValue.equals("") || sValue.length() > 2)
                    return false;
                return true;
            }
        };

        for (Semester sem : currWPDVersion.getTreeSemesters()) {
            HBox hbForSem = new HBox(5);
            hbForSem.setAlignment(Pos.CENTER);

            hbForSem.setId(String.valueOf(sem.getNUMBER_OF_SEMESTER()));

            TextField tfForNumber = new TextField(String.valueOf(sem.getNUMBER_OF_SEMESTER()));
            tfForNumber.setId("numOfSem " + sem.getNUMBER_OF_SEMESTER());
            TextField tfForQuantity = new TextField(String.valueOf(sem.getQUANTITY_OF_WEEK()));
            tfForQuantity.setId("quaOfWeek " + sem.getNUMBER_OF_SEMESTER());
            tfForNumber.textProperty().addListener(cl2);
            tfForQuantity.textProperty().addListener(cl2);

            Button bRemoveSem = new Button("-");

            bRemoveSem.setId(String.valueOf(sem.getNUMBER_OF_SEMESTER()));
            bRemoveSem.setOnAction(e -> {
                clickRemoveSemester(e);
                popOver.getRoot().autosize();
                popOver.show(bSemester);
            });

            hbForSem.getChildren().addAll(tfForNumber, tfForQuantity, bRemoveSem);
            hbForSem.setId(String.valueOf(sem.getNUMBER_OF_SEMESTER()));
            VBox.setMargin(hbForSem, new Insets(0, 15, 0, 15));
            vbForSemester.getChildren().add(hbForSem);
        }

        Button bAddSemester = new Button("Добавить семестр");
        bAddSemester.setOnAction(e -> {
            HBox hbForSem = new HBox(5);
            hbForSem.setAlignment(Pos.CENTER);

            TextField tfForNumber = new TextField();
            tfForNumber.setPromptText("№ семестра");
            tfForNumber.setId("numOfSem");
            TextField tfForQuantity = new TextField();
            tfForQuantity.setPromptText("количество недель");
            tfForQuantity.setId("quaOfWeek");
            tfForNumber.textProperty().addListener(cl2);
            tfForQuantity.textProperty().addListener(cl2);

            Button bRemoveSem = new Button("-");
            bRemoveSem.setTooltip(new Tooltip("Удалить данный семестр"));
            bRemoveSem.setOnAction(ev -> {
                clickRemoveSemester(ev);
                popOver.getRoot().autosize();
                popOver.show(bSemester);
            });

            hbForSem.getChildren().addAll(tfForNumber, tfForQuantity, bRemoveSem);
            hbForSem.setId("");
            VBox.setMargin(hbForSem, new Insets(0, 15, 0, 15));
            vbForSemester.getChildren().add(vbForSemester.getChildren().size() - 2, hbForSem);
            popOver.getRoot().autosize();
            popOver.show(bSemester);
        });
        vbForSemester.getChildren().add(bAddSemester);

        bSaveSemester.setOnAction(e -> clickBSaveSemester(e));
        vbForSemester.getChildren().add(bSaveSemester);
        vbForSemester.setPadding(new Insets(20, 20, 20, 20));

        createPopOver();
        popOver.setContentNode(vbForSemester);
        popOver.getRoot().autosize();
        popOver.show(bSemester);
        popOver.setOnCloseRequest(e -> {
            if (currWPDVersion.getTreeSemesters().size() != 0)
                bSemester.setText(currWPDVersion.getStringSemester());
            else
                bSemester.setText("Добавить");
        });
    }

    private PopOver createPopOver() {
        if (popOver == null)
            popOver = new PopOver();

        popOver.setDetachable(false);
        popOver.setDetached(false);
        popOver.arrowSizeProperty().set(10);
        popOver.setArrowLocation(ArrowLocation.TOP_RIGHT);
        popOver.arrowIndentProperty().set(10);
        popOver.cornerRadiusProperty().set(8);
        popOver.headerAlwaysVisibleProperty().set(false);
        return popOver;
    }

    /**
     * Сохраняет текущее содержание семестров и удаляет из
     * currWPDVersion.getSemester удалённые из этого окна
     * 
     * @param e
     */
    // TODO При изменении номера непустого семестра всё крашиться
    private void clickBSaveSemester(ActionEvent e) {
        VBox vbForSemester = (VBox) ((Button) e.getSource()).getParent();
        for (Iterator<Semester> i = currWPDVersion.getTreeSemesters().iterator(); i.hasNext();) {
            Semester semesterTemp = i.next();
            boolean delete = true;
            for (Node nodeTemp : vbForSemester.getChildren()) {
                if (nodeTemp instanceof HBox) {
                    if (nodeTemp.getId().equals(String.valueOf(semesterTemp.getNUMBER_OF_SEMESTER()))) { // Изменить
                                                                                                         // существующий
                        semesterTemp.setNUMBER_OF_SEMESTER(getNumberOfSemester((HBox) nodeTemp));
                        semesterTemp.setQUANTITY_OF_WEEK(getQuantityOfWeek((HBox) nodeTemp));
                        delete = false;
                    }
                }
            }
            if (delete)
                i.remove();
        }

        for (Node nodeTemp : vbForSemester.getChildren()) { // Добаение новых
                                                            // узлов
            if (nodeTemp instanceof HBox) {
                if ("".equals(nodeTemp.getId())) { // Добавить семестр
                    int numOfSem = getNumberOfSemester((HBox) nodeTemp);
                    int quaOfWeek = getQuantityOfWeek((HBox) nodeTemp);
                    if (numOfSem > 0 || quaOfWeek > 0) {
                        Semester newSem = new Semester();
                        newSem.setNUMBER_OF_SEMESTER(numOfSem);
                        newSem.setQUANTITY_OF_WEEK(quaOfWeek);
                        newSem.setWPDVersion(currWPDVersion);
                        currWPDVersion.getTreeSemesters().add(newSem);

                        for (Node node : ((HBox) nodeTemp).getChildren()) { // раздадим
                                                                            // id
                                                                            // новым
                                                                            // полям
                                                                            // нового
                                                                            // семестра
                            if (node instanceof TextField) {
                                if ("numOfSem".equals(node.getId()))
                                    node.setId("numOfSem " + newSem.getNUMBER_OF_SEMESTER());
                                if ("quaOfWeek".equals(node.getId()))
                                    node.setId("quaOfWeek " + newSem.getNUMBER_OF_SEMESTER());
                            }
                            if (node instanceof Button) {
                                node.setId("" + newSem.getNUMBER_OF_SEMESTER());
                            }
                        }
                        nodeTemp.setId(String.valueOf(newSem.getNUMBER_OF_SEMESTER()));
                    }
                }
            }
        }

        if (currWPDVersion.getTreeSemesters().size() != 0)
            bSemester.setText(currWPDVersion.getStringSemester());
        else
            bSemester.setText("Добавить");

        fxmlCtrlTable71.refresh();
        fxmlCtrlThematicalPlan.refresh();
    }

    /**
     * Достаёт из нужного TextField количество недель
     * 
     * @param nodeTemp
     * @return
     */
    private int getQuantityOfWeek(HBox hboxTemp) {
        for (Node node : hboxTemp.getChildren()) {
            if (node instanceof TextField && node.getId().contains("quaOfWeek")) {
                int temp = 0;
                try {
                    temp = Integer.parseInt(((TextField) node).getText());
                } catch (NumberFormatException e) {
                    temp = 0;
                }
                return temp;
            }
        }
        return 0;
    }

    /**
     * Достаёт из нужного TextField номер семестра
     * 
     * @param hboxTemp
     * @return
     */
    private int getNumberOfSemester(HBox hboxTemp) {
        for (Node node : hboxTemp.getChildren()) {
            if (node instanceof TextField && node.getId().contains("numOfSem")) {
                int temp = 0;
                try {
                    temp = Integer.parseInt(((TextField) node).getText());
                } catch (NumberFormatException e) {
                    temp = 0;
                }
                return temp;
            }
        }
        return 0;
    }

    /**
     * Удаляет HBox на кнопку рядом с ним
     * 
     * @param e
     */
    // ERROR Если был удалён тот семестр, что выведен в ssvTableTP, то его надо
    // бы отчистить
    private void clickRemoveSemester(ActionEvent e) {
        // Приводим к VBox // к hbForSem // к vbForSemester
        VBox vbForSemester = (VBox) ((Button) e.getSource()).getParent().getParent();

        if (((Button) e.getSource()).getId() == null)
            vbForSemester.getChildren().remove((HBox) ((Button) e.getSource()).getParent());
        else
            for (Node nodeTemp : vbForSemester.getChildren()) {
                if (nodeTemp instanceof HBox && ((Button) e.getSource()).getId().equals(nodeTemp.getId())) {
                    vbForSemester.getChildren().remove(nodeTemp);
                    break;
                }
            }
    }

    @FXML
    void clickBFileChooser(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть шаблон");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            tfPath.setText(file.getPath());
        }
    }

    // TODO first достать данные из полей и вставить их в объект PoCM
    @FXML
    void clickBSave(ActionEvent event) {
        save();
    }

    /**
     * Генерация по шаблону
     * 
     * @param event
     */
    @FXML
    void clickBGenerate(ActionEvent event) {
        gDoc.generate(currWPDVersion); // генерируем
    }

    /**
     * Удаление данной версии (вызывает окно для подтверждения) Кнопка "Удалить"
     * 
     * @param event
     */
    @FXML
    void clickBDelete(ActionEvent event) {
        log.info("Удаление WPDVersion");
        switch (showDialogSave()) {
        case SAVE:
            delete();
            break;
        case CANCEL:
            break;
        }
    }

    private Response showDialogSave() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Удаление версии");
        alert.setHeaderText(null);
        alert.setContentText("Вы уверены, что хотите удалить данную версию?");

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("Logo.png"));

        ButtonType buttonSave = new ButtonType("Удалить");
        ButtonType buttonCancel = new ButtonType("Отмена");

        alert.getButtonTypes().setAll(buttonSave, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonSave) {
            return Response.SAVE;
        } else {
            return Response.CANCEL;
        }
    }

    /**
     * Удаляет текущую версию
     */
    public void delete() {

        Long id = currWPDVersion.getId();

        // Закрываем вкладку
        parentCtrl.closeTab(id);

        DAO_HandBookDiscipline dao_hbd = new DAO_HandBookDiscipline();
        HandbookDiscipline hbd = dao_hbd.getById(HandbookDiscipline.class, currWPDVersion.getHbD().getId());
        if (!hbd.getVersions().removeIf(p -> p.getId().equals(id)))
            System.err.println("Не смог удалить версии в hbd");
        else
            dao_hbd.update(hbd);

        System.err.println(hbd.toString()); // посмотрим, что там

        DAO_WPDVersion dao_vers = new DAO_WPDVersion();
        dao_vers.remove(currWPDVersion);

        // Вывод всех версий, которые ссылаются на данную дисциплину
        /*
         * List<WPDVersion> listOfVersion = dao_vers.getAllByNumber(id); for
         * (int i = 0; i < listOfVersion.size(); i++) {
         * System.err.println("Name = " + listOfVersion.get(i).getName() +
         * "\nID = " + listOfVersion.get(i).getId().toString()); if
         * (listOfVersion.get(i).getPoCM() != null) {
         * System.err.println("PoCM == " +
         * listOfVersion.get(i).getPoCM().toString()); } else
         * System.err.println("PoCM == null"); if
         * (listOfVersion.get(i).getThematicPlans() != null) { if
         * (listOfVersion.get(i).getThematicPlans().size() != 0) for
         * (ThematicPlan tp : listOfVersion.get(i).getThematicPlans())
         * System.err.println("ThematicPlan == " + tp.toString()); } else
         * System.err.println("ThematicPlan == null"); }
         */
    }

    // *************************************************************************************************************************
    // *************************************************************************************************************************
    // **
    // ** Вкладка "Общие"
    // **
    // *************************************************************************************************************************
    // *************************************************************************************************************************

    /*
     * @FXML void clickBAddRowStudyLoad(ActionEvent event) {
     * olDataOfStudyLoad.add(new RowSL("", "", "")); }
     * 
     * @FXML void clickBDeleteRowStudyLoad(ActionEvent event) { int
     * selectedIndex = tvStudyLoad.getSelectionModel().getSelectedIndex();
     * //System.out.println("Del: " + selectedIndex); // int row =
     * myTableView.getSelectionRow(); // int col =
     * myTableView.getSelectionCol(); //
     * myTableView.getModel().getSelectedValue(row,col); if (selectedIndex >= 0)
     * tvStudyLoad.getItems().remove(selectedIndex); // for (int i = 0; i <
     * dataOfStudyLoad.size(); i++)
     * System.out.println(dataOfStudyLoad.get(i).toString()); }
     */

    // *************************************************************************************************************************
    // *************************************************************************************************************************
    // **
    // ** Вкладка "Тематический план"
    // **
    // *************************************************************************************************************************
    // *************************************************************************************************************************

    /**
     * Обновление содержимого вкладки "Тематический план"
     */
    // private void loadTabThematicalPlan() {
    // createTree();
    //
    // tvRoot.addEventHandler(MouseEvent.MOUSE_RELEASED, new
    // EventHandler<MouseEvent>() {
    //
    // private ContextMenu menu = new ContextMenu();
    //
    // @Override
    // public void handle(MouseEvent event) {
    // if (event.getButton() == MouseButton.SECONDARY) {
    // TreeItem<String> selected = tvRoot.getSelectionModel().getSelectedItem();
    //
    // if (selected != null) {
    // openContextMenu(selected, event.getScreenX(), event.getScreenY());
    // }
    // } else {
    // menu.hide();
    // }
    // }
    //
    // private void openContextMenu(TreeItem<String> item, double x, double y) {
    // menu.getItems().clear();
    // MenuItem mI;
    //
    // List<MenuItem> liMI = new ArrayList<MenuItem>();
    // if (item.getValue().contains("Семестр")) {
    // mI = new MenuItem("Добавить модуль");
    // mI.setOnAction(e -> {
    // showModalModule(item);
    // });
    // liMI.add(mI);
    // }
    // if (item.getValue().contains("Модуль")) {
    // mI = new MenuItem("Добавить раздел");
    // mI.setOnAction(e -> {
    // showModalSection(item);
    // });
    // liMI.add(mI);
    // mI = new MenuItem("Изменить модуль");
    // mI.setOnAction(e -> {
    // showModalModule(item);
    // });
    // liMI.add(mI);
    // mI = new MenuItem("Удалить модуль");
    // mI.setOnAction(e -> {
    // int numOfSem = Integer.parseInt(item.getParent().getValue().split("
    // ")[1]);
    // int numOfMod = Integer.parseInt(item.getValue().split(" ")[1]);
    //
    // currWPDVersion.getSemester(numOfSem).getTreeModule().remove(currWPDVersion.getSemester(numOfSem).getModule(numOfMod));
    // createTree();
    //
    // // TEST
    // System.err.println(currWPDVersion.toString());
    // });
    // liMI.add(mI);
    // }
    // if (item.getValue().contains("Раздел")) {
    // mI = new MenuItem("Добавить тему");
    // mI.setOnAction(e -> {
    // showModalTheme(item);
    // });
    // liMI.add(mI);
    // mI = new MenuItem("Изменить раздел");
    // mI.setOnAction(e -> {
    // showModalSection(item);
    // });
    // liMI.add(mI);
    // mI = new MenuItem("Удалить раздел");
    // mI.setOnAction(e -> {
    // int numOfSem =
    // Integer.parseInt(item.getParent().getParent().getValue().split(" ")[1]);
    // int numOfMod = Integer.parseInt(item.getParent().getValue().split("
    // ")[1]);
    // int numOfSec = Integer.parseInt(item.getValue().split(" ")[1]);
    //
    // currWPDVersion.getSemester(numOfSem).getModule(numOfMod).getTreeSection().remove(currWPDVersion.getSemester(numOfSem).getModule(numOfMod).getSection(numOfSec));
    // createTree();
    //
    // // TEST
    // System.err.println(currWPDVersion.toString());
    // });
    // liMI.add(mI);
    // }
    // if (item.getValue().contains("Тема")) {
    // mI = new MenuItem("Изменить тему");
    // mI.setOnAction(e -> {
    // showModalTheme(item);
    // });
    // liMI.add(mI);
    // mI = new MenuItem("Удалить тему");
    // mI.setOnAction(e -> {
    // int numOfSem =
    // Integer.parseInt(item.getParent().getParent().getParent().getValue().split("
    // ")[1]);
    // int numOfMod =
    // Integer.parseInt(item.getParent().getParent().getValue().split(" ")[1]);
    // int numOfSec = Integer.parseInt(item.getParent().getValue().split("
    // ")[1]);
    // int numOfTheme = Integer.parseInt(item.getValue().split(" ")[1]);
    //
    // currWPDVersion.getSemester(numOfSem).getModule(numOfMod).getSection(numOfSec).getTreeTheme().remove(currWPDVersion.getSemester(numOfSem).getModule(numOfMod).getSection(numOfSec).getTheme(numOfTheme));
    // createTree();
    //
    // // TEST
    // System.err.println(currWPDVersion.toString());
    // });
    // liMI.add(mI);
    // }
    //
    // menu.getItems().addAll(liMI);
    //
    // //show menu
    // menu.show(tvRoot, x, y);
    // }
    //
    // /**
    // * Вызов диалогового окна добавления / изменения модуля
    // * @param item если содежит в себе "Модуль", то редактирование, если
    // "Семестр", то добавление
    // */
    // private void showModalModule(TreeItem<String> item) {
    // FXMLLoader fxmlLoader = new
    // FXMLLoader(getClass().getClassLoader().getResource("modal/ModalModule.fxml"));
    // Parent root = null;
    // try {
    // root = (Parent) fxmlLoader.load();
    // } catch (IOException e) {
    // System.err.println("Не удалось загрузить форму настройки таблицы
    // тематического плана");
    // e.printStackTrace();
    // }
    // Scene scene = new Scene(root);
    //
    // Stage stageModalModule = new Stage();
    // FXMLCtrlModalModule fxmlCtrlModalModule = fxmlLoader.getController();
    // fxmlCtrlModalModule.init(stageModalModule);
    // stageModalModule.setResizable(false);
    // fxmlCtrlModalModule.setController(fxmlCtrlCurrTab); // Запомним
    // контроллер новой вкладки для перерсовки
    //
    // // установка корневого элемента
    // if (item.getValue().contains("Модуль")) {
    // // Изменение выбранного модуля
    // int numOfSem = Integer.parseInt(item.getParent().getValue().split("
    // ")[1]);
    // int numOfMod = Integer.parseInt(item.getValue().split(" ")[1]);
    // fxmlCtrlModalModule.setRoot(currWPDVersion.getSemester(numOfSem),
    // numOfMod);
    // } else
    // if (item.getValue().contains("Семестр")) {
    // // Добавление нового модуля
    // int numOfSem = Integer.parseInt(item.getValue().split(" ")[1]);
    // fxmlCtrlModalModule.setRoot(currWPDVersion.getSemester(numOfSem));
    // } else return;
    //
    // stageModalModule.setScene(scene);
    // stageModalModule.setTitle("Добавление / изменение модуля");
    // stageModalModule.getIcons().add(new Image("Logo.png"));
    // stageModalModule.initModality(Modality.APPLICATION_MODAL);
    // stageModalModule.showAndWait();
    // }
    //
    // /**
    // * Вызов диалогового окна добавления / изменения секции
    // * @param item если содежит в себе "Раздел", то редактирование, если
    // "Модуль", то добавление
    // */
    // private void showModalSection(TreeItem<String> item) {
    // FXMLLoader fxmlLoader = new
    // FXMLLoader(getClass().getClassLoader().getResource("modal/ModalSection.fxml"));
    // Parent root = null;
    // try {
    // root = (Parent) fxmlLoader.load();
    // } catch (IOException e) {
    // System.err.println("Не удалось загрузить форму настройки таблицы
    // тематического плана");
    // e.printStackTrace();
    // }
    // Scene scene = new Scene(root);
    //
    // Stage stageModalSection = new Stage();
    // FXMLCtrlModalSection fxmlCtrlModalSection = fxmlLoader.getController();
    // fxmlCtrlModalSection.init(stageModalSection);
    // stageModalSection.setResizable(false);
    // fxmlCtrlModalSection.setController(fxmlCtrlCurrTab); // Запомним
    // контроллер новой вкладки для перерсовки
    //
    // // установка корневого элемента
    // if (item.getValue().contains("Раздел")) {
    // // Изменение выбранного раздела
    // int numOfSem =
    // Integer.parseInt(item.getParent().getParent().getValue().split(" ")[1]);
    // int numOfMod = Integer.parseInt(item.getParent().getValue().split("
    // ")[1]);
    // int numOfSec = Integer.parseInt(item.getValue().split(" ")[1]);
    // fxmlCtrlModalSection.setRoot(currWPDVersion.getSemester(numOfSem),
    // numOfMod, numOfSec);
    // } else
    // if (item.getValue().contains("Модуль")) {
    // // Добавление нового модуля
    // int numOfSem = Integer.parseInt(item.getParent().getValue().split("
    // ")[1]);
    // int numOfMod = Integer.parseInt(item.getValue().split(" ")[1]);
    // fxmlCtrlModalSection.setRoot(currWPDVersion.getSemester(numOfSem),
    // numOfMod);
    // } else return;
    //
    // stageModalSection.setScene(scene);
    // stageModalSection.setTitle("Добавление / изменение раздела");
    // stageModalSection.getIcons().add(new Image("Logo.png"));
    // stageModalSection.initModality(Modality.APPLICATION_MODAL);
    // stageModalSection.showAndWait();
    // }
    //
    // /**
    // * Вызов диалогового окна добавления / изменения секции
    // * @param item если содежит в себе "Раздел", то редактирование, если
    // "Модуль", то добавление
    // */
    // private void showModalTheme(TreeItem<String> item) {
    // FXMLLoader fxmlLoader = new
    // FXMLLoader(getClass().getClassLoader().getResource("modal/ModalTheme.fxml"));
    // Parent root = null;
    // try {
    // root = (Parent) fxmlLoader.load();
    // } catch (IOException e) {
    // System.err.println("Не удалось загрузить форму настройки таблицы
    // тематического плана");
    // e.printStackTrace();
    // }
    // Scene scene = new Scene(root);
    //
    // Stage stageModalTheme = new Stage();
    // FXMLCtrlModalTheme fxmlCtrlModalTheme = fxmlLoader.getController();
    // fxmlCtrlModalTheme.init(stageModalTheme);
    // stageModalTheme.setResizable(false);
    // fxmlCtrlModalTheme.setController(fxmlCtrlCurrTab); // Запомним контроллер
    // новой вкладки для перерсовки
    //
    // // установка корневого элемента
    // if (item.getValue().contains("Тема")) {
    // // Изменение выбранной темы
    // int numOfSem =
    // Integer.parseInt(item.getParent().getParent().getParent().getValue().split("
    // ")[1]);
    // int numOfMod =
    // Integer.parseInt(item.getParent().getParent().getValue().split(" ")[1]);
    // int numOfSec = Integer.parseInt(item.getParent().getValue().split("
    // ")[1]);
    // int numOfTheme = Integer.parseInt(item.getValue().split(" ")[1]);
    // fxmlCtrlModalTheme.setRoot(currWPDVersion.getSemester(numOfSem),
    // numOfMod, numOfSec, numOfTheme);
    // } else
    // if (item.getValue().contains("Раздел")) {
    // // Добавление новой темы
    // int numOfSem =
    // Integer.parseInt(item.getParent().getParent().getValue().split(" ")[1]);
    // int numOfMod = Integer.parseInt(item.getParent().getValue().split("
    // ")[1]);
    // int numOfSec = Integer.parseInt(item.getValue().split(" ")[1]);
    // fxmlCtrlModalTheme.setRoot(currWPDVersion.getSemester(numOfSem),
    // numOfMod, numOfSec);
    // } else return;
    //
    // stageModalTheme.setScene(scene);
    // stageModalTheme.setTitle("Добавление / изменение темы");
    // stageModalTheme.getIcons().add(new Image("Logo.png"));
    // stageModalTheme.initModality(Modality.APPLICATION_MODAL);
    // stageModalTheme.showAndWait();
    // }
    // });
    // }

    // *************************************************************************************************************************
    // *************************************************************************************************************************
    // **
    // ** Выгрузка в компоненты содержимого БД
    // **
    // *************************************************************************************************************************
    // *************************************************************************************************************************

    // /**
    // * Создаёт строку для указанной позиции
    // * @param posRow позиция для новой строки
    // * @param lValueOfOldCell список значений ячеек.
    // * @return строку
    // */
    // private ObservableList<SpreadsheetCell> createRowForTTP(int posRow,
    // List<String> lValueOfOldCell) {
    // ObservableList<SpreadsheetCell> olRow =
    // FXCollections.observableArrayList();
    // if (lValueOfOldCell == null) { // Используется для создания новой строки
    // for (int column = 0; column < 4; column++) {
    // SpreadsheetCell ssC = SpreadsheetCellType.INTEGER.createCell(posRow,
    // column, 1, 1, 0);
    // ssC.setEditable(false);
    // olRow.add(ssC);
    // }
    // for (int column = 4; column < 6; column++) {
    // olRow.add(SpreadsheetCellType.STRING.createCell(posRow, column, 1, 1,
    // ""));
    // }
    // for (int column = 6; column < ssvTableTP.getGrid().getColumnCount();
    // column++) {
    // olRow.add(SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1,
    // 0));
    // }
    // } else { // Используется при удалении строки и переносе значений на
    // строку выше, и вставки строки
    // for (int column = 0; column < 4; column++) {
    // int temp = 0;
    // try {
    // temp = Integer.parseInt(lValueOfOldCell.get(column));
    // } catch (NumberFormatException | NullPointerException e) {
    // temp = 0;
    // }
    //
    // SpreadsheetCell ssC = SpreadsheetCellType.INTEGER.createCell(posRow,
    // column, 1, 1, temp);
    // ssC.setEditable(false);
    // olRow.add(ssC);
    // }
    // for (int column = 4; column < 6; column++) {
    // SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(posRow,
    // column, 1, 1, lValueOfOldCell.get(column));
    // ((SpreadsheetCellBase) cell).setTooltip(lValueOfOldCell.get(column)); //
    // add tooltip
    // olRow.add(cell);
    // }
    // for (int column = 6; column < ssvTableTP.getGrid().getColumnCount();
    // column++) {
    // int temp = 0;
    // try {
    // temp = Integer.parseInt(lValueOfOldCell.get(column));
    // } catch (NumberFormatException | NullPointerException e) {
    // temp = 0;
    // }
    // olRow.add(SpreadsheetCellType.INTEGER.createCell(posRow, column, 1, 1,
    // temp));
    // }
    // }
    // return olRow;
    // }

    /**
     * Загрузка по ID версии полей в этой вкладке
     * 
     * @param id_Vers
     *            ID версии
     */
    private void load(Long id_Vers) {

        DAO<WPDVersion> dao_Vers = new DAO_WPDVersion();
        currWPDVersion = dao_Vers.getById(WPDVersion.class, id_Vers);

        // TODO Уточнить: так и должно быть? Сортировка загруженного Set из
        // Hibernate
        for (Semester sem : currWPDVersion.getTreeSemesters()) {
            for (Module mod : sem.getTreeModule()) {
                for (Section sec : mod.getTreeSection()) {
                    TreeSet<ThematicPlan> setTheme = new TreeSet<>((ThematicPlan a, ThematicPlan b) -> a.compareTo(b));
                    setTheme.addAll(sec.getTreeTheme());
                    sec.getTreeTheme().clear();
                    sec.setTreeTheme(setTheme);
                }
                TreeSet<Section> setSec = new TreeSet<>((Section a, Section b) -> a.compareTo(b));
                setSec.addAll(mod.getTreeSection());
                mod.getTreeSection().clear();
                mod.setTreeSection(setSec);
            }
            TreeSet<Module> setMod = new TreeSet<>((Module a, Module b) -> a.compareTo(b));
            setMod.addAll(sem.getTreeModule());
            sem.getTreeModule().clear();
            sem.setTreeModule(setMod);
        }
        TreeSet<Semester> setSem = new TreeSet<>((Semester a, Semester b) -> a.compareTo(b));
        setSem.addAll(currWPDVersion.getTreeSemesters());
        currWPDVersion.getTreeSemesters().clear();
        currWPDVersion.setTreeSemesters(setSem);
        // Конец сортировки

        if (currWPDVersion.getThematicPlans() != null) {
            for (ThematicPlan theme : currWPDVersion.getThematicPlans())
                fxmlCtrlThematicalPlan.addTheme(theme);
        }

        if (currWPDVersion.getPoCM() == null) {
            PoCM newPoCM = new PoCM();
            currWPDVersion.setPoCM(newPoCM);
            newPoCM.setWpdVersion(currWPDVersion);
            DAO_PoCM dao_PoCM = new DAO_PoCM();
            newPoCM.setId(dao_PoCM.add(newPoCM));
            currWPDVersion.setPoCM(newPoCM);
        } else {
            // TODO Выгрузить в список RowPoCM and Загрузка полей во вкладку
        }

        tfVersion.setText(currWPDVersion.getName()); // Name должен всегда
                                                     // существовать
        if (currWPDVersion.getTemplateName() != null) // Грузим шаблон при
                                                      // открытии, но не при
                                                      // создании
            tfPath.setText(currWPDVersion.getTemplateName());

        /*
         * http://stackoverflow.com/questions/21242110/convert-java-util-date-to
         * -java-time-localdate
         */
        if (currWPDVersion.getDate() != null) {
            Instant instant = currWPDVersion.getDate().toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            dpDateOfCreate.setValue(zdt.toLocalDate()); // Попробуем достать
                                                        // дату создания
        } else
            dpDateOfCreate.setValue(LocalDate.now());

        /*
         * olSemesters.clear(); for (Semester sem :
         * currWPDVersion.getTreeSemesters()) {
         * olSemesters.add(String.valueOf(sem.getNUMBER_OF_SEMESTER())); } if
         * (olSemesters.size() == 0) currSemester = null; else {
         * cbSemesters.setItems(olSemesters);
         * cbSemesters.getSelectionModel().selectFirst();
         * bAddRowT71.setDisable(false); cbSemesters.setDisable(false);
         * currSemester = currWPDVersion.getTreeSemesters().iterator().next(); }
         */

        // Вывод того, что есть
        System.err.println(currWPDVersion.toString());
        System.err.println(currWPDVersion.getPoCM().toString());
    }

    // *************************************************************************************************************************
    // *************************************************************************************************************************
    // **
    // ** Блок инициализации компонентов
    // **
    // *************************************************************************************************************************
    // *************************************************************************************************************************

    // /**
    // * Задаёт header всей таблицы ssvTableTP
    // * @param grid BaseGrid этой таблицы
    // * @return первую строку
    // */
    // private ObservableList<ObservableList<SpreadsheetCell>>
    // setHeaderForTTP(GridBase grid) {
    // ObservableList<ObservableList<SpreadsheetCell>> rowsHeader =
    // FXCollections.observableArrayList();
    //
    // // 1-ая строка
    // final ObservableList<SpreadsheetCell> olHeader =
    // FXCollections.observableArrayList();
    // olHeader.add(SpreadsheetCellType.STRING.createCell(0, 0, 1, 1, "№
    // семестра")); // Принадлежность к модулю
    // olHeader.add(SpreadsheetCellType.STRING.createCell(0, 1, 1, 1, "№
    // модуля")); // Принадлежность к модулю
    // olHeader.add(SpreadsheetCellType.STRING.createCell(0, 2, 1, 1, "№
    // раздела")); // Принадлежность к модулю
    // olHeader.add(SpreadsheetCellType.STRING.createCell(0, 3, 1, 1, "№
    // темы")); // Принадлежность к модулю
    // olHeader.add(SpreadsheetCellType.STRING.createCell(0, 4, 1, 1,"Название
    // темы"));
    // olHeader.add(SpreadsheetCellType.STRING.createCell(0, 5, 1, 1,"Описание
    // темы"));
    // olHeader.add(SpreadsheetCellType.STRING.createCell(0, 6, 1, 1, "Л"));
    // olHeader.add(SpreadsheetCellType.STRING.createCell(0, 7, 1, 1, "ПЗ"));
    // olHeader.add(SpreadsheetCellType.STRING.createCell(0, 8, 1, 1, "ЛР"));
    // olHeader.add(SpreadsheetCellType.STRING.createCell(0, 9, 1, 1, "КСР"));
    // olHeader.add(SpreadsheetCellType.STRING.createCell(0, 10, 1, 1, "СРС"));
    //
    // // запрещает редактирование
    // for (SpreadsheetCell cell : olHeader) {
    // cell.setEditable(false);
    // }
    // rowsHeader.add(olHeader); // первая строка заполнена
    // return rowsHeader;
    // }

    // /**
    // * Specify a custom row height.
    // * @return Map
    // */
    // private Map<Integer, Double> generateRowHeight(int val) {
    // Map<Integer, Double> rowHeight = new HashMap<>();
    // rowHeight.put(0, 24.0); // For Header
    // for (int i = 1; i < val; i++) { // for other rows
    // rowHeight.put(i, 35.0);
    // }
    // return rowHeight;
    // }

    // /**
    // * Инициализация компонента ssvTableTP
    // */
    // private void initSSVTableTP()
    // // FIXME СРС должна быть String?
    // {
    // ehTP = new EventHandler<GridChange>() {
    // public void handle(GridChange change) {
    // System.err.println("TEST");
    // }
    // };
    // int rowCount = 1;
    // int columnCount = 11;
    // GridBase grid = new GridBase(rowCount, columnCount);
    //
    // //GridBase.MapBasedRowHeightFactory rowHeightFactory = new
    // GridBase.MapBasedRowHeightFactory(generateRowHeight(1));
    // //grid.setRowHeightCallback(rowHeightFactory);
    // grid.setRowHeightCallback(new
    // GridBase.MapBasedRowHeightFactory(generateRowHeight(1)));
    // ObservableList<ObservableList<SpreadsheetCell>> rows =
    // setHeaderForTTP(grid);
    // grid.setRows(rows);
    // grid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTP);
    // ssvTableTP = new SpreadsheetView(grid);
    // ssvTableTP.getStylesheets().add(getClass().getResource("/SpreadSheetView.css").toExternalForm());
    // ssvTableTP.setShowRowHeader(true);
    // ssvTableTP.setShowColumnHeader(true);
    //
    // hbReplacementThematicalPlan.getChildren().add(ssvTableTP); // FIXME USE
    // MasterDetailPane
    // HBox.setHgrow(ssvTableTP, Priority.ALWAYS);
    // HBox.setMargin(ssvTableTP, new Insets(15, 0, 15, 0));
    //
    // rootElement.setExpanded(true);
    // tvRoot.setRoot(rootElement);
    // tvRoot.getSelectionModel().selectedItemProperty().addListener(new
    // ChangeListener<TreeItem<String>>() {
    //
    // @Override
    // public void changed(ObservableValue<? extends TreeItem<String>>
    // observable, TreeItem<String> old_val, TreeItem<String> new_val) {
    //
    // saveTheme();
    //
    // if (new_val == null) return;
    // // Поиск выбранного элемента в treeRoot
    // if (new_val.getValue().split(" ")[0].equals("Семестр")) {
    // repaintSSVTableTP(
    // Integer.parseInt(new_val.getValue().split(" ")[1]) // берём № семестра
    // );
    // }
    // if (new_val.getValue().split(" ")[0].equals("Модуль")) {
    // repaintSSVTableTP(
    // Integer.parseInt(new_val.getParent().getValue().split(" ")[1]), // берём
    // № семестра
    // Integer.parseInt(new_val.getValue().split(" ")[1]) // берём № модуля
    // );
    // }
    // if (new_val.getValue().split(" ")[0].equals("Раздел")) {
    // repaintSSVTableTP(
    // Integer.parseInt(new_val.getParent().getParent().getValue().split("
    // ")[1]), // берём № семестра
    // Integer.parseInt(new_val.getParent().getValue().split(" ")[1]), // берём
    // № модуля
    // Integer.parseInt(new_val.getValue().split(" ")[1]) // берём № раздела
    // );
    // }
    // if (new_val.getValue().split(" ")[0].equals("Тема")) {
    // repaintSSVTableTP(
    // Integer.parseInt(new_val.getParent().getParent().getParent().getValue().split("
    // ")[1]), // берём № семестра
    // Integer.parseInt(new_val.getParent().getParent().getValue().split("
    // ")[1]), // берём № модуля
    // Integer.parseInt(new_val.getParent().getValue().split(" ")[1]), // берём
    // № раздела
    // Integer.parseInt(new_val.getValue().split(" ")[1]) // берём № темы
    // );
    // }
    // }
    //
    // });
    // tvRoot.setShowRoot(false);
    // //createTree();
    // }

    // /**
    // * сохраняет изменения темы из ssvTableTP
    // */
    // private void saveTheme() {
    // int rowCount = ssvTableTP.getGrid().getRowCount();
    // for (int i = 1; i < rowCount; i++) {
    // int belongingToTheSemester = (int)
    // ssvTableTP.getGrid().getRows().get(i).get(0).getItem();
    // int belongingToTheModule = (int)
    // ssvTableTP.getGrid().getRows().get(i).get(1).getItem();
    // int belongingToTheSection = (int)
    // ssvTableTP.getGrid().getRows().get(i).get(2).getItem();
    // int numberOfTheme = (int)
    // ssvTableTP.getGrid().getRows().get(i).get(3).getItem();
    //
    // ThematicPlan theme =
    // currWPDVersion.getSemester(belongingToTheSemester).getModule(belongingToTheModule).getSection(belongingToTheSection).getTheme(numberOfTheme);
    //
    // if (theme != null) {
    // //theme.setWPDVerion(currWPDVersion);
    // theme.setTitle((String)
    // ssvTableTP.getGrid().getRows().get(i).get(4).getItem());
    // theme.setDescription((String)
    // ssvTableTP.getGrid().getRows().get(i).get(5).getItem());
    // theme.setL((Integer)
    // ssvTableTP.getGrid().getRows().get(i).get(6).getItem());
    // theme.setPZ((Integer)
    // ssvTableTP.getGrid().getRows().get(i).get(7).getItem());
    // theme.setLR((Integer)
    // ssvTableTP.getGrid().getRows().get(i).get(8).getItem());
    // theme.setKSR((Integer)
    // ssvTableTP.getGrid().getRows().get(i).get(9).getItem());
    // theme.setSRS((Integer)
    // ssvTableTP.getGrid().getRows().get(i).get(10).getItem());
    // System.out.println("Theme save\n" + theme.toString());
    // }
    // }
    // }

    // /**
    // * перерисовывает содержимое ssvTableTP для выбранного в tvRoot элемента,
    // будь то модуль или раздел, или тема (тематический план)
    // * @param temp массив int, первое число определяет № модуля, 2-ое - №
    // раздела, 3-е № темы
    // */
    // private void repaintSSVTableTP(int... temp) {
    //
    // createSSVTableTP();
    //
    // List<ThematicPlan> liTheme = new ArrayList<>();
    // try {
    // switch (temp.length) {
    // case 0: break;
    // case 1:
    // for (Module module : currWPDVersion.getSemester(temp[0]).getTreeModule())
    // {
    // Set<Section> setSection = module.getTreeSection();
    // for (Section section : setSection) {
    // liTheme.addAll(section.getTreeTheme());
    // }
    // }
    // break;
    // case 2:
    // Set<Section> setSection =
    // currWPDVersion.getSemester(temp[0]).getModule(temp[1]).getTreeSection();
    // for (Section section : setSection) {
    // liTheme.addAll(section.getTreeTheme()); // скопируем у каждой секции
    // }
    // break;
    // case 3:
    // liTheme.addAll(currWPDVersion.getSemester(temp[0]).getModule(temp[1]).getSection(temp[2]).getTreeTheme());
    // break;
    // case 4:
    // liTheme.add(currWPDVersion.getSemester(temp[0]).getModule(temp[1]).getSection(temp[2]).getTheme(temp[3]));
    // break;
    // default:
    // throw new Exception("не правильное количество аргументов у массива
    // тематического плана");
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // pasteIntoSSVTableTP(liTheme);
    // }

    // /**
    // * Вставляет данные в ssvTableTP из liTheme
    // */
    // private void pasteIntoSSVTableTP(List<ThematicPlan> liTheme) {
    // for (ThematicPlan theme : liTheme) {
    // addRowSSVTableTP(theme);
    // }
    // }

    // /**
    // * Добавляет строку в ssvTableTP в соотвествии с № модуля, раздела, темы
    // * @param theme строка из liTheem или null, если хотим заполнить по
    // стандарту
    // */
    // private void addRowSSVTableTP(ThematicPlan theme) {
    //
    // GridBase newGrid = null;
    // if (ssvTableTP.getGrid() != null)
    // newGrid = new GridBase(ssvTableTP.getGrid().getRowCount() + 1,
    // ssvTableTP.getGrid().getColumnCount()); // Создадим сетку с +1 строкой
    // else newGrid = new GridBase(1, 11);
    // int newRowPos = ssvTableTP.getGrid().getRowCount(); // и количество строк
    //
    // ObservableList<ObservableList<SpreadsheetCell>> newRows =
    // ssvTableTP.getGrid().getRows(); // а так же существующие строки
    //
    // List<String> liValueOftheme = new ArrayList<String>();
    // liValueOftheme.add(String.valueOf(theme.getBelongingToTheSemester()));
    // liValueOftheme.add(String.valueOf(theme.getBelongingToTheModule()));
    // liValueOftheme.add(String.valueOf(theme.getBelongingToTheSection()));
    // liValueOftheme.add(String.valueOf(theme.getNumber()));
    // liValueOftheme.add(theme.getTitle());
    // liValueOftheme.add(theme.getDescription());
    // liValueOftheme.add(String.valueOf(theme.getL()));
    // liValueOftheme.add(String.valueOf(theme.getPZ()));
    // liValueOftheme.add(String.valueOf(theme.getLR()));
    // liValueOftheme.add(String.valueOf(theme.getKSR()));
    // liValueOftheme.add(String.valueOf(theme.getSRS()));
    //
    // //liValueOftheme.forEach(System.err::println);
    //
    // final ObservableList<SpreadsheetCell> olNew = createRowForTTP(newRowPos,
    // liValueOftheme); // Добавление на место последней строки пустой строки
    // newRows.add(olNew);
    //
    // newGrid.setRows(newRows);
    // newGrid.setRowHeightCallback(new
    // GridBase.MapBasedRowHeightFactory(generateRowHeight(newGrid.getRowCount())));
    // newGrid.addEventHandler(GridChange.GRID_CHANGE_EVENT, ehTP);
    // ssvTableTP.setGrid(newGrid);
    // }

    /**
     * Перенаправление с других классов в контроллер вкладки ThematicalPlan и
     * отрисовка дерева в tvRoot
     */
    public void createTree() {
        fxmlCtrlThematicalPlan.createTree();
    }

    public void init(Long id_Vers) {

        createTab(id_Vers);

        load(id_Vers); // Загрузка из БД

        bSemester.setText(currWPDVersion.getTreeSemesters().size() != 0 ? currWPDVersion.getStringSemester() : "");

        if (currWPDVersion.getTreeSemesters().size() != 0)
            bSemester.setText(currWPDVersion.getStringSemester());
        else
            bSemester.setText("Добавить");

        tfPath.textProperty().addListener((observable, oldValue, newValue) -> {
            currWPDVersion.setTemplateName(newValue);
            log.info("new Path == " + currWPDVersion.getTemplateName());
        });

        tfVersion.textProperty().addListener((observable, oldValue, newValue) -> {
            currWPDVersion.setName(newValue);
        });
    }

    /**
     * Создание вкладок: Общие, Тематический план, Таблица 71
     * 
     * @param id_Vers
     */
    private void createTab(Long id_Vers) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("newtab/General.fxml"));
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            log.error("Не удалось подгрузить вкладку \"Общие\"");
            throw new RuntimeException("Не удалось подгрузить вкладку \"Общие\"", e);
        }

        Tab t = new Tab();
        t.setText("Общие");
        fxmlCtrlGeneral = fxmlLoader.getController();

        fxmlCtrlGeneral.setStage(stage); // Запомним Stage главного окна
        fxmlCtrlGeneral.setController(fxmlCtrlGeneral); // Запомним контроллер
                                                        // главного окна
        fxmlCtrlGeneral.setParentCtrl(fxmlCtrlCurrTab);
        fxmlCtrlGeneral.init(currWPDVersion); // инициализируем

        t.setContent(root);
        t.setClosable(false);
        mainTabPane.getTabs().add(t);

        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("newtab/ThematicalPlan.fxml"));
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            log.error("Не удалось подгрузить вкладку \"Тематический план\"");
            throw new RuntimeException("Не удалось подгрузить вкладку \"Тематический план\"", e);
        }

        t = new Tab();
        t.setText("Тематический план");
        fxmlCtrlThematicalPlan = fxmlLoader.getController();

        fxmlCtrlThematicalPlan.setStage(stage);
        fxmlCtrlThematicalPlan.setController(fxmlCtrlThematicalPlan);
        fxmlCtrlThematicalPlan.setParentCtrl(fxmlCtrlCurrTab);
        fxmlCtrlThematicalPlan.init(currWPDVersion);

        t.setContent(root);
        t.setClosable(false);
        mainTabPane.getTabs().add(t);

        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("newtab/Table71.fxml"));
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            log.error("Не удалось подгрузить вкладку \"Таблица 7.1\"");
            throw new RuntimeException("Не удалось подгрузить вкладку \"Таблица 7.1\"", e);
        }

        t = new Tab();
        t.setText("Таблица 7.1");
        fxmlCtrlTable71 = fxmlLoader.getController();

        fxmlCtrlTable71.setStage(stage);
        fxmlCtrlTable71.setController(fxmlCtrlTable71);
        fxmlCtrlTable71.setParentCtrl(fxmlCtrlCurrTab);
        fxmlCtrlTable71.init(currWPDVersion);

        t.setContent(root);
        t.setClosable(false);
        mainTabPane.getTabs().add(t);

    }

    // *************************************************************************************************************************
    // *************************************************************************************************************************
    // **
    // ** Методы для работы со вкладкой и её контроллером
    // **
    // *************************************************************************************************************************
    // *************************************************************************************************************************

    public void setParentCtrl(FXMLCtrlMain fxmlCtrlMain) {
        this.parentCtrl = fxmlCtrlMain;
    }

    public void setStage(Stage stage2) {
        this.stage = stage2;
    }

    public void setController(FXMLCtrlNewTab fxmlCtrlNewTab) {
        this.fxmlCtrlCurrTab = fxmlCtrlNewTab;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String sValue) {
        this.tabName = sValue;
    }

    public WPDVersion getWPDVerison() {
        return currWPDVersion;
    }

    /**
     * Каскадное сохранение текущей WPDVersion
     */
    // TODO организовать каскадное сохранение
    public void save() {

        DAO_WPDVersion dao_wpdVersion = new DAO_WPDVersion();
        dao_wpdVersion.update(currWPDVersion);

        /*
         * http://stackoverflow.com/questions/20446026/get-value-from-date-
         * picker
         */
        /*
         * LocalDate localDate = dpDateOfCreate.getValue(); Instant instant =
         * Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
         * currWPDVersion.setDate(Date.from(instant));
         */ // Попробуем занести дату создания

        DAO<PoCM> dao_pocm = new DAO_PoCM();
        dao_pocm.update(currWPDVersion.getPoCM());

        // ОБНОВЛЕНИЕ ТЕМАТИЧЕСКОГО ПЛАНА
        // Удалить всё WPDVerison, кроме ID, Name, TemplateName (всего того, что
        // относится только к WPDVersion)
        // Сохранить новые содержащиеся в currWPD темы в БД

        DAO<Semester> dao_semester = new DAO_Semester();
        // Удаление всего ненужного из BD
        WPDVersion tempVersion = dao_wpdVersion.getById(WPDVersion.class, currWPDVersion.getId());
        for (Iterator<Semester> i = tempVersion.getTreeSemesters().iterator(); i.hasNext();) {
            Semester sem = i.next();
            dao_semester.remove(sem); // удаляем семестры, остальное должно
                                      // удалиться само
            i.remove();
        }
        dao_wpdVersion.update(tempVersion);

        // TEST
        /*
         * tempVersion = dao_wpdVersion.getById(WPDVersion.class,
         * currWPDVersion.getId()); System.err.println("NEW SIZE == " +
         * tempVersion.getTreeSemesters().size());
         */

        // Сохранение в БД
        for (Semester s : currWPDVersion.getTreeSemesters()) {
            s.setWPDVersion(currWPDVersion);
            s.setId(dao_semester.add(s));
            DAO<Record> dao_record = new DAO<Record>() {
            };
            for (Record rec : s.getRowT71()) {
                rec.setSemester(s);
                rec.setId(dao_record.add(rec));
            }
            DAO<Module> dao_module = new DAO_Module();
            for (Module mod : s.getTreeModule()) {
                mod.setId(dao_module.add(mod));
                mod.setSemester(s);
                DAO<Section> dao_section = new DAO_Section();
                for (Section sec : mod.getTreeSection()) {
                    sec.setId(dao_section.add(sec));
                    sec.setModule(mod);
                    DAO<ThematicPlan> dao_theme = new DAO_ThematicPlan();
                    for (ThematicPlan theme : sec.getTreeTheme()) {
                        theme.setSection(sec);
                        theme.setId(dao_theme.add(theme));
                    }
                }
            }
        }

        // TEST
        // tempVersion = dao_wpdVersion.getById(WPDVersion.class,
        // currWPDVersion.getId());
        // System.err.println("CHO TAM? SIZE ROWT71 == " +
        // tempVersion.getTreeSemesters().iterator().next().getRowT71().size());

        // Блок обновления названия вкладки и списка Версий в cbVersion
        if (!tabName.split(":")[1].equals(currWPDVersion.getName()))
        // Если сменилось название версии, то подгрузим контроллер
        // и изменим из него значение названия вкладки и обновим список названий
        // версий
        {
            parentCtrl.updateOlVersion(currWPDVersion.getHbD().getId()); // Обновляет
                                                                         // список,
                                                                         // содержащийся
                                                                         // в
                                                                         // cbVersion
            if (!parentCtrl.updateTabName(tabName, currWPDVersion.getName())) { // обновляет
                                                                                // название
                                                                                // вкладки
                System.err.println("Возникла ошибка при обновлении названия вкадки");
            }
        }

        // System.err.println(currWPDVersion.toString());

        // TEST
        /*
         * WPDVersion newWPDVersion = dao_wpdVersion.getById(WPDVersion.class,
         * currWPDVersion.getId());
         * System.err.println(newWPDVersion.toString()); // посмотрим что он там
         * сохранил
         */
    }
}
