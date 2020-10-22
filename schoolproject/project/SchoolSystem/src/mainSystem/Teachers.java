package mainSystem;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Teachers extends Main
{
	
	boolean flag = false;
	
	@FXML
	Button dummyBtn;
	
    @FXML
    void manageCurriculumClick(ActionEvent event) throws IOException
    {
    	ResultSet rs = null;
    	
    	try {
    		rs = SQLConnecter.connect().createStatement().executeQuery("SELECT * FROM Teachers WHERE Teacher_ID = '"+Main.currentUser.getId()+"'");
    		if(!rs.isBeforeFirst()) {
    			new Alert(Alert.AlertType.ERROR,"You Must Login As A Teacher Before Continuing").showAndWait();
    			flag = true;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(rs != null)
					rs.close();
			}catch(SQLException e1) {
				e1.printStackTrace();
			}
		}
    	
    	if(!flag) {
    		flag = false;
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/ManageCurriculum.fxml"));
        	Parent root = loader.load();
        	Scene scene = new Scene(root);
    		Stage stage = new Stage();
    		stage.setScene(scene);
    		stage.show();
    	}
    }
    
    @FXML
    void viewMyStudentsClick(ActionEvent e) throws IOException{
    	ResultSet rs = null;
    	try {
			rs = SQLConnecter.connect().createStatement().executeQuery("SELECT * FROM Teachers WHERE Teacher_ID = '"+Main.currentUser.getId()+"'");
			if(!rs.isBeforeFirst()) {
				new Alert(Alert.AlertType.ERROR,"You Must Login As A Teacher Before Continuing").showAndWait();
				flag = true;
			}
		}
		catch(SQLException ee) {
			ee.printStackTrace();
		}finally{
			try {
				if(rs != null)
					rs.close();
			}catch(SQLException e1) {
				e1.printStackTrace();
			}
		}
    	if(!flag) {
    		flag = false;
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/ViewStudentGrades.fxml"));
        	Parent root = loader.load();
        	Scene scene = new Scene(root);
        	Stage stage = new Stage();
        	stage.setScene(scene);
        	stage.show();
    	}
    }
    
    @FXML
    void ManageStatusClick(ActionEvent e) throws IOException{
    	ResultSet rs = null;
    	try {
			rs = SQLConnecter.connect().createStatement().executeQuery("SELECT * FROM Teachers WHERE Teacher_ID = '"+Main.currentUser.getId()+"'");
			if(!rs.isBeforeFirst()) {
				new Alert(Alert.AlertType.ERROR,"You Must Login As A Teacher Before Continuing").showAndWait();
				flag = true;
			}
		}
		catch(SQLException ee) {
			ee.printStackTrace();
		}finally{
			try {
				if(rs != null)
					rs.close();
			}catch(SQLException e1) {
				e1.printStackTrace();
			}
		}
    	if(!flag) {
    		flag = false;
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/manageStatus.fxml"));
        	Parent root = loader.load();
        	Scene scene = new Scene(root);
        	Stage stage = new Stage();
        	stage.setScene(scene);
        	stage.show();
    	}
    }
    
    @FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) dummyBtn.getScene().getWindow();
    	stage1.close();
	}
}
