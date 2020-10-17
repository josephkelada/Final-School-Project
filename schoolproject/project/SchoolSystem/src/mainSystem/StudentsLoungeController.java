package mainSystem;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class StudentsLoungeController implements Initializable
{
	StudentClasses student = new StudentClasses();
	
	@FXML
    private TableView<StudentClasses> table;
	
	@FXML
	private TableColumn<StudentClasses,Integer > col_ID;
	
	@FXML
    private TableColumn<StudentClasses, String> col_Grade;

    @FXML
    private TableColumn<StudentClasses, String> col_ClassName;

    @FXML
    private TableColumn<StudentClasses, String> col_CourseName;

    ObservableList<StudentClasses> classList = null;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		col_ID.setCellValueFactory(cellData -> cellData.getValue().getClassIDProperty().asObject());//we are basically saying cellData = to cellData.getValue(),it saves us the trouble of creating a method
		col_Grade.setCellValueFactory(cellData -> cellData.getValue().getGradeProperty());
		col_ClassName.setCellValueFactory(cellData -> cellData.getValue().getClassNameProperty());
		col_CourseName.setCellValueFactory(cellData -> cellData.getValue().getCourseNameProperty());
	}
	
	@FXML
	void viewClassesClick(ActionEvent event) {
		try {
			classList = student.getAllRecords(Main.currentUser.getId());
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		showAllRecords(classList);
	}
	
	void showAllRecords(ObservableList<StudentClasses> sc) {
		table.setItems(sc);
	}
	
	@FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) table.getScene().getWindow();
    	stage1.close();
	}
}
