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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;


public class RemoveClassController implements Initializable{
	
	ArrayList<String>allData = new ArrayList<String>();
	
	@FXML
	private Button rmvBtn;
	
	@FXML
	private ComboBox<String>allClasses;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		populateCbBX();
		
		ObservableList<String> list = FXCollections.observableArrayList(allData);
		allClasses.setItems(list);
	}
	
	void populateCbBX() {
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		
		try {
			rs = con.createStatement().executeQuery("SELECT ClassID,ClassName FROM All_Classes WHERE ClassID NOT IN ((SELECT ClassID FROM Grades) UNION (SELECT ClassID FROM Teachers WHERE ClassID IS NOT NULL))");
			
			while(rs.next()) {
				allData.add("ClassID : "+rs.getInt("ClassID")+" - ClassName : "+rs.getString("ClassName")+"");
			}
			rs.close();
			con.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(rs != null)
					rs.close();
				if(con != null)
					con.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
    void removeClassCick(ActionEvent event) {
		if(!allClasses.getSelectionModel().isEmpty()) {
			try {
				String format = String.format("%s%n%s", "You Are About to Remove All Subclasses Within The Class","Are You Sure About This ?");
				
				Alert alert  = new Alert(Alert.AlertType.CONFIRMATION,format);
				Optional<ButtonType> result = alert.showAndWait();
				
				if (result.get() == ButtonType.OK){
					SQLConnecter.executeQuery("DELETE FROM SubClasses WHERE ClassID = "+Integer.parseInt(allClasses.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""))+"");
					SQLConnecter.executeQuery("DELETE FROM All_Classes WHERE ClassID = "+Integer.parseInt(allClasses.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""))+"");
					new Alert(Alert.AlertType.INFORMATION,"Class And SubClasses Deleted").showAndWait();
				} else {
				    // ... user chose CANCEL or closed the dialog
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		else {
			new Alert(Alert.AlertType.ERROR,"You Must Select A Class").showAndWait();
		}
    }
	
	@FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) rmvBtn.getScene().getWindow();
    	stage1.close();
	}
}
