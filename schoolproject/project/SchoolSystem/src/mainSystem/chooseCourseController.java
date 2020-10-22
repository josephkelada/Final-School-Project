package mainSystem;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class chooseCourseController implements Initializable{
	
	ArrayList<String> allCourses = new ArrayList<String>();
	
	@FXML
    private ComboBox<String> CoursesCbBx;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		populateCbBx();
		
		CoursesCbBx.setItems(FXCollections.observableArrayList(allCourses));
	}
	
	void populateCbBx() {
		ResultSet rs = null;
		Connection con = SQLConnecter.connect();
		
		try {
			rs = con.createStatement().executeQuery("SELECT CourseName FROM All_Courses WHERE CourseName != (SELECT CurrentCourse FROM Students WHERE StudentID = "+Main.currentUser.getId()+")");
			
			while(rs.next()) {
				allCourses.add(rs.getString("CourseName"));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
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
    void selectClick(ActionEvent event) {
		ResultSet rs = null;
		boolean flagStatus = false,flagReq = false;
		ResultSet rs2 = null;
		Connection con = SQLConnecter.connect();
		
		if(Main.currentUser.getId() == 0) {
			new Alert(Alert.AlertType.ERROR,"Please Login As A Student First").showAndWait();
		}
		else {
			try {
				rs = con.createStatement().executeQuery("SELECT * FROM Student_Management_Courses WHERE StudentID = "+Main.currentUser.getId()+"");
				
				rs2 = con.createStatement().executeQuery("SELECT Status FROM Status WHERE StudentID = '"+Main.currentUser.getId()+"'");
				
				if(!rs.isBeforeFirst()) {//meaning there are no requests
			 
					while(rs2.next()) {//checking if any of the classes in the student's course have been started
						
						if((rs2.getString("Status").equals("In-Progress"))) {
							flagStatus = true;//meaning started his course 
						}
					}
					rs.close();
					flagReq = checkStudentRequests();
					if (!flagStatus && flagReq) {//if student has no prior requests or status's
						if(!CoursesCbBx.getSelectionModel().isEmpty()) {
							SQLConnecter.executeQuery("INSERT INTO Student_Management_Courses (StudentID,Student_Request) VALUES ("+Main.currentUser.getId()+",'"+getCourseName(con,Main.currentUser.getId())+"-"+CoursesCbBx.getSelectionModel().getSelectedItem()+"')");
							String format = String.format("%s%n%s", "Course has Been Requested ", "Admin Will Now Review Your Request");
							new Alert(Alert.AlertType.INFORMATION,format).showAndWait();
						}
						else {
							new Alert(Alert.AlertType.ERROR,"You Must Select A Course").showAndWait();
						}
					}
					else {//if the user already has a class that is in progress
						String format = String.format("%s%n%s", "Try Later , Course Has Already Started!! or", "You Have A Pending Request");
						new Alert(Alert.AlertType.ERROR,format).showAndWait();
					}
					
				}
				else {
					String format = String.format("%s%n%s", "Administration Can Only Process", " One Request at a time , please Try Again Later");
					new Alert(Alert.AlertType.ERROR,format).showAndWait();
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(rs != null)
						rs.close();
					if(rs2 != null)
						rs2.close();
					if(con != null)
						con.close();
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) CoursesCbBx.getScene().getWindow();
    	stage1.close();
	}
	
	boolean checkStudentRequests() {//checks if student has any prior course or class change requests 
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		ResultSet rs2 = null;
		boolean flag = false;
		
		try {
			rs = con.createStatement().executeQuery("SELECT * FROM Student_Management_Courses WHERE StudentID = "+Main.currentUser.getId()+"");//courses
			
			if(!rs.isBeforeFirst()) {
				rs2 = con.createStatement().executeQuery("SELECT * FROM Student_Management_Classes WHERE StudentID = "+Main.currentUser.getId()+"");//classes
				
				if(!rs2.isBeforeFirst()) {
					flag = true;
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(rs != null)
					rs.close();
				if(rs2 != null)
					rs2.close();
				if(con != null)
					con.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
	
	String getCourseName(Connection con,int id) {
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		int classID = 0;
		int courseID = 0;
		String courseName = "";
		
		try {
			rs = con.createStatement().executeQuery("SELECT ClassID FROM Grades WHERE StudentID = '"+id+"'");
			
			if(rs.next()) {
				classID = (rs.getInt("ClassID"));//simple query to get the courseID
				}
			rs.close();
			
			rs1 = con.createStatement().executeQuery("SELECT CourseID FROM All_Classes WHERE ClassID = '"+classID+"'");
			
			if(rs1.next()) {
				courseID = rs1.getInt("CourseID");
			}
			rs1.close();
			
			rs2 = con.createStatement().executeQuery("SELECT CourseName FROM All_Courses WHERE CourseID = '"+courseID+"'");
			
			if(rs2.next()) {
				courseName = rs2.getString("CourseName");
			}
			rs2.close();
			if(courseName.isEmpty())
				courseName = "No Course";
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return courseName;
	}
}
