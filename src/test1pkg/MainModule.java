package test1pkg;

import java.io.UnsupportedEncodingException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.MysqlDataTruncation;
import com.mysql.jdbc.Statement;


import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class MainModule {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/mydbv2";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "1234";


	
	public static void main(String[] args) throws TwitterException, SQLException, ClassNotFoundException, UnsupportedEncodingException {
		Connection conn = null;
		Statement stmt = null;

		Class.forName("com.mysql.jdbc.Driver");

		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);

		stmt = (Statement) conn.createStatement();
		String sql;
		ResultSet rs = null;
		
		
		//Just put event_id and quer_no from 1 to 8 here ...and voila
		int event_id = 39;
		int query_no = 7;
		///
		
		String title="";
		String city="";
		String venue="";
		String place_id="";
		String country="";
		sql = "SELECT * from event where id=" + event_id;
		rs = stmt.executeQuery(sql);
		
		
		while(rs.next()){
			title=rs.getString("name");
			place_id = rs.getString("place_id");
			venue = rs.getString("venue");
			
		}
		
		//could use prepared statements...but have to search syntax so LITE.
		sql = "SELECT * from place where id=" + '"'+place_id+'"';
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			city = rs.getString("name");
			country = rs.getString("country");
		}
		
		
		//System.out.println(title +','+place+','+venue);

		Twitter twitter = new TwitterFactory().getInstance();
		
		String queryStr="";
		
		if(query_no==1){
			queryStr = '"'+title+'"' + '"'+city+'"';
		}
		else if(query_no==2){
			queryStr = title + '"'+city+'"';
		}
		else if(query_no==3){
			//queryStr = title + place;          //remove stopwords ka code banana hai....get a list of stopwords!
		}
		else if(query_no==4){
			queryStr = '"'+title+'"' + '"'+venue+'"';	
		}
		else if(query_no==5){
			queryStr = title + '"'+venue+'"';
		}
		else if(query_no==6){
			queryStr = '"'+title+'"';
		}
		else if(query_no==7){
			queryStr = title;
		}
		else if(query_no==8){
			//queryStr = title;          //remove stopwords ka code banana hai....get a list of stopwords!
		}
		else if(query_no==9){
			queryStr = title + '"'+country+'"';
		}
		else if(query_no==10){
			queryStr = '"'+title+'"' + '"'+country+'"';
		}
		
		
		
		Query query = new Query("lang:en "+queryStr);
		query.count(100);
		
		int searchResultCount=1;
		int count=0;
		long lowestTweetId = 650507134577147905L;
		long highestTweetId = 0;
		//query.setSinceId(646391336271237121L);
		
		
		while(searchResultCount!=0){
			
			query.setMaxId(lowestTweetId-1);
			
			QueryResult result = twitter.search(query);
			searchResultCount = result.getTweets().size();
			java.sql.PreparedStatement ps = null;
			
			for (Status status : result.getTweets()) {
				
				if (status.getId() < lowestTweetId) {
	                lowestTweetId = status.getId();
	               // System.out.println(lowestTweetId);
	                
	            }
	            if (status.getId() > highestTweetId) {
	                highestTweetId = status.getId();
	                
	            }
				if(status.isRetweet()){
					status = status.getRetweetedStatus();
				}
				
				
				
				
				sql = "SELECT * from user where id=" + status.getUser().getId();
				rs = stmt.executeQuery(sql);
				
				if (!rs.next()) {
//					System.out.println("inside insert user");
//					User newUser=new User(status.getUser().getId(),status.getUser().getDescription(), status.getUser().getFollowersCount(), status.getUser().getFriendsCount(), new java.sql.Date(status.getUser().getCreatedAt().getTime()), status.getUser().getLang(), status.getUser().getLocation(), status.getUser().getName() ,status.getUser().getScreenName(), status.getUser().getStatusesCount(),status.getUser().isVerified());				
//					newUser.insertUser();
					sql = "INSERT INTO user VALUES(?,?,?,?,?,?,?,?,?,?,?)";
					ps = conn.prepareStatement(sql);
				
					ps.setLong(1, status.getUser().getId());
					ps.setString(2, status.getUser().getName());
					ps.setString(3, status.getUser().getScreenName());
					if(status.getUser().getLang().length()>5)
						ps.setString(4, null);
					else
						ps.setString(4, status.getUser().getLang());
					ps.setString(5, status.getUser().getDescription());
					ps.setInt(6, status.getUser().getStatusesCount());
					ps.setInt(7, status.getUser().getFollowersCount());
					ps.setInt(8, status.getUser().getFriendsCount());
					ps.setDate(9, new java.sql.Date(status.getUser().getCreatedAt().getTime()));
					ps.setString(10, status.getUser().getLocation());
					ps.setBoolean(11, status.getUser().isVerified());
					try{
						ps.executeUpdate();
					}
					catch(MysqlDataTruncation e){
						System.out.println("error"+status.getUser().getLang().length());
						System.out.println(status.getUser().getLang());
						System.out.println(status.getUser());
					}
				}

				if (status.getPlace() != null) {

					sql = "SELECT * from place where id=" + '"' + status.getPlace().getId() + '"';
					rs = stmt.executeQuery(sql);
					
					if (!rs.next()) {
						
						sql = "INSERT INTO place VALUES(?,?,?,?,?,?,?)";
						ps = conn.prepareStatement(sql);

						ps.setString(1, status.getPlace().getId());
						ps.setString(2, status.getPlace().getName());
						ps.setString(3, status.getPlace().getCountry());
						ps.setString(4, status.getPlace().getCountryCode());
						ps.setString(5, status.getPlace().getPlaceType());
						ps.setString(6, status.getPlace().getFullName());
						ps.setString(7, status.getPlace().getStreetAddress());
						ps.executeUpdate();

					}
				}

				sql = "SELECT * from tweet where id=" + status.getId(); //perform and query here with eventid
				rs = stmt.executeQuery(sql);

				if (!rs.next()) {
					//System.out.println("text :"+status.getText());
					//System.out.println("user:"+status.getUser().getName());
					System.out.println(status.getId());
					sql = "INSERT INTO tweet VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					ps = conn.prepareStatement(sql);

					ps.setLong(1, status.getId());
					ps.setInt(2, event_id);//event -id
					//ps.setString(3, new String(status.getText().getBytes("UTF-8")));
					
					ps.setLong(3, status.getUser().getId());
					if (status.getPlace() != null) {
						ps.setString(4, status.getPlace().getId());
					} else {
						ps.setString(4, "dummy");
					}
					ps.setString(5, status.getText().replaceAll("[^\\x00-\\x7f-\\x80-\\xad]",""));
					ps.setString(6, status.getLang());
					ps.setDate(7, new java.sql.Date(status.getCreatedAt().getTime()));
					ps.setInt(8, status.getFavoriteCount());
					ps.setInt(9, status.getRetweetCount());
//					ps.setString(6, "_utf8" + '"'+status.getText()+'"');
					ps.setBoolean(10, status.isPossiblySensitive());
					ps.setBoolean(11, status.isRetweet());
					if (status.getGeoLocation() != null) {
						ps.setDouble(12, status.getGeoLocation().getLatitude());
						ps.setDouble(13, status.getGeoLocation().getLongitude());
					} else {
						ps.setDouble(12, 0);
						ps.setDouble(13, 0);
					}
					ps.setInt(14, query_no);
					ps.setInt(15, 1);
					ps.executeUpdate();
				}
				
				
				
	           
	            
	           count++;
          if(count>=7000){
	        	   	System.out.println(count);
		       		System.out.println(lowestTweetId);
       		System.out.println(highestTweetId);
//		       		query.setMaxId(lowestTweetId-1);
//		       		try {
//		       		    
//		       		    TimeUnit.MINUTES.sleep(15);
//		       		    count=0;
//		       		   
//		       		} catch (InterruptedException e) {
//		       		    //Handle exception
//		       		}
//		       		//System.exit(0);
          }

				
				
			}
			System.out.println(count);
			
		}
		System.out.println(count);
		System.out.println(lowestTweetId);
		System.out.println(highestTweetId);
		
		stmt.close();
		conn.close();

	}
/*	public static boolean isUserPresent(Long id) throws SQLException{
		System.out.println("if user is present");
		String sql = "SELECT * from user where user_id=" + id;
		Statement stmt=null;
		ResultSet rs = stmt.executeQuery(sql);
		if(!rs.next()){
			System.out.println("user not present");
			return false;
		}
		return true;
	}*/
}
