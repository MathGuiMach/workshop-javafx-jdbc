package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;
	private SellerService sellerService;
	private DepartmentService departmentService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpBirthDate;
	@FXML
	private TextField txtBaseSalary;
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	@FXML
	private Label lblErrorName;
	@FXML
	private Label lblErrorEmail;
	@FXML
	private Label lblErrorBirthDate;
	@FXML
	private Label lblErrorBaseSalary;

	private ObservableList<Department> obsList;

	public void setEntity(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService selServ, DepartmentService depServ) {
		this.sellerService = selServ;
		this.departmentService = depServ;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity is null");
		}
		if (sellerService == null) {
			throw new IllegalStateException("Service is null");
		}
		try {
			entity = getFormData();
			sellerService.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.getCurrentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Error Saving Object", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.getCurrentStage(event).close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
		
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 100);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity is null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null)
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		if(entity.getDepartment() == null)
			comboBoxDepartment.getSelectionModel().selectFirst();
		else
			comboBoxDepartment.setValue(entity.getDepartment());
	}

	public void loadAssociatedObjects() {
		if (departmentService == null)
			throw new IllegalStateException("Department Service is null");
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private Seller getFormData() {
		Seller obj = new Seller();

		ValidationException exception = new ValidationException("Validation Error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals(""))
			exception.addError("name", "Field can't be Empty");
		obj.setName(txtName.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals(""))
			exception.addError("email", "Field can't be Empty");
		obj.setEmail(txtEmail.getText());
		
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals(""))
			exception.addError("baseSalary", "Field can't be Empty");
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));

		if (dpBirthDate.getValue() == null)
			exception.addError("birthDate", "Field can't be Empty");
		else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		obj.setDepartment(comboBoxDepartment.getValue());
		
		if (exception.getErrors().size() > 0)
			throw exception;

		return obj;
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		lblErrorName.setText(fields.contains("name") ? errors.get("name") : "");
		lblErrorEmail.setText(fields.contains("email") ? errors.get("email") : "");
		lblErrorBaseSalary.setText(fields.contains("baseSalary") ? errors.get("baseSalary") : "");
		lblErrorBirthDate.setText(fields.contains("birthDate")  ? errors.get("birthDate") : "");
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}
}
