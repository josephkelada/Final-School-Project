package mainSystem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

public class Student extends Person //inheritance
{
	IntegerProperty studentIdProperty;
	/*StringProperty FNameProperty;
	StringProperty LNameProperty;
	StringProperty emailProperty;
	StringProperty PhoneNbProperty;*/
	StringProperty coursProperty;
	StringProperty gradeProperty;
	
	public Student() 
	{
		this.studentIdProperty = new SimpleIntegerProperty();
		this.gradeProperty = new SimpleStringProperty();
		/*this.LNameProperty = new SimpleStringProperty();
		this.emailProperty = new SimpleStringProperty();
		this.PhoneNbProperty = new SimpleStringProperty();*/
		this.coursProperty = new SimpleStringProperty();
	}
	
	//id

	public IntegerProperty getStudentIdProperty() 
	{
		return studentIdProperty;
	}
	
	public int getStudentId() 
	{
		return studentIdProperty.get();
	}

	public void setStudentId(int id) 
	{
		studentIdProperty.set(id);
	}
	
	//ClassiD
	/*
	//fName

	public StringProperty getFNameProperty() 
	{
		return FNameProperty;
	}
	
	public String getFName() 
	{
		return FNameProperty.get();
	}

	public void setFName(String fName) 
	{
		FNameProperty.set(fName);
	}
	
	//lName

	public StringProperty getLNameProperty() 
	{
		return LNameProperty;
	}
	
	public String getLName() 
	{
		return LNameProperty.get();
	}

	public void setLName(String lName) 
	{
		LNameProperty.set(lName);
	}
	
	//email

	public StringProperty getEmailProperty() 
	{
		return emailProperty;
	}
	
	public String getEmail() 
	{
		return emailProperty.get();
	}

	public void setEmail(String email) 
	{
		this.emailProperty.set(email);
	}
	
	//phoneNb

	public StringProperty getPhoneNbProperty() 
	{
		return PhoneNbProperty;
	}
	
	public String getPhoneNb() 
	{
		return PhoneNbProperty.get();
	}

	public void setPhoneNb(String phoneNbProperty) 
	{
		PhoneNbProperty.set(phoneNbProperty);
	}*/
	
	//cours

	public StringProperty getCoursProperty() 
	{
		return coursProperty;
	}
	
	public String getCours() 
	{
		return coursProperty.get();
	}

	public void setCours(String cours) 
	{
		coursProperty.set(cours);
	}
	
	//grade

	public StringProperty getGradeProperty() 
	{
		return gradeProperty;
	}
	
	public String getGrade() 
	{
		return gradeProperty.get();
	}

	public void setGrade(String cours) 
	{
		gradeProperty.set(cours);
	}
	
	public void insert(int id,String pwd)
	{
		ResultSet rs = null;
		Connection con = SQLConnecter.connect();
		String queryPass = "";
		String querySelect = "";
		queryPass = "UPDATE Users SET Password = '"+pwd+"' WHERE ID = '"+id+"'";
		querySelect = "SELECT * FROM USERS WHERE ID = '"+id+"'";
			
		try 
		{
			rs = con.createStatement().executeQuery(querySelect);
			
			if(!(rs.next()))
			{
				new Alert(Alert.AlertType.ERROR,"Account Non Existent Please Contact Administration").showAndWait();
			}
			else
			{
				SQLConnecter.executeQuery(queryPass);
				new Alert(Alert.AlertType.INFORMATION,"Account Created !").showAndWait();
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	ObservableList<Student> getAllRecords(int id) throws SQLException//polymorphism
	{
		ResultSet rs = null;
		ResultSet rs2 = null;
		ObservableList<Student> personList = null;
		Connection con = SQLConnecter.connect();
		try 
		{
			rs2 = con.createStatement().executeQuery("SELECT * FROM Users WHERE ID = "+id+"");
			rs = con.createStatement().executeQuery("SELECT * FROM Students WHERE StudentID = '"+id+"'");
			personList = getStudentObjects(rs,getCourseName(con,id),con,rs2);
			con.close();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}finally
		{
			if(con != null)
				con.close();
			if(rs != null)
				rs.close();
			if(rs2 != null)
				rs2.close();
		}
		return personList;
	}
	
	public ObservableList<Student> getStudentObjects(ResultSet rs,String courseName,Connection con,ResultSet rs2)
	{
		ObservableList<Student> persList = FXCollections.observableArrayList();
		
		try 
		{
			while(rs.next())
			{
				Student pers = new Student();
				pers.setStudentId(rs.getInt("StudentID"));
				if(rs2.next()) {
					pers.setEmail(rs2.getString("Email"));
					pers.setFName(rs2.getString("FirstName"));
					pers.setLName(rs2.getString("LastName"));
					pers.setPhoneNb(rs2.getString("PhoneNumber"));
				}
				pers.setCours(courseName);
				persList.add(pers);
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return persList;
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
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return courseName;
	}
	
	ObservableList<Student> getParentsKid(int id) {
		Connection con = SQLConnecter.connect();
		ResultSet rs2 = null;
		ResultSet rs = null;
		ResultSet rs3 = null;
		ArrayList<Integer> allGrades = new ArrayList<Integer>();
		int currentGrade = 0;
		
		ObservableList<Student> persList = FXCollections.observableArrayList();
		
		try 
		{
			rs = con.createStatement().executeQuery("SELECT LastName FROM Users WHERE ID = "+Main.currentUser.getId()+"");
			
			if(rs.next()) {
				rs2 = con.createStatement().executeQuery("SELECT * FROM Students WHERE ParentLastName = '"+rs.getString("LastName")+"'");
			}
			rs.close();
			
			while(rs2.next())
			{
				Student pers = new Student();
				pers.setStudentId(rs2.getInt("StudentID"));
				pers.setCours(rs2.getString("CurrentCourse"));
				rs = con.createStatement().executeQuery("SELECT * FROM Users WHERE ID = "+rs2.getInt("StudentID")+"");
				
				if(rs.next()) {
					pers.setEmail(rs.getString("Email"));
					pers.setFName(rs.getString("FirstName"));
					pers.setLName(rs.getString("LastName"));
					pers.setPhoneNb(rs.getString("PhoneNumber"));
				}
				rs3 = con.createStatement().executeQuery("SELECT Grade FROM Grades WHERE StudentID = "+rs2.getInt("StudentID")+"");
				
				while(rs3.next()) {
					allGrades.add(Integer.parseInt(rs3.getString("Grade").replaceAll("[^0-9]", "")));
				}
				
				for(int i = 0; i < allGrades.size();i++) {
					currentGrade += allGrades.get(i);
				}
				
				currentGrade = currentGrade / allGrades.size();//getting the average grade so far
				if(currentGrade == 1) {
					pers.setGrade("Not-Started");//if all grades = -1 , that means the student hasn't started yet
				}
				else {
					pers.setGrade(Integer.toString(currentGrade));
				}
				
				persList.add(pers);
			}
			rs.close();
			rs2.close();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}finally{
			try {
				if(con != null)
					con.close();
				if(rs != null)
					rs.close();
				if(rs2 != null)
					rs2.close();
			}catch(SQLException e1) {
				e1.printStackTrace();
			}
		}
		return persList;	
	}
}
