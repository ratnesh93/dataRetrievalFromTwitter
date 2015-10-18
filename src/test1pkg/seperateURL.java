package test1pkg;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class seperateURL {
	
	// JDBC driver name and database URL
		static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		static final String DB_URL = "jdbc:mysql://localhost/mydbv2";

		// Database credentials
		static final String USER = "root";
		static final String PASS = "1234";

	public static String expandUrl(String shortenedUrl) throws IOException {
		String USER_AGENT = "Mozilla/5.0";
		try{
        URL url = new URL("http://expandurl.appspot.com/expand?url="+URLEncoder.encode(shortenedUrl, "UTF-8"));    
        // open connection
        HttpURLConnection con = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY); 
        
        con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		org.json.JSONObject jo = new org.json.JSONObject(response.toString());
		in.close();

		//print result
		//System.out.println(jo.get("end_url"));
        con.disconnect();
         
        return jo.get("end_url").toString();
		}
		catch(SocketTimeoutException e){
			return "-1";
		}
        
    }
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		
		Connection conn = null;
		Statement stmt = null;
		Statement stmt2 = null;
		
		
		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();
		stmt2 = (Statement) conn.createStatement();
		String sql;
		ResultSet rs = null;
		ResultSet rs2 =null;
		java.sql.PreparedStatement ps= null;
	
		Pattern pattern = Pattern.compile("(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?\u00AB\u00BB\u201C\u201D\u2018\u2019]))");
		
		sql = "SELECT distinct event_id FROM tweet";
		rs2 = stmt.executeQuery(sql);
		int event_id;
		
		HashMap<String , Integer> hm ;
		
		try
	      {
	         FileInputStream fis = new FileInputStream("hashmap.ser");
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         hm = (HashMap) ois.readObject();
	         ois.close();
	         fis.close();
	      }
		catch(FileNotFoundException e){
			hm = new HashMap();
		}
		
		
		
		while(rs2.next()){
			
			event_id = rs2.getInt(1);
			//event_id = 10;
			if(event_id!=16)
				continue;
			sql = "SELECT text from tweet where event_id =" + event_id;
			rs = stmt2.executeQuery(sql);
			
			String text="";
			String url="";
			String expandedurl="";
			int linkcount=0;
			int count=0;
			while(rs.next()){
				text = rs.getString("text");
				count++;
				//System.out.println("tweet = " + text);
				Matcher matcher = pattern.matcher(text);
				    // Check all occurrences
			    while (matcher.find()) {
			       // System.out.print("Start index: " + matcher.start());
			        //System.out.print(" End index: " + matcher.end());
			       // System.out.println(" Found: " + matcher.group());
			    	
			    	url = matcher.group();
			    	
			    	url = expandUrl(url);
			    	
			    	//if net connection goes.
			    	if(url=="-1"){
			    		
			    		try
			            {
			                   FileOutputStream fos =new FileOutputStream("hashmap.ser");
			                   ObjectOutputStream oos = new ObjectOutputStream(fos);
			                   oos.writeObject(hm);
			                   oos.close();
			                   fos.close();
			                   System.out.printf("Serialized HashMap data is saved in hashmap.ser");
			            }catch(IOException ioe)
			             {
			                   ioe.printStackTrace();
			             }
			    		
			    		break;
			    	}
			    		
			    	if(!hm.containsKey(url)){
			    		//System.out.println("2 "+expandedurl);
			    		
			    		hm.put(url, 1);
			    		linkcount++;
			    		System.out.println(linkcount + " " +count + " "+url);
			    	}
			    		
			    	else
			    		hm.put(url, hm.get(url)+1);
			    }

			}
			System.out.println(linkcount);
			Set set = hm.entrySet();
		      // Get an iterator
		     Iterator i = set.iterator();
		      // Display elements
		     while(i.hasNext()) {
		         Map.Entry<String,Integer> me = (Map.Entry)i.next();
		         sql = "INSERT INTO links VALUES(null,?,?,?)";
		 		 ps = conn.prepareStatement(sql);
		 		 
		 		 ps.setString(1, me.getKey());
		 		 ps.setInt(2, me.getValue());
		 		 ps.setInt(3, event_id);
		 		 
		 		 ps.executeUpdate();
		         
		     }
			
			
			hm.clear();
			
		}
		

		
	}

}
