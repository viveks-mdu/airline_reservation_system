// Vivek Anand Sampath
package airline;

import java.util.*;
import java.sql.*;
import java.sql.Date;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class SearchByName extends JPanel{
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
	
	public static ArrayList<FlightInstances> get_flight_instances(String name){
		int count=0;
		ArrayList<FlightInstances> data = new ArrayList<FlightInstances>();
		
		try {
			Statement stmnt = connection.createStatement();
			name = name.toLowerCase();
			String query = "select flight_number, date, seat_number, customer_name, customer_phone from seat_reservation where lower(customer_name) like '%"+name+"%'";
			
			ResultSet result = stmnt.executeQuery(query);
			
			int flight_number, customer_phone;
			String seat_number, customer_name;
			Date cal_date;
			while(result.next()){
				flight_number = result.getInt("flight_number");
				cal_date = result.getDate("date");
				seat_number = result.getString("seat_number");
				customer_name = result.getString("customer_name");
				customer_phone = result.getInt("customer_phone");
				
				FlightInstances passenger_list = new FlightInstances(flight_number, cal_date, seat_number, customer_name, customer_phone);
				data.add(passenger_list);
			}
			
		} catch(SQLException e){
			throw new RuntimeException("Error in the query");
		}
		
		return data;
	}
	
	public static void search_by_name_screen(){
		final JFrame jfrm = new JFrame();
		jfrm.setSize(400, 600);
		jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	    jfrm.setLayout(new FlowLayout());
//	    jfrm.setLayout(new GridLayout(2,2));;
		
		JLabel jlab_name = new JLabel("Passenger name:");
		jfrm.add(jlab_name);
		final JTextField jtf_name = new JTextField(20);
		jtf_name.setPreferredSize(new Dimension(150,20));
		jfrm.add(jtf_name);
		
		final JLabel jlab_status = new JLabel();
		final JScrollPane jsp = new JScrollPane();
		
		JButton jbtn_search = new JButton("Search by name");
		jbtn_search.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JTable jtab_tickets = new JTable();
				System.out.println("event action triggered!");
				String name = jtf_name.getText();
				
				ArrayList<FlightInstances> arylist = get_flight_instances(name);
				
				int count = 0;

				if(arylist.size() > 0){					
					Object[][] data = new Object[arylist.size()][5];
					Iterator<FlightInstances> it = arylist.iterator();
					while(it.hasNext()) {
						FlightInstances obj = it.next();
						data[count][0] = obj.flight_number;
						data[count][1] = obj.cal_date;
					    data[count][2] = obj.seat_number;
					    data[count][3] = obj.customer_name;
					    data[count][4] = obj.customer_phone;
					    
					    count++;
					}
					String[] colHeads = {"Flight Number", "Date", "Seat Number", "Name", "Phone number"};
					
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
				search_by_name_screen();
			}
		});
		
		System.out.println("Program completed.");
	}
}

class FlightInstances{
	public int flight_number;
	public Date cal_date;
	public String seat_number;
	public String customer_name;
	public int customer_phone;
	
	FlightInstances(int flgt_num, Date cl_date, String seat_num, String name, int phone){
		this.flight_number = flgt_num;
		this.cal_date = cl_date;
		this.seat_number = seat_num;
		this.customer_name = name;
		this.customer_phone = phone;
	}
}
