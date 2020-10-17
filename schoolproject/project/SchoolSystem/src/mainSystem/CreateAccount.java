package mainSystem;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateAccount extends Main implements Initializable
{
	Student student = new Student();
	
	@FXML
    private PasswordField PassTxt;

    @FXML
    private PasswordField confirmPassTxt;
	
	@FXML
    private Label classLbl;
	
	@FXML
    private TextField signupID;
    
    String s;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
	}
    
	@FXML
    void okClick(ActionEvent event) throws IOException, SQLException
    {
		try
		{
			if (PassTxt.getText().equals(confirmPassTxt.getText()) && !(PassTxt.getText().isEmpty()))//only students and teachers can make a new account
			{
				student.insert(Integer.parseInt(signupID.getText()),PassTxt.getText());	
			}
			else 
			{
				new Alert(Alert.AlertType.ERROR,"Make Sure You Type a Password and that they are the same").showAndWait();
			}
		}
		catch(NullPointerException e)
		{
			String s = String.format("Make Sure To Select a User type\nThen Select A Class if your'e a Student \nAswell as fill out all the fields");
			new Alert(Alert.AlertType.ERROR,s).showAndWait();
		}
	}
	
	@FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) confirmPassTxt.getScene().getWindow();
    	stage1.close();
	}
}
