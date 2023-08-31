module electionWithGUI {
	requires javafx.controls;
	requires java.desktop;
	requires javafx.graphics;
	requires javafx.base;
	requires java.sql;
	
	opens electionMVC to javafx.graphics, javafx.fxml;
}
