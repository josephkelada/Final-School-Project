package mainSystem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Person 
{
	protected IntegerProperty IdProperty;
	protected StringProperty FNameProperty;
	protected StringProperty LNameProperty;
	protected StringProperty emailProperty;
	protected StringProperty PhoneNbProperty;
	protected StringProperty personTypeProperty;

	public Person() 
	{
		this.IdProperty = new SimpleIntegerProperty();
		this.FNameProperty = new SimpleStringProperty();
		this.LNameProperty = new SimpleStringProperty();
		this.emailProperty = new SimpleStringProperty();
		this.PhoneNbProperty = new SimpleStringProperty();
		this.personTypeProperty = new SimpleStringProperty();
	}
	
	//id

	public IntegerProperty getIdProperty() 
	{
		return IdProperty;
	}
	
	public Integer getId() 
	{
		return IdProperty.get();
	}

	public void setId(int id) 
	{
		IdProperty.set(id);
	}
	
	//personType
	
	public StringProperty getPersonTypeProperty() 
	{
		return personTypeProperty;
	}

	public void setPersonType(String type) 
	{
		personTypeProperty.set(type);
	}
	
	public String getPersonType() 
	{
		return personTypeProperty.get();
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
	}
	
	//functions

	void insert(String Fname,String Lname,String email,String PhoneNb,String type) throws SQLException
	{
		ResultSet rs = null;
		Connection con = SQLConnecter.connect();
		String pFname = "";
		String pLname = "";
		
		String query ="";
		if (type == "Student")
		{
			rs = con.createStatement().executeQuery("SELECT FirstName,LastName FROM Users WHERE LastName = '"+Lname+"' AND Type = 'Parent'");
			
			if(rs.next()) {
				pFname = rs.getString("FirstName");
				pLname = rs.getString("LastName");
			}
			else {//means the student has no parents in the system
				new Alert(Alert.AlertType.ERROR,"No Parents In System,Please Add Student's Parents").showAndWait();
			}
			query ="INSERT INTO Users (FirstName, LastName, PhoneNumber,Email,Type) VALUES ('"+Fname+"', '"+Lname+"', '"+PhoneNb+"','"+email+"','Student');"
					+ "INSERT INTO Students (StudentID,ParentFirstName,ParentLastName,CurrentCourse) SELECT @@IDENTITY,'"+pFname+"', '"+pLname+"', 'No Course';";
			rs.close();
			con.close();
		}
		else if (type == "Parent")
		{
			query ="INSERT INTO Users (FirstName, LastName, PhoneNumber,Email,Type) VALUES ('"+Fname+"', '"+Lname+"', '"+PhoneNb+"','"+email+"','Parent');";
		}
		else if (type == "Teacher")
		{
			query ="INSERT INTO Users (FirstName, LastName, PhoneNumber,Email,Type) VALUES ('"+Fname+"', '"+Lname+"', '"+PhoneNb+"','"+email+"','Teacher')"
					+ "INSERT INTO Teachers (Teacher_ID,ClassID) SELECT @@IDENTITY,NULL";//adding the most recent record which is the id
		}
		new Alert(Alert.AlertType.INFORMATION,"User Added!").showAndWait();
		SQLConnecter.executeQuery(query);	
	}
	
	void delete(int id) throws SQLException
	{
		ResultSet rs = null;
		ResultSet rs2 = null;
		Connection con = SQLConnecter.connect();
		boolean flag = false;
		String type = "";
		
		rs = con.createStatement().executeQuery("SELECT Type FROM Users WHERE ID = "+id+"");
		
		if(rs.next()) {
			type = rs.getString("Type");
		}
		rs.close();
		if (type == "Student")
		{
			SQLConnecter.executeQuery("DELETE FROM Students WHERE StudentID = '"+id+"';DELETE FROM Users WHERE ID = '"+id+"'; ");
		}
		else if (type.equals("Teacher"))
		{
			rs = con.createStatement().executeQuery("SELECT ClassID FROM Teachers WHERE Teacher_ID = "+id+"");
			
			while(rs.next()) {
				rs2 = con.createStatement().executeQuery("SELECT Status FROM Status WHERE ClassID = "+rs.getInt("ClassID")+"");
				
				while(rs2.next()) {//going through every status to check for one class in progress
					if(rs2.getString("Status").equals("In-Progress")) {
						flag = true;
					}
				}
			}
			if(!flag) {
				Alert alert  = new Alert(Alert.AlertType.CONFIRMATION,"Are You Sure You want To Delete?");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					SQLConnecter.executeQuery("DELETE FROM Teachers WHERE Teacher_ID = '"+id+"';DELETE FROM Users WHERE ID = '"+id+"';");
				} else {
				    // ... user chose CANCEL or closed the dialog
				}
			}
			else {
				String format = String.format("%s%n%s", "Teacher Has Class In Progress","Please Go To \"Update Teacher Class\"");
				new Alert(Alert.AlertType.ERROR,format).showAndWait();
			}
			rs.close();
			rs2.close();
			con.close();
		}
		else if(type.equals("Parent")) {
			SQLConnecter.executeQuery("DELETE FROM Users WHERE ID = "+id+"");
		}
	}
	
	void updatePhoneNb(int id,String newPhoneNb) throws SQLException
	{
		String query ="UPDATE Users SET PhoneNumber = '"+newPhoneNb+"' WHERE ID = '"+id+"'";
		SQLConnecter.executeQuery(query);
	}
	
	void updateEmail(int id,String newEmail) throws SQLException
	{
		String query ="UPDATE Users SET Email = '"+newEmail+"' WHERE ID = '"+id+"'";
		SQLConnecter.executeQuery(query);
	}
	
	Person checkValidity(int id,String pwd,Person user) //also sets the current User
	{
		ResultSet rs = null;
		Connection con = SQLConnecter.connect();
		try 
		{
			rs = con.createStatement().executeQuery("SELECT * FROM Users WHERE ID = '"+id+"' AND Password = '"+pwd+"'");
			
			if(!(rs.next()))
			{
				new Alert(Alert.AlertType.ERROR,"Wrong Credentials,Try Again or Create New Account").showAndWait();
			}
			else 
			{
				user.setId(rs.getInt("ID"));
				user.setEmail(rs.getString("Email"));
				user.setFName(rs.getString("FirstName"));
				user.setLName(rs.getString("LastName"));
				user.setPhoneNb(rs.getString("PhoneNumber"));
				user.setPersonType(rs.getString("Type"));
				
				new Alert(Alert.AlertType.INFORMATION,"You Are Logged in As a "+rs.getString("Type")+"! Select One of the Options Above to Start").showAndWait();
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		finally {
			try {
				if(con != null) {
					con.close();
				}
					if(rs != null) {
						rs.close();
					}	
				} catch (SQLException e) {
					e.printStackTrace();
				}
					
				
			
		}
		return user;
	}
	
	ObservableList<Person> getAllRecords(String type) throws SQLException
	{
		ResultSet rs = null;
		ObservableList<Person> personList = null;
		Connection con = SQLConnecter.connect();
		try 
		{
			rs = con.createStatement().executeQuery("SELECT * FROM Users WHERE Type = '"+type+"'");
			personList = getPersonObjects(rs);
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
		}
		return personList;
	}
	
	public ObservableList<Person> getPersonObjects(ResultSet rs)
	{
		ObservableList<Person> persList = FXCollections.observableArrayList();
		
		try 
		{
			while(rs.next())
			{
				Person pers = new Person();
				pers.setId(rs.getInt("ID"));
				pers.setEmail(rs.getString("Email"));
				pers.setFName(rs.getString("FirstName"));
				pers.setLName(rs.getString("LastName"));
				pers.setPhoneNb(rs.getString("PhoneNumber"));
				pers.setPersonType(rs.getString("Type"));
				persList.add(pers);
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return persList;
	}
	
	void getMessages() {
		ResultSet rs = null;
		Connection con = SQLConnecter.connect();
		
		try {
			rs = con.createStatement().executeQuery("SELECT TOP(1) * FROM All_Requests WHERE StudentID = "+Main.currentUser.getId()+" ORDER BY RequestID DESC");//getting the most recently added record
			
			if(rs.next()) {
				//means there is a request
				
				String format = String.format("%s%n%s%n%s", "You Have One Notification !","Administration has "+rs.getString("Admin_Answer")+" Your Request : ",""+rs.getString("Student_Request")+"");
				new Alert(Alert.AlertType.INFORMATION,format).showAndWait();
			}
			rs.close();
			con.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
}

