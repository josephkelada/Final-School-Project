package mainSystem;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Admissions extends Main
{
	
	@FXML
	private Button dummyBtnStd;
	
	@FXML
    void prerequisitesClick(ActionEvent event) throws IOException
    {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/Prerequesites.fxml"));
    	Parent root = loader.load();
    	Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
    }
	
	@FXML
    private Button yesRequirementsBtn;

    @FXML
    void yesRequirements(ActionEvent event) throws IOException //enable user to admit himself
    {
    	Stage stage1 = (Stage) yesRequirementsBtn.getScene().getWindow();
    	stage1.close();
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/AdmissionsGranted.fxml"));
    	Parent root = loader.load();
    	Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
    }
    
    @FXML
    void noRequirements(ActionEvent event)
    {
    	new Alert(Alert.AlertType.INFORMATION, "Please Exit This Application and Improve your GPA").showAndWait();
    }
    
    @FXML
    void exitPrerequesites(ActionEvent event) 
    {
    	Stage stage = (Stage) yesRequirementsBtn.getScene().getWindow();
    	stage.close();
    }
    
    @FXML
    void chooseCourseClick(ActionEvent event) throws IOException 
    {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/chooseCourse.fxml"));
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
