package eu.cloudscale.showcase.test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Connection;


public class JDBCtest
{
	public static void main(String args[])
	{
		try
        {
	        Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/tpcw?user=root&password=toor");
			Statement statment = conn.createStatement();
			long start = System.currentTimeMillis();
			ResultSet resultSet = statment.executeQuery( "SELECT i_id, i_title, a_fname, a_lname , SUM(ol_qty) AS val FROM orders, order_line, item, author WHERE order_line.ol_o_id = orders.o_id AND item.i_id = order_line.ol_i_id AND item.i_subject = 'ARTS' AND item.i_a_id = author.a_id GROUP BY i_id ORDER BY orders.o_date, val DESC LIMIT 50" );
			System.out.println(start-(System.currentTimeMillis())/1E9);
        }
        catch ( ClassNotFoundException e )
        {
	        e.printStackTrace();
        }
        catch ( SQLException e )
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}
}
