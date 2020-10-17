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
import javafx.scene.control.Alert;

public class StudentClasses 
{
	IntegerProperty studentIDProperty;
	StringProperty FNameProperty;
	StringProperty LNameProperty;
	IntegerProperty classIDProperty;
	StringProperty gradeProperty;
	StringProperty courseNameProperty;
	StringProperty classNameProperty;
	
	public StudentClasses() {
		this.FNameProperty = new SimpleStringProperty();
		this.LNameProperty = new SimpleStringProperty();
		this.studentIDProperty = new SimpleIntegerProperty();
		this.classIDProperty = new SimpleIntegerProperty();
		this.classNameProperty = new SimpleStringProperty();
		this.gradeProperty = new SimpleStringProperty();
		this.courseNameProperty = new SimpleStringProperty();
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
	
	//className
	
	public StringProperty getClassNameProperty() {
		return classNameProperty;
	}
	
	public String getClassName() {
		return classNameProperty.get();
	}
	
	public void setClassName(String n) {
		classNameProperty.set(n);
	}
	
	//grade
	
	public StringProperty getGradeProperty() {
		return gradeProperty;
	}
	
	public String getGrade() {
		return gradeProperty.get();
	}
	
	public void setGrade(String g) {
		gradeProperty.set(g);
	}
	
	//course
	
	public StringProperty getCourseNameProperty() {
		return courseNameProperty;	
	}
	
	public String getCourseName() {
		return courseNameProperty.get();
	}
	
	public void setCourseName(String name) {
		courseNameProperty.set(name);
	}
	
	//functions
	
	ObservableList<StudentClasses> getAllRecords(int id) throws SQLException{
		Connection con = SQLConnecter.connect();
		ObservableList<StudentClasses> stList = FXCollections.observableArrayList();
		
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		String courseName = "";
		
		try {
			courseName = getCurrCourse(id);		
			stList = getStObjects(con,rs,courseName,stList,id);
			con.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
				con.close();
			if(rs != null)
				rs.close();
			if(rs3 != null)
				rs3.close();
			if(rs1 != null)
				rs1.close();
			if(rs2 != null)
				rs2.close();
		}
		return stList;
	}
	
	ObservableList<StudentClasses> getStObjects(Connection con,ResultSet rs,String cN,ObservableList<StudentClasses> stList,int id) throws SQLException{
		int count = 0;
		ResultSet rs3 = null;
		try {
			
			rs = con.createStatement().executeQuery("SELECT ClassID,Grade From Grades WHERE StudentID = '"+id+"'");
			
			while(rs.next()) {
				count++;//checking if there are records
				StudentClasses sc =  new StudentClasses();
				sc.setClassID(rs.getInt("classID"));
				sc.setGrade(rs.getString("Grade"));
				rs3 = con.createStatement().executeQuery("SELECT ClassName FROM All_Classes WHERE ClassID = '"+sc.getClassID()+"'");//running the query everytime with a diferent classID
				if(rs3.next()) {
					sc.setClassName(rs3.getString("ClassName"));
				}
				sc.setCourseName(cN);
				stList.add(sc);
			}
			if(count == 0) {
				new Alert(Alert.AlertType.ERROR,"You don't have any Classes Currently").showAndWait();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
				con.close();
			if(rs != null)
				rs.close();
			if(rs3 != null)
				rs3.close();
		}
		return stList;
	}
	
	ObservableList<StudentClasses> getStudentRecordsTchr(int classID) throws SQLException{
		
		ObservableList<StudentClasses> sc = FXCollections.observableArrayList();
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		
		try {
			rs  = con.createStatement().executeQuery("SELECT StudentID,Grade FROM Grades WHERE ClassID = '"+classID+"'");
			
			while(rs.next()) {//while there are records it means that there are students in the class
				StudentClasses StudClass = new StudentClasses();
				StudClass.setStudentID(rs.getInt("StudentID"));
				StudClass.setGrade(rs.getString("Grade"));
				
				rs2 = con.createStatement().executeQuery("SELECT FirstName, LastName FROM Users WHERE ID = '"+StudClass.getStudentID()+"'");
				
				if(rs2.next()) {
					StudClass.setFName(rs2.getString("FirstName"));
					StudClass.setLName(rs2.getString("LastName"));	
				}
				StudClass.setCourseName(getCurrCourse(StudClass.getStudentID()));
				
				rs3 = con.createStatement().executeQuery("SELECT ClassName FROM All_Classes WHERE ClassID = '"+classID+"'");
				if(rs3.next()) {
					StudClass.setClassName(rs3.getString("ClassName"));

					sc.add(StudClass);
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
				con.close();
			if(rs != null)
				rs.close();
			if(rs3 != null)
				rs3.close();
			if(rs2 != null)
				rs2.close();
		}
		return sc;
	}
	
	String getCurrCourse(int studentID) {
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		String courseName = "";
		int classID = 0;
		int courseID = 0;
		try {
			rs = con.createStatement().executeQuery("SELECT ClassID,Grade FROM Grades WHERE StudentID = '"+studentID+"'");
			
			if(rs.next()) {
				classID = (rs.getInt("ClassID"));//simple query to get the courseID
				}
			rs.close();
			
			rs1 = con.createStatement().executeQuery("SELECT CourseID FROM All_Classes WHERE ClassID = '"+classID+"'");
			
			if(rs1.next()) {
				courseID = rs1.getInt("CourseID");
			}
			
			rs2 = con.createStatement().executeQuery("SELECT CourseName FROM All_Courses WHERE CourseID = '"+courseID+"'");
			
			if(rs2.next()) {
				courseName = rs2.getString("CourseName");
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return courseName;
	}
	
	ObservableList<StudentClasses> getSpecificStudent(int studentID,int classID)throws SQLException{
		
		ObservableList<StudentClasses> sc = FXCollections.observableArrayList();
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		
		try {
			rs  = con.createStatement().executeQuery("SELECT Grade FROM Grades WHERE StudentID = '"+studentID+"' AND ClassID = '"+classID+"'");
			
			while(rs.next()) {
				StudentClasses StudClass = new StudentClasses();
				StudClass.setStudentID(studentID);
				StudClass.setGrade(rs.getString("Grade"));//get new grade every iteration
				
				rs2 = con.createStatement().executeQuery("SELECT FirstName, LastName FROM Users WHERE ID = '"+studentID+"'");
				
				if(rs2.next()) {
					StudClass.setFName(rs2.getString("FirstName"));
					StudClass.setLName(rs2.getString("LastName"));	
				}
				StudClass.setCourseName(getCurrCourse(studentID));
				
				rs3 = con.createStatement().executeQuery("SELECT ClassName FROM All_Classes WHERE ClassID = '"+classID+"'");
				if(rs3.next()) {
					StudClass.setClassName(rs3.getString("ClassName"));

					sc.add(StudClass);
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
				con.close();
			if(rs != null)
				rs.close();
			if(rs3 != null)
				rs3.close();
			if(rs2 != null)
				rs2.close();
		}
		return sc;
	}
	
	void updateStudentGrade(String grade,int studentID,int classID) {
		try {
			SQLConnecter.executeQuery("UPDATE Grades SET Grade = '"+grade+"' WHERE StudentID = '"+studentID+"' AND ClassID = '"+classID+"'");
			SQLConnecter.executeQuery("UPDATE Status SET Status = 'In-Progress' WHERE StudentID = "+studentID+" AND ClassID = "+classID+"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void updateStudentFinalGrade(String grade,int studentID,int classID) {
		try {
			SQLConnecter.executeQuery("UPDATE Grades SET FinalGrade = '"+grade+"' WHERE StudentID = '"+studentID+"' AND ClassID = '"+classID+"'");
			SQLConnecter.executeQuery("UPDATE Status SET Status = 'Completed' WHERE StudentID = "+studentID+" AND ClassID = "+classID+"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
















