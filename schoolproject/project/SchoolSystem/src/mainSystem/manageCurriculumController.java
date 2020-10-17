package mainSystem;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class manageCurriculumController implements Initializable{
	
	boolean flag = false;
	Connection con;
	
	ArrayList<String> myClassNames = new ArrayList<String>();
	ArrayList<String> mySubClassNames = new ArrayList<String>();
	
	@FXML
    private ComboBox<String> addClassIdCbBox;
	
	@FXML
	private ComboBox<String> addClassIdCbBox1;
	
	@FXML
	private ComboBox<String> SubClassNamesCbBx;
	
    @FXML
    private TextField addSubClassTxtBx;
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		getMyClasses();
		
		ObservableList<String> list = FXCollections.observableArrayList(myClassNames);
		addClassIdCbBox.setItems(list);
		addClassIdCbBox1.setItems(list);
		
		if(addClassIdCbBox.getItems().size() <= 0) {
			String format = String.format("%s%n%s", "You Currently have No Classes But You Can View The Form","Please Contact Administration");
			new Alert(Alert.AlertType.ERROR,format).showAndWait();
		}
	}

    @FXML
    void addClassClick(ActionEvent event) {
    	
    	con = SQLConnecter.connect();
    	Statement d = null;
    	ResultSet rs = null;
    	try {
    		
    		d = con.createStatement();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
    	
    	try {
    		if(addClassIdCbBox.getItems().size() <= 0) {
    			String format = String.format("%s%n%s", "You Currently have No Classes","Please Contact Administration");
    			new Alert(Alert.AlertType.ERROR,format).showAndWait();
    		}
    		else {
    			if(!addClassIdCbBox.getSelectionModel().isEmpty()) {
        			if(!addSubClassTxtBx.getText().isEmpty() && !(addSubClassTxtBx.getText().matches(".*\\d.*"))) {//check if there is any digit from 0 to infinite and occurence
        				rs = d.executeQuery("SELECT * FROM Grades WHERE ClassID = '"+Integer.parseInt(addClassIdCbBox.getSelectionModel().getSelectedItem().substring(0, 3).replaceAll("[^0-9]", ""))+"';");
        				
        				if(!rs.isBeforeFirst()) {//check if there are any students in the teacher's course
        					SQLConnecter.executeQuery("INSERT INTO SubClasses (ClassID,SubClassName) VALUES ('"+Integer.parseInt(addClassIdCbBox.getSelectionModel().getSelectedItem().substring(0, 3).replaceAll("[^0-9]", ""))+"','"+addSubClassTxtBx.getText()+"');");
        					new Alert(Alert.AlertType.INFORMATION,"SubClass Added!").showAndWait();
        				}
        				else if (rs.next()){
        					String format = String.format("%s%n%s", "Students Have Not Finished The Class Yet!","You Cannot Add A SubClass For this Class");
        					new Alert(Alert.AlertType.ERROR,format).showAndWait();
        				}
        				d.close();
        				rs.close();
        				con.close();
        			}
        			else {
        				new Alert(Alert.AlertType.ERROR,"Subclass Can Only Contain Letters").showAndWait();
        			}
        		}
        		else {
        			new Alert(Alert.AlertType.ERROR,"You Must Choose A Class").showAndWait();
        		}
    		}			
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    	}
    }

	void getMyClasses(){//populate our arraylist which contains all classes from teacher
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		ResultSet rs1 = null;
		int count = 0;
		
		try {
			rs = con.createStatement().executeQuery("SELECT ClassID FROM Teachers WHERE Teacher_ID = '"+Main.currentUser.getId()+"' AND ClassID IS NOT NULL");
			//running a different query everytime since the we need the class name depending on the classID 
			while(rs.next()) {
				count++;
				rs1 = con.createStatement().executeQuery("SELECT ClassName FROM All_Classes WHERE ClassID = '"+rs.getInt("ClassID")+"'");
				if(rs1.next()) {
					myClassNames.add(""+rs.getInt("ClassID")+" - "+rs1.getString("ClassName")+"");//making it look like so "37 - History"
				}
				rs1.close();
			}
			if(count == 0) {
				new Alert(Alert.AlertType.ERROR,"You Currently have No Classes");
			}
			rs.close();
			con.close();
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	@FXML
	void removeSubClassClick(ActionEvent e) {
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		try {
			if(addClassIdCbBox.getItems().size() <= 0) {
    			String format = String.format("%s%n%s", "You Currently have No Classes","Please Contact Administration");
    			new Alert(Alert.AlertType.ERROR,format).showAndWait();
    		}
    		else {
    			if(!addClassIdCbBox1.getSelectionModel().isEmpty()) {
    				if(!SubClassNamesCbBx.getSelectionModel().isEmpty()) {
    					
    					rs = con.createStatement().executeQuery("SELECT * FROM Grades WHERE ClassID = '"+Integer.parseInt(addClassIdCbBox1.getSelectionModel().getSelectedItem().substring(0, 3).replaceAll("[^0-9]", ""))+"';");
            			
            			if(rs.next()) {
            				String format = String.format("%s%n%s", "Students Have Not Finished The Class Yet!","You Cannot Add A SubClass For this Class");
            				new Alert(Alert.AlertType.ERROR,format).showAndWait();
            			}
            			else {
            				rs = con.createStatement().executeQuery("SELECT SubClassID FROM SubClasses WHERE ClassID = '"+Integer.parseInt(addClassIdCbBox1.getSelectionModel().getSelectedItem().substring(0, 3).replaceAll("[^0-9]", ""))+"' AND SubClassName LIKE '%"+SubClassNamesCbBx.getSelectionModel().getSelectedItem()+"%'");
            				
            				if(rs.next()) {
            					SQLConnecter.executeQuery("DELETE FROM SubClasses WHERE SubClassID = '"+rs.getInt("SubClassID")+"'");
            					new Alert(Alert.AlertType.INFORMATION,"SubClass Deleted!").showAndWait();
            					selectSubNamesRem();
            				}
            				
            				rs.close();
            				con.close();
            			}
            		}
    				else {
    					new Alert(Alert.AlertType.ERROR,"You Must Select A SubClass To Remove").showAndWait();
    				}
    			}
    			else {
    				new Alert(Alert.AlertType.ERROR,"You Must Select A Class").showAndWait();
    			}
    		}
       	}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	@FXML
	void selectSubNames(ActionEvent e) {//fxml func 
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		mySubClassNames = new ArrayList<String>();
		
		try {
			
			rs = con.createStatement().executeQuery("SELECT SubClassName FROM SubClasses WHERE ClassID = '"+Integer.parseInt(addClassIdCbBox1.getSelectionModel().getSelectedItem().substring(0, 3).replaceAll("[^0-9]", ""))+"'");//getting the first 2 chars of the selected item whic his the classid
			
			while(rs.next()) {
				mySubClassNames.add(rs.getString("SubClassName"));
			}

			rs.close();
			con.close();
			
			ObservableList<String> list = FXCollections.observableArrayList(mySubClassNames);
			SubClassNamesCbBx.setItems(list);
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	void selectSubNamesRem() {//regular function 
		Connection con = SQLConnecter.connect();
		ResultSet rs = null;
		mySubClassNames = new ArrayList<String>();
		SubClassNamesCbBx.getItems().clear();
		
		try {
			
			rs = con.createStatement().executeQuery("SELECT SubClassName FROM SubClasses WHERE ClassID = '"+Integer.parseInt(addClassIdCbBox1.getSelectionModel().getSelectedItem().substring(0, 3).replaceAll("[^0-9]", ""))+"'");//getting the first 2 chars of the selected item whic his the classid
			
			while(rs.next()) {
				mySubClassNames.add(rs.getString("SubClassName"));
			}

			rs.close();
			con.close();
			
			ObservableList<String> list = FXCollections.observableArrayList(mySubClassNames);
			SubClassNamesCbBx.setItems(list);
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	@FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) addClassIdCbBox1.getScene().getWindow();
    	stage1.close();
	}
}
