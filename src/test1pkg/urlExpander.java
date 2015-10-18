package test1pkg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;

import twitter4j.JSONObject;

public class urlExpander {

	public static void main(String[] args) throws IOException {
        String shortenedUrl = "http://t.co/KOWLKT8Ae0.";
        String expandedURL = expandUrl(shortenedUrl);
         
        System.out.println(shortenedUrl + "-->" + expandedURL); 
//        expandedURL = expandUrl(shortenedUrl);
//        
//        System.out.println(shortenedUrl + "-->" + expandedURL); 
//        expandedURL = expandUrl(expandedURL);
//        
//        System.out.println(expandedURL + "-->" + expandedURL); 
		
        
    }
	
     
    public static String expandUrl(String shortenedUrl) throws IOException {
    	String USER_AGENT = "Mozilla/5.0";
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
}
