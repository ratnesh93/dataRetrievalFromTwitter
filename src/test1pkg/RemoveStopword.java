package test1pkg;


import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class RemoveStopword {

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
		
		
		int event_id = 1;
		
		while(event_id<43){
			
			sql = "SELECT id,text from tweet where event_id =" + event_id;
			rs = stmt.executeQuery(sql);

			String text = "";
			Long id;
			while (rs.next()) {
				id = rs.getLong("id");
				text = rs.getString("text");
				System.out.println("tweet = " + text);
				text = text.replaceAll("(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?\u00AB\u00BB\u201C\u201D\u2018\u2019]))", " ");
				//text = text.replaceAll("www[^\\s]+", "");
				text = text.replaceAll("[^a-zA-Z0-9#@ '-]", " ");
				String temp="";
				System.out.println("tweet after removal of links= " + text);

				StringTokenizer st = new StringTokenizer(text);
				while (st.hasMoreTokens()) {
					String stopword = st.nextToken();
					
					sql = "SELECT * from stopwords WHERE word = ?";
					ps = conn.prepareStatement(sql);
					ps.setString(1, stopword);
					rs2 = ps.executeQuery();

					if (!rs2.next()) {
						
						temp=temp+stopword+' ';
						
						//System.out.println("tweet after removing stopword = " + text);
					}
					else{
						System.out.println("stopword : " + stopword);
					}
				}
				if(temp!=""){
					temp=temp.substring(0, temp.length()-1);
					text = temp;
				}
				
				sql = "SELECT * from filtertweet where tweet_id = " + id;
				rs3 = stmt2.executeQuery(sql);
				if (!rs3.next()) {
					sql = "INSERT INTO filtertweet VALUES(?,?)";
					ps = conn.prepareStatement(sql);
					ps.setLong(1, id);
					System.out.println("filtered text " + text + "\n\n");
					ps.setString(2, text.toLowerCase());
					ps.executeUpdate();
				}
				//break;

			}
			event_id++;
			
		}
		}
			
		}
		
		


