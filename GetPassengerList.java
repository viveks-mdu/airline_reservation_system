// Vivek Anand Sampath
package airline;

import java.util.*;
import java.sql.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class GetPassengerList extends JPanel{
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
	
	public static ArrayList<PassengerList> get_passengers(String flight_number, String cal_date){
		int count=0;
		ArrayList<PassengerList> data = new ArrayList<PassengerList>();
		
		try {
			Statement stmnt = connection.createStatement();
			String query = "select seat_number, customer_name, customer_phone from seat_reservation where flight_number = "+flight_number+" and date='"+cal_date+"'";
			
			ResultSet result = stmnt.executeQuery(query);
			
			String seat_number, customer_name;
			int customer_phone;
			while(result.next()){
				seat_number = result.getString("seat_number");
				customer_name = result.getString("customer_name");
				customer_phone = result.getInt("customer_phone");
				
				PassengerList passenger_list = new PassengerList(seat_number, customer_name, customer_phone);
				data.add(passenger_list);
			}
			
		} catch(SQLException e){
			throw new RuntimeException("Error in the query");
		}
		
		return data;
	}
	
	public static void get_passengers_screen(){
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
		final JScrollPane jsp = new JScrollPane();
		
		JButton jbtn_search = new JButton("Get Passengers");
		jbtn_search.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JTable jtab_tickets = new JTable();
				System.out.println("event action triggered!");
				String flight_number = jtf_flight_number.getText();
				String cal_date = jtf_date.getText();
				
				ArrayList<PassengerList> arylist = get_passengers(flight_number, cal_date);
				
				int count = 0;

				if(arylist.size() > 0){					
					Object[][] data = new Object[arylist.size()][3];
					Iterator<PassengerList> it = arylist.iterator();
					while(it.hasNext()) {
						PassengerList obj = it.next();
					    data[count][0] = obj.seat_number;
					    data[count][1] = obj.customer_name;
					    data[count][2] = obj.customer_phone;
					    
					    count++;
					}
					String[] colHeads = {"Seat Number", "Name", "Phone number"};
					
					jtab_tickets = new JTable(data, colHeads);
					jsp.getViewport ().add (jtab_tickets);
					jfrm.setMinimumSize(new Dimension(600, 23));
//			    	jtab_tickets.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					
					jfrm.add(jsp);
					jlab_status.setText("");
					jfrm.revalidate();
					jfrm.getContentPane().repaint();
				} else {
					jlab_status.setText("No results fetched");
					jfrm.remove(jsp);
					jfrm.revalidate();
					jfrm.getContentPane().repaint();
				}
				
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
				get_passengers_screen();
			}
		});
		
		System.out.println("Program completed.");
	}
}

class PassengerList{
	public String seat_number;
	public String customer_name;
	public int customer_phone;
	
	PassengerList(String st_num, String name, int phone){
		this.seat_number = st_num;
		this.customer_name = name;
		this.customer_phone = phone;
	}
}
