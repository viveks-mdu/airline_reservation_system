// Vivek Anand Sampath
package airline;

import java.util.*;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FindSeatAvailability extends JPanel{
	public static Connection connection = null;
	public static void configure_mysql_connection(){
		String url = "jdbc:mysql://localhost:3306/airline";
		String username = "java";
		String password = "java";
		
		try {
		    System.out.println("Connecting database...");
		    connection = DriverManager.getConnection(url, username, password);
		    System.out.println("Database connected!");
		} catch (SQLException e) {
		    throw new RuntimeException("Cannot connect the database!", e);
		}
	}
	
	public static boolean test_mysql_connection(){
		
		try {
			Statement stmt = connection.createStatement();
		    ResultSet result = stmt.executeQuery("SELECT * FROM airplane_type");
		    
		    while (result.next()) {
		        String x = result.getString("company");
		        System.out.println(x);
		    }
		    
		    stmt.close();
		    
		    return true;
		} catch (SQLException e) {
			return false;
//			throw new RuntimeException("Error in executing query");
		} 
//		finally {
//			System.out.println("Closing the connection");
//			if(connection != null) try { connection.close(); } catch (SQLException ignore) {}
//		}
		
	}
	
	public static int find_seats(String flight_number, String cal_date){
		int count=0;
		int total_num_of_seats = 0, num_of_reserved = 0;
		
		try {
			Statement stmnt = connection.createStatement();
			String query = null;
			
			query = "select air.total_number_of_seats num "
					+" from flight_instance fi, airplane air "
					+" where fi.flight_number = "+flight_number
					+" and fi.date = '"+cal_date+"'"
					+" and fi.airplane_id = air.airplane_id";
			
			ResultSet result = stmnt.executeQuery(query);
			
			result.last();
		    int rows = result.getRow();
		    result.beforeFirst();
			
		    if(rows > 0){
		    	while(result.next()){
		    		total_num_of_seats = result.getInt("num");
		    	}
		    	
		    	query = "select count(*) as count"
		    			+" from seat_reservation "
		    			+" where flight_number = "+flight_number
		    			+" and date = '"+cal_date+"'";
		    	
		    	result = stmnt.executeQuery(query);
		    	
		    	while(result.next()){
		    		num_of_reserved = result.getInt("count");
		    	}
		    	
		    	return (total_num_of_seats - num_of_reserved);
		    } else {
		    	return -1;
		    }
			
		} catch(SQLException e){
			throw new RuntimeException("Error in the query");
		}
	}
	
	public static void find_seats_screen(){
		final JFrame jfrm = new JFrame();
		jfrm.setSize(400, 600);
		jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	    jfrm.setLayout(new FlowLayout());
//	    jfrm.setLayout(new GridLayout(2,2));;
		
		JLabel jlab_flight_number = new JLabel("Flight number:");
		jfrm.add(jlab_flight_number);
		final JTextField jtf_flight_number = new JTextField(20);
		jtf_flight_number.setPreferredSize(new Dimension(150,20));
		jfrm.add(jtf_flight_number);
		
		JLabel jlab_date = new JLabel("Date(YYYY/MM/DD format):");
		jfrm.add(jlab_date);
		final JTextField jtf_date = new JTextField(20);
		jfrm.add(jtf_date);
		
		final JLabel jlab_status = new JLabel();
		
		JButton jbtn_search = new JButton("Check seat availability");
		jbtn_search.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				System.out.println("event action triggered!");
				String flight_number = jtf_flight_number.getText();
				String cal_date = jtf_date.getText();
				
				int count_seats = find_seats(flight_number, cal_date);
				
				if(count_seats >= 0){
					jlab_status.setText("Number of seats available: "+count_seats);
				} else {
					jlab_status.setText("No results fetched");
				}
				jfrm.revalidate();
				jfrm.getContentPane().repaint();
			}
		});
		jfrm.add(jbtn_search);
		
		jfrm.add(jlab_status);
		
		jfrm.setVisible(true);
	}
	
	public static void main(String args[]){
		System.out.println("Program started ...");
		
		configure_mysql_connection();
		
		boolean mysql_status = test_mysql_connection();
		if(!mysql_status){
			return;
		}
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				find_seats_screen();
			}
		});
		
		System.out.println("Program completed.");
	}
}
