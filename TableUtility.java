package com.utility;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 *
 * @author Hemavathi.R
 * This Class is for TableUtility to use table information from SQL Server
 */
public interface TableUtility {
	// Add some logger with variable log here


	/**
	 * Get ColumnNames if table name is passed with connection
	 * @param tableName
	 * @param conn
	 * @return
	 */
	static List<String> getColumnNames(String tableName,Connection conn) {
		List<String> columnNames = new ArrayList<>();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '"
					+ tableName + "'";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				//Adding this logic as a quick fix for country__v.name__v
				String colName = rs.getString("COLUMN_NAME");
				if (colName.contains(".")) {
					colName = colName.substring(0, colName.indexOf('.'));
				}
				columnNames.add(colName);
			}
		} catch ( SQLException e) {
			log.info("getColumns() Error: TableUtility:" + e.getMessage());
		}finally {
			ConnectionUtils.closeStatement(stmt);
		}

		return columnNames;

	}
	/**
	 * Check whether a column exist in the table
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	static String ifColumnExist(String tableName,String columnName)
	{
		String queryResult = "FALSE";
		String query="IF EXISTS \r\n"
				+ "(\r\n"
				+ "  SELECT * \r\n"
				+ "  FROM INFORMATION_SCHEMA.COLUMNS \r\n"
				+ "  WHERE table_name = '"+tableName+"'\r\n"
				+ "  AND column_name = '"+columnName+"'\r\n"
				+ ")\r\n"
				+ "SELECT 'TRUE' AS [Status] ;\r\n"
				+ "ELSE\r\n"
				+ "SELECT 'FALSE' AS [Status];";
		Connection con=null;
		Statement stmt=null;
		try {
			con=ConnectionUtils.getDBConnection();
			stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery(query);
			while(rs.next()) {
				queryResult= rs.getString("Status");
			}
			rs.close();
			stmt.close();
		} catch ( SQLException e) {
			log.error("Exception in ifColumnExist ::" +e.getMessage());
		}
		finally
		{
			ConnectionUtils.closeConnection(stmt,con);
		}
		return queryResult;
	}

	/**
	 * Get List of columns exist in the selected table
	 * @param tableName
	 * @return
	 */
	static List<String> getColumnNames(String tableName) {

		List<String> columnNames = new ArrayList<>();
		Connection conn=null;
		try {
			conn=ConnectionUtils.getDBConnection();
			columnNames = getColumnNames(tableName,conn);
		}
		finally {
			ConnectionUtils.closeConnection(conn);
		}
		return columnNames;

	}
	/**
	 * 
	 * @author Hemavathi.R
	 * @param tableName
	 * @return
	 */
	static String dropTable(String tableName) {

		String dropQuery = "drop table " + tableName;
		log.info(dropQuery);
		String result = "";
		Connection conn=null;
		Statement stmt = null;
		try {
			conn=ConnectionUtils.getDBConnection();
			 stmt = conn.createStatement();
			stmt.execute(dropQuery);
			log.info("Table " +tableName +" dropped Successfully");
			result = "success";
			stmt.close();

		} catch ( SQLException e) {
			log.error(e.getMessage());
			result = "failure";
			e.printStackTrace();
		}finally {
			ConnectionUtils.closeConnection(conn);
		}

		return result;
	}
	/**
	 * 
	 * @param tableName
	 * @return
	 */
	static boolean tableDoesNotExist(String tableName) {
		Connection conn = null;
		Statement stmt = null ;
		String batchQuery = "select TOP(1) * from " + tableName;
		ResultSet rs;
		boolean tableNotExist=false;
		try {
			conn = ConnectionUtils.getDBConnection();
			stmt=conn.createStatement();
			rs = stmt.executeQuery(batchQuery);
			if(rs==null || !rs.next()) {
				tableNotExist= true;
			}
		} catch (SQLException e) {
			tableNotExist=true;
		} finally {
			ConnectionUtils.closeConnection(stmt,conn);
		}
		return tableNotExist;
	}
}
