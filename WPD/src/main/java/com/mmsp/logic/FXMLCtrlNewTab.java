package com.mmsp.logic;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import com.mmsp.dao.impl.DAOImpl;
import com.mmsp.model.PoCM;
import com.mmsp.model.WPDData;
import com.mmsp.model.ThematicPlan;
import com.mmsp.model.WPDVersion;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class FXMLCtrlNewTab extends VBox {

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
	
	public class RowT41 {
		 
        private SimpleStringProperty numberOfModule;
        private SimpleStringProperty numberOfDisciplineSection;
        private SimpleStringProperty numberOfDisciplineTopics;
        private SimpleStringProperty numberOfHours;
        private SimpleStringProperty numberOfL;
        private SimpleStringProperty numberOfPZ;
        private SimpleStringProperty numberOfLR;
        private SimpleStringProperty KSR;
        private SimpleStringProperty finalAttestation;
        private SimpleStringProperty individualWork;
        private SimpleStringProperty laborIntensity;
        
        public SimpleStringProperty numberOfModuleProperty() {
            if (numberOfModule == null) {
            	numberOfModule = new SimpleStringProperty(this, "numberOfModule");
            }
            return numberOfModule;
        }
        public SimpleStringProperty numberOfDisciplineSectionProperty() {
            if (numberOfDisciplineSection == null) {
            	numberOfDisciplineSection = new SimpleStringProperty(this, "numberOfDisciplineSection");
            }
            return numberOfDisciplineSection;
        }
        public SimpleStringProperty numberOfDisciplineTopicsProperty() {
            if (numberOfDisciplineTopics == null) {
            	numberOfDisciplineTopics = new SimpleStringProperty(this, "numberOfDisciplineTopics");
            }
            return numberOfDisciplineTopics;
        }
        public SimpleStringProperty numberOfHoursProperty() {
            if (numberOfHours == null) {
            	numberOfHours = new SimpleStringProperty(this, "numberOfHours");
            }
            return numberOfHours;
        }
        public SimpleStringProperty numberOfLProperty() {
            if (numberOfL == null) {
            	numberOfL = new SimpleStringProperty(this, "numberOfL");
            }
            return numberOfL;
        }
        public SimpleStringProperty numberOfPZProperty() {
            if (numberOfPZ == null) {
            	numberOfPZ = new SimpleStringProperty(this, "numberOfPZ");
            }
            return numberOfPZ;
        }
        public SimpleStringProperty numberOfLRProperty() {
            if (numberOfLR == null) {
            	numberOfLR = new SimpleStringProperty(this, "numberOfLR");
            }
            return numberOfLR;
        }
        public SimpleStringProperty KSRProperty() {
            if (KSR == null) {
            	KSR = new SimpleStringProperty(this, "KSR");
            }
            return KSR;
        }
        public SimpleStringProperty finalAttestationProperty() {
            if (finalAttestation == null) {
            	finalAttestation = new SimpleStringProperty(this, "finalAttestation");
            }
            return finalAttestation;
        }
        public SimpleStringProperty individualWorkProperty() {
            if (individualWork == null) {
            	individualWork = new SimpleStringProperty(this, "individualWork");
            }
            return individualWork;
        }
        public SimpleStringProperty laborIntensityProperty() {
            if (laborIntensity == null) {
            	laborIntensity = new SimpleStringProperty(this, "laborIntensity");
            }
            return laborIntensity;
        }
        
        public RowT41(int value) { // FIXME Исправить повторяющийся код
        	this.numberOfModule = new SimpleStringProperty(String.valueOf(value));
            this.numberOfDisciplineSection = new SimpleStringProperty("");
            this.numberOfDisciplineTopics = new SimpleStringProperty("");
            this.numberOfHours = new SimpleStringProperty("");
            this.numberOfL = new SimpleStringProperty("");
            this.numberOfPZ = new SimpleStringProperty("");
            this.numberOfLR = new SimpleStringProperty("");
            this.KSR = new SimpleStringProperty("");
            this.finalAttestation = new SimpleStringProperty("");
            this.individualWork = new SimpleStringProperty("");
            this.laborIntensity = new SimpleStringProperty("");
        }
        public RowT41() {
        	this.numberOfModule = new SimpleStringProperty("");
            this.numberOfDisciplineSection = new SimpleStringProperty("");
            this.numberOfDisciplineTopics = new SimpleStringProperty("");
            this.numberOfHours = new SimpleStringProperty("");
            this.numberOfL = new SimpleStringProperty("");
            this.numberOfPZ = new SimpleStringProperty("");
            this.numberOfLR = new SimpleStringProperty("");
            this.KSR = new SimpleStringProperty("");
            this.finalAttestation = new SimpleStringProperty("");
            this.individualWork = new SimpleStringProperty("");
            this.laborIntensity = new SimpleStringProperty("");
        }
        public RowT41(String numberOfModule, String numberOfDisciplineSection, String numberOfDisciplineTopics, 
        		String numberOfHours, String numberOfL, String numberOfPZ, String numberOfLR, String KSR,
        		String finalAttestation, String individualWork, String laborIntensity) {
        	
            this.numberOfModule = new SimpleStringProperty(numberOfModule);
            this.numberOfDisciplineSection = new SimpleStringProperty(numberOfDisciplineSection);
            this.numberOfDisciplineTopics = new SimpleStringProperty(numberOfDisciplineTopics);
            this.numberOfHours = new SimpleStringProperty(numberOfHours);
            this.numberOfL = new SimpleStringProperty(numberOfL);
            this.numberOfPZ = new SimpleStringProperty(numberOfPZ);
            this.numberOfLR = new SimpleStringProperty(numberOfLR);
            this.KSR = new SimpleStringProperty(KSR);
            this.finalAttestation = new SimpleStringProperty(finalAttestation);
            this.individualWork = new SimpleStringProperty(individualWork);
            this.laborIntensity = new SimpleStringProperty(laborIntensity);
        }
        
        public String getNumberOfModule() {
            return numberOfModule.get();
        }
        public void setNumberOfModule(String value) {
        	numberOfModule.set(value);
        }
        public String getNumberOfDisciplineSection() {
            return numberOfDisciplineSection.get();
        }
        public void setNumberOfDisciplineSection(String value) {
        	numberOfDisciplineSection.set(value);
        }
		public String getNumberOfDisciplineTopics() {
			return numberOfDisciplineTopics.get();
		}
		public void setNumberOfDisciplineTopics(String value) {
			this.numberOfDisciplineTopics.set(value);
		}
		public String getNumberOfHours() {
			return numberOfHours.get();
		}
		public void setNumberOfHours(String value) {
			this.numberOfHours.set(value);
		}
		public String getNumberOfL() {
			return numberOfL.get();
		}
		public void setNumberOfL(String value) {
			this.numberOfL.set(value);
		}
		public String getNumberOfPZ() {
			return numberOfPZ.get();
		}
		public void setNumberOfPZ(String value) {
			this.numberOfPZ.set(value);
		}
		public String getNumberOfLR() {
			return numberOfLR.get();
		}
		public void setNumberOfLR(String value) {
			this.numberOfLR.set(value);
		}
		public String getKSR() {
			return KSR.get();
		}
		public void setKSR(String value) {
			this.KSR.set(value);
		}
		public String getFinalAttestation() {
			return finalAttestation.get();
		}
		public void setFinalAttestation(String value) {
			this.finalAttestation.set(value);
		}
		public String getIndividualWork() {
			return individualWork.get();
		}
		public void setIndividualWork(String value) {
			this.individualWork.set(value);
		}
		public String getLaborIntensity() {
			return laborIntensity.get();
		}
		public void setLaborIntensity(String value) {
			this.laborIntensity.set(value);
		}        
    }
	
	class EditingCell extends TableCell<RowSL, String> { // Для UX, что б не надо было после редактирования жать Enter
		 
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
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
            textField.focusedProperty().addListener(new ChangeListener<Boolean>(){
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, 
                    Boolean arg1, Boolean arg2) {
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
	
	private final ObservableList<RowSL> dataOfStudyLoad = FXCollections.observableArrayList();
	
	private TreeSet<Integer> tsFNOS = new TreeSet<Integer>();
	
	private final Stage stage;
	
	@FXML
    private TextField tfVersion;

    @FXML
    private Button bAddRowStudyLoad;

    @FXML
    private Button bDeleteRowStudyLoad;

    @FXML
    private ListView<String> lvTypeOfControlMeasures;

    @FXML
    private Button bGenerate;

    @FXML
    private Button bDelete;

    @FXML
    private TextField tFPath;

    @FXML
    private Button bCallFileChooser;

    @FXML
    private Button bSave;

    @FXML
    private TableColumn<RowSL, String> colTVViewOfStudyLoad; // Вид учебной нагрузки
    
    @FXML
    private TableColumn<RowSL, String> colTVNumberOfHours; // Часов
    
    @FXML
    private TableColumn<RowSL, String> colTVLadderpointsUnit; // ЗЕ
    
    @FXML
    private TableView<RowSL> tvStudyLoad;
    
    @FXML
    private TreeTableColumn<RowT41, String> colTTVNumberOfModule;
    
    @FXML
    private TreeTableColumn<RowT41, String> colTTVNumberOfDisciplineSection;
    
    @FXML
    private TreeTableColumn<RowT41, String> colTTVNumberOfDisciplineTopics;
    
    @FXML
    private TreeTableColumn<RowT41, String> colTTVNumberOfHours;

    @FXML
    private TreeTableColumn<RowT41, String> colTTVNumberOfL;
    
    @FXML
    private TreeTableColumn<RowT41, String> colTTVNumberOfPZ;
    
    @FXML
    private TreeTableColumn<RowT41, String> colTTVNumberOfLR;

    @FXML
    private TreeTableColumn<RowT41, String> colTTVKSR;
    
    @FXML
    private TreeTableColumn<RowT41, String> colTTVFinalAttestation;
    
    @FXML
    private TreeTableColumn<RowT41, String> colTTVIndividualWork;
    
    @FXML
    private TreeTableColumn<RowT41, String> colTTVLaborIntensity;

    @FXML
	private TreeTableView<RowT41> ttvTable41;
    
	@FXML
    private Button bAddRowT41;
    
    private TreeItem<RowT41> root; // корневой элемент ttvTable41

    @FXML
    private CheckMenuItem cmi7;

    @FXML
    private CheckMenuItem cmi8;

    @FXML
    private CheckMenuItem cmi5;

    @FXML
    private CheckMenuItem cmi6;

    @FXML
    private CheckMenuItem cmi3;

    @FXML
    private DatePicker dPDateOfCreate;

    @FXML
    private CheckMenuItem cmi4;

    @FXML
    private CheckMenuItem cmi1;

    @FXML
    private CheckMenuItem cmi2;

    @FXML
    private MenuButton mbNumberOfSemesters;

    private void initTtvTable41() {
    	colTTVNumberOfModule.setCellValueFactory(new Callback<CellDataFeatures<RowT41, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<RowT41, String> p) {
				// p.getValue() returns the TreeItem instance for a particular
				// TreeTableView row, and the second getValue() call returns the
				// RowT41 instance contained within the TreeItem.
				return p.getValue().getValue().numberOfModuleProperty();
			}
		});
    	colTTVNumberOfDisciplineSection.setCellValueFactory(new Callback<CellDataFeatures<RowT41, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<RowT41, String> p) {
				return p.getValue().getValue().numberOfDisciplineSectionProperty();
			}
		});
    	colTTVNumberOfDisciplineTopics.setCellValueFactory(new Callback<CellDataFeatures<RowT41, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<RowT41, String> p) {
				return p.getValue().getValue().numberOfDisciplineTopicsProperty();
			}
		});
    	colTTVNumberOfHours.setCellValueFactory(new Callback<CellDataFeatures<RowT41, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<RowT41, String> p) {
				return p.getValue().getValue().numberOfHoursProperty();
			}
		});
    	colTTVNumberOfL.setCellValueFactory(new Callback<CellDataFeatures<RowT41, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<RowT41, String> p) {
				return p.getValue().getValue().numberOfLProperty();
			}
		});
    	colTTVNumberOfPZ.setCellValueFactory(new Callback<CellDataFeatures<RowT41, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<RowT41, String> p) {
				return p.getValue().getValue().numberOfPZProperty();
			}
		});
    	colTTVNumberOfLR.setCellValueFactory(new Callback<CellDataFeatures<RowT41, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<RowT41, String> p) {
				return p.getValue().getValue().numberOfLRProperty();
			}
		});
    	colTTVKSR.setCellValueFactory(new Callback<CellDataFeatures<RowT41, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<RowT41, String> p) {
				return p.getValue().getValue().KSRProperty();
			}
		});
    	colTTVFinalAttestation.setCellValueFactory(new Callback<CellDataFeatures<RowT41, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<RowT41, String> p) {
				return p.getValue().getValue().finalAttestationProperty();
			}
		});
    	colTTVIndividualWork.setCellValueFactory(new Callback<CellDataFeatures<RowT41, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<RowT41, String> p) {
				return p.getValue().getValue().individualWorkProperty();
			}
		});
    	colTTVLaborIntensity.setCellValueFactory(new Callback<CellDataFeatures<RowT41, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<RowT41, String> p) {
				return p.getValue().getValue().laborIntensityProperty();
			}
		});
    	
    	root = new TreeItem<RowT41>(new RowT41());
    	ttvTable41.setShowRoot(false);
    	ttvTable41.setRoot(root);
    }

    private void initTvStudyLoad() {
    	Callback<TableColumn<RowSL, String>, TableCell<RowSL, String>> cellFactory =
                new Callback<TableColumn<RowSL, String>, TableCell<RowSL, String>>() {
                    public TableCell<RowSL, String> call(TableColumn<RowSL, String> p) {
                       return new EditingCell();
                    }
                };
        colTVViewOfStudyLoad.setMinWidth(150.0);
        colTVViewOfStudyLoad.setCellValueFactory(new PropertyValueFactory<RowSL, String>("viewOfStudyLoad"));
        //colViewOfStudyLoad.setCellFactory(TextFieldTableCell.forTableColumn());
        colTVViewOfStudyLoad.setCellFactory(cellFactory);
        colTVViewOfStudyLoad.setOnEditCommit(
            new EventHandler<CellEditEvent<RowSL, String>>() {
                @Override
                public void handle(CellEditEvent<RowSL, String> t) {
                    ((RowSL) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setViewOfStudyLoad(t.getNewValue());
                }
            }
        );
        colTVNumberOfHours.setCellValueFactory(new PropertyValueFactory<RowSL, String>("numberOfHours"));
        //colNumberOfHours.setCellFactory(TextFieldTableCell.forTableColumn());
        colTVNumberOfHours.setCellFactory(cellFactory);
        colTVNumberOfHours.setOnEditCommit(
            new EventHandler<CellEditEvent<RowSL, String>>() {
                @Override
                public void handle(CellEditEvent<RowSL, String> t) {
                    ((RowSL) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setNumberOfHours(t.getNewValue());
                }
            }
        );
        colTVLadderpointsUnit.setCellValueFactory(new PropertyValueFactory<RowSL, String>("ladderpointsUnit"));
        colTVLadderpointsUnit.setCellFactory(cellFactory);
        //colLadderpointsUnit.setCellFactory(TextFieldTableCell.forTableColumn());
        colTVLadderpointsUnit.setOnEditCommit(
            new EventHandler<CellEditEvent<RowSL, String>>() {
                @Override
                public void handle(CellEditEvent<RowSL, String> t) {
                    ((RowSL) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setLadderpointsUnit(t.getNewValue());
                }
            }
        );
        tvStudyLoad.setItems(dataOfStudyLoad);  
    }
    
    private void initLvTypeOfControlMeasures() {
    	lvTypeOfControlMeasures.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ObservableList<String> items = FXCollections.observableArrayList ( // Перенести расшифровки аббревиатур в подсказки
        		"ТТ – текущее тестирование", 
        		"РТ – рубежное тестирование по модулю", 
        		"КР – рубежная контрольная работа по модулю", 
        		"РГР (КР) –  расчетно-графическая работа");
        
        lvTypeOfControlMeasures.setItems(items);
    }
    
	/*
     * Описание методов поведения TableView, TreeTableView and ListView
     */
    private void initT() {
    	initTtvTable41();
    	initTvStudyLoad();
    	initLvTypeOfControlMeasures();
    }

	@FXML
    void clickBFileChooser(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Открыть шаблон");
    	File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            tFPath.setText(file.getPath());
        }
    }

    @FXML
    void ccmi1(ActionEvent event) {
		if (cmi1.isSelected()) tsFNOS.add(1); else tsFNOS.remove(1);
		mbNumberOfSemesters.setText(tsFNOS.toString());
    }

    @FXML
    void ccmi2(ActionEvent event) {
		if (cmi2.isSelected()) tsFNOS.add(2); else tsFNOS.remove(2);
		mbNumberOfSemesters.setText(tsFNOS.toString());
    }

    @FXML
    void ccmi3(ActionEvent event) {
    	if (cmi3.isSelected()) tsFNOS.add(3); else tsFNOS.remove(3);
    	mbNumberOfSemesters.setText(tsFNOS.toString());
    }

    @FXML
    void ccmi4(ActionEvent event) {
		if (cmi4.isSelected()) tsFNOS.add(4); else tsFNOS.remove(4);
		mbNumberOfSemesters.setText(tsFNOS.toString());
    }

    @FXML
    void ccmi5(ActionEvent event) {
		if (cmi5.isSelected()) tsFNOS.add(5); else tsFNOS.remove(5);
		mbNumberOfSemesters.setText(tsFNOS.toString());
    }

    @FXML
    void ccmi6(ActionEvent event) {
		if (cmi6.isSelected()) tsFNOS.add(6); else tsFNOS.remove(6);
		mbNumberOfSemesters.setText(tsFNOS.toString());
    }

    @FXML
    void ccmi7(ActionEvent event) {
		if (cmi7.isSelected()) tsFNOS.add(7);  else tsFNOS.remove(7);
		mbNumberOfSemesters.setText(tsFNOS.toString());
    }

    @FXML
    void ccmi8(ActionEvent event) {
		if (cmi8.isSelected()) tsFNOS.add(8); else tsFNOS.remove(8);
		mbNumberOfSemesters.setText(tsFNOS.toString());
    }

    @FXML
    void clickBSave(ActionEvent event) {
    	PoCM pocm = new PoCM();
    	WPDData subject = new WPDData();
    	ThematicPlan thematicPlan = new ThematicPlan();
    	WPDVersion wpdVersion = new WPDVersion();
    	
    	
    	
    	wpdVersion.setSubject(subject);
    	wpdVersion.setThematicPlan(thematicPlan);

    	// TODO add PoCM in WPDVersion OneToOne!
    	// wpdVersion.setPoCM(pocm);
    	
    	// TODO достать данные из полей и вставить их в объекты
    	
    	DAOImpl dao = new DAOImpl();
    	dao.add(pocm);
    	dao.add(subject);
    	dao.add(thematicPlan);
    	dao.add(wpdVersion);
    }

    @FXML
    void clickBGenerate(ActionEvent event) {

    }

    @FXML
    void clickBDelete(ActionEvent event) {

    }

    @FXML
    void clickBAddRowStudyLoad(ActionEvent event) {
    	dataOfStudyLoad.add(new RowSL("", "", ""));
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
    	//for (int i = 0; i < dataOfStudyLoad.size(); i++) System.out.println(dataOfStudyLoad.get(i).toString());
    }
    
    @FXML // Добавление строки в Т41
    void clickBAddRowT41(ActionEvent event) {
    	System.out.println(root.getChildren().size());
    	TreeItem<RowT41> item = new TreeItem<>(new RowT41(root.getChildren().size() + 1));
    	root.getChildren().add(item);
    }
	
	public FXMLCtrlNewTab(Stage curr_stage, String value) throws IOException { // TODO Добавить аргументы
		
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("NewTab.fxml"));
        
        loader.setController(this);
        
        loader.setRoot(this);
        
        loader.load();
        
        stage = curr_stage;

        tfVersion.setText(value);
        
        mbNumberOfSemesters.setText(tsFNOS.toString());
        
        initT();
	}

}
