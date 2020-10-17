package mainSystem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StudentRequest {
	IntegerProperty studentIDProperty;
	StringProperty fNameProperty;
	StringProperty lNameProperty;
	IntegerProperty classIDProperty;
	StringProperty requestProperty;
	StringProperty adminAnswerProperty;
	StringProperty classStatusProperty;
	
	public StudentRequest() {
		this.fNameProperty = new SimpleStringProperty();
		this.lNameProperty = new SimpleStringProperty();
		this.studentIDProperty = new SimpleIntegerProperty();
		this.classIDProperty = new SimpleIntegerProperty();
		this.classStatusProperty = new SimpleStringProperty();
		this.requestProperty = new SimpleStringProperty();
		this.adminAnswerProperty = new SimpleStringProperty();
	}
	
	//classID
	
	public IntegerProperty getClassIDProperty() {
		return classIDProperty;
	}
	
	public int getClassID() {
		return classIDProperty.get();
	}
	
	public void setClassID(int id) {
		classIDProperty.set(id);
	}
	
	//fName

	public StringProperty getFNameProperty() 
	{
		return fNameProperty;
	}
	
	public String getFName() 
	{
		return fNameProperty.get();
	}

	public void setFName(String fName) 
	{
		fNameProperty.set(fName);
	}
	
	//lName

	public StringProperty getLNameProperty() 
	{
		return lNameProperty;
	}
	
	public String getLName() 
	{
		return lNameProperty.get();
	}

	public void setLName(String lName) 
	{
		lNameProperty.set(lName);
	}
	
	//studentID
	
	public IntegerProperty getStudentIDProperty() {
		return studentIDProperty;
	}
	
	public int getStudentID() {
		return studentIDProperty.get();
	}
	
	public void setStudentID(int id) {
		studentIDProperty.set(id);
	}
	
	//Request
	
	public StringProperty getRequestProperty() {
		return requestProperty;
	}
	
	public String getRequest() {
		return requestProperty.get();
	}
	
	public void setRequest(String request) {
		requestProperty.set(request);
	}
	
	//AdminAnswerProperty
	
	public StringProperty getAnswerProperty() 
	{
		return adminAnswerProperty;
	}
	
	public String getAdminAnswer() 
	{
		return adminAnswerProperty.get();
	}

	public void setAdminAnswer(String answer) 
	{
		adminAnswerProperty.set(answer);
	}
	
	//classStatus
	
	public StringProperty getClassStatusProperty() 
	{
		return classStatusProperty;
	}
	
	public String getClassStatus() 
	{
		return classStatusProperty.get();
	}

	public void setClassStatus(String status) 
	{
		classStatusProperty.set(status);
	}
	
	//functions 
	
	ObservableList<StudentRequest> populateTable(char requestChar,int studentID){
		ObservableList<StudentRequest> srList = FXCollections.observableArrayList();
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		String requestNoSpecChar = "";
		int classID = 0;
		int courseID = 0;
		
		try {
			rs = con.createStatement().executeQuery("SELECT ID,FirstName,LastName FROM Users WHERE ID = "+studentID+"");
			
			while(rs.next()) {
				StudentRequest sr = new StudentRequest();
				sr.setStudentID(rs.getInt("ID"));
				sr.setFName(rs.getString("FirstName"));
				sr.setLName(rs.getString("LastName"));
				
				//get the requested class
				rs2 = con.createStatement().executeQuery("SELECT Student_Request,Admin_Answer FROM Student_Management_Classes WHERE StudentID = "+studentID+"");
				
				if (rs2.next()) {
					sr.setAdminAnswer(rs2.getString("Admin_Answer"));
					sr.setRequest(rs2.getString("Student_Request"));
					requestNoSpecChar = (rs2.getString("Student_Request")).substring(1);//removing the the + or - sign from the request 
				}
				rs2.close();
				//we can match courseID with the className
				if(requestChar == '-') {//if the user wants to remove a class 
					rs2 = con.createStatement().executeQuery("SELECT ClassID FROM Grades WHERE StudentID = "+studentID+"");
					
					if(rs2.next()) {
						classID = rs2.getInt("ClassID");//getting a random class id for the simple purpose of finding the courseID
					}
					rs2.close();
					
					rs2 = con.createStatement().executeQuery("SELECT CourseID FROM All_Classes WHERE ClassID = "+classID+"");
					
					if (rs2.next()) {
						courseID = rs2.getInt("CourseID");
					}
					rs2.close();
					
					rs2 = con.createStatement().executeQuery("SELECT ClassID FROM All_Classes WHERE CourseID = "+courseID+" AND ClassName = '"+requestNoSpecChar+"'");
					
					if(rs2.next()) {
						sr.setClassID(rs2.getInt("ClassID"));
						
						rs3 = con.createStatement().executeQuery("SELECT Status FROM Status WHERE ClassID = "+rs2.getInt("ClassID")+" AND StudentID = "+studentID+"");
						
						if(rs3.next()) {
							sr.setClassStatus(rs3.getString("Status"));
						}
						rs3.close();
					}
					rs2.close();
				}
				else if(requestChar == '+') {
					sr.setClassID(9999);
					sr.setClassStatus("9999");
				}
				srList.add(sr);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(con != null)
					con.close();
				if(rs != null)
					rs.close();
				if(rs2 != null)
					rs2.close();
				if(rs3 != null)
					rs3.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return srList;		
	}
	
	ObservableList<StudentRequest> populateTableCourse(int studentID){
		ObservableList<StudentRequest> srList = FXCollections.observableArrayList();
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		
		try {
			rs = con.createStatement().executeQuery("SELECT ID,FirstName,LastName FROM Users WHERE ID = "+studentID+"");
			
			while(rs.next()) {
				StudentRequest sr = new StudentRequest();
				sr.setStudentID(rs.getInt("ID"));
				sr.setFName(rs.getString("FirstName"));
				sr.setLName(rs.getString("LastName"));
				sr.setClassID(9999);
				sr.setClassStatus("9999");
				
				//get the requested class
				rs2 = con.createStatement().executeQuery("SELECT Student_Request,Admin_Answer FROM Student_Management_Courses WHERE StudentID = "+studentID+"");
				
				if (rs2.next()) {
					sr.setAdminAnswer(rs2.getString("Admin_Answer"));
					sr.setRequest(rs2.getString("Student_Request"));
				}
				rs2.close();
				
				rs3 = con.createStatement().executeQuery("SELECT Status FROM Status WHERE StudentID = "+studentID+"");
				
				while(rs3.next()) {
					if(rs3.getString("Status").equals("In-Progress")) {
						sr.setClassStatus("In-Progress");
					}
				}
				rs3.close();
				
				srList.add(sr);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(con != null)
					con.close();
				if(rs != null)
					rs.close();
				if(rs2 != null)
					rs2.close();
				if(rs3 != null)
					rs3.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return srList;		
	}
}
