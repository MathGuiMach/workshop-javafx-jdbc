package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService departmentService;

	@FXML
	private TableView<Seller> tableViewSeller;
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	@FXML
	private TableColumn<Seller, Seller> tableColumnEdit;
	@FXML
	private TableColumn<Seller, Seller> tableColumnDelete;
	@FXML
	private Button btNew;

	private ObservableList<Seller> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Seller d = new Seller();
		createDialogForm(d, "/gui/SellerForm.fxml", Utils.getCurrentStage(event));
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	public void setSellerService(SellerService ds) {
		this.departmentService = ds;
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.getCurrentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDelete.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation","Are you sure you want to delete?");
		if(result.get() == ButtonType.OK) {
			if(departmentService == null) {
				throw new IllegalStateException("Service is null");
			}
			try {
				departmentService.delete(obj);
				updateTableView();
			}
			catch(DbIntegrityException e) {
				Alerts.showAlert("Error Removing Object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

	public void updateTableView() {
		if (departmentService == null) {
			throw new IllegalStateException("DaoSeller is null");
		}
		List<Seller> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons();
		initRemoveButtons(); 
	}

	private void createDialogForm(Seller d, String absoluteName, Stage parentStage) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//
//			SellerFormController controller = loader.getController();
//			controller.setEntity(d);
//			controller.setSellerService(departmentService);
//			controller.subscribeDataChangeListener(this);
//			controller.updateFormData();
//
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Enter Seller data");
//			dialogStage.setScene(new Scene(pane));
//			dialogStage.setResizable(false);
//			dialogStage.initOwner(parentStage);
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.showAndWait();
//
//		} catch (IOException e) {
//			Alerts.showAlert("IO Exception", "Error loading View", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}
}
