package test1pkg;


import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class trigram {

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
		
		int event_id=1;
		
		while(event_id<44){
			
			sql = "select a.text from"+
				  " (select filtertweet.text,retweetCount,event_id from filtertweet"+
				  " INNER JOIN tweet ON tweet_id = id) a"+
				  " where event_id="+event_id;
			rs = stmt.executeQuery(sql);

			String text = "";
			Long id;
			int rc=0;
			HashMap<String , Integer> hm =new HashMap();
			int bicount=0;
			int count=0;
			while (rs.next()) {
				count++;
				text = rs.getString("text");
				//rc = rs.getInt("retweetCount");
				//System.out.println("tweet = " + id);
				StringTokenizer st = new StringTokenizer(text);
				String prev="";
				String prev2="";
				if(st.hasMoreTokens())
					prev = st.nextToken();
				if(st.hasMoreTokens())
					prev2 = st.nextToken();
				String trigram="";
				String term="";
				
				
				while (st.hasMoreTokens()) {
					term = st.nextToken();
					trigram = prev +" "+prev2+" " +term;
					prev = prev2;
					prev2 = term;
					if(!hm.containsKey(trigram)){
						hm.put(trigram, 1);
						System.out.println(trigram + " " + count);
						bicount++;
					}
						
					else
						hm.put(trigram,hm.get(trigram)+1);
					
						
					
					
					
				}
			}
			
			System.out.println(bicount + " " + count);
			Set set = hm.entrySet();
		      // Get an iterator
		    Iterator i = set.iterator();
		      // Display elements
		    while(i.hasNext()) {
		         Map.Entry<String,Integer> me = (Map.Entry)i.next();
		         sql = "INSERT INTO trigrams VALUES(?,?,?)";
		 		 ps = conn.prepareStatement(sql);
		 		 
		 		 ps.setString(1, me.getKey());
		 		 ps.setInt(3, me.getValue());
		 		 ps.setInt(2, event_id); 
		 		 ps.executeUpdate();
		         
		     }
			
			
			hm.clear();
			event_id++;
			
			
		}
					//System.out.println(term + rc);
					
//					sql = "SELECT * from terms where event_id = " + id +" and term=" + '"'+term+'"';
//					rs3 = stmt2.executeQuery(sql);
//					if (!rs3.next()) {
						
					//}
//					else{
//						sql = "UPDATE terms SET count = ? WHERE term = ? AND event_id = ?";
//						ps = conn.prepareStatement(sql);
//						ps.setLong(3, id);
//						System.out.println("term " + term + "\n\n");
//						ps.setString(2, term);
//						ps.setInt(1,rc+1+rs3.getInt("count"));
//						ps.executeUpdate();
//					}

		//}
		
		

	}
}
