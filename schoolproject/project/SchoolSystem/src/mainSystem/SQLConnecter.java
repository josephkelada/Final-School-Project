package mainSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnecter 
{
	static Statement stmt = null;
	
	static Connection connect()
	{
		Connection con = null;
		String url = "jdbc:sqlserver://LAPTOP-D2HSVIJ9\\JOSEPHSQL;databaseName=SchoolSystem";
		String username = "sa";
		String password = "igetnukes123";
		try 
		{
			con = DriverManager.getConnection(url,username,password);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return con;
	}
	
	public static void executeQuery(String stmnt) throws SQLException
	{
		Connection con = connect();
		try 
		{
			stmt = con.createStatement();
			stmt.executeUpdate(stmnt);
			stmt.close();
			con.close();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}finally
		{
			if(con != null)
				con.close();
			if(stmt != null)
				stmt.close();
		}
	}
}
