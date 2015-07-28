// Vivek Anand Sampath
package airline;

import java.util.*;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FindFlights extends JPanel{
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
	
	public static HashMap<Integer,Set> find_tickets(String dep_airport, String arr_airport, int num_of_flights){
		HashMap<Integer,Set> data = new HashMap<Integer,Set>();
		
		try {
			Statement stmnt = connection.createStatement();
			
			String query = null;
			int flight_number;
			for(String day: new HashSet<String>(Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"))){
				// 1 hop
				int i = num_of_flights;
//				for(int i=1;i<=num_of_flights;i++){
					
					if(i == 1){
						query = "select flight_number from flight where departure_airport_code = '"+dep_airport+"' and arrival_airport_code = '"+arr_airport
								+"' and lower(weekdays) like '%"+day+"%'";
					}
					
					if(i == 2){
						query = "select f1.flight_number "
								+"from " 
								+" (select flight_number, departure_airport_code, flight_number via_flights, departure_airport_code connection_stops, arrival_airport_code, scheduled_departure_time, scheduled_arrival_time "
								+" from flight"
								+" where departure_airport_code = '"+dep_airport+"' and lower(weekdays) like '%"+day+"%')" 
								+" f1,"
								+" flight f2"
								+" where f1.arrival_airport_code = f2.departure_airport_code"
								+" and connection_stops not like concat('%',f2.arrival_airport_code,'%')"
								+" and addtime(f1.scheduled_arrival_time, '1:00:00') < f2.scheduled_departure_time"
								+" and f2.arrival_airport_code = '"+arr_airport+"'"
								+" and lower(weekdays) like '%"+day+"%'";
					}
					
					if(i == 3){
						query = "select f3.flight_number"
								+" from"
								+" (select f1.departure_airport_code, f1.flight_number, concat(via_flights,'->',f2.flight_number) via_flights, concat(connection_stops,'->',f2.departure_airport_code) connection_stops, f2.arrival_airport_code, f1.scheduled_departure_time, f2.scheduled_arrival_time"
								+" from " 
								+" (select flight_number, departure_airport_code, flight_number via_flights, departure_airport_code connection_stops, arrival_airport_code, scheduled_departure_time, scheduled_arrival_time "
								+" from flight "
								+" where departure_airport_code = '"+dep_airport+"' and lower(weekdays) like '%"+day+"%') " 
								+" f1, "
								+" flight f2 "
								+" where f1.arrival_airport_code = f2.departure_airport_code "
								+" and connection_stops not like concat('%',f2.arrival_airport_code,'%') "
								+" and addtime(f1.scheduled_arrival_time, '1:00:00') < f2.scheduled_departure_time "
								+" and lower(weekdays) like '%"+day+"%') "
								+" f3, "
								+" flight f4 "
								+" where f4.departure_airport_code = f3.arrival_airport_code "
								+" and connection_stops not like concat('%',f4.arrival_airport_code,'%') "
								+" and addtime(f3.scheduled_arrival_time, '1:00:00') < f4.scheduled_departure_time "
								+" and lower(weekdays) like '%"+day+"%'"
								+" and f4.arrival_airport_code = '"+arr_airport+"'";
					}
					
					ResultSet result = stmnt.executeQuery(query);
					
					while(result.next()){
						flight_number = result.getInt("flight_number");
						
						if(data.containsKey(flight_number)){
							Set<String> s = data.get(flight_number);
							s.add(day);
							data.put(flight_number, s);
						} else {
							Set<String> s = new HashSet<String>();
							s.add(day);
							data.put(flight_number, s);
						}
					}
				}
//			}
		} catch(SQLException e){
			throw new RuntimeException("Error in the query");
		}
		
		return data;
	}
	
	public static void find_tickets_screen(){
		final JFrame jfrm = new JFrame();
		jfrm.setSize(400, 600);
		jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	    jfrm.setLayout(new FlowLayout());
		
		JLabel jlab_dep_airport = new JLabel("Departure airport:");
		jfrm.add(jlab_dep_airport);
		final JTextField jtf_dep_airport = new JTextField(20);
		jtf_dep_airport.setPreferredSize(new Dimension(150,20));
		jfrm.add(jtf_dep_airport);
		
		JLabel jlab_arr_airport = new JLabel("Arrival airport:");
		jfrm.add(jlab_arr_airport);
		final JTextField jtf_arr_airport = new JTextField(20);
		jfrm.add(jtf_arr_airport);
		
		JLabel jlab_con_flights_count = new JLabel("Number of connecting flights(1-3):");
		jfrm.add(jlab_con_flights_count);
		final JTextField jtf_con_flights_count = new JTextField(20);
		jfrm.add(jtf_con_flights_count);
		
		final JLabel jlab_status = new JLabel();
		final JScrollPane jsp = new JScrollPane();
		
		JButton jbtn_search = new JButton("Find tickets");
		jbtn_search.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JTable jtab_tickets = new JTable();
				System.out.println("event action triggered!");
				String dep_airport = jtf_dep_airport.getText();
				String arr_airport = jtf_arr_airport.getText();
				int num_of_flights = Integer.parseInt(jtf_con_flights_count.getText());
				if(num_of_flights >3 || num_of_flights < 1){
					num_of_flights = 1;
				}
				
				HashMap<Integer,Set> arylist = find_tickets(dep_airport, arr_airport, num_of_flights);
				
				Object[][] data = new Object[arylist.size()][8];
				Set<Integer> keys = arylist.keySet();
				
				int count = 0;
				if(arylist.size() > 0){
					System.out.println(count);
					for(int i: keys){
						data[count][0] = i;
						if(arylist.get(i).contains("Sun")){
							data[count][1] = "Y";
						} else {
							data[count][1] = "-";
						}
						if(arylist.get(i).contains("Mon")){
							data[count][2] = "Y";
						} else {
							data[count][2] = "-";
						}
						if(arylist.get(i).contains("Tue")){
							data[count][3] = "Y";
						} else {
							data[count][3] = "-";
						}
						if(arylist.get(i).contains("Wed")){
							data[count][4] = "Y";
						} else {
							data[count][4] = "-";
						}
						if(arylist.get(i).contains("Thu")){
							data[count][5] = "Y";
						} else {
							data[count][5] = "-";
						}
						if(arylist.get(i).contains("Fri")){
							data[count][6] = "Y";
						} else {
							data[count][6] = "-";
						}
						if(arylist.get(i).contains("Sat")){
							data[count][7] = "Y";
						} else {
							data[count][7] = "-";
						}
						
						count++;
					}
	
					String[] colHeads = {"Flight number", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri","Sat"};
					
					jtab_tickets = new JTable(data, colHeads);
					jsp.getViewport ().add (jtab_tickets);
					jfrm.setMinimumSize(new Dimension(600, 23));
					
					jfrm.add(jsp);
					jlab_status.setText("Displaying results with "+num_of_flights+" exactly flights");
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
				find_tickets_screen();
			}
		});
		
		System.out.println("Program completed.");
	}
}
