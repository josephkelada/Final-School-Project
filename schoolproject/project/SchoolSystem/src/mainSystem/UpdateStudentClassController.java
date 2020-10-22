package mainSystem;

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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
public class UpdateStudentClassController implements Initializable{
	
	StudentRequest studentRequest = new StudentRequest();
	
	ArrayList<String> additionCbBxList = new ArrayList<String>();
	ArrayList<String> removalCbBxList = new ArrayList<String>();
	ArrayList<String> courseChangeCbBxList = new ArrayList<String>();
	
	char requestChar = '0';
	char requestCharCourse = '0';
	boolean classTruth = false;
	
	@FXML
    private RadioButton radioBtnCourseAdd1;

    @FXML
    private ToggleGroup courseGroup;

    @FXML
    private RadioButton radioBtnCourseAdd2;

    @FXML
    private RadioButton radioBtnRemv1;

    @FXML
    private ToggleGroup classRemove;

    @FXML
    private RadioButton radioBtnRemv2;

    @FXML
    private RadioButton radioBtnAdd1;

    @FXML
    private ToggleGroup classAdd;

    @FXML
    private RadioButton radioBtnAdd2;
	
	@FXML
    private TableView<StudentRequest> table;

    @FXML
    private TableColumn<StudentRequest, Integer> col_StudentID;

    @FXML
    private TableColumn<StudentRequest, String> col_FName;

    @FXML
    private TableColumn<StudentRequest, String> col_LName;

    @FXML
    private TableColumn<StudentRequest, Integer> col_ClassID;

    @FXML
    private TableColumn<StudentRequest, String> col_ClassStatus;

    @FXML
    private TableColumn<StudentRequest, String> col_Request;

    @FXML
    private TableColumn<StudentRequest, String> col_Answer;
    
	@FXML
    private ComboBox<String> additionCbBX;

    @FXML
    private ComboBox<String> removalCbBx;

    @FXML
    private ComboBox<String> courseChangeCbBX;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		col_StudentID.setCellValueFactory(cellData -> cellData.getValue().getStudentIDProperty().asObject());
		col_FName.setCellValueFactory(cellData -> cellData.getValue().getFNameProperty());
		col_LName.setCellValueFactory(cellData -> cellData.getValue().getLNameProperty());
		col_ClassID.setCellValueFactory(cellData -> cellData.getValue().getClassIDProperty().asObject());
		col_ClassStatus.setCellValueFactory(cellData -> cellData.getValue().getClassStatusProperty());
		col_Request.setCellValueFactory(cellData -> cellData.getValue().getRequestProperty());
		col_Answer.setCellValueFactory(cellData -> cellData.getValue().getAnswerProperty());
		
