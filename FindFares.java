// Vivek Anand Sampath
package airline;

import java.util.*;
import java.sql.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class FindFares extends JPanel{
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
	
	public static ArrayList<TicketFares> find_fares(String flight_number){
		int count=0;
		ArrayList<TicketFares> data = new ArrayList<TicketFares>();
		
		try {
			Statement stmnt = connection.createStatement();
			String query = "select fare_code, amount from fare where flight_number = "+flight_number;
			
			ResultSet result = stmnt.executeQuery(query);
			
			String fare_code;
			double amount;
			while(result.next()){
				fare_code = result.getString("fare_code");
				amount = result.getDouble("amount");
				
				TicketFares tckt_list = new TicketFares(fare_code, amount);
				data.add(tckt_list);
			}
			
		} catch(SQLException e){
			throw new RuntimeException("Error in the query");
		}
		
		return data;
	}
	
	public static void find_fares_screen(){
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
		
		final JLabel jlab_status = new JLabel();
		final JScrollPane jsp = new JScrollPane();
		
		JButton jbtn_search = new JButton("List fares");
		jbtn_search.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JTable jtab_tickets = new JTable();
				System.out.println("event action triggered!");
				String flight_number = jtf_flight_number.getText();
				
				ArrayList<TicketFares> arylist = find_fares(flight_number);
				
				int count = 0;

				if(arylist.size() > 0){					
					Object[][] data = new Object[arylist.size()][2];
					Iterator<TicketFares> it = arylist.iterator();
					while(it.hasNext()) {
						TicketFares obj = it.next();
					    data[count][0] = obj.fare_code;
					    data[count][1] = obj.amount;
					    
					    count++;
					}
					String[] colHeads = {"Fare code", "amount"};
					
					jtab_tickets = new JTable(data, colHeads);
					jsp.getViewport ().add (jtab_tickets);
//					jsp.add(jtab_tickets);
					jfrm.setMinimumSize(new Dimension(600, 23));
//			    	jtab_tickets.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					
					jfrm.add(jsp);
					jlab_status.setText("");
					jfrm.revalidate();
					jfrm.getContentPane().repaint();
				} else {
					jlab_status.setText("No matches found");
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
				find_fares_screen();
			}
		});
		
		System.out.println("Program completed.");
	}
}

class TicketFares{
	public String fare_code;
	public double amount;
	
	TicketFares(String fare_cd, double amnt){
		this.fare_code = fare_cd;
		this.amount = amnt;
	}
}
