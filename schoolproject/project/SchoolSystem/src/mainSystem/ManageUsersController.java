package mainSystem;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

//i know that i could of implemented a function to optimize validaton so we don't have so many if clauses
public class ManageUsersController implements Initializable
{
	Person person = new Person();
	
	@FXML
    private ComboBox<String> typeCbBox;
	
	@FXML
    private ComboBox<String> typeCbBox2;
	
	@FXML
    private TextField firstNametxt;
	
	@FXML
    private TextField userIdDelete;
	
	@FXML
	private TextField userIdEmail;
	
	@FXML
	private TextField userIdPhone;
	
	@FXML
	private TextField newPhoneNb;
	
	@FXML
	private TextField newEmail;

    @FXML
    private TextField phoneNbTxt;

    @FXML
    private TextField lastNametxt;

    @FXML
    private TextField emailTxt;
	
	@FXML
    private TableView<Person> table;
	
	@FXML
	private TableColumn<Person,Integer > col_ID;

    @FXML
    private TableColumn<Person, String> col_Fname;

    @FXML
    private TableColumn<Person, String> col_Lname;

    @FXML
    private TableColumn<Person, String> col_Email;

    @FXML
    private TableColumn<Person, String> col_PhoneNb;
    
    @FXML
    private TableColumn<Person, String> col_Type;
    
    ObservableList<Person> personList = null;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		col_ID.setCellValueFactory(cellData -> cellData.getValue().getIdProperty().asObject());//we are basically saying cellData = to cellData.getValue(),it saves us the trouble of creating a method
		col_Email.setCellValueFactory(cellData -> cellData.getValue().getEmailProperty());
		col_Fname.setCellValueFactory(cellData -> cellData.getValue().getFNameProperty());
		col_Lname.setCellValueFactory(cellData -> cellData.getValue().getLNameProperty());
		col_PhoneNb.setCellValueFactory(cellData -> cellData.getValue().getPhoneNbProperty());
		col_Type.setCellValueFactory(cellData -> cellData.getValue().getPersonTypeProperty());
		
