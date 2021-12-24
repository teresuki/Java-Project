package JDBC;
import java.sql.*;
import java.util.Random;
public class HealthDatabase {
	private static String adminPassword;
	public HealthDatabase(String adminPassword) {
			this.adminPassword = adminPassword;
		}
	
	public static boolean isCustomerInfoExist(String key, String value) throws SQLException, ClassNotFoundException
	{
		String query = "Select "+key+ " from customer where "+key+" = "+'"'+value+'"';
		System.out.println(query);
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/health_system","root",adminPassword);
			Statement st = con.createStatement();
			st.executeQuery(query);
			ResultSet rs = st.getResultSet();
			if(rs.next()) {
				rs.close();
				st.close();
				return true;
			}
		} catch(SQLException e) {
			System.out.println("Error in database:\n" + e.getMessage());
			return false;
		}
		return false;
		
	}
	private String generateRandomString() {
		Random rand= new Random();
		String result="";
		String alphabet ="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int i=0;
		for(i=0;i<8;i++) {
			if(i==0 || i==5) {
				int randNum = rand.nextInt((9-0)+1)+0;
				result= result+String.valueOf(randNum);
			} else {
				int randNum = rand.nextInt((26-0)+1)+0;
				result = result+alphabet.charAt(randNum);
			}
			
		}
		return result;
	}
	// generate unique appointment id
	public String generateAppointmentID() throws SQLException, ClassNotFoundException
	{
		String query = "select appointmentID from appointment";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/health_system","root",adminPassword);
			Statement st = con.createStatement();
			st.executeQuery(query);
			ResultSet rs = st.getResultSet();
			String myAppointmentID = generateRandomString();
			while(rs.next()) {
				String databseAppointmentID = rs.getString(1);
				if(myAppointmentID==databseAppointmentID) {
					myAppointmentID = generateRandomString();
				}
			}
			return myAppointmentID;
		} catch (SQLException e)
		{
			System.out.println("Error in database:\n" + e.getMessage());
			return "Error";
			
		}
		
	}
	
}
