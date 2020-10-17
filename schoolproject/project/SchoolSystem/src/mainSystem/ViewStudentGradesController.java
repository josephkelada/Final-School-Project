package mainSystem;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ViewStudentGradesController implements Initializable{
	
	StudentClasses student = new StudentClasses();
	
	ArrayList<String> myClassNames = new ArrayList<String>();
	
	ArrayList<String> myStudentIDs = new ArrayList<String>();
	
	@FXML
    private Button updateGradeBtn;

    @FXML
    private Button updateFinalGradeBtn;
	
	@FXML
	private TextField studentGradeTxBx;
	
	@FXML
	private TextField studentFinalGradeTxBx;
	
	@FXML
	private ComboBox<String> allMyClassesCbBx;
	
	@FXML
	private ComboBox<String> AllStudentsCbBx;
	
	@FXML
    private TableView<StudentClasses> table;
	
	@FXML
	private TableColumn<StudentClasses,Integer > col_StudentID;
	
	@FXML
    private TableColumn<StudentClasses, String> col_Grade;
	
	@FXML
	private TableColumn<StudentClasses,String> col_Fname;
	
	@FXML
	private TableColumn<StudentClasses,String> col_Lname;

    @FXML
    private TableColumn<StudentClasses, String> col_ClassName;

    @FXML
    private TableColumn<StudentClasses, String> col_CourseName;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		try {
			col_StudentID.setCellValueFactory(cellData -> cellData.getValue().getStudentIDProperty().asObject());//we are basically saying cellData = to cellData.getValue(),it saves us the trouble of creating a method
			col_Grade.setCellValueFactory(cellData -> cellData.getValue().getGradeProperty());
			col_ClassName.setCellValueFactory(cellData -> cellData.getValue().getClassNameProperty());
			col_CourseName.setCellValueFactory(cellData -> cellData.getValue().getCourseNameProperty());
			col_Lname.setCellValueFactory(cellData -> cellData.getValue().getLNameProperty());
			col_Fname.setCellValueFactory(cellData -> cellData.getValue().getFNameProperty());
			
			getMyClasses();
			
			ObservableList<String> list = FXCollections.observableArrayList(myClassNames);
			allMyClassesCbBx.setItems(list);
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	void selectAllStudentsClick(ActionEvent e) {
		try {
			table.setItems(student.getStudentRecordsTchr(Integer.parseInt(allMyClassesCbBx.getSelectionModel().getSelectedItem().substring(0, 2).replaceAll("\\s", ""))));
			
			getMyStudents();
			
			ObservableList<String> list = FXCollections.observableArrayList(myStudentIDs);
			AllStudentsCbBx.setItems(list);
			
			AllStudentsCbBx.setPromptText("All My Students In"+allMyClassesCbBx.getSelectionModel().getSelectedItem().replaceAll("[^\\sa-zA-Z]", "")+"");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@FXML
    void selectStudentClick(ActionEvent event) {
		try {
			//make validation in case there are no students for the selected class in the grades table
			table.setItems(student.getSpecificStudent(Integer.parseInt(AllStudentsCbBx.getSelectionModel().getSelectedItem().substring(0, 3).replaceAll("[^0-9]","")),(Integer.parseInt(allMyClassesCbBx.getSelectionModel().getSelectedItem().substring(0, 3).replaceAll("[^0-9]", "")))));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	
	void getMyClasses() throws SQLException{//populate our arraylist which contains all classes from teacher
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		ResultSet rs1 = null;
		
		try {
			rs = con.createStatement().executeQuery("SELECT ClassID FROM Teachers WHERE Teacher_ID = '"+Main.currentUser.getId()+"' AND ClassID IS NOT NULL");
			//running a different query everytime since the we need the class name depending on the classID 
			while(rs.next()) {
				rs1 = con.createStatement().executeQuery("SELECT ClassName FROM All_Classes WHERE ClassID = '"+rs.getInt("ClassID")+"'");
				if(rs1.next()) {
					myClassNames.add(""+rs.getInt("ClassID")+" - "+rs1.getString("ClassName")+"");//making it look like so "37 - History"
				}
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
				con.close();
			if(rs != null)
				rs.close();
			if(rs1 != null)
				rs1.close();
		}
	}
	
	void getMyStudents() throws SQLException {
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		ResultSet rs1 = null;
		int count = 0;
		try {
			updateGradeBtn.setDisable(false);
			updateFinalGradeBtn.setDisable(false);
			rs = con.createStatement().executeQuery("SELECT StudentID FROM Grades WHERE ClassID = '"+Integer.parseInt(allMyClassesCbBx.getSelectionModel().getSelectedItem().substring(0, 2).replaceAll("\\s", ""))+"'");
			//running a different query every time since the we need the firstname depending on the new student id
			while(rs.next()) {
				count++;
				rs1 = con.createStatement().executeQuery("SELECT FirstName FROM Users WHERE ID = '"+rs.getInt("StudentID")+"'");
				if(rs1.next()) {
					myStudentIDs.add(""+rs.getInt("StudentID")+" - "+rs1.getString("FirstName")+"");//making it look like so "37 - History"
				}
			}
			if(count == 0) {
				new Alert(Alert.AlertType.ERROR,"You Have No Students In This Class").showAndWait();
				updateGradeBtn.setDisable(true);
				updateFinalGradeBtn.setDisable(true);
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
				con.close();
			if(rs != null)
				rs.close();
			if(rs1 != null)
				rs1.close();
		}	
	}
	
	@FXML
	void updateClick(ActionEvent e) {
		if(AllStudentsCbBx.getSelectionModel().isEmpty()){
			new Alert(Alert.AlertType.ERROR,"You Must Select A Student").showAndWait();
		}
		else {
			if(studentGradeTxBx.getText().matches("\\d+") && !studentGradeTxBx.getText().isEmpty()) {
				String grade = studentGradeTxBx.getText();
				int gradeInt = Integer.parseInt(grade);
				if(gradeInt > 0 && gradeInt < 100) {
					student.updateStudentGrade(grade + '%', Integer.parseInt(AllStudentsCbBx.getSelectionModel().getSelectedItem().substring(0, 3).replaceAll("[^0-9]","")),Integer.parseInt(allMyClassesCbBx.getSelectionModel().getSelectedItem().substring(0, 3).replaceAll("[^0-9]", "")));
					new Alert(Alert.AlertType.INFORMATION,"Grade Added!").showAndWait();
				}
				else {
					new Alert(Alert.AlertType.ERROR,"Grade Must be Between 0 and 100").showAndWait();
				}
			}
			else {
				new Alert(Alert.AlertType.ERROR,"Grade Must be only a number").showAndWait();
			}
		}
	}
	
	@FXML
	void updateFinalClick(ActionEvent e) {
		if(AllStudentsCbBx.getSelectionModel().isEmpty()){
			new Alert(Alert.AlertType.ERROR,"You Must Select A Student").showAndWait();
		}
		else {
			if(studentFinalGradeTxBx.getText().matches("\\d+") && !studentFinalGradeTxBx.getText().isEmpty()) {
				String grade = studentFinalGradeTxBx.getText();
				int gradeInt = Integer.parseInt(grade);
				if(gradeInt > 0 && gradeInt < 100) {
					student.updateStudentFinalGrade(grade + '%', Integer.parseInt(AllStudentsCbBx.getSelectionModel().getSelectedItem().substring(0, 3).replaceAll("[^0-9]","")),Integer.parseInt(allMyClassesCbBx.getSelectionModel().getSelectedItem().substring(0, 3).replaceAll("[^0-9]", "")));
					new Alert(Alert.AlertType.INFORMATION,"Final Grade Added!").showAndWait();
				}
				else {
					new Alert(Alert.AlertType.ERROR,"Final Grade Must be Between 0 and 100").showAndWait();
				}
			}
			else {
				new Alert(Alert.AlertType.ERROR,"Final Grade Must be only a number").showAndWait();
			}
		}
	}
	
	@FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) table.getScene().getWindow();
    	stage1.close();
	}
}
