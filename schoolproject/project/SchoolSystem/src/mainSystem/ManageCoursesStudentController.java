package mainSystem;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ManageCoursesStudentController implements Initializable{//yes i know i could use polymorphism to create a closeConnection function but i don't have time for now 

	StudentClasses sc = new StudentClasses();
	Alert alert = new Alert(Alert.AlertType.INFORMATION,"Request Filed!");
	
	ArrayList<String> classesList = new ArrayList<String>();
	ArrayList<String> curtClasses = new ArrayList<String>();
	ArrayList<String> allCourses = new ArrayList<String>();
	String courseName;
	Connection con = SQLConnecter.connect();
	ResultSet rs = null;
	String format = String.format("%s%n%s", "Administration Can Only Process", " One Request at a time , please Try Again Later");
	
	@FXML
    private ComboBox<String> currentClassesCbBox;
	
	@FXML
	private ComboBox<String> allCoursesCbBox;

    @FXML
    private ComboBox<String> allClassesCbBox;
	
    @FXML
    private TableView<StudentClasses> table;

    @FXML
    private TableColumn<StudentClasses,Integer> col_ID;

    @FXML
    private TableColumn<StudentClasses, String> col_CourseName;

    @FXML
    private TableColumn<StudentClasses, String> col_ClassName;

    @FXML
    private TableColumn<StudentClasses, String> col_Grade;
    
    ObservableList<StudentClasses> classList = null;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		col_ID.setCellValueFactory(cellData -> cellData.getValue().getClassIDProperty().asObject());//we are basically saying cellData = to cellData.getValue(),it saves us the trouble of creating a method
		col_Grade.setCellValueFactory(cellData -> cellData.getValue().getGradeProperty());
		col_ClassName.setCellValueFactory(cellData -> cellData.getValue().getClassNameProperty());
		col_CourseName.setCellValueFactory(cellData -> cellData.getValue().getCourseNameProperty());
		
		allClassesCbBox.setPromptText("All Classes In "+getSQLQuery()+"");//fills up the combo boxes 
		
		ObservableList<String> list = FXCollections.observableArrayList(classesList);
		allClassesCbBox.setItems(list);
		
		list = FXCollections.observableArrayList(curtClasses);
		currentClassesCbBox.setItems(list);
		
		list = FXCollections.observableArrayList(allCourses);
		allCoursesCbBox.setItems(list);
	}
	
	@FXML
	void viewClassesClick(ActionEvent event) {
		try {
			classList = sc.getAllRecords(Main.currentUser.getId());
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		showAllRecords(classList);
	}
	
	void showAllRecords(ObservableList<StudentClasses> sc) {
		table.setItems(sc);
	}
	
	
	String getSQLQuery() { //gets all classes that the student is in 
		ArrayList<String> allClasses = new ArrayList<String>();
		con  = SQLConnecter.connect();
		int courseID = 0;
		ResultSet rs1 = null;
		
		try {
			rs = con.createStatement().executeQuery("SELECT CurrentCourse FROM Students WHERE StudentID = '"+Main.currentUser.getId()+"'");
			
			if(rs.next()) {
				setCourseName(rs.getString("CurrentCourse"));
			}
			rs.close();
			rs = con.createStatement().executeQuery("SELECT ClassID FROM Grades WHERE StudentID = '"+Main.currentUser.getId()+"'");
		
			while(rs.next()) {
				rs1 = con.createStatement().executeQuery("SELECT ClassName FROM All_Classes WHERE ClassID = '"+rs.getInt("ClassID")+"'");
				
				while(rs1.next()) {
					curtClasses.add(rs1.getString("ClassName"));
				}
			}
			rs.close();
			
			rs = con.createStatement().executeQuery("SELECT CourseID FROM All_Courses WHERE CourseName = '"+getCourseName()+"'");
			
			if(rs.next()) {
				courseID = rs.getInt("CourseID");
			}
			rs.close();
			
			rs = con.createStatement().executeQuery("SELECT DISTINCT ClassName FROM All_Classes WHERE CourseID =  '"+courseID+"'");
			
			while(rs.next()) {
				allClasses.add(rs.getString("ClassName"));
			}
			rs.close();
			
			setClassesList(allClasses,curtClasses);
			
			rs = con.createStatement().executeQuery("SELECT DISTINCT CourseName FROM All_Courses WHERE CourseID != '"+courseID+"'");
			
			while(rs.next()) {
				allCourses.add(rs.getString("CourseName"));
			}
			rs.close();
			con.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(con != null)
					con.close();
				if(rs != null)
					rs.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return getCourseName();
	}
	
	void setClassesList(ArrayList<String>allClasses,ArrayList<String> curtClasses) {//sets the CbBox AllClasses the user is not taking
		String a,b = "";
		boolean flag = false;
		for (int i = 0;i < allClasses.size();i++) {
			
			for(int j = 0; j < curtClasses.size();j++) {
				flag = true;
				a = allClasses.get(i).toString();
				b = curtClasses.get(j).toString();
				if(a.equals(b)) {
					
					break;// means the student is taking this class so we don't add it to the list
				}
				if (j >= curtClasses.size() -1) {//if the loop went through every record and didnt break
					classesList.add(allClasses.get(i));
					//means the student is not taking the class this semester
				}
			}
		}
		if(!flag) {//if it's false it means that curtClasses list is empty meaning the student has no classes
			for(int i = 0 ; i < allClasses.size();i++) {
				classesList.add(allClasses.get(i));
			}
		}
	}
	
	@FXML
	void requestChangeCourseClick(ActionEvent event) {
		boolean flag = false;
		con  = SQLConnecter.connect();
		try {
			rs = con.createStatement().executeQuery("SELECT Status FROM Status WHERE StudentID = '"+Main.currentUser.getId()+"'");
			 
			while(rs.next()) {//checking if any of the classes in the student's course have been started
				if((rs.getString("Status").equals("In-Progress"))) {
					flag = true;//meaning started his course 
				}
			}
			rs.close();
			
			if (!flag) {//meaning he didnt start his course yet 
				
				rs = con.createStatement().executeQuery("SELECT * FROM Student_Management_Courses WHERE StudentID = "+Main.currentUser.getId()+"");

				if(!rs.isBeforeFirst()) {//check if there is already a request made
					if(!(allCoursesCbBox.getSelectionModel().isEmpty())) {//if he did pick a value in the cbbx 
						if(getCourseName() == "" || getCourseName() == null) {
							alert.showAndWait();
							SQLConnecter.executeQuery("INSERT INTO Student_Management_Courses (StudentID,Student_Request) VALUES ("+Main.currentUser.getId()+",'No Course-"+allCoursesCbBox.getSelectionModel().getSelectedItem().toString()+"')");
						}
						else {
							alert.showAndWait();
							SQLConnecter.executeQuery("INSERT INTO Student_Management_Courses (StudentID,Student_Request) VALUES ("+Main.currentUser.getId()+",'"+getCourseName()+"-"+allCoursesCbBox.getSelectionModel().getSelectedItem().toString()+"')");
						}
					}
					else {
						new Alert(Alert.AlertType.ERROR,"You Must Pick A Class").showAndWait();
					}
				}
				else {
					new Alert(Alert.AlertType.ERROR,format).showAndWait();
				}
			}
			else {
				new Alert(Alert.AlertType.ERROR,"Course Has Already Started, Unable to Change").showAndWait();
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally {
			try {
				if(con != null)
					con.close();
				if(rs != null)
					rs.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	void requestChangeClassClick(ActionEvent event) {
		int count = 0;
		con = SQLConnecter.connect();
		try {
			rs = con.createStatement().executeQuery("SELECT * FROM Grades WHERE StudentID = '"+Main.currentUser.getId()+"'");
			
			while(rs.next()) {
				count++;//gives us the amt of classes the student is taking
			}
			rs.close();
			
			if(count > 7) {//if student has more then 6 courses 
				String h = String.format("%s%n%s", "You Already Have The Max Amount Of Classes","Please Remove A Class To Add This One");
				new Alert(Alert.AlertType.ERROR,h).showAndWait();
			}
			else {
				rs = con.createStatement().executeQuery("SELECT * FROM Student_Management_Classes WHERE StudentID = "+Main.currentUser.getId()+"");
				
				if(!rs.isBeforeFirst()) {//only execute below if the Request doesn't exist in the table
					 if (!(allClassesCbBox.getSelectionModel().isEmpty())) {
						 alert.showAndWait();
						 SQLConnecter.executeQuery("INSERT INTO Student_Management_Classes (StudentID,Student_Request) VALUES ('"+Main.currentUser.getId()+"','+"+allClassesCbBox.getSelectionModel().getSelectedItem().toString()+"')");
					 }
					 else {
						 new Alert(Alert.AlertType.ERROR,"You Must Pick A Class").showAndWait();
					 }
				}
				else {
					new Alert(Alert.AlertType.ERROR,format).showAndWait();
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(con != null)
					con.close();
				if(rs != null)
					rs.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	void rqRemoveClassClick(ActionEvent Event) {
		con = SQLConnecter.connect();
		int count = 0;
		try {
			rs = con.createStatement().executeQuery("SELECT * FROM Grades WHERE StudentID = '"+Main.currentUser.getId()+"'");
			
			while(rs.next()) {
				count++;//gives us the amt of classes the student is taking
			}
			rs.close();
			if(count == 0) {
				new Alert(Alert.AlertType.ERROR,"You Have No Classes To Remove Yet").showAndWait();
			}
			else {
				rs = con.createStatement().executeQuery("SELECT * FROM Student_Management_Classes WHERE StudentID = "+Main.currentUser.getId()+"");
				
				if(count > 4) {//if student has bigger then 4 classes he can remove one 
					if(!rs.isBeforeFirst()) {//only execute below if the Request doesn't exist in the table
						if(getClassStatus().equals("Not-Started") || getClassStatus().equals("Completed")) {
							if(!(currentClassesCbBox.getSelectionModel().isEmpty())) {
								alert.showAndWait();
								SQLConnecter.executeQuery("INSERT INTO Student_Management_Classes (StudentID,Student_Request) VALUES ("+Main.currentUser.getId()+",'-"+currentClassesCbBox.getSelectionModel().getSelectedItem().toString()+"')");
							}
							else {
								 new Alert(Alert.AlertType.ERROR,"You Must Pick A Class").showAndWait();
							 }
						}
						else {
							new Alert(Alert.AlertType.ERROR,"Class is in Progress,Finish Your Class First").showAndWait();
						}
					}
					else {
						new Alert(Alert.AlertType.ERROR,format).showAndWait();
					}
				}
				else if (count <= 4) {
					new Alert(Alert.AlertType.ERROR,"You Need more then 4 classes To remove one").showAndWait();
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(con != null)
					con.close();
				if(rs != null)
					rs.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getCourseName() {//encapsulation
		return this.courseName;
	}
	
	public void setCourseName(String c) {
		this.courseName = c;
	}
	
	String getClassStatus() {
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		int classID = 0;
		int courseID = 0;
		String status = "";
		try {
			rs2 = con.createStatement().executeQuery("SELECT ClassID FROM Grades WHERE StudentID = "+Main.currentUser.getId()+"");
			
			if(rs2.next()) {
				classID = rs2.getInt("ClassID");//getting a random class id for the simple purpose of finding the courseID
			}
			rs2.close();
			
			rs2 = con.createStatement().executeQuery("SELECT CourseID FROM All_Classes WHERE ClassID = "+classID+"");
			
			if (rs2.next()) {
				courseID = rs2.getInt("CourseID");
			}
			rs2.close();
			
			rs2 = con.createStatement().executeQuery("SELECT ClassID FROM All_Classes WHERE CourseID = "+courseID+" AND ClassName = '"+currentClassesCbBox.getSelectionModel().getSelectedItem().toString()+"'");
			
			if(rs2.next()) {
				
				rs3 = con.createStatement().executeQuery("SELECT Status FROM Status WHERE ClassID = "+rs2.getInt("ClassID")+" AND StudentID = "+Main.currentUser.getId()+"");
				
				if(rs3.next()) {
					status = rs3.getString("Status");
				}
				rs3.close();
			}
			rs2.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return status;
	}
	
	@FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) table.getScene().getWindow();
    	stage1.close();
	}
}