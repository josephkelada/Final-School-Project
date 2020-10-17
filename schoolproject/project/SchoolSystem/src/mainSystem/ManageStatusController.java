package mainSystem;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class ManageStatusController implements Initializable{
	
	ArrayList<String>allStudentsStatus = new ArrayList<String>();
	
	@FXML
	private ComboBox<String>allStudentsCbBx;
	
	@FXML
	private ComboBox<String>statusCbBx;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		populateComboBoxes();
		
		ObservableList<String> list = FXCollections.observableArrayList("Not-Started","In-Progress","Completed");
		statusCbBx.setItems(list);
		
		list = FXCollections.observableArrayList(allStudentsStatus);
		allStudentsCbBx.setItems(list);
	}
	
	void populateComboBoxes() {
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		
		try {
			rs = con.createStatement().executeQuery("SELECT ClassID FROM Teachers WHERE Teacher_ID = "+Main.currentUser.getId()+"");
			
			while(rs.next()) {
				rs1 = con.createStatement().executeQuery("SELECT StudentID FROM Grades WHERE ClassID = "+rs.getInt("ClassID")+"");
				
				while(rs1.next()) {
					rs2 = con.createStatement().executeQuery("SELECT Status FROM Status WHERE StudentID = "+rs1.getInt("StudentID")+"");
					
					if(rs2.next()) {
						allStudentsStatus.add("Student ID : "+rs1.getInt("StudentID")+" - Class ID : "+rs.getInt("ClassID")+" - Status : "+rs2.getString("Status")+"");
					}
				}
			}
			if(allStudentsStatus.isEmpty()) {
				new Alert(Alert.AlertType.ERROR,"There Are No Students In Your Class").showAndWait();
				allStudentsCbBx.setPromptText("No Students In My Classes");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if(con != null)
					con.close();
				if(rs != null)
					rs.close();
				if(rs1 != null)
					rs1.close();
				if(rs2 != null)
					rs2.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	void updateStatusClick(ActionEvent e) {
		try {
			
			if(allStudentsStatus.isEmpty()) {
				new Alert(Alert.AlertType.ERROR,"There Are No Students In Your Class!").showAndWait();
			}
			else if(!allStudentsCbBx.getSelectionModel().isEmpty()) {
				if(!statusCbBx.getSelectionModel().isEmpty()) {
					
					int length = allStudentsCbBx.getSelectionModel().getSelectedItem().length();
					String status = allStudentsCbBx.getSelectionModel().getSelectedItem().substring(40, length).replaceAll("[^a-zA-Z-]", "");
		
					if(status.equals("Completed")) {
						String msg = String.format("%s%n%s", "Student Has Already Completed This Class,","Are you sure You Want to Edit The Status?");
						Alert alert  = new Alert(Alert.AlertType.CONFIRMATION,msg);
						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK){
							SQLConnecter.executeQuery("UPDATE Status SET Status = '"+statusCbBx.getSelectionModel().getSelectedItem().toString()+"' WHERE StudentID = '"+Integer.parseInt(allStudentsCbBx.getSelectionModel().getSelectedItem().toString().substring(13,16).replaceAll("[^0-9]", ""))+"'");
						} else {
						    // ... user chose CANCEL dont do anything 
						}
					}
				}
				else {
					new Alert(Alert.AlertType.ERROR,"You Must Select A Status!").showAndWait();
				}
			}
			else {
				new Alert(Alert.AlertType.ERROR,"You Must Select A Student!").showAndWait();
			}
		}catch(SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	@FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) statusCbBx.getScene().getWindow();
    	stage1.close();
	}
}
 