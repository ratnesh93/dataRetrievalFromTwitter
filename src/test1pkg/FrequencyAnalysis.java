package test1pkg;


import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class FrequencyAnalysis {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/mydbv2";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "1234";

	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		Connection conn = null;
		Statement stmt = null, stmt2;

		Class.forName("com.mysql.jdbc.Driver");

		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);

		stmt = (Statement) conn.createStatement();
		stmt2 = (Statement) conn.createStatement();
		String sql;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		java.sql.PreparedStatement ps = null;
		
		
		
//		while(event_id<31){
			
			sql = "select filtertweet.text,retweetCount,event_id from filtertweet"+
						" INNER JOIN tweet ON tweet_id = id;";
			rs = stmt.executeQuery(sql);

			String text = "";
			Long id;
			int rc=0;
			while (rs.next()) {
				id = rs.getLong("event_id");
				text = rs.getString("text");
				rc = rs.getInt("retweetCount");
				//System.out.println("tweet = " + id);
				StringTokenizer st = new StringTokenizer(text);
				while (st.hasMoreTokens()) {
					String term = st.nextToken();
					//System.out.println(term + rc);
					
					sql = "SELECT * from terms where event_id = " + id +" and term=" + '"'+term+'"';
					rs3 = stmt2.executeQuery(sql);
					if (!rs3.next()) {
						sql = "INSERT INTO terms VALUES(?,?,?)";
						ps = conn.prepareStatement(sql);
						ps.setLong(2, id);
						//System.out.println("term " + term + "\n\n");
						ps.setString(1, term);
						ps.setInt(3,rc+1);
						ps.executeUpdate();
					}
					else{
						sql = "UPDATE terms SET count = ? WHERE term = ? AND event_id = ?";
						ps = conn.prepareStatement(sql);
						ps.setLong(3, id);
						System.out.println("term " + term + "\n\n");
						ps.setString(2, term);
						ps.setInt(1,rc+1+rs3.getInt("count"));
						ps.executeUpdate();
					}
					
					
				
				}
		}
			
			
			
		//}
		
		

	}
}
