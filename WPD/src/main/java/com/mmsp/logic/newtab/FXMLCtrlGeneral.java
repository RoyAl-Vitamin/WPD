package com.mmsp.logic.newtab;

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
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Вкладка "Общие" для FXMLCtrlNewTab
 * 
 * @author rav
 *
 */
public class FXMLCtrlGeneral extends HBox {

    public static class RowSL { // класс строки // RowStudyLoad
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

    class EditingCell extends TableCell<RowSL, String> { // Для UX, что б не
        // надо было после
        // редактирования жать
        // Enter

        private TextField textField;

        public EditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                    if (!arg2) {
                        commitEdit(textField.getText());
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

    private final ObservableList<RowSL> olDataOfStudyLoad = FXCollections.observableArrayList();

    @FXML
    private Button bAddRowStudyLoad;

    @FXML
    private Button bDeleteRowStudyLoad;

    @FXML // Вид учебной нагрузки
    private TableColumn<RowSL, String> colTVViewOfStudyLoad;

    @FXML // Часов
    private TableColumn<RowSL, String> colTVNumberOfHours;

    @FXML // ЗЕ
    private TableColumn<RowSL, String> colTVLadderpointsUnit;

    @FXML
    private TableView<RowSL> tvStudyLoad;

    private Stage stage;

    private FXMLCtrlGeneral fxmlCtrlGeneral;

    private FXMLCtrlNewTab parentCtrl;

    private WPDVersion wpdVersion;

    @FXML
    void clickBAddRowStudyLoad(ActionEvent event) {
        olDataOfStudyLoad.add(new RowSL("", "", ""));
    }

    @FXML
    void clickBDeleteRowStudyLoad(ActionEvent event) {
        int selectedIndex = tvStudyLoad.getSelectionModel().getSelectedIndex();
        // System.out.println("Del: " + selectedIndex);
        /*
         * int row = myTableView.getSelectionRow(); int col =
         * myTableView.getSelectionCol();
         * myTableView.getModel().getSelectedValue(row,col);
         */
        if (selectedIndex >= 0)
            tvStudyLoad.getItems().remove(selectedIndex);
        // for (int i = 0; i < dataOfStudyLoad.size(); i++)
        // System.out.println(dataOfStudyLoad.get(i).toString());
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

    // UNDONE Инициализация компонента номером версии
    public void init(WPDVersion wpdVers) {
        wpdVersion = wpdVers;
        initTvStudyLoad();
    }

    private void initTvStudyLoad() {
        Callback<TableColumn<RowSL, String>, TableCell<RowSL, String>> cellFactory = new Callback<TableColumn<RowSL, String>, TableCell<RowSL, String>>() {
            public TableCell<RowSL, String> call(TableColumn<RowSL, String> p) {
                return new EditingCell();
            }
        };
        // colTVViewOfStudyLoad.setMinWidth(150.0);
        colTVViewOfStudyLoad.setCellValueFactory(new PropertyValueFactory<RowSL, String>("viewOfStudyLoad"));
        // colViewOfStudyLoad.setCellFactory(TextFieldTableCell.forTableColumn());
        colTVViewOfStudyLoad.setCellFactory(cellFactory);
        colTVViewOfStudyLoad.setOnEditCommit(new EventHandler<CellEditEvent<RowSL, String>>() {

            @Override
            public void handle(CellEditEvent<RowSL, String> t) {
                ((RowSL) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                        .setViewOfStudyLoad(t.getNewValue());
            }
        });
        colTVNumberOfHours.setCellValueFactory(new PropertyValueFactory<RowSL, String>("numberOfHours"));
        // colNumberOfHours.setCellFactory(TextFieldTableCell.forTableColumn());
        colTVNumberOfHours.setCellFactory(cellFactory);
        colTVNumberOfHours.setOnEditCommit(new EventHandler<CellEditEvent<RowSL, String>>() {

            @Override
            public void handle(CellEditEvent<RowSL, String> t) {
                ((RowSL) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                        .setNumberOfHours(t.getNewValue());
            }
        });
        colTVLadderpointsUnit.setCellValueFactory(new PropertyValueFactory<RowSL, String>("ladderpointsUnit"));
        colTVLadderpointsUnit.setCellFactory(cellFactory);
        // colLadderpointsUnit.setCellFactory(TextFieldTableCell.forTableColumn())
        ;
        colTVLadderpointsUnit.setOnEditCommit(new EventHandler<CellEditEvent<RowSL, String>>() {

            @Override
            public void handle(CellEditEvent<RowSL, String> t) {
                ((RowSL) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                        .setLadderpointsUnit(t.getNewValue());
            }
        });

        tvStudyLoad.setItems(olDataOfStudyLoad);
    }
}
