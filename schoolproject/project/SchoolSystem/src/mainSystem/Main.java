package mainSystem;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.microsoft.sqlserver.jdbc.StringUtils;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Main extends Application
{
	@FXML
	Button dummyBtnStd;
	
	@FXML
	Button loginBtn;
	
	@FXML
	Label welcomeTxt;
	
	@FXML
	Button admissionsBtn;
	
	@FXML
	Button createAccBtn;
	
	@FXML
	Button signInBtn;
	
	@FXML
	Button parentsBtn;
	
	@FXML
	Button teacherLoungeBtn;
	
	@FXML
	Button studentLougneBtn;
	
	@FXML
	Button adminBtn;
	
	@FXML
	public TextField userIDTxtBx;

    @FXML
    private PasswordField  paswdTxtBx;
	
	public static Person currentUser = new Person();
	
	String format = String.format("%s%n", "You Must Login As A Student First And Be Assigned To A Course");
	
	@Override
	public void start(Stage stage)
	{
	try{
		Parent root = FXMLLoader.load(Main.class.getResource("/mainSystem/index.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	   }
	   catch(Exception e)
	   {
		e.printStackTrace();
	   }
	}
	
	@FXML
    void admissionsClick(ActionEvent event) throws IOException,SQLException
	{
		Connection con = SQLConnecter.connect();
    	ResultSet rs1 = con.createStatement().executeQuery("SELECT * FROM Students WHERE StudentID = '"+Main.currentUser.getId()+"'");
		
    	try {
    		if(!rs1.isBeforeFirst()) {
    			new Alert(Alert.AlertType.ERROR,format.substring(0, 33)).showAndWait();
    		}
    		else {
    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/Admissions.fxml"));
    	    	Parent root = loader.load();
    	    	Scene scene = new Scene(root);
    			Stage stage = new Stage();
    			stage.setScene(scene);
    			stage.show();
    		}
    	} 
    	catch (SQLException e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if(con != null)
    				con.close();
    			if(rs1 != null)
    				rs1.close();
    		}
    		catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
	}

    @FXML
    void adminClick(ActionEvent event) throws IOException, SQLException 
    {
    	Connection con = SQLConnecter.connect();
    	ResultSet rs1 = con.createStatement().executeQuery("SELECT ID FROM Users WHERE ID = '"+Main.currentUser.getId()+"' AND Type = 'Admin'");
		
    	try {
    		if(!rs1.isBeforeFirst()) {
    			new Alert(Alert.AlertType.ERROR,"You Must Be An Admin To Access This").showAndWait();
    		}
    		else {
    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/Admin.fxml"));
    	    	Parent root = loader.load();
    	    	Scene scene = new Scene(root);
    			Stage stage = new Stage();
    			stage.setScene(scene);
    			stage.show();
    		}
    	} 
    	catch (SQLException e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if(con != null)
    				con.close();
    			if(rs1 != null)
    				rs1.close();
    		}
    		catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
    }

    @FXML
    void parentsClick(ActionEvent event) throws IOException, SQLException 
    {
    	Connection con = SQLConnecter.connect();
    	ResultSet rs1 = con.createStatement().executeQuery("SELECT ID FROM Users WHERE ID = '"+Main.currentUser.getId()+"' AND Type = 'Parent'");
		
    	try {
    		if(!rs1.isBeforeFirst()) {
    			new Alert(Alert.AlertType.ERROR,"You Must Be A Parent To Access This").showAndWait();
    		}
    		else {
    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/Parents.fxml"));
    	    	Parent root = loader.load();
    	    	Scene scene = new Scene(root);
    			Stage stage = new Stage();
    			stage.setScene(scene);
    			stage.show();
    		}
    	} 
    	catch (SQLException e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if(con != null)
    				con.close();
    			if(rs1 != null)
    				rs1.close();
    		}
    		catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    @FXML
    void viewStudentDetailsClick(ActionEvent event) throws IOException 
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/ViewStudentsParents.fxml"));
    	Parent root = loader.load();
    	Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
    }

    @FXML
    void studentsLoungeClick(ActionEvent event) throws IOException, SQLException 
    {
    	Connection con = SQLConnecter.connect();
    	ResultSet rs1 = con.createStatement().executeQuery("SELECT * FROM Students WHERE StudentID = '"+Main.currentUser.getId()+"'");
		
    	try {
    		if(!rs1.isBeforeFirst()) {
    			new Alert(Alert.AlertType.ERROR,format).showAndWait();
    		}
    		else {
    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/StudentLounge.fxml"));
    	    	Parent root = loader.load();
    	    	Scene scene = new Scene(root);
    			Stage stage = new Stage();
    			stage.setScene(scene);
    			stage.show();
    		}
    	} 
    	catch (SQLException e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if(con != null)
    				con.close();
    			if(rs1 != null)
    				rs1.close();
    		}
    		catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
    }

    @FXML
    void teachersLoungeClick(ActionEvent event) throws IOException, SQLException 
    {
    	Connection con = SQLConnecter.connect();
    	ResultSet rs1 = con.createStatement().executeQuery("SELECT * FROM Teachers WHERE Teacher_ID = '"+Main.currentUser.getId()+"'");
		
    	try {
    		if(!rs1.isBeforeFirst()) {
    			new Alert(Alert.AlertType.ERROR,"You Must Be Logged In As A Student").showAndWait();
    		}
    		else {
    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/TeacherLounge.fxml"));
    	    	Parent root = loader.load();
    	    	Scene scene = new Scene(root);
    			Stage stage = new Stage();
    			stage.setScene(scene);
    			stage.show();
    		}
    	} 
    	catch (SQLException e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if(con != null)
    				con.close();
    			if(rs1 != null)
    				rs1.close();
    		}
    		catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    @FXML
    void aboutClick(ActionEvent event) throws IOException
    {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/About.fxml"));
    	Parent root = loader.load();
    	Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
    }
    
    @FXML
    void createAccClick(ActionEvent event) throws IOException 
    {	
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/signup.fxml"));
    	Parent root = loader.load();
    	Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
    }
    
    @FXML
    void signInClick(ActionEvent event)
    {
    	try 
    	{
    		if(StringUtils.isNumeric(userIDTxtBx.getText()))
    		{
    			currentUser = currentUser.checkValidity(Integer.parseInt(userIDTxtBx.getText()),paswdTxtBx.getText(),currentUser);
    			currentUser.getMessages();
    			
    			switch(currentUser.getPersonType()) {
    			case "Student":
    				teacherLoungeBtn.setDisable(true);
    				parentsBtn.setDisable(true);
    				adminBtn.setDisable(true);
    				admissionsBtn.setDisable(false);
    				studentLougneBtn.setDisable(false);
    				break;
    			case "Teacher":
    				teacherLoungeBtn.setDisable(false);
    				admissionsBtn.setDisable(true);
    				parentsBtn.setDisable(true);
    				adminBtn.setDisable(true);
    				studentLougneBtn.setDisable(true);
    				break;
    			case "Admin":
    				adminBtn.setDisable(false);
    				teacherLoungeBtn.setDisable(true);
    				admissionsBtn.setDisable(true);
    				parentsBtn.setDisable(true);
    				studentLougneBtn.setDisable(true);
    				break;
    			case "Parent":
    				parentsBtn.setDisable(false);
    				teacherLoungeBtn.setDisable(true);
    				admissionsBtn.setDisable(true);
    				adminBtn.setDisable(true);
    				studentLougneBtn.setDisable(true);
    				break;
    			}
    			//setting our main page
    			//if you want to access logins at all times simply remove these lines below
    			userIDTxtBx.setVisible(false);
    			paswdTxtBx.setVisible(false);
    			signInBtn.setVisible(false);
    			createAccBtn.setVisible(false);
    			welcomeTxt.setVisible(true);
    			//
    			loginBtn.setVisible(true);//for devs
    		}
    		else
    		{
    			new Alert(Alert.AlertType.ERROR,"User ID Must be a number , Please Try Again").showAndWait();
    		}
		} 
    	catch (NumberFormatException e) 
    	{
			e.printStackTrace();
		} 	
    }
    
    @FXML
    void viewCoursesClick(ActionEvent event) throws IOException//studentLounge
    {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/ViewCoursesStudent.fxml"));
    	Parent root = loader.load();
    	Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
    }
    
    @FXML
    void manageCoursesClick(ActionEvent event) throws IOException{
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/ManageCourseStudent.fxml"));
    	Parent root = loader.load();
    	Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
    }
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@FXML
	void loginClick(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/Index.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) dummyBtnStd.getScene().getWindow();
    	stage1.close();
	}
}
