package mainSystem;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class Parents implements Initializable
{
	Student student = new Student();
	
	@FXML
    private TableView<Student> table;
	
	@FXML
	private TableColumn<Student,Integer > col_ID;
	
	@FXML
    private TableColumn<Student, String> col_Class;

    @FXML
    private TableColumn<Student, String> col_Fname;

    @FXML
    private TableColumn<Student, String> col_Lname;

    @FXML
    private TableColumn<Student, String> col_Email;

    @FXML
    private TableColumn<Student, String> col_Grade;
    
    ObservableList<Student> personList = null;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		col_ID.setCellValueFactory(cellData -> cellData.getValue().getStudentIdProperty().asObject());//we are basically saying cellData = to cellData.getValue(),it saves us the trouble of creating a method
		col_Email.setCellValueFactory(cellData -> cellData.getValue().getEmailProperty());
		col_Fname.setCellValueFactory(cellData -> cellData.getValue().getFNameProperty());
		col_Lname.setCellValueFactory(cellData -> cellData.getValue().getLNameProperty());
		col_Grade.setCellValueFactory(cellData -> cellData.getValue().getGradeProperty());
		col_Class.setCellValueFactory(cellData -> cellData.getValue().getCoursProperty());
	}
	
	void selectAllRecords(ObservableList<Student> personList)
	{
		table.setItems(personList);
	}
	
	@FXML
    void findClick(ActionEvent event) 
	{
		personList = student.getParentsKid(Main.currentUser.getId());
		selectAllRecords(personList);
    }
	
	@FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) table.getScene().getWindow();
    	stage1.close();
	}
}