		ObservableList<String> list = FXCollections.observableArrayList("Parent","Student","Teacher");
		typeCbBox.setItems(list);
		typeCbBox2.setItems(list);
	}
	
	@FXML
    void select(ActionEvent event) 
	{
		String s = typeCbBox.getSelectionModel().getSelectedItem().toString();
    	if(s == "Student")
    	{
    		try 
    		{
    			personList = person.getAllRecords(s);
    		} catch (SQLException e) 
    		{
    			e.printStackTrace();
    		}
    		selectAllRecords(personList);
    	}
    	
    	else if (s == "Parent")
    	{
    		try 
    		{
    			personList = person.getAllRecords(s);
    		} catch (SQLException e) 
    		{
    			e.printStackTrace();
    		}
    		selectAllRecords(personList);
    	}
    	
    	else if (s == "Teacher")
    	{
    		try 
    		{
    			personList = person.getAllRecords(s);
    		} catch (SQLException e) 
    		{
    			e.printStackTrace();
    		}
    		selectAllRecords(personList);
    	}
    }
	
	@FXML
    void addUserClick(ActionEvent event) throws SQLException 
	{
		select();
		if(!(firstNametxt.getText().isEmpty()) && !(lastNametxt.getText().isEmpty()) && !(emailTxt.getText().isEmpty()) && !(phoneNbTxt.getText().isEmpty()) && !(typeCbBox2.getSelectionModel().isEmpty())){
			if(firstNametxt.getText().matches("[a-zA-Z]+") && lastNametxt.getText().matches("[a-zA-Z]+")) {//contains only one letter or more and nothing else
				if(phoneNbTxt.getText().matches("^\\d{10}$")) {//make sure their are only 10 digits
					if((emailTxt.getText().contains("@"))) {
						person.insert(firstNametxt.getText(), lastNametxt.getText(), emailTxt.getText(), phoneNbTxt.getText(),typeCbBox2.getSelectionModel().getSelectedItem().toString());
					}
					else {
						new Alert(Alert.AlertType.ERROR,"Email is not valid").showAndWait();
					}
				}
				else {
					new Alert(Alert.AlertType.ERROR,"Phone Number Must Be Only 10 Digits").showAndWait();
				}
			}
			else {
				new Alert(Alert.AlertType.ERROR,"FirstName and LastName Must Only Contain Letters").showAndWait();
			}
		}
		else {
			new Alert(Alert.AlertType.ERROR,"Make Sure To Enter All Fields").showAndWait();
		}
		select();
    }
	
	void selectAllRecords(ObservableList<Person> personList)
	{
		table.setItems(personList);
	}
	
	@FXML
    void deleteUserClick(ActionEvent event) throws NumberFormatException, SQLException 
	{
		ResultSet rs = null;
		Connection con = SQLConnecter.connect();
		if(!userIdDelete.getText().isEmpty()) {
			if(userIdDelete.getText().matches("\\d+")) {
				
				rs = con.createStatement().executeQuery("SELECT * FROM Users WHERE ID = "+Integer.parseInt(userIdDelete.getText())+"");
				
				if(!rs.isBeforeFirst()) {//if there are no records
					new Alert(Alert.AlertType.ERROR,"User doesn't Exist ").showAndWait();
				}
				else{
					person.delete(Integer.parseInt(userIdDelete.getText()));
					new Alert(Alert.AlertType.ERROR,"User Deleted!").showAndWait();
				}
				rs.close();
			}
			else {
				new Alert(Alert.AlertType.ERROR,"User ID Must Only Be Numeric").showAndWait();
			}
		}
		else {
			new Alert(Alert.AlertType.ERROR,"You Must Enter a valid ID").showAndWait();
		}
		select();
    }

    @FXML
    void updateEmailClick(ActionEvent event)
    {
    	Connection con = SQLConnecter.connect();
    	ResultSet rs1 = null;
    	
    	try 
    	{
    		if(!(userIdEmail.getText().isEmpty()) && !(newEmail.getText().isEmpty()) && !(newEmail.getText().isEmpty())) {
    			if((newEmail.getText().contains("@"))) {
    				if((userIdEmail.getText().matches("\\d+"))) {//doesn't have anything other then numbers
    					rs1 = con.createStatement().executeQuery("SELECT * FROM Users WHERE ID = "+Integer.parseInt(userIdEmail.getText())+"");
    					if(!rs1.isBeforeFirst()) {
    						new Alert(Alert.AlertType.ERROR,"User doesn't Exist ").showAndWait();
    					}
    					else {
    						person.updateEmail(Integer.parseInt(userIdEmail.getText()), newEmail.getText());
    						new Alert(Alert.AlertType.INFORMATION,"User Updated!").showAndWait();
    					}
    					rs1.close();
    				}
    				else {
    					new Alert(Alert.AlertType.ERROR,"ID should only contain numbers").showAndWait();
    				}
    			}
    			else 
    			{
    				new Alert(Alert.AlertType.ERROR,"Email is not valid").showAndWait();
    			}
    		}
    		else {
    			new Alert(Alert.AlertType.ERROR,"You Must Enter An Email And ID").showAndWait();
    		}
		} catch (NumberFormatException | SQLException e) 
    	{
			e.printStackTrace();
		}
    	select();
    }

    @FXML
    void updatePhoneClick(ActionEvent event) throws NumberFormatException, SQLException 
    {    	
    	ResultSet rs = null;
    	Connection con = SQLConnecter.connect();
    	
    	if(!(newPhoneNb.getText().isEmpty()) && !(userIdPhone.getText().isEmpty()) && !(newPhoneNb.getText().isEmpty())) {
    		if(userIdPhone.getText().matches("\\d+") && newPhoneNb.getText().matches("^\\d{10}$")) {//check if txtBoxes only contain ints
    			rs = con.createStatement().executeQuery("SELECT * FROM Users WHERE ID = "+Integer.parseInt(userIdPhone.getText())+"");
				if(!rs.isBeforeFirst()) {//if there are no records
					new Alert(Alert.AlertType.ERROR,"User doesn't Exist ").showAndWait();
				}
				else{
					person.updatePhoneNb(Integer.parseInt(userIdPhone.getText()), newPhoneNb.getText());
					new Alert(Alert.AlertType.INFORMATION,"User Successfully Updated").showAndWait();
				}
				rs.close();
    		}
    		else {
    			new Alert(Alert.AlertType.ERROR,"ID Should be numeric And Phone Nb should have 10 numbers").showAndWait();
    		}
    	}
    	else {
    		new Alert(Alert.AlertType.ERROR,"You Must Enter A Valid Phone Number And ID").showAndWait();
    	}
    	select();
    }
    
    @FXML
	void exitClick(ActionEvent e) {
		Stage stage1 = (Stage) table.getScene().getWindow();
    	stage1.close();
	}
    
    void select() {
    	
    	String s = typeCbBox.getSelectionModel().getSelectedItem().toString();
    	if(s == "Student")
    	{
    		try 
    		{
    			personList = person.getAllRecords(s);
    		} catch (SQLException e) 
    		{
    			e.printStackTrace();
    		}
    		selectAllRecords(personList);
    	}
    	
    	else if (s == "Parent")
    	{
    		try 
    		{
    			personList = person.getAllRecords(s);
    		} catch (SQLException e) 
    		{
    			e.printStackTrace();
    		}
    		selectAllRecords(personList);
    	}
    	
    	else if (s == "Teacher")
    	{
    		try 
    		{
    			personList = person.getAllRecords(s);
    		} catch (SQLException e) 
    		{
    			e.printStackTrace();
    		}
    		selectAllRecords(personList);
    	}
    }
}
