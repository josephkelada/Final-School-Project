package mainSystem;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UpdateTeacherClassController implements Initializable{
	
	ArrayList<String> allClassData = new ArrayList<String>();
	ArrayList<String> allTeacherData = new ArrayList<String>();
	ArrayList<String> visibleData = new ArrayList<String>();
	ArrayList<String> teacherClasses = new ArrayList<String>();
	
	@FXML
	private ComboBox<String> allClassesCbBx;
	
	@FXML
	private ComboBox<String> teacherClassesCbBx;
	
	@FXML
    private Label chooseTchrLbl;
	
	@FXML
    private Button assignTeacherBtn;
	
	@FXML
	private ComboBox<String>visibleTeacherCbBx;

	@FXML
	private ComboBox<String> allTeachersCbBx;
	
	ObservableList<String> list;
	 
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		populateCbBx();
		
		list = FXCollections.observableArrayList(allClassData);
		allClassesCbBx.setItems(list);
		
		list = FXCollections.observableArrayList(allTeacherData);
		allTeachersCbBx.setItems(list);
		
		list = FXCollections.observableArrayList(visibleData);
		visibleTeacherCbBx.setItems(list);
	}
	 
	void populateCbBx() {
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
	
		try {
			rs = con.createStatement().executeQuery("SELECT ClassID,ClassName FROM All_Classes WHERE ClassID NOT IN ((SELECT ClassID FROM Grades) UNION (SELECT ClassID FROM Teachers WHERE ClassID IS NOT NULL))");
			 
			while(rs.next()) {
				allClassData.add("ClassID : "+rs.getInt("ClassID")+" - ClassName : "+rs.getString("ClassName")+"");
			}
			rs.close();
			
			rs = con.createStatement().executeQuery("SELECT DISTINCT Teacher_ID FROM Teachers");
			 
			while(rs.next()) {
				allTeacherData.add("TeacherID : "+rs.getInt("Teacher_ID")+"");
			}
			rs.close();
			
			rs = con.createStatement().executeQuery("SELECT Teacher_ID FROM Teachers WHERE Teacher_ID NOT IN (SELECT Teacher_ID FROM Teachers WHERE ClassID IS NOT NULL)");
			 
			while(rs.next()) {
				visibleData.add("Valid TeacherID : "+rs.getInt("Teacher_ID")+"");
			}
			rs.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(con != null)
					con.close();
				if(rs != null)
					rs.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	void applyClick(ActionEvent e) {
		if(!checkDisabled()) {
			Connection con = SQLConnecter.connect();
			ResultSet rs = null;
			String format ="";
			
			if(!allTeachersCbBx.getSelectionModel().isEmpty()) {
				
				if(!allClassesCbBx.getSelectionModel().isEmpty()) {
					int teacherID = Integer.parseInt(allTeachersCbBx.getSelectionModel().getSelectedItem().replaceAll("[^0-9]",""));
					
					try {
						rs = con.createStatement().executeQuery("SELECT ClassID FROM Teachers WHERE Teacher_ID = "+teacherID+" AND ClassID IS NOT NULL");
						
						if(!rs.isBeforeFirst()) {//if the rs contains no rows it means that teacher is not teaching any classes currently
							format = String.format("%s", "Teacher Will Only Teach The New Class");
							new Alert(Alert.AlertType.WARNING,format).showAndWait();
							SQLConnecter.executeQuery("UPDATE Teachers SET ClassID = "+Integer.parseInt(allClassesCbBx.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""))+" WHERE Teacher_ID = "+teacherID+"");
						}
						else {//means teacher is already teaching smtg
							setVisible();
							format = String.format("%s%n%s%n%s%n%s", "Teacher Will Now Teach The New Class","And You Can Remove The Old Class Only If it has no Students by Clicking \"Remove Class\" Below","Or You Can Assign A teacher To The Old Class By Clicking \"Assign To Old Class\" Below Or ","Simply Keep The Same Teacher And Exit");
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.getDialogPane().setContent( new Text(format));//big msg
							alert.showAndWait();
							SQLConnecter.executeQuery("INSERT INTO Teachers VALUES("+teacherID+","+Integer.parseInt(allClassesCbBx.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""))+")");
						}
						rs.close();
						
						rs = con.createStatement().executeQuery("SELECT ClassID FROM Teachers WHERE ClassID != "+Integer.parseInt(allClassesCbBx.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""))+" AND Teacher_ID = "+Integer.parseInt(allTeachersCbBx.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""))+"");
							 
						while(rs.next()) {
								teacherClasses.add("ClassID : "+rs.getInt("ClassID")+"");
							}
						teacherClassesCbBx.setItems(FXCollections.observableArrayList(teacherClasses));
						
						rs.close();
					}catch(SQLException e1) {
						e1.printStackTrace();
					}finally {
						refreshClasses();
						try {
							if(con != null)
								con.close();
							if(rs != null)
								rs.close();
						}
						catch(SQLException e2) {
							e2.printStackTrace();
						}
					}
				}
				else {
					new Alert(Alert.AlertType.ERROR,"You Must Select A Class!").showAndWait();
				}
			}
			else {
				new Alert(Alert.AlertType.ERROR,"You Must Select A Teacher").showAndWait();
			}
		}
	}
	
	@FXML
	void assignTeacherClick(ActionEvent e) {//assign teacher that doesn't have any classes//validate to make sure that there are available teachers 
		Connection con = SQLConnecter.connect();//below is just incase the old teacher has more then one class so admin can choose which class to attribute to the new replacement teacher 
		ResultSet rs = null;
		if(!visibleTeacherCbBx.getSelectionModel().isEmpty()) {
			if(!teacherClassesCbBx.getSelectionModel().isEmpty()) {
				try {
					new Alert(Alert.AlertType.INFORMATION,"Class Added To Teacher!").showAndWait();
					SQLConnecter.executeQuery("UPDATE Teachers SET ClassID = NULL WHERE Teacher_ID = "+Integer.parseInt(allTeachersCbBx.getSelectionModel().getSelectedItem().replaceAll("[^0-9]",""))+" AND ClassID = "+Integer.parseInt(teacherClassesCbBx.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""))+"");//removing the class that the old teacher used to teach
					SQLConnecter.executeQuery("UPDATE Teachers SET ClassID = "+Integer.parseInt(teacherClassesCbBx.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""))+" WHERE Teacher_ID = "+Integer.parseInt(visibleTeacherCbBx.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""))+"");
				}catch(SQLException e1) {
					e1.printStackTrace();
				}finally {
					try {
						if(con != null)
							con.close();
						if(rs != null)
							rs.close();
					}catch(SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
			else {
				new Alert(Alert.AlertType.ERROR,"Please Select A Class!").showAndWait();
			}
		}
		else {
			new Alert(Alert.AlertType.ERROR,"Please Select A Teacher!").showAndWait();
		}
	}
	
	void setInvisible() {//to limit user error we remove the option to perform a teacher replacement twice
		chooseTchrLbl.setVisible(false);
		assignTeacherBtn.setVisible(false);
		visibleTeacherCbBx.setVisible(false);
		teacherClassesCbBx.setVisible(false);
	}
	
	void setVisible() {
		chooseTchrLbl.setVisible(true);
		assignTeacherBtn.setVisible(true);
		visibleTeacherCbBx.setVisible(true);
		teacherClassesCbBx.setVisible(true);
		allClassesCbBx.setVisible(false);
		allTeachersCbBx.setVisible(false);//just so we don't confuse the user and let him focus 
	}
	
	void refreshClasses() {
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		try {
			allClassData  = new ArrayList<String>();//resetting our lists
			visibleData = new ArrayList<String>();
			
			rs = con.createStatement().executeQuery("SELECT ClassID,ClassName FROM All_Classes WHERE ClassID NOT IN ((SELECT ClassID FROM Grades) UNION (SELECT ClassID FROM Teachers WHERE ClassID IS NOT NULL))");
			 
			while(rs.next()) {
				allClassData.add("ClassID : "+rs.getInt("ClassID")+" - ClassName : "+rs.getString("ClassName")+"");
			}
			list = FXCollections.observableArrayList(allClassData);
			allClassesCbBx.setItems(list);
			rs.close();
			
			rs = con.createStatement().executeQuery("SELECT Teacher_ID FROM Teachers WHERE Teacher_ID NOT IN (SELECT Teacher_ID FROM Teachers WHERE ClassID IS NOT NULL)");
			 
			while(rs.next()) {
				visibleData.add("Valid TeacherID : "+rs.getInt("Teacher_ID")+"");
			}
			list = FXCollections.observableArrayList(visibleData);
			visibleTeacherCbBx.setItems(list);
			
			rs.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if(con != null)
					con.close();
				if(rs != null)
					rs.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	void removeClassClick(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainSystem/RemoveClassAdmin.fxml"));
    	Parent root = loader.load();
    	Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
	}
	
	boolean checkDisabled() {
		boolean flag = false;
		if(!allClassesCbBx.isVisible()) {
			flag = true;
			new Alert(Alert.AlertType.ERROR,"Please Exit To Update Another Teacher").showAndWait();
		}
		return flag;
	}
	
	@FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) teacherClassesCbBx.getScene().getWindow();
    	stage1.close();
	}
}