		populateLists();
	}
    
    @FXML
    void selectRemoval(ActionEvent e) {
    	try {
    		table.setItems(studentRequest.populateTable('-',Integer.parseInt(removalCbBx.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""))));
    	}catch(NullPointerException e1) {
    		
    	}
    	
    }
    
    @FXML
    void selectAddition(ActionEvent e) {
    	try {
    		table.setItems(studentRequest.populateTable('+',Integer.parseInt(additionCbBX.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""))));
    	}catch(NullPointerException e1) {
    		
    	}
    }
    
    @FXML
    void selectCourseChange(ActionEvent e) {
    	try {
    		table.setItems(studentRequest.populateTableCourse(Integer.parseInt(courseChangeCbBX.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""))));
    	}catch(NullPointerException e1) {
    		
    	}
    }
    
    @FXML
    void applyClick(ActionEvent e) {
    	boolean selected = false;
    	int classID = 0;
    	int studentID = 0;
    	String request = "";
    	try {
    		if(radioBtnAdd1.isSelected()) {
    			if(additionCbBxList.isEmpty()) {
    				new Alert(Alert.AlertType.INFORMATION,"There Are No Addition Requests").showAndWait();
    			}
    			else {
    				if(!additionCbBX.getSelectionModel().isEmpty()) {
        				selected = true;
            			studentID = Integer.parseInt(additionCbBX.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            			request = additionCbBX.getSelectionModel().getSelectedItem().substring(34, additionCbBX.getSelectionModel().getSelectedItem().length());
            			classID = getClassID(studentID);
            			if(classTruth) {
            				SQLConnecter.executeQuery("INSERT INTO Grades (StudentID,ClassID,Grade) VALUES ("+studentID+","+classID+",-1)");
                			SQLConnecter.executeQuery("INSERT INTO Status (StudentID,ClassID,Status) VALUES ("+studentID+","+classID+",'Not-Started')");
                			SQLConnecter.executeQuery("INSERT INTO All_Requests (StudentID,Student_Request,Admin_Answer) VALUES("+studentID+",'"+request+"','Approved')");
                			SQLConnecter.executeQuery("DELETE FROM Student_Management_Classes WHERE StudentID = "+studentID+"");
                			new Alert(Alert.AlertType.INFORMATION,"Student Request Accepted!").showAndWait();
            			}
        			}
        			else {
        				new Alert(Alert.AlertType.ERROR,"You Must Select An Addition request").showAndWait();
        			}
    			}
    		}
    		else if(radioBtnAdd2.isSelected()) {
    			if(additionCbBxList.isEmpty()) {
    				new Alert(Alert.AlertType.INFORMATION,"There Are No Addition Requests").showAndWait();
    			}
    			else {//if there addition requests
    				if(!additionCbBX.getSelectionModel().isEmpty()) {
        				selected = true;
            			studentID = Integer.parseInt(additionCbBX.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            			request = additionCbBX.getSelectionModel().getSelectedItem().substring(34, additionCbBX.getSelectionModel().getSelectedItem().length());
            			SQLConnecter.executeQuery("INSERT INTO All_Requests (StudentID,Student_Request,Admin_Answer) VALUES("+studentID+",'"+request+"','Declined')");	
            			SQLConnecter.executeQuery("DELETE FROM Student_Management_Classes WHERE StudentID = "+studentID+"");
            			new Alert(Alert.AlertType.INFORMATION,"Student Request Declined!").showAndWait();
        			}
        			else {
        				new Alert(Alert.AlertType.ERROR,"You Must Select An Addition request").showAndWait();
        			}
    			}
    		}
    		if (radioBtnRemv1.isSelected()) {
    			if(removalCbBxList.isEmpty()) {
    				new Alert(Alert.AlertType.INFORMATION,"There Are No Removal Requests").showAndWait();
    			}
    			else {
    				if(!removalCbBx.getSelectionModel().isEmpty()) {
        				selected = true;
            			studentID = Integer.parseInt(removalCbBx.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            			request = removalCbBx.getSelectionModel().getSelectedItem().substring(34, removalCbBx.getSelectionModel().getSelectedItem().length());
            			SQLConnecter.executeQuery("DELETE FROM Status WHERE StudentID = "+studentID+" AND ClassID = "+getClassID(studentID)+"");
            			SQLConnecter.executeQuery("DELETE FROM Grades WHERE StudentID = "+studentID+" AND ClassID = "+getClassID(studentID)+"");
            			SQLConnecter.executeQuery("INSERT INTO All_Requests (StudentID,Student_Request,Admin_Answer) VALUES("+studentID+",'"+request+"','Approved')");
            			SQLConnecter.executeQuery("DELETE FROM Student_Management_Classes WHERE StudentID = "+studentID+"");
            			new Alert(Alert.AlertType.INFORMATION,"Student Request Accepted!").showAndWait();
        			}
        			else {
        				new Alert(Alert.AlertType.ERROR,"You Must Select A Removal request").showAndWait();
        			}
    			}
    		}
    		else if(radioBtnRemv2.isSelected()) {
    			if(removalCbBxList.isEmpty()) {
    				new Alert(Alert.AlertType.INFORMATION,"There Are No Removal Requests").showAndWait();
    			}
    			else {
    				if(!removalCbBx.getSelectionModel().isEmpty()) {//if user selected a reques
        				selected = true;
            			studentID = Integer.parseInt(removalCbBx.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            			request = removalCbBx.getSelectionModel().getSelectedItem().substring(34, removalCbBx.getSelectionModel().getSelectedItem().length());
            			SQLConnecter.executeQuery("INSERT INTO All_Requests (StudentID,Student_Request,Admin_Answer) VALUES("+studentID+",'"+request+"','Declined')");
            			SQLConnecter.executeQuery("DELETE FROM Student_Management_Classes WHERE StudentID = "+studentID+"");
            			new Alert(Alert.AlertType.INFORMATION,"Student Request Declined!").showAndWait();
        			}
        			else {
        				new Alert(Alert.AlertType.ERROR,"You Must Select A Removal request").showAndWait();
        			}
    			}
    		}
    		if(radioBtnCourseAdd1.isSelected()) {
    			if(courseChangeCbBxList.isEmpty()) {
    				new Alert(Alert.AlertType.INFORMATION,"There Are No Course Change Requests").showAndWait();
    			}
    			else {
    				if(!courseChangeCbBX.getSelectionModel().isEmpty()) {
        				selected = true;
            			studentID = Integer.parseInt(courseChangeCbBX.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            			request = courseChangeCbBX.getSelectionModel().getSelectedItem().substring(34, courseChangeCbBX.getSelectionModel().getSelectedItem().length());
            			SQLConnecter.executeQuery("UPDATE Students SET CurrentCourse = '"+request.replaceAll(".+-", "")+"' WHERE StudentID = "+studentID+"");
            			SQLConnecter.executeQuery("INSERT INTO All_Requests (StudentID,Student_Request,Admin_Answer) VALUES("+studentID+",'"+request+"','Approved')");
            			SQLConnecter.executeQuery("DELETE FROM Student_Management_Courses WHERE StudentID = "+studentID+"");
            			new Alert(Alert.AlertType.INFORMATION,"Student Request Accepted!").showAndWait();
        			}
        			else {
        				new Alert(Alert.AlertType.ERROR,"You Must Select A Course Change request").showAndWait();
        			}
    			}
    		}
    		else if(radioBtnCourseAdd2.isSelected() ) {
    			if(courseChangeCbBxList.isEmpty()) {
    				new Alert(Alert.AlertType.INFORMATION,"There Are No Course Change Requests").showAndWait();
    			}
    			else {
    				if(!courseChangeCbBX.getSelectionModel().isEmpty()) {
        				selected = true;
            			studentID = Integer.parseInt(courseChangeCbBX.getSelectionModel().getSelectedItem().replaceAll("[^0-9]", ""));
            			request = courseChangeCbBX.getSelectionModel().getSelectedItem().substring(34, courseChangeCbBX.getSelectionModel().getSelectedItem().length());
            			SQLConnecter.executeQuery("INSERT INTO All_Requests (StudentID,Student_Request,Admin_Answer) VALUES("+studentID+",'"+request+"','Declined')");
            			SQLConnecter.executeQuery("DELETE FROM Student_Management_Courses WHERE StudentID = "+studentID+"");
            			new Alert(Alert.AlertType.INFORMATION,"Student Request Declined!").showAndWait();
        			}
        			else {
        				new Alert(Alert.AlertType.ERROR,"You Must Select A Course Change request").showAndWait();
        			}
    			}
    		}
    		if(!selected) {
    			new Alert(Alert.AlertType.INFORMATION,"Select An Option Above Or Exit").showAndWait();
    		}
    		//i could put all radio btns in a group then clear selection 
    		resetRadioBtns();
    		resetCbBxAndPopulate();
    	}catch(SQLException e1) {
    		e1.printStackTrace();
    	}
    }
    
    void populateLists() {
    	Connection con = SQLConnecter.connect();
    	ResultSet rs = null;
    	
    	try {
    		rs = con.createStatement().executeQuery("SELECT * FROM Student_Management_Classes");
    		
    		while(rs.next()) {
    			if(rs.getString("Student_Request").charAt(0) == '+') {//meaning we are looking for class addition requests 
    				additionCbBxList.add("StudentID : "+rs.getInt("StudentID")+" - Student_Request : "+rs.getString("Student_Request")+"");	
    			}
    			else if(rs.getString("Student_Request").charAt(0) == '-') {
    				removalCbBxList.add("StudentID : "+rs.getInt("StudentID")+" - Student_Request : "+rs.getString("Student_Request")+"");
    			}
    		}
    		rs.close();
    		
    		rs = con.createStatement().executeQuery("SELECT * FROM Student_Management_Courses");
    		
    		while(rs.next()) {
    			courseChangeCbBxList.add("StudentID : "+rs.getInt("StudentID")+" - Student_Request : "+rs.getString("Student_Request")+"");
    		}
    		rs.close();
    		con.close();
    		populateCbBxs();
    		checkLists();
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}finally{
			try {
				if(rs != null)
					rs.close();
				if(con != null)
					con.close();
			}catch(SQLException e1) {
				e1.printStackTrace();
			}
		}
    }
    
    int getClassID(int studentID) {
    	Connection con = SQLConnecter.connect();
		ResultSet rs2 = null;
		String requestNoSpecChar = "";
		char requestWithChar = '0';
		String currentCourse = "";
		int classID = 0;
		int courseID = 0;
		
    	try {
    		rs2 = con.createStatement().executeQuery("SELECT Student_Request FROM Student_Management_Classes WHERE StudentID = "+studentID+"");
    		
    		if (rs2.next()) {
    			requestWithChar = rs2.getString("Student_Request").charAt(0);
    			requestNoSpecChar = (rs2.getString("Student_Request")).substring(1);//removing the the + or - sign from the request 
    		}
    		rs2.close();
    		
    		rs2 = con.createStatement().executeQuery("SELECT CurrentCourse FROM Students WHERE StudentID = "+studentID+"");
			
			if(rs2.next()) {
				currentCourse = rs2.getString("CurrentCourse");//getting a random class id for the simple purpose of finding the courseID
			}
			rs2.close();
			
			rs2 = con.createStatement().executeQuery("SELECT CourseID FROM All_Courses WHERE CourseName = '"+currentCourse+"'");
			
			if (rs2.next()) {
				courseID = rs2.getInt("CourseID");
			}
			rs2.close();
			
			rs2 = con.createStatement().executeQuery("SELECT ClassID FROM All_Classes WHERE CourseID = "+courseID+" AND ClassName = '"+requestNoSpecChar+"'");
			
			if(rs2.next()) {
				classID = rs2.getInt("ClassID");
			}
			rs2.close();
			
			checkValidity(requestWithChar,rs2, con, classID);
			
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
    	finally {
			try {
				if(con != null)
					con.close();
				if(rs2 != null)
					rs2.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
    	return classID;
    }
    
    void checkValidity(char requestWithChar, ResultSet rs2, Connection con, int classID) {
    	//check if there is a teacher teaching that class 
    	try {
    		if(requestWithChar == '+') {//only executes if we are adding a class
    			rs2 = con.createStatement().executeQuery("SELECT * FROM Teachers WHERE ClassID = "+classID+"");
    			
    			if(!rs2.isBeforeFirst()) {
    				new Alert(Alert.AlertType.ERROR,"There Are No Teachers Teaching This Class").showAndWait();
    			}
    			else {
    				classTruth = true;//if there is a teacher teaching the class
    			}
    		}
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    void checkLists() {
    	String msg = "";
		if(additionCbBxList.isEmpty()) {
			msg += "Addition Requests, ";
		}
		if(removalCbBxList.isEmpty()) {
			msg += "Removal Requests ,";
		}
		if(courseChangeCbBxList.isEmpty()) {
			msg += "Course Change Requests";
		}
		if(msg.charAt(msg.length() -1) == ',') {
			msg = msg.substring(0, msg.length() -1);
		}
		String format = String.format("%s%n%s", "There Are No ",msg);
		new Alert(Alert.AlertType.INFORMATION,format).showAndWait();
    }
    
    void resetRadioBtns() {
    	radioBtnAdd1.setSelected(false);
		radioBtnAdd2.setSelected(false);
		radioBtnRemv1.setSelected(false);
		radioBtnRemv2.setSelected(false);
		radioBtnCourseAdd1.setSelected(false);
		radioBtnCourseAdd2.setSelected(false);
    }
    
    void resetCbBxAndPopulate() {    	
    	additionCbBxList = new ArrayList<String>();
    	removalCbBxList = new ArrayList<String>();
    	courseChangeCbBxList = new ArrayList<String>();
    	
    	additionCbBX.getItems().clear();
    	removalCbBx.getItems().clear();
    	courseChangeCbBX.getItems().clear();
 
    	populateLists();
    }
    
    void populateCbBxs() {
    	ObservableList<String> list = FXCollections.observableArrayList(additionCbBxList);
		additionCbBX.setItems(list);
		
		list = FXCollections.observableArrayList(removalCbBxList);
		removalCbBx.setItems(list);
		
		list = FXCollections.observableArrayList(courseChangeCbBxList);
		courseChangeCbBX.setItems(list);
    }
    
    @FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) table.getScene().getWindow();
    	stage1.close();
	}
}
