module workshopJavafxJdbc {
	requires javafx.controls;
	
	opens application to javafx.graphics, javafx.fxml;
	opens gui to javafx.graphics, javafx.fxml;
}
