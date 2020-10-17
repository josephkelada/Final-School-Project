module SchoolSystem 
{		
		requires transitive javafx.graphics;
		requires transitive javafx.fxml;
		requires transitive javafx.controls;
		requires javafx.base;
		requires transitive java.sql;
		requires jdk.compiler;
		requires com.microsoft.sqlserver.jdbc;//for stringutils
		opens mainSystem to javafx.fxml;
		exports mainSystem;
}