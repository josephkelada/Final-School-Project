package mainSystem;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Admin extends Main
{	
	@FXML
	Button dummyBtnStd;
	
	@FXML
    void manageUsersClick(ActionEvent event) throws IOException
    {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/ManageUsers.fxml"));
    	Parent root = loader.load();
    	Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
    }
	
	@FXML
    void updateTeacherClassClick(ActionEvent event) throws IOException
    {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/updateTeacherClass.fxml"));
    	Parent root = loader.load();
    	Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
    }
    
    @FXML
    void updateStudentClassClick(ActionEvent event) throws IOException
    {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/updateStudentClass.fxml"));
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